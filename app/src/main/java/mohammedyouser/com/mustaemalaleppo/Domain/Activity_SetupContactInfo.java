package mohammedyouser.com.mustaemalaleppo.Domain;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mohammedyouser.com.mustaemalaleppo.Device.GpsUtils;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.SessionManager;


import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


public class Activity_SetupContactInfo extends AppCompatActivity implements View.OnClickListener {

    private EditText mNameField;
    private EditText mEmailField;
    private ImageView img_btn_userImage;
    private ImageView img_btn_userImage_add;

    private DatabaseReference db_root_users;
    private StorageReference mStorageReference;


    private Uri uri_user_img;
    private Uri uri_download;


    private Bitmap bitmap;
    private String userID;

    private static final int REQUEST_PERMISSION_LOCATION_GET = 787;
    private static final int REQUEST_PERMISSION_IMG_READ = 786;

    public static double current_User_Lat;
    public static double current_User_Long;
    public static boolean locationUpdatedConfirm = false;
    private boolean isGPS = false;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = true;
    private TextView m_tv_location;
    private ProgressBar m_pb_location;
    private View m_btn_update_location;
    private String userAddress;
    private SessionManager sessionManager;
    private TextView  m_tv_userPhoneNumber;
    private String userPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_contact_info);
        sessionManager = new SessionManager(this, "uriSession");


        FirebaseAuth auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();

        mStorageReference = FirebaseStorage.getInstance().getReference();

        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);
        db_root_users.keepSynced(true);


        mNameField = findViewById(R.id.editText_displayName);
        mEmailField = findViewById(R.id.editText_email);

        img_btn_userImage = findViewById(R.id.img_btn_developer_image);
        img_btn_userImage_add = findViewById(R.id.img_btn_user_image_add);
        Button mSave = findViewById(R.id.button_save_user_info);
        Button mIgnore = findViewById(R.id.button_ignore_user_info);

        m_tv_userPhoneNumber = findViewById(R.id.tv_userPhoneNumber);

        m_tv_location = findViewById(R.id.tv_location);
        m_pb_location = findViewById(R.id.pb_location);
        m_btn_update_location = findViewById(R.id.btn_update_my_location);


        mSave.setOnClickListener(this);
        mIgnore.setOnClickListener(this);
        img_btn_userImage.setOnClickListener(this);
        img_btn_userImage_add.setOnClickListener(this);
        m_btn_update_location.setOnClickListener(this);

        setSupportActionBar(findViewById(R.id.toolBar));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadContactInfoFromDB();
        setUserPhoneNumber(userID);


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INTENT_KEY_USER_IMAGE_URI, String.valueOf(uri_user_img));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("user_img_uri")) {
            uri_user_img = Uri.parse(savedInstanceState.getString(INTENT_KEY_USER_IMAGE_URI));
            setImage_circle(this, uri_user_img, 0.3f, img_btn_userImage);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save_user_info: {
                // setUpContactInfo(getFinalStorageReference());
                manage_adding_userImg(uri_user_img);
            }
            break;

            case R.id.button_ignore_user_info: {
                finish();
            }
            break;
            case R.id.img_btn_developer_image:
                startCropActivity(this);

                break;
            case R.id.img_btn_user_image_add:
                startCropActivity(this);

                break;
            case R.id.btn_update_my_location:
                confirmUpdateLocation();


                break;
        }


    }

    private void startCropActivity(Activity activity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri_user_img = result.getUri();
                sessionManager.createSession("uriKey", String.valueOf(uri_user_img));

                setImage_circle(this, uri_user_img, 1, img_btn_userImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView) {
        showProgressDialog(this, getString(R.string.title_account_ifo), getString(R.string.message_info_PLEASE_WAIT));
        Log.d(TAG, "setImage_circle:1");
        if (imageURL == null) return;

        imageView.setImageTintMode(null);
        Glide.with(context)
                .load(imageURL)
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(thumbnail)
/*
                .diskCacheStrategy(DiskCacheStrategy.ALL)
*/
                .into(imageView)


        ;

        hideProgressDialog();
    }

    private void setUpContactInfo(final StorageReference storageReference) {

        if (TextUtils.isEmpty(getUserName_fromField()) ||
                TextUtils.isEmpty(getUserEmail_fromField())) {
            Toast.makeText(this, R.string.message_error_empty_field, Toast.LENGTH_SHORT).show();

        } else {
            showProgressDialog(this, getString(R.string.message_info_UPDATING_CONTACT_INFO), getString(R.string.message_info_PLEASE_WAIT));
            // First upload User image to firebase
            if (uri_user_img == null) {
                uri_user_img = Uri.EMPTY;

            }
            try {
                if (uri_user_img != null) {
                    //file:///data/user/0/mohammedyouser.com.mustaemalaleppo/cache/cropped4888811657471856332.jpg
                    Log.d(TAG, "setUpContactInfo:555 " + uri_user_img);
                    if (uri_user_img.getScheme().equals("file")) {
                        manage_adding_userImg(uri_user_img);
                        storageReference.putFile(uri_user_img).addOnSuccessListener(taskSnapshot -> {
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                uri_download = uri;
                                Log.d("d_uri", String.valueOf(uri_download));
                                storeUserInfoToFirebase(uri);
                            });

                            // Second save user name and the URL of user image to firebase


                        }).addOnFailureListener(e -> {
                            hideProgressDialog();
                        });
                    } else {
                        storeUserInfoToFirebase(null);
                    }
                } else {
                    storeUserInfoToFirebase(null);
                }


            } catch (Exception e) {

                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();

            }
            hideProgressDialog();
            /*finally {
                storeUserInfoToFirebase();

                hideProgressDialog();
                finish();
            }*/
        }

    }

    private StorageReference getFinalStorageReference() {


        return mStorageReference.child(PATH_USERS).child(userID).child(PATH_USER_IMAGE);
    }

    private String getUserName_fromField() {

        return String.valueOf(mNameField.getText());
    }

    private String getUserEmail_fromField() {

        return String.valueOf(mEmailField.getText());

    }

    private void loadContactInfoFromDB() {

        setUserName_from_DB(userID);
        setUserEmail_from_DB(userID);
        setUserImage_from_DB(userID);
        setUserAddress_from_DB(userID);


    }

    private void setUserAddress_from_DB(String userID) {
        userAddress = get_UserAddress_from_DB(userID);
        m_tv_location.setText(userAddress);
        m_pb_location.setVisibility(View.GONE);
    }

    private String setUserName_from_DB(final String userID) {

        final String[] userName = {new String()};
        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName[0] = (String) dataSnapshot.child(userID).child(PATH_USER_NAME).getValue();
                mNameField.setText(userName[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userName[0];
    }

    private String setUserEmail_from_DB(final String userID) {

        final String[] userEmail = {new String()};
        db_root_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userID).child(PATH_USER_EMAIL).getValue() != null) {
                    userEmail[0] = (String) dataSnapshot.child(userID).child(PATH_USER_EMAIL).getValue();
                    mEmailField.setText(userEmail[0]);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userEmail[0];


    }

    private Uri setUserImage_from_DB(final String userID) {


        db_root_users.child(userID).child(PATH_USER_IMAGE).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    uri_user_img = Uri.parse(String.valueOf(dataSnapshot.getValue()));
                    setImage_circle(getBaseContext(), uri_user_img, 1, img_btn_userImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return uri_user_img;

    }

    private Uri getUserImageUri(SessionManager sessionManager) {
        String imgUri = sessionManager.getSession("uriSession").get("uriKey");
        if (imgUri != null) {
            return Uri.parse(imgUri);
        }
        return null;
    }


    private void storeUserInfoToFirebase(Uri uri_download) {
        db_root_users.child(userID).child(PATH_USER_NAME).setValue(getUserName_fromField());
        if (uri_download != null) {
            db_root_users.child(userID).child(PATH_USER_IMAGE).setValue(String.valueOf(uri_download));
        }
        db_root_users.child(userID).child(PATH_USER_EMAIL).setValue(getUserEmail_fromField());
        setImage_circle(this, uri_user_img, 0.3f, img_btn_userImage);


        hideProgressDialog();
        finish();


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /*Location Handling Start*/

    private void prepareGPS() {
        new GpsUtils(this).turnGPSOn(isGPSEnable -> {
            // turn on GPS
            isGPS = isGPSEnable;
        });
    }

    private void setUpLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_LOCATION_GET);
            return;
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(Activity_SetupContactInfo.this).removeLocationUpdates(this);
                if (locationResult == null || locationResult.getLocations().size() == 0) {
                    return;
                }
                int latestLocationIndex = locationResult.getLocations().size() - 1;
                for (Location location : locationResult.getLocations()) {
                    LatLng sydney = new LatLng(-34, 151);
                    if (location != null) {

                        Activity_SetupContactInfo.GeoCoderHandler m_geocoderHandler = new Activity_SetupContactInfo.GeoCoderHandler();
                        getAddressFromLocation_GeoCoderProcessor(locationResult.getLocations().get(latestLocationIndex), getBaseContext(), m_geocoderHandler);
                        store_UserLocation_To_DB(new LatLng(locationResult.getLocations().get(latestLocationIndex).getLatitude(),

                                locationResult.getLocations().get(latestLocationIndex).getLongitude()), userID);
                        Toast.makeText(Activity_SetupContactInfo.this, "Location Updated!", Toast.LENGTH_SHORT).show();


                    }
                }
            }


            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
        getFusedLocationProviderClient().requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());

    }

    private void initializeUserLocIfNeeded() {
        db_root_users.child(userID).child(PATH_USER_LOCATION_LAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (String.valueOf(snapshot.getValue()).equals("null")) {
                    prepareGPS();
                    setUpLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void getAddressFromLocation_GeoCoderProcessor(final Location location, final Context context, final Handler handler) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)); //.append("\n");
                        }
                        //  sb.append(address.getSubThoroughfare()).append("\n");
                        if(!address.getThoroughfare().equals("null"))
                            sb.append(address.getThoroughfare()).append("\n");//\n

                        sb.append(address.getLocality()).append("\n");//\n
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e("Location Address Loader", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        bundle.putDouble("lat", location.getLatitude());
                        bundle.putDouble("long", location.getLongitude());
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = " Unable to get address for this location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(10 * 1000);
        return locationRequest;
    }

    private FusedLocationProviderClient getFusedLocationProviderClient() {


        return LocationServices.getFusedLocationProviderClient(this);
    }
    /*
    Alternative way for enable GPS
    * LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
boolean enabled = service
    .isProviderEnabled(LocationManager.GPS_PROVIDER);

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
if (!enabled) {
  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    startActivity(intent);
}
    * */

    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    current_User_Lat = bundle.getDouble("lat");
                    current_User_Long = bundle.getDouble("long");
                    break;
                default:
                    locationAddress = null;
            }
            Log.e("location Address=", locationAddress);
            store_UserAddress_To_DB(locationAddress);
            update_UI_with_Location(locationAddress);

        }


    }

    private void store_UserAddress_To_DB(String locationAddress) {
        db_root_users.child(userID).child(PATH_USER_ADDRESS).setValue(String.valueOf(locationAddress));

    }

    private void update_UI_with_Location(String locationAddress) {

        m_pb_location.setVisibility(View.GONE);
        m_tv_location.setText(locationAddress);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
/*
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
*/
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_IMG_READ:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms", "perms granted");
                    upload_userImg_and_data(uri_user_img);

                } else {
                    Log.d("perms", "perms not granted");

                    Toast.makeText(Activity_SetupContactInfo.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();

                }
                break;
            case REQUEST_PERMISSION_LOCATION_GET:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms_location", "perms granted");
                    setUpLocation();

                } else {
                    Log.d("perms_location", "perms not granted");

                    Toast.makeText(Activity_SetupContactInfo.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();


                }
                break;
        }

    }

    private void stopLocationUpdates() {
        if (locationCallback != null)
            getFusedLocationProviderClient().removeLocationUpdates(locationCallback);
    }

    private void store_UserLocation_To_DB(LatLng latLng, String userID) {

        db_root_users.child(userID).child(PATH_USER_LOCATION_LAT).setValue(String.valueOf(latLng.latitude));
        db_root_users.child(userID).child(PATH_USER_LOCATION_LONG).setValue(String.valueOf(latLng.longitude));

    }

    private String get_UserAddress_from_DB(final String userID) {
        db_root_users.child(userID).child(PATH_USER_ADDRESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_userAddress) {
                userAddress = String.valueOf(snapshot_userAddress.getValue());
                if (userAddress != null) {
                    m_tv_location.setText(userAddress);

                } else {
                    m_tv_location.setText(getString(R.string.default_val_user_address));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return userAddress;
    }

    private void confirmUpdateLocation() {
        showDialogFragment(new Fragment_UpdateLocation_Alert_Dialog());
        getSupportFragmentManager().setFragmentResultListener(BUNDLE_KEY_REQUEST_LOCATION_UPDATE, this, (requestKey, bundle) -> {
            if (bundle.getBoolean(BUNDLE_KEY_REQUEST_LOCATION_UPDATES)) {
                prepareGPS();
                setUpLocation();
            }
        });

    }

    public void showDialogFragment(DialogFragment newFragment) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        // save transaction to the back stack
        ft.addToBackStack("dialog");
        newFragment.show(ft, "dialog");
        getSupportFragmentManager().executePendingTransactions();
    }


    private void manage_adding_userImg(Uri uri_user_img) {
        if (uri_user_img == null) {
            Log.d(TAG, "manage_adding_userImg: 1");
            storeUserInfoToFirebase(null);
            return;
        }
        if (uri_user_img.getScheme() == null) {
            storeUserInfoToFirebase(null);
            return;
        }
        if (!uri_user_img.getScheme().equals("file")) {
            storeUserInfoToFirebase(null);
        } else {
            Log.d(TAG, "manage_adding_userImg: 3");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_IMG_READ);
                } else {
                    upload_userImg_and_data(uri_user_img);
                }
            }
        }


    }


    private void upload_userImg_and_data(Uri uri_user_img) {
        Log.d(TAG, "upload_userImg_and_data: ");
        showProgressDialog(this, getString(R.string.message_info_UPDATING_CONTACT_INFO), getString(R.string.message_info_PLEASE_WAIT));

        getFinalStorageReference().putFile(uri_user_img)
                .addOnSuccessListener(taskSnapshot -> {
                    getFinalStorageReference().getDownloadUrl().addOnSuccessListener(uri -> {

                        storeUserInfoToFirebase(uri);

                    });

                })
                .addOnFailureListener(e ->
                        Log.d(TAG, "upload_userImg_and_data: "+e.getMessage()))
                .addOnCompleteListener(task -> hideProgressDialog());
    }
    private String setUserPhoneNumber(final String userID) {

        db_root_users.child(userID).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userPhoneNumber = (String) dataSnapshot.child(PATH_USER_PHONE_NUMBER).getValue();
                m_tv_userPhoneNumber.setText(userPhoneNumber);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userPhoneNumber;


    }


}



/*Location Handling End*/

