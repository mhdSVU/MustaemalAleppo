package mohammedyouser.com.mustaemalaleppo.Domain;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import mohammedyouser.com.mustaemalaleppo.Data.Category;
import mohammedyouser.com.mustaemalaleppo.Data.City;
import mohammedyouser.com.mustaemalaleppo.Device.GpsUtils;
import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Activity_Ineed_Ihave;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

public class Activity_Add_Item extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String TAG = "Empty Uri";
    private static final int REQUEST_PERMISSION_IMG_READ = 786;
    private static final int REQUEST_PERMISSION_LOCATION_GET = 787;

    private EditText mTitle;
    private EditText mPrice;
    private EditText mDetails;
    private ImageButton img_btn_itemImage;


    private DatabaseReference item_DB_Ref;

    private StorageReference storageReference;

    private DatabaseReference db_ref_items;

    private String userID;

    private Uri uri_itemImg = Uri.EMPTY;

    private Spinner spinner_cities;
    private City[] cities;
    private String item_city;

    private String item_category;
    private Button save;
    private Button cancel;

    private Spinner spinner_categories;
    private Category[] categories;

    private DatabaseReference databaseReference_items_users;
    private boolean isGPS = false;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = true;
    private TextView m_tv_location;
    private ProgressBar m_pb_location;
    private String itemKey_to_all;
    private String itemKey_all_CityCat_state;
    private double itemLat;
    private double itemLong;
    private DatabaseReference db_root;
    private DatabaseReference db_root_user;
    private String state = PATH_IHAVE;
    private String userAddress;
    private RadioButton m_rb_current_location;
    private RadioButton m_rb_default_location;
    private String[] CitiesNames;
    private String[] CategoriesNames;
    private String[] CitiesNames_locale;
    private String[] CategoriesNames_locale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()),this);

        setContentView(R.layout.activity_add_new_item);

        updateValuesFromBundle(savedInstanceState);

        doMainInitializations();

        initialListeners();


    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        requestingLocationUpdates = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* if (requestingLocationUpdates) {
            prepareGPS();
            setUpLocation();
        }*/
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
  /*      outState.putBoolean(BUNDLE_KEY_REQUESTING_LOCATION_UPDATES,
                requestingLocationUpdates);
        outState.putString(INTENT_KEY_ITEM_IMG_URI, String.valueOf(uri_itemImg));*/

        // ...
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    /*    if (savedInstanceState.containsKey("INTENT_KEY_ITEM_IMG_URI")) {
            uri_itemImg = Uri.parse(savedInstanceState.getString("INTENT_KEY_ITEM_IMG_URI"));
            setItemImage(this, uri_itemImg, 0.3f, img_btn_itemImage);
        }*/
    }

    private void doMainInitializations() {
        initializeMyViews();
        initialAuthenticationRef();
        initialStorageRef();
        initialDatabaseRefs();
        initialize_UserLoc_from_DB();
        updateUI_address(get_UserAddress_from_DB());


    }

    private void updateUI_address(String userAddress) {
        m_tv_location.setText(userAddress);
        m_pb_location.setVisibility(View.GONE);

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
                LocationServices.getFusedLocationProviderClient(Activity_Add_Item.this).removeLocationUpdates(this);
                if (locationResult.getLocations().size() == 0) {
                    return;
                }
                int latestLocationIndex = locationResult.getLocations().size() - 1;
                for (Location location : locationResult.getLocations()) {
                    LatLng sydney = new LatLng(-34, 151);
                    if (location != null) {
                        GeoCoderHandler m_geocoderHandler = new GeoCoderHandler();
                        getAddressFromLocation_GeoCoderProcessor(locationResult.getLocations().get(latestLocationIndex), getBaseContext(), m_geocoderHandler);
                        storeLocationToDB(new LatLng(locationResult.getLocations().get(latestLocationIndex).getLatitude(),
                                locationResult.getLocations().get(latestLocationIndex).getLongitude()));

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
            if (message.what == 1) {
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
                itemLat = bundle.getDouble("lat");
                itemLong = bundle.getDouble("long");
                if (Objects.equals(locationAddress, getString(R.string.message_error_no_address))) {
                    showSnackBar(Activity_Add_Item.this, getString(R.string.message_error_loc_cuurent)).
                            setAction(getString(R.string.btn_retry), v -> {
                                m_tv_location.setText("");
                                m_pb_location.setVisibility(View.VISIBLE);
                                prepareGPS();
                                setUpLocation();

                            }).show();
                }
            } else {
                locationAddress = null;
            }
            m_pb_location.setVisibility(View.GONE);
            m_tv_location.setText(locationAddress);


        }
    }

    private void prepareGPS() {
        new GpsUtils(this).turnGPSOn(isGPSEnable -> {
            // turn on GPS
            isGPS = isGPSEnable;
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
                        sb.append(address.getThoroughfare()).append("\n");
                        sb.append(address.getLocality()).append("\n");
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
                        result = context.getString(R.string.message_error_no_address);
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    private void stopLocationUpdates() {
        if (locationCallback != null)
            getFusedLocationProviderClient().removeLocationUpdates(locationCallback);
    }


    private void updateUI(Address address, EditText mDetails) {
        if (address == null) {
            mDetails.setText(getString(R.string.message_info_error_no_location));
            return;
        }
        mDetails.setText(address.getThoroughfare());

    }

    private void initialAuthenticationRef() {

        //Firebase Authentication initialization
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            userID = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "initialAuthenticationRef: " + userID);

    }

    private void initialStorageRef() {
        //Firebase Storage initialization
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mustaemalaleppo.appspot.com/");
    }

    private void initialDatabaseRefs() {

        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_user = db_root.child(PATH_USERS).child(userID);


        db_ref_items = db_root.child(PATH_ITEMS);
        databaseReference_items_users = db_root.child(PATH_USER_ITEMS);

        itemKey_to_all = itemKey_all_CityCat_state = get_DB_Ref_All_Items(state).push().getKey();


    }

    private void initializeMyViews() {
        TextView mAddNewLabel = findViewById(R.id.tv_userPhoneNumber);
        mTitle = findViewById(R.id.item_title);
        mPrice = findViewById(R.id.item_price);
        mDetails = findViewById(R.id.item_details);
        m_tv_location = findViewById(R.id.tv_location);
        m_pb_location = findViewById(R.id.pb_location);
        m_rb_current_location = findViewById(R.id.rb_loc_current);
        m_rb_default_location = findViewById(R.id.rb_loc_default);


        spinner_categories = findViewById(R.id.spinner_categories);
        spinner_cities = findViewById(R.id.spinner_cities);

        spinner_cities.setOnItemSelectedListener(this);
        spinner_categories.setOnItemSelectedListener(this);

        img_btn_itemImage = findViewById(R.id.img_btn_itemImage);


        save = findViewById(R.id.add);
        cancel = findViewById(R.id.cancel);

        initialCategories();
        initialCities();


        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    public static Snackbar showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }

    private void initialListeners() {


        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        img_btn_itemImage.setOnClickListener(this);
        m_rb_current_location.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                m_tv_location.setText("");
                m_pb_location.setVisibility(View.VISIBLE);
                prepareGPS();
                setUpLocation();
            } else {
                updateUI_address(get_UserAddress_from_DB());
            }
        });
        m_rb_default_location.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                get_UserAddress_from_DB();
                initialize_UserLoc_from_DB();
                m_pb_location.setVisibility(View.GONE);

            }
        });


    }

    private void initialCities() {

        build_spinnerCities_array_data();

        build_spinnerCities_array_locale();

        build_spinnerCities_view();


    }

    private void build_spinnerCities_view() {
        //creating arrayAdapter_cities for the spinner_cities
        ArrayAdapter<String> arrayAdapter_cities = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, CitiesNames_locale);
        arrayAdapter_cities.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_cities.setAdapter(arrayAdapter_cities);
    }

    private void build_spinnerCities_array_data() {
        //build data citiesNames
        // Reading json file from assets dir
        String json = null;
        try {
            InputStream inputStream = getAssets().open(ASSETS_FILE_NAME_CITIES);
            byte[] buffer = new byte[getAssets().open(ASSETS_FILE_NAME_CITIES).available()];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();

        }
        //from json String to cities object
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        cities = gson.fromJson(json, City[].class);

        //creating the CountryNames array from cities array
        CitiesNames = new String[cities.length];
        for (int i = 0; i < cities.length; i++) {
            CitiesNames[i] = cities[i].getName();
        }
    }

    private void build_spinnerCities_array_locale() {
        if (LocaleHelper.getLocale(this, "en").equals("ar")) {
            // Reading json file from assets dir
            String json_ar = null;
            try {
                InputStream inputStream = getAssets().open("my_cities_ar");
                byte[] buffer = new byte[getAssets().open("my_cities_ar").available()];
                inputStream.read(buffer);
                inputStream.close();
                json_ar = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();

            }
            //from json String to cities object
            GsonBuilder gsonBuilder_ar = new GsonBuilder();
            Gson gson_ar = gsonBuilder_ar.create();
            cities = gson_ar.fromJson(json_ar, City[].class);

            //creating the CountryNames array from cities array
            CitiesNames_locale = new String[cities.length];
            for (int i = 0; i < cities.length; i++) {
                CitiesNames_locale[i] = cities[i].getName();
            }


        } else if (LocaleHelper.getLocale(this, "en").equals("en")) {
            CitiesNames_locale = new String[cities.length];
            System.arraycopy(CitiesNames, 0, CitiesNames_locale, 0, cities.length);
        }

    }

    private void initialCategories() {

        build_spinnerCategories_array_data();

        build_spinnerCategories_array_locale();

        build_spinnerCategories_view();


    }

    private void build_spinnerCategories_array_data() {
        // Reading json file from assets dir
        String json = null;
        try {
            InputStream is = getAssets().open("my_categories");
            byte[] buffer = new byte[getAssets().open("my_categories").available()];
            is.read(buffer);
            is.close();
            json = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();

        }
        //from json String to  Categories object
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        categories = gson.fromJson(json, Category[].class);

        //creating the CountryNames array from cities array
        CategoriesNames = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            CategoriesNames[i] = categories[i].getName();
        }

    }

    private void build_spinnerCategories_array_locale() {
        if (LocaleHelper.getLocale(this, "en").equals("ar")) {
            // Reading json file from assets dir
            String json_ar = null;
            try {
                InputStream inputStream = getAssets().open("my_categories_ar");
                byte[] buffer = new byte[getAssets().open("my_categories_ar").available()];
                inputStream.read(buffer);
                inputStream.close();
                json_ar = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();

            }
            //from json String to cities object
            GsonBuilder gsonBuilder_ar = new GsonBuilder();
            Gson gson_ar = gsonBuilder_ar.create();
            categories = gson_ar.fromJson(json_ar, Category[].class);

            //creating the CountryNames array from cities array
            CategoriesNames_locale = new String[categories.length];
            for (int i = 0; i < categories.length; i++) {
                CategoriesNames_locale[i] = categories[i].getName();
            }


        } else if (LocaleHelper.getLocale(this, "en").equals("en")) {
            CategoriesNames_locale = new String[categories.length];
            System.arraycopy(CategoriesNames, 0, CategoriesNames_locale, 0, categories.length);
        }


    }

    private void build_spinnerCategories_view() {
        //creating arrayAdapter_categories for the spinner_cities
        ArrayAdapter<String> arrayAdapter_categories = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, CategoriesNames_locale);
        arrayAdapter_categories.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner_categories.setAdapter(arrayAdapter_categories);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.spinner_cities) {
            if ((view) != null)
                //   selectedCity = String.valueOf(((TextView) view).getText());
                item_city = CitiesNames[position];

        }
        if (parent.getId() == R.id.spinner_categories) {
            if ((view) != null)
                //   selectedCategory = String.valueOf(((TextView) view).getText());
                item_category = CategoriesNames[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private DatabaseReference get_DB_Ref_selected_Cit_Cat(String state) {
        item_DB_Ref = db_ref_items.child(state).child(item_city).child(item_category);
        return item_DB_Ref;
    }

    private DatabaseReference get_DB_Ref_AllCities(String state) {

        item_DB_Ref = db_ref_items.child(state).child(PATH_ALL_CITIES).child(item_category);

        return item_DB_Ref;
    }

    private DatabaseReference get_DB_Ref_AllCategories(String state) {
        item_DB_Ref = db_ref_items.child(state).child(item_city).child(PATH_ALL_CATEGORIES);
        return item_DB_Ref;
    }

    private DatabaseReference get_DB_Ref_All_Items(String itemState) {

        item_DB_Ref = db_ref_items.child(itemState).child(PATH_ALL_ITEMS);

        return item_DB_Ref;
    }

    private DatabaseReference get_DB_Ref_CurrentUser_selected_Cit_Cat(String state) {

        item_DB_Ref = databaseReference_items_users.child(state).child(item_city).child(item_category).child(PATH_USERS_IDS).child(userID);

        return item_DB_Ref;
    }

    private DatabaseReference get_DB_Ref_CurrentUser_AllCities(String state) {

        item_DB_Ref = databaseReference_items_users.child(state).child(PATH_ALL_CITIES).child(item_category).child(PATH_USERS_IDS).child(userID);

        return item_DB_Ref;
    }

    private DatabaseReference get_DB_Ref_CurrentUser_AllCategories(String state) {

        item_DB_Ref = databaseReference_items_users.child(state).child(item_city).child(PATH_ALL_CATEGORIES).child(PATH_USERS_IDS).child(userID);

        return item_DB_Ref;
    }

    private DatabaseReference get_DB_Ref_CurrentUser_AllItems(String state) {

        item_DB_Ref = databaseReference_items_users.child(state).child(PATH_ALL_ITEMS).child(PATH_USERS_IDS).child(userID);

        return item_DB_Ref;
    }

    private StorageReference getImgStorageRef() {

        return storageReference.child(PATH_STORAGE_USERS_PICTURES).child(userID).child(String.valueOf(itemKey_to_all));
    }


//add item sart
    //


    private void addItem_to_single_DB_Ref(final DatabaseReference custom_newItem_DB_Ref, Uri uri_itemImg_download) {
        //Writing item data to firebase
        custom_newItem_DB_Ref.child(PATH_ITEM_TITLE).setValue(String.valueOf(mTitle.getText()));
        custom_newItem_DB_Ref.child(PATH_ITEM_PRICE).setValue(String.valueOf(mPrice.getText()));
        custom_newItem_DB_Ref.child(PATH_ITEM_CATEGORY).setValue(item_category);
        custom_newItem_DB_Ref.child(PATH_ITEM_COUNTRY).setValue(COUNTRY_NAME_SYR);
        custom_newItem_DB_Ref.child(PATH_ITEM_CITY).setValue(item_city);

        db_root_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_currentUser) {
                custom_newItem_DB_Ref.child(PATH_ITEM_USER_NAME).setValue(String.valueOf(snapshot_currentUser.child(PATH_USER_NAME).getValue()));
                Log.d(TAG, "onDataChange: " + snapshot_currentUser.child(PATH_USER_NAME).getValue());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME).setValue(getCurrentDate());


        custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME_REVERSE).setValue(ServerValue.TIMESTAMP);
        custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME_REVERSE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (!(Long.parseLong(String.valueOf(dataSnapshot.getValue())) < 0)) {
                        custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME_REVERSE).
                                setValue(-Long.parseLong(String.valueOf(dataSnapshot.getValue())));
                    }
                }
                 /*   if (!(Long.parseLong(dataSnapshot.getValue().toString()) < 0)) {
                        custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME_REVERSE).setValue(0 - Long.parseLong(dataSnapshot.getValue().toString()));
                    } else {
                        custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME_REVERSE).removeValue();
                    }
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });

        if (String.valueOf(mDetails.getText()).equals("null")) {
            custom_newItem_DB_Ref.child(PATH_ITEM_DETAILS).setValue(getString(R.string.tv_details_default_val));

        } else {
            custom_newItem_DB_Ref.child(PATH_ITEM_DETAILS).setValue(String.valueOf(mDetails.getText()));

        }

        custom_newItem_DB_Ref.child(PATH_ITEM_USER_ID).setValue(userID);
        custom_newItem_DB_Ref.child(PATH_ITEM_IMAGE).setValue(String.valueOf(uri_itemImg_download));

        custom_newItem_DB_Ref.child(PATH_ITEM_LAT).setValue(itemLat);
        custom_newItem_DB_Ref.child(PATH_ITEM_LONG).setValue(itemLong);


    }

    private LatLng storeLocationToDB(LatLng latLng) {
        return latLng;
    }

    private boolean validateAddItemUserInput() {
        if (TextUtils.isEmpty(mTitle.getText().toString())) {
            mTitle.setError(getString(R.string.message_error_empty_field));
        }
        if (TextUtils.isEmpty(mPrice.getText().toString())) {
            mPrice.setError(getString(R.string.message_error_empty_field));
        }


        return !(TextUtils.isEmpty(mTitle.getText().toString()) ||
                TextUtils.isEmpty(mPrice.getText().toString()));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //Crop Case
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(intent);
            if (resultCode == RESULT_OK) {
                uri_itemImg = result.getUri();
                setItemImage(this, uri_itemImg, 1, img_btn_itemImage);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult:Error  uri" + uri_itemImg);

                error.printStackTrace();
            }
        }
       //GPS Case
            if (requestCode == GPS_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                isGPS = true; // flag maintain before get location
            }
        }

    }


    private void setItemImage(Uri uri) {
        try {
            Bitmap smallerBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(uri)), 640, 480, true);
            img_btn_itemImage.setImageBitmap(smallerBitmap);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
    }

    private void setItemImage(Context context, Uri imageURL, float thumbnail, ImageButton imageButton) {
        Glide.with(context)
                .load(imageURL)
                //   .apply(RequestOptions.circleCropTransform())
                .thumbnail(thumbnail)
/*
                .diskCacheStrategy(DiskCacheStrategy.ALL)
*/
                .into(imageButton)


        ;


    }


    private void addItem_one_CityCat_state(final StorageReference storageReference, Uri uri_itemImg_download, String state) {

        if (validateAddItemUserInput()) {

            addItem_To_All_DB_Refs(uri_itemImg_download, state);

            Toast.makeText(Activity_Add_Item.this, getResources().getString(R.string.message_info_added_ok), Toast.LENGTH_SHORT).show();
            Intent intent_item_added = new Intent(this, Activity_Ineed_Ihave.class);
            intent_item_added.putExtra(INTENT_KEY_USER_ID, userID);
            intent_item_added.putExtra(INTENT_KEY__STATE, state);
            startActivity(intent_item_added);
            finish();
        } else {

            Toast.makeText(Activity_Add_Item.this, getString(R.string.message_error_required_fields), Toast.LENGTH_SHORT).show();


        }
    }

    private void addItem_all_CityCat_state(final StorageReference storageReference, Uri uri_itemImg_download, String state) {

        if (validateAddItemUserInput()) {

            //Adding Item to "ALL Cities and ALL Categories"
            addItem_to_single_DB_Ref(get_DB_Ref_All_Items(state).child(itemKey_all_CityCat_state), uri_itemImg_download);

            //Adding Item to "ALL Cities and ALL Categories belong to current user"
            addItem_to_single_DB_Ref(get_DB_Ref_CurrentUser_AllItems(state).child(itemKey_all_CityCat_state), uri_itemImg_download);
            Intent intent_item_added = new Intent(this, Activity_Ineed_Ihave.class);
            intent_item_added.putExtra(INTENT_KEY_USER_ID, userID);
            intent_item_added.putExtra(INTENT_KEY__STATE, state);
            startActivity(intent_item_added);

            finish();
        } else {

            Toast.makeText(Activity_Add_Item.this, getString(R.string.message_error_required_fields), Toast.LENGTH_SHORT).show();


        }

    }

    private void addItem_To_All_DB_Refs(Uri uri_itemImg_download, String state) {
        //Adding Item to "ALL Cities and ALL Categories"
        addItem_to_single_DB_Ref(get_DB_Ref_All_Items(state).child(itemKey_to_all), uri_itemImg_download);

        //Adding Item to "selected Category and selected City"
        addItem_to_single_DB_Ref(get_DB_Ref_selected_Cit_Cat(state).child(itemKey_to_all), uri_itemImg_download);

        //Adding Item to "ALL Cities and selected Category"
        addItem_to_single_DB_Ref(get_DB_Ref_AllCities(state).child(itemKey_to_all), uri_itemImg_download);

        //Adding Item to "ALL Categories and selected City"
        addItem_to_single_DB_Ref(get_DB_Ref_AllCategories(state).child(itemKey_to_all), uri_itemImg_download);


        //
        // For current user's Items
        //

        //Adding Item to "ALL Cities and ALL Categories belong to current user"
        addItem_to_single_DB_Ref(get_DB_Ref_CurrentUser_AllItems(state).child(itemKey_to_all), uri_itemImg_download);

        //Adding Item to "selected Category and selected City belong to current user"
        addItem_to_single_DB_Ref(get_DB_Ref_CurrentUser_selected_Cit_Cat(state).child(itemKey_to_all), uri_itemImg_download);

        //Adding Item to "ALL Cities and selected Category belong to current user"
        addItem_to_single_DB_Ref(get_DB_Ref_CurrentUser_AllCities(state).child(itemKey_to_all), uri_itemImg_download);


        //Adding Item to "ALL Categories and selected City belong to current user"
        addItem_to_single_DB_Ref(get_DB_Ref_CurrentUser_AllCategories(state).child(itemKey_to_all), uri_itemImg_download);


    }


    ///////////////////////////////////////////////////

    private void manageAddPerm(String state) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_IMG_READ);
            } else {
                upload_itemImg_and_data(state);
            }
        }
        else {
            upload_itemImg_and_data(state);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_IMG_READ:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms", "perms granted");
                    upload_itemImg_and_data(state);

                } else {
                    Log.d("perms", "perms not granted");

                    Toast.makeText(Activity_Add_Item.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();

                }
                break;
            case REQUEST_PERMISSION_LOCATION_GET:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms_location", "perms granted");
                    setUpLocation();

                } else {
                    Log.d("perms_location", "perms not granted");

                    Toast.makeText(Activity_Add_Item.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();


                }
                break;
        }

    }

    private void upload_itemImg_and_data(String state) {
        if ((uri_itemImg != null) && (!uri_itemImg.equals(Uri.EMPTY))) {
            // item_DB_Ref.removeEventListener(selectedItem_db_ref_listener_img);
            showProgressDialog(this, getString(R.string.message_info_ADDING_ITEM), getString(R.string.message_info_PLEASE_WAIT));

            getImgStorageRef().putFile(uri_itemImg)
                    .addOnSuccessListener(taskSnapshot -> getImgStorageRef().getDownloadUrl().addOnSuccessListener(uri -> manage_addItem_state(uri, state)))
                    .addOnFailureListener(e ->
                            Toast.makeText(Activity_Add_Item.this, getResources().getString(R.string.message_info_modifing_error) + "\n" + e.toString(), Toast.LENGTH_LONG).show())
                    .addOnCompleteListener(task -> hideProgressDialog());
        } else {
            manage_addItem_state(null, state);
        }


    }

    private void manage_addItem_state(Uri uri_itemImg_download, String state) {
        if (item_city.equals(PATH_ALL_CITIES) && item_category.equals(PATH_ALL_CATEGORIES)) {

            addItem_all_CityCat_state(getImgStorageRef(), uri_itemImg_download, state);

        } else {

            addItem_one_CityCat_state(getImgStorageRef(), uri_itemImg_download, state);
        }
    }

    //////////////////////////////////////////////////

    // add item end
    ///


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(BUNDLE_KEY_REQUEST_LOCATION_UPDATES)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                    BUNDLE_KEY_REQUEST_LOCATION_UPDATES);
        }

        // ...

        // Update UI to match restored state
        //updateUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                checkItemState();
                if (!uri_itemImg.equals(Uri.EMPTY)) {
                    manageAddPerm(state);
                } else {
                    manage_addItem_state(null, state);

                }

                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.img_btn_itemImage:
                startCropActivity();
                break;


        }


    }

    private void checkItemState() {
        if (((RadioButton) findViewById(R.id.rb_ineed)).isChecked())
            state = PATH_INEED;
    }

    private void startCropActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }


    @Nullable
    @Override
    public View getCurrentFocus() {
        return super.getCurrentFocus();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public String getCurrentDate() {

        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = SimpleDateFormat.getDateInstance();

        return formatter.format(date);
    }

    private String get_UserAddress_from_DB() {
        db_root_user.child(PATH_USER_ADDRESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_userAddress) {
                userAddress = String.valueOf(snapshot_userAddress.getValue());
                if (Objects.equals(userAddress, "null")) {
                    m_tv_location.setText(getString(R.string.default_val_user_address));

                }
                else{
                    m_tv_location.setText(userAddress);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return userAddress;
    }

    private void initialize_UserLoc_from_DB() {
        db_root_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_userLoc) {
                itemLat = Double.parseDouble(String.valueOf(snapshot_userLoc.child(PATH_USER_LOCATION_LAT).getValue()));
                itemLong = Double.parseDouble(String.valueOf(snapshot_userLoc.child(PATH_USER_LOCATION_LONG).getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
