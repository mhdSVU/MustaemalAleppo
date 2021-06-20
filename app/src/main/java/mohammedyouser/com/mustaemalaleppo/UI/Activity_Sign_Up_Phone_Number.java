package mohammedyouser.com.mustaemalaleppo.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import mohammedyouser.com.mustaemalaleppo.Domain.Fragment_Dialog_Must_Agree_Ts_and_Cs_Alert;
import mohammedyouser.com.mustaemalaleppo.Domain.Fragment_Dialog_VPN_Alert;
import mohammedyouser.com.mustaemalaleppo.Device.GpsUtils;
import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_IMAGE_URI;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_NAME;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_PASSWORD;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_PHONE_NUMBER;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USER_LOCATION_LAT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USER_LOCATION_LONG;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_READ_PERMISSION_ITEM_MODIFY;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.adjustLanguage;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.calledAlready;

public class Activity_Sign_Up_Phone_Number extends AppCompatActivity implements View.OnClickListener {

    private static final String STR_TAG_FRAGMENT_VERIFY = "fragment_phone_number_verify_tag";
    private EditText m_et_userName;
    private EditText m_et_phoneNumber;
    private EditText m_et_password;
    private EditText m_et_password_confirm;
    private TextInputLayout m_til_password;
    private TextInputLayout m_til_password_confirm;
    private TextInputLayout m_til_phone_number;
    private TextInputLayout m_til_et_userName;
    private ImageButton m_img_btn_user_image;
    private Button m_btn_sign_up;
    private CheckBox m_cb_agree_ts_and_cs;
    private CountryCodePicker m_cpp;

    private DatabaseReference db_root_users;
    private Uri uri_user_img = null;

    private Bitmap bitmap;
    private TextView m_tv_sign_in;
    private Fragment_Sign_Up_Phone_Number_Verify_and_Create_Account fragment_phone_number_verify;

    private TextView m_tv_location;
    private ProgressBar m_pb_location;

    public static double current_User_Lat;
    public static double current_User_Long;

    private static final int REQUEST_PERMISSION_LOCATION_GET = 787;
    private boolean isGPS = false;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = true;
    private Spanned terms;
    private Spanned privacy;
    private Spanned iagree;
    private Spanned and;
    private TextView m_tv_iagree;
    private TextView m_tv_terms;
    private TextView m_tv_and;
    private TextView m_tv_privacy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()),this);

        setContentView(R.layout.activity__sign__up__phone__number);

        m_et_userName =  findViewById(R.id.et_userName);
        m_et_phoneNumber = findViewById(R.id.et_phoneNumber);
        m_et_password = findViewById(R.id.et_password);
        m_et_password_confirm =  findViewById(R.id.et_password_confirm);
        m_img_btn_user_image =  findViewById(R.id.img_btn_developer_image);
        m_btn_sign_up = findViewById(R.id.btn_sign_up);
        m_tv_sign_in = findViewById(R.id.tv_sign_in);
        m_cpp =  findViewById(R.id.ccp);
        m_til_password = findViewById(R.id.til_password);
        m_til_password_confirm = findViewById(R.id.til_password_confirm);
        m_til_phone_number = findViewById(R.id.til_et_phoneNumber);
        m_til_et_userName = findViewById(R.id.til_et_userName);
        m_cb_agree_ts_and_cs = findViewById(R.id.cb_agree_ts_and_cs);
        m_tv_iagree = findViewById(R.id.tv_iagree);
        m_tv_terms = findViewById(R.id.tv_terms);
        m_tv_and = findViewById(R.id.tv_and);
        m_tv_privacy = findViewById(R.id.tv_privacy);

        terms = Html.fromHtml("<a href=\"https://drive.google.com/file/d/1xd4bnCruesMuj73Wfh91Tf06rEkLGb8B/view?usp=sharing\">"+getString(R.string.terms)+"</a>");
        privacy = Html.fromHtml("<a href=\"https://drive.google.com/file/d/1qg7K8LFaEBYtKX6MFf-WKJbgEnsWRMDf/view?usp=sharing\">"+getString(R.string.privacy)+"</a>");

        m_tv_terms.setText(terms);
        m_tv_privacy.setText(privacy);

        m_tv_terms.setMovementMethod(LinkMovementMethod.getInstance());
        m_tv_privacy.setMovementMethod(LinkMovementMethod.getInstance());


        m_img_btn_user_image.setOnClickListener(this);
        m_tv_sign_in.setOnClickListener(this);
        m_tv_sign_in.setMovementMethod(LinkMovementMethod.getInstance());
        m_btn_sign_up.setOnClickListener(this);
        m_cpp.setOnClickListener(this);
        m_et_password.setOnEditorActionListener((v, actionId, event) -> {
            m_til_password.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            return true;
        });
        m_et_password_confirm.setOnEditorActionListener((v, actionId, event) -> {
            m_til_password_confirm.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            return true;
        });
        m_et_password.setOnEditorActionListener((v, actionId, event) -> {
            m_til_password.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

            return false;
        });


        doFirebaseInitializations();
        if (savedInstanceState != null) {
            fragment_phone_number_verify = (Fragment_Sign_Up_Phone_Number_Verify_and_Create_Account) getSupportFragmentManager().findFragmentByTag(STR_TAG_FRAGMENT_VERIFY);
        } else {
            fragment_phone_number_verify = new Fragment_Sign_Up_Phone_Number_Verify_and_Create_Account();
        }

        confirm_download_VPN();

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
    protected void onStart() {
        super.onStart();
    }
    
    private void doFirebaseInitializations() {

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        DatabaseReference db_root;
        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);
        db_root_users.keepSynced(true);
    }


    private void show_Phone_Number_Verify_Fragment_Dialog(Bundle bundle) {

        boolean validationState = validateInput_UserAccountInfo();
        if (validationState) {
            fragment_phone_number_verify = Fragment_Sign_Up_Phone_Number_Verify_and_Create_Account.newInstance(createBundle_UserAccountInfo());

            fragment_phone_number_verify.show(getSupportFragmentManager(), STR_TAG_FRAGMENT_VERIFY);
        } else {
            Toast.makeText(this, getString(R.string.message_info_error_information_missing), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateInput_UserAccountInfo() {
        boolean c1 = checkError(m_et_userName);
        boolean c2 = checkError(m_et_phoneNumber);
        boolean c3 = checkError(m_et_password);
        boolean c4 = checkError(m_et_password_confirm);
        return c1 && c2 && c3 && c4;
    }

    private boolean checkError(View view) {
        boolean validationState = true;
     /*   if (view instanceof ImageButton) {
            if (getContentOfView(view).equals(Uri.EMPTY)) {
                Toast.makeText(this, "You havn't add any image for your account!", Toast.LENGTH_SHORT).show();
            }

        }*/

        if (view.getId() == R.id.et_phoneNumber) {
            if (TextUtils.isEmpty(getContentOfView(view))) {
                (m_til_phone_number).setError(getString(R.string.message_error_empty_field));
                ( view).requestFocus();
                validationState = false;
            }

        }
        if (view.getId() == R.id.et_password) {
            if (TextUtils.isEmpty(getContentOfView(view))) {
                m_til_password.setError(getString(R.string.message_error_empty_field));
                ( view).requestFocus();

                validationState = false;
            }
            if (TextUtils.getTrimmedLength(getContentOfView(view)) < 6 && TextUtils.getTrimmedLength(getContentOfView(view)) > 0) {
                (m_til_password).setError(getString(R.string.message_error_password_too_short));
                ( view).requestFocus();

                validationState = false;
            }

            if (!getContentOfView(view).equals(getContentOfView(m_et_password_confirm))) {
                (m_til_password_confirm).setError(getString(R.string.message_error_password_doesnt_match));
                ( view).requestFocus();

                validationState = false;
            }

        }
        if (view.getId() == R.id.et_password_confirm) {
            if (TextUtils.isEmpty(getContentOfView(view))) {
                (m_til_password_confirm).setError(getString(R.string.message_error_empty_field));
                ( view).requestFocus();

                validationState = false;

            }
            if (!getContentOfView(view).equals(getContentOfView(m_et_password_confirm))) {
                (m_til_password_confirm).setError(getString(R.string.message_error_password_doesnt_match));
                ( view).requestFocus();

                validationState = false;

            }

        }
        if (view.getId() == R.id.et_userName) {
            if (TextUtils.isEmpty(getContentOfView(view))) {
                (m_til_et_userName).setError(getString(R.string.message_error_empty_field));
                ( view).requestFocus();
                validationState = false;
            }

        }

        return validationState;
    }

    private Bundle createBundle_UserAccountInfo() {

        Bundle bundle = new Bundle();

        bundle.putString(INTENT_KEY_USER_PHONE_NUMBER, getFinalPhoneNumber());
        bundle.putString(INTENT_KEY_USER_PASSWORD, getContentOfView(m_et_password));
        bundle.putString(INTENT_KEY_USER_IMAGE_URI, getContentOfView(m_img_btn_user_image));
        bundle.putString(INTENT_KEY_USER_NAME, getContentOfView(m_et_userName));

        return bundle;
    }

    private String getFinalPhoneNumber() {
        if ((getContentOfView(m_et_phoneNumber).length()) == 0) {
            return null;
        }
        String selectedCountryCode = m_cpp.getSelectedCountryCode();
        String phoneNumberWithoutZero = getContentOfView(m_et_phoneNumber);
        if (getContentOfView(m_et_phoneNumber).charAt(0) == '0') {
            phoneNumberWithoutZero = getContentOfView(m_et_phoneNumber).substring(1);
        }

        return "+" + selectedCountryCode + phoneNumberWithoutZero;
    }

    private String getContentOfView(View view) {
        if (view instanceof ImageButton) {
            return String.valueOf(getSelectedUserImageUri());
        }

        return String.valueOf(((EditText) view).getText()).trim();
    }


    private Uri getSelectedUserImageUri() {
        return uri_user_img;

    }

    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView) {

        Glide.with(context)
                .load(imageURL)
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(thumbnail)
/*
                .diskCacheStrategy(DiskCacheStrategy.ALL)
*/
                .into(imageView)


        ;


    }

    private void setUserImage(Uri uri) {
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
            m_img_btn_user_image.setImageBitmap(bitmap);//TODO
            setImage_circle(this, uri, 0.3f, m_img_btn_user_image);

        } catch (FileNotFoundException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE_GALLERY = 5;
/*        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            //seting Item image
            uri_user_img = data.getData();
            setUserImage(uri_user_img);


        }*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri_user_img = result.getUri();
                setUserImage(uri_user_img);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void startCropActivity(Activity activity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_sign_up: {

             /*   if (requestingLocationUpdates) {
                    prepareGPS();

                }*/
                if (!m_cb_agree_ts_and_cs.isChecked()) {
                    show_Must_Agree_Ts_Cs_Fragment_Dialog();
                    return;
                }

                show_Phone_Number_Verify_Fragment_Dialog(createBundle_UserAccountInfo());

            }

            break;
         /*   case R.id.ccp: {

            }

            break;*/
            case R.id.img_btn_developer_image:
                manage_Adding_UserImg_Perm();

                break;
            case R.id.tv_sign_in:
                startSignInActivity();
        }


    }

    private void show_Must_Agree_Ts_Cs_Fragment_Dialog() {
        showDialogFragment(new Fragment_Dialog_Must_Agree_Ts_and_Cs_Alert(), "frg_must_agree_ts_cs");

    }


    private void startSignInActivity() {
        startActivity(new Intent(this, Activity_Sign_In_Phone_Number.class).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT ));
    }

    private void manage_Adding_UserImg_Perm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION_ITEM_MODIFY);
            } else {
                startCropActivity(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length >= 0) {
            if (requestCode == REQUEST_PERMISSION_LOCATION_GET) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms_location", "perms granted");
                    // setUpLocation();

                } else {
                    Log.d("perms_location", "perms not granted");

                    Toast.makeText(Activity_Sign_Up_Phone_Number.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();


                }
            }


        }
        if (grantResults.length >= 2) {
            if (requestCode == REQUEST_READ_PERMISSION_ITEM_MODIFY &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("perms", "perms granted");
                //upload_itemImg_and_data();
                startCropActivity(this);


            } else {
                Log.d("perms", "perms not granted");

                Toast.makeText(Activity_Sign_Up_Phone_Number.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();

            }
        }
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
                        result = context.getString(R.string.message_info_error_no_location);
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

    private static class  GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            if (message.what == 1) {
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
                current_User_Lat = bundle.getDouble("lat");
                current_User_Long = bundle.getDouble("long");
            }
        }
    }

    private void update_UI_with_Location(String locationAddress) {

        m_pb_location.setVisibility(View.GONE);
        m_tv_location.setText(locationAddress);
    }

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
                LocationServices.getFusedLocationProviderClient(Activity_Sign_Up_Phone_Number.this).removeLocationUpdates(this);
                if (locationResult.getLocations().size() == 0) {
                    return;
                }
                int latestLocationIndex = locationResult.getLocations().size() - 1;
                for (Location location : locationResult.getLocations()) {
                    LatLng sydney = new LatLng(-34, 151);
                    if (location != null) {

                        Activity_Sign_Up_Phone_Number.GeoCoderHandler m_geocoderHandler = new Activity_Sign_Up_Phone_Number.GeoCoderHandler();
                        getAddressFromLocation_GeoCoderProcessor(locationResult.getLocations().get(latestLocationIndex), getBaseContext(), m_geocoderHandler);
                        store_UserLocation_To_DB(new LatLng(locationResult.getLocations().get(latestLocationIndex).getLatitude(),

                                locationResult.getLocations().get(latestLocationIndex).getLongitude()), "userID");//TODO record loc


                    }
                }
            }


            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
        getFusedLocationProviderClient().requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
     /*   fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            updateUI(locationToAddress(location), mDetails);
            // storeToDB(locationToAddress(location));

        });*/
    }

    private void store_UserLocation_To_DB(LatLng latLng, String userID) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    db_root_users.child(userID).child(PATH_USER_LOCATION_LAT).setValue(String.valueOf(latLng.latitude));
                    db_root_users.child(userID).child(PATH_USER_LOCATION_LONG).setValue(String.valueOf(latLng.longitude));


                });
  /*      db_root_users.child(userID).child(PATH_USER_LOCATION_LAT).setValue(String.valueOf(latLng.latitude));
        db_root_users.child(userID).child(PATH_USER_LOCATION_LONG).setValue(String.valueOf(latLng.longitude));*/

    }

    private void confirm_download_VPN() {
      /*  Fragment fragment = getSupportFragmentManager().findFragmentByTag("VPN_Alert_Fragment");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        Fragment_Dialog_VPN_Alert fragmentDialogVPNAlert = new Fragment_Dialog_VPN_Alert();
        fragmentDialogVPNAlert.show(getFragmentManager(), "VPN_Alert_Fragment");*/
        showDialogFragment(new Fragment_Dialog_VPN_Alert(), "frg_vpn");
    }

    public void showDialogFragment(DialogFragment newFragment, String tag) {
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
        newFragment.show(ft, tag);
        getSupportFragmentManager().executePendingTransactions();
    }


}