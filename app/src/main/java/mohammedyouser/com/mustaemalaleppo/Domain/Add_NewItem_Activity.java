
package mohammedyouser.com.mustaemalaleppo.Domain;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.threeten.bp.Instant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

public class Add_NewItem_Activity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG ="Empty Uri";

    private EditText mTitle;
    private EditText mPrice;
    private EditText mDetails;
    private ImageButton mItemImage;


    private DatabaseReference finalDatabaseReference;

    private StorageReference storageReference;

    private DatabaseReference databaseReference_items;

    private String userID;

    private Uri download_uri;
    private Bitmap bitmap;
    private Uri uri_itemImage;

    private Spinner spinner_cities;
    private City[] cities;
    private String selectedCity;

    private Spinner spinner_categories;
    private Category[] categories;

    private String selectedCategory;
    private Button save;
    private Button cancel;


    private DatabaseReference databaseReference_items_users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
     
        doMainInitializations();
        initialListeners();


    }



    private void doMainInitializations() {
        initializeMyViews();
        initialAuthenticationRef();
        initialStorageRef();
        initialDatabaseRefs();

    }

    private void initialAuthenticationRef() {

        //Firebase Authentication initialization
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null)
        userID = mAuth.getCurrentUser().getUid();

    }
    private void initialStorageRef() {
           //Firebase Storage initialization
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mustaemalaleppo.appspot.com/");
    }
    private void initialDatabaseRefs() {

        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();

        databaseReference_items= db_root.child(PATH_ITEMS);
        databaseReference_items_users= db_root.child(PATH_ITEMS_USERS);


    }



    private DatabaseReference get_DB_Ref_selected_Cit_Cat(){




        if(getIntent().getExtras().getString(INTENT_KEY__STATEVALUE)!=null)
        {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED))
            {

                finalDatabaseReference = databaseReference_items.child(PATH_INEED).child(selectedCity).child(selectedCategory);




            }
            else  if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)){


                finalDatabaseReference = databaseReference_items.child(PATH_IHAVE).child(selectedCity).child(selectedCategory);



            }
        }
        return finalDatabaseReference;
    }
    private DatabaseReference get_DB_Ref_AllCities(){




        if(getIntent().getExtras().getString(INTENT_KEY__STATEVALUE)!=null)
        {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED))
            {

                finalDatabaseReference = databaseReference_items.child(PATH_INEED).child(PATH_ALL_CITIES).child(selectedCategory);




            }
            else  if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)){


                finalDatabaseReference = databaseReference_items.child(PATH_IHAVE).child(PATH_ALL_CITIES).child(selectedCategory);



            }
        }
        return finalDatabaseReference;
    }
    private DatabaseReference get_DB_Ref_AllCategories(){




        if(getIntent().getExtras().getString(INTENT_KEY__STATEVALUE)!=null)
        {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED))
            {

                finalDatabaseReference = databaseReference_items.child(PATH_INEED).child(selectedCity).child(PATH_ALL_CATEGORIES);




            }
            else  if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)){


                finalDatabaseReference = databaseReference_items.child(PATH_IHAVE).child(selectedCity).child(PATH_ALL_CATEGORIES);



            }
        }
        return finalDatabaseReference;
    }
    private DatabaseReference get_DB_Ref_AlItems(){




        if(getIntent().getExtras().getString(INTENT_KEY__STATEVALUE)!=null)
        {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED))
            {

                finalDatabaseReference = databaseReference_items.child(PATH_INEED).child(PATH_ALL_ITEMS);




            }
            else  if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)){


                finalDatabaseReference = databaseReference_items.child(PATH_IHAVE).child(PATH_ALL_ITEMS);



            }
        }
        return finalDatabaseReference;
    }

    private DatabaseReference get_DB_Ref_CurrentUser_selected_Cit_Cat() {




        if(getIntent().getExtras().getString(INTENT_KEY__STATEVALUE)!=null)
        {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED))
            {

                finalDatabaseReference = databaseReference_items_users.child(PATH_INEED).child(selectedCity).child(selectedCategory).child(PATH_USERS_IDS).child(userID);




            }
            else  if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)){


                finalDatabaseReference = databaseReference_items_users.child(PATH_IHAVE).child(selectedCity).child(selectedCategory).child(PATH_USERS_IDS).child(userID);




            }
        }
        return finalDatabaseReference;
    }
    private DatabaseReference get_DB_Ref_CurrentUser_AllCities() {




        if(getIntent().getExtras().getString(INTENT_KEY__STATEVALUE)!=null)
        {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED))
            {

                finalDatabaseReference = databaseReference_items_users.child(PATH_INEED).child(PATH_ALL_CITIES).child(selectedCategory).child(PATH_USERS_IDS).child(userID);




            }
            else  if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)){


                finalDatabaseReference = databaseReference_items_users.child(PATH_IHAVE).child(PATH_ALL_CITIES).child(selectedCategory).child(PATH_USERS_IDS).child(userID);




            }
        }
        return finalDatabaseReference;
    }
    private DatabaseReference get_DB_Ref_CurrentUser_AllCategories() {




        if(getIntent().getExtras().getString(INTENT_KEY__STATEVALUE)!=null)
        {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED))
            {

                finalDatabaseReference = databaseReference_items_users.child(PATH_INEED).child(selectedCity).child(PATH_ALL_CATEGORIES).child(PATH_USERS_IDS).child(userID);




            }
            else  if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)){


                finalDatabaseReference = databaseReference_items_users.child(PATH_IHAVE).child(selectedCity).child(PATH_ALL_CATEGORIES).child(PATH_USERS_IDS).child(userID);




            }
        }
        return finalDatabaseReference;
    }
    private DatabaseReference get_DB_Ref_CurrentUser_AllItems() {




        if(getIntent().getExtras().getString(INTENT_KEY__STATEVALUE)!=null)
        {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED))
            {

                finalDatabaseReference = databaseReference_items_users.child(PATH_INEED).child(PATH_ALL_ITEMS).child(PATH_USERS_IDS).child(userID);




            }
            else  if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)){


                finalDatabaseReference = databaseReference_items_users.child(PATH_IHAVE).child(PATH_ALL_ITEMS).child(PATH_USERS_IDS).child(userID);




            }
        }
        return finalDatabaseReference;
    }




    private StorageReference getImgStorageRef() {
        return storageReference.child(PATH_STORAGE_USERS_PICTURES).child(userID);
    }


    private void initializeMyViews() {
        TextView mAddNewLabel = (TextView) findViewById(R.id.textView_adding_state);
        mAddNewLabel.setText(getIntent().getExtras().getString(INTENT_KEY__STATELABEL));
        mTitle =  findViewById(R.id.item_title);
        mPrice =  findViewById(R.id.item_price);
        mDetails = findViewById(R.id.item_details);


        spinner_categories=  findViewById(R.id.spinner_categories);
        spinner_cities= findViewById(R.id.spinner_cities);

        mItemImage = findViewById(R.id.img_btn_itemImage);


        save =    findViewById(R.id.save);
        cancel =  findViewById(R.id.cancel);

        initialCategories();
        initialCities();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void initialCategories() {
        // Reading json file from assets dir
        String json = null;
        try {
            InputStream is = getAssets().open(ASSETS_FILE_NAME_CATEGORIES);
            byte[] buffer = new byte[getAssets().open(ASSETS_FILE_NAME_CATEGORIES).available()];
            is.read(buffer);
            is.close();
            json = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();

        }
        //from json String to  Categories object
        GsonBuilder gsonBuilder=new GsonBuilder();
        Gson gson= gsonBuilder.create();
        categories =  gson.fromJson(json,Category[].class);

        //creating the CountryNames array from cities array
        String[]CategoriesNames=new String[categories.length];
        for(int i = 0; i< categories.length; i++)
        {
            CategoriesNames[i] = categories[i].getName();
        }

        //creating arrayAdapter_categories for the spinner_cities
        ArrayAdapter<String> arrayAdapter_categories = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, CategoriesNames);
        spinner_categories.setAdapter(arrayAdapter_categories);

        // set listener for the spinner_cities...Done!
        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory=categories[adapterView.getSelectedItemPosition()].getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initialCities() {

        // Reading json file from assets dir
        String json = null;
        try {
            InputStream is = getAssets().open(ASSETS_FILE_NAME_CITIES);
            byte[] buffer = new byte[getAssets().open(ASSETS_FILE_NAME_CITIES).available()];
            is.read(buffer);
            is.close();
            json = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();

        }
        //from json String to cities object
        GsonBuilder gsonBuilder=new GsonBuilder();
        Gson gson= gsonBuilder.create();
        cities =  gson.fromJson(json,City[].class);

        //creating the CountryNames array from cities array
        String[]CitiesNames=new String[cities.length];
        for(int i = 0; i< cities.length; i++)
        {
            CitiesNames[i] = cities[i].getName();
        }

        //creating arrayAdapter_cities for the spinner_cities
        ArrayAdapter<String> arrayAdapter_cities = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, CitiesNames);
        spinner_cities.setAdapter(arrayAdapter_cities);

        // set listener for the spinner_cities...Done!
        spinner_cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCity=cities[adapterView.getSelectedItemPosition()].getName();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initialListeners() {


        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        mItemImage.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save: {

                if(selectedCity.equals(PATH_ALL_CITIES)&&selectedCategory.equals(PATH_ALL_CATEGORIES)) {

                    storeItemDataToFirebase_ALLState(getImgStorageRef());

                }
                else {

                    storeItemDataToFirebase(getImgStorageRef());
                }

            }


            break;
            case R.id.cancel:
            {

                finish();

            }
            break;
            case R.id.img_btn_itemImage:

                 CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
                break;




        }


    }


    private  void addItem(final DatabaseReference  custom_newItem_DB_Ref) {
            //Writing item data to firebase
            custom_newItem_DB_Ref.child(PATH_ITEM_TITLE).setValue(String.valueOf(mTitle.getText()));
            custom_newItem_DB_Ref.child(PATH_ITEM_PRICE).setValue(String.valueOf(mPrice.getText()));
            custom_newItem_DB_Ref.child(PATH_ITEM_CATEGORY).setValue(selectedCategory);
            custom_newItem_DB_Ref.child(PATH_ITEM_COUNTRY).setValue(COUNTRY_NAME_SYR);
            custom_newItem_DB_Ref.child(PATH_ITEM_CITY).setValue(selectedCity);
            custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME).setValue(getCurrentDate());


            custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME_REVERSE).setValue(ServerValue.TIMESTAMP);
            custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME_REVERSE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (!(Long.parseLong(dataSnapshot.getValue().toString()) < 0)) {
                        custom_newItem_DB_Ref.child(PATH_ITEM_DATE_AND_TIME_REVERSE).setValue(0 - Long.parseLong(dataSnapshot.getValue().toString()));
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
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });


            custom_newItem_DB_Ref.child(PATH_ITEM_DETAILS).setValue(String.valueOf(mDetails.getText()));
            custom_newItem_DB_Ref.child(PATH_ITEM_USER_ID).setValue(userID);
            custom_newItem_DB_Ref.child(PATH_ITEM_IMAGE).setValue(String.valueOf(download_uri));

        Log.d(TAG, "addItem: "+download_uri);


    }

    private boolean validateAddItemUserInput() {
      if (TextUtils.isEmpty(mTitle.getText().toString())) {
           mTitle.setError(getString(R.string.message_error_empty_field));
        }
        if (TextUtils.isEmpty(mPrice.getText().toString())){
            mPrice.setError(getString(R.string.message_error_empty_field));
        }



        return !(    TextUtils.isEmpty(mTitle.getText().toString())||
                TextUtils.isEmpty(mPrice.getText().toString()));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:entered "+ uri_itemImage);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri_itemImage=result.getUri();
                Log.d(TAG, "onActivityResult: uri is : "+ uri_itemImage);
                setItemImage(uri_itemImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult:Error  uri"+ uri_itemImage);

                error.printStackTrace();
            }
        }

    }

    private void setItemImage(Uri uri) {
             try {
            InputStream inputStream= getContentResolver().openInputStream(uri);

            bitmap=   BitmapFactory.decodeStream(inputStream);
                 Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);

                 mItemImage.setImageBitmap(smallBitmap);
        }
        catch (FileNotFoundException e)
        {

        e.printStackTrace();
        }
    }


    private void storeItemDataToFirebase(final StorageReference storageReference) {

        if ( validateAddItemUserInput()){

            showProgressDialog(this,getString(R.string.message_info_ADDING_ITEM),getString(R.string.message_info_PLEASE_WAIT));


//First upload selected image

            if(uri_itemImage ==null)
            {
                uri_itemImage=Uri.EMPTY;
                Log.d(TAG, "storeItemDataToFirebase: empty");
                addItem_To_All_DB_Refs();


                hideProgressDialog();

                Toast.makeText(Add_NewItem_Activity.this,getResources().getString(R.string.message_info_added_ok), Toast.LENGTH_SHORT).show();

                finish();
            }

else {
                storageReference.putFile(uri_itemImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                               download_uri=uri;
                                Log.d(TAG, "storeItemDataToFirebase: onSuccess: "+download_uri);
                                addItem_To_All_DB_Refs();

                            }
                        });
                        //Secondly Add item to all needed database references


                        hideProgressDialog();

                        Toast.makeText(Add_NewItem_Activity.this, getResources().getString(R.string.message_info_added_ok), Toast.LENGTH_SHORT).show();

                        finish();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Add_NewItem_Activity.this, getResources().getString(R.string.message_info_added_error) + "\n" + e.toString(), Toast.LENGTH_LONG).show();

                    }
                });
            }


        }
        else {

            Toast.makeText(Add_NewItem_Activity.this,getString(R.string.message_error_required_fields), Toast.LENGTH_SHORT).show();


        }
    }
    private void storeItemDataToFirebase_ALLState(final StorageReference storageReference) {

        if (validateAddItemUserInput()) {

            showProgressDialog(this, getString(R.string.message_info_ADDING_ITEM), getString(R.string.message_info_PLEASE_WAIT));


//First upload selected image


            if (uri_itemImage == null) {
                Log.d(TAG, "storeItemDataToFirebase: Empty uri! ");

                uri_itemImage = Uri.EMPTY;
                String itemKey = get_DB_Ref_AlItems().push().getKey();

                //Adding Item to "ALL Cities and ALL Categories"
                addItem(get_DB_Ref_AlItems().child(itemKey));

                //Adding Item to "ALL Cities and ALL Categories belong to current user"
                addItem(get_DB_Ref_CurrentUser_AllItems().child(itemKey));


                hideProgressDialog();
                finish();
            } else {


                storageReference.putFile(uri_itemImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Add_NewItem_Activity.this, getResources().getString(R.string.message_info_added_ok), Toast.LENGTH_SHORT).show();

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                download_uri=uri;
                                Log.d(TAG, "storeItemDataToFirebase_ALLState: not empty success! "+download_uri);
                                String itemKey = get_DB_Ref_AlItems().push().getKey();

                                //Adding Item to "ALL Cities and ALL Categories"
                                addItem(get_DB_Ref_AlItems().child(itemKey));

                                //Adding Item to "ALL Cities and ALL Categories belong to current user"
                                addItem(get_DB_Ref_CurrentUser_AllItems().child(itemKey));

                            }
                        });

          /*              String itemKey = get_DB_Ref_AlItems().push().getKey();

                        //Adding Item to "ALL Cities and ALL Categories"
                        addItem(get_DB_Ref_AlItems().child(itemKey));

                        //Adding Item to "ALL Cities and ALL Categories belong to current user"
                        addItem(get_DB_Ref_CurrentUser_AllItems().child(itemKey));*/


                        hideProgressDialog();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "storeItemDataToFirebase: not empty failure! ");


                        Toast.makeText(Add_NewItem_Activity.this, getResources().getString(R.string.message_info_added_error) + "\n" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                });


            }

        }

        else {

            Toast.makeText(Add_NewItem_Activity.this,getString(R.string.message_error_required_fields), Toast.LENGTH_SHORT).show();


        }

    }

    private void addItem_To_All_DB_Refs() {
String itemKey= get_DB_Ref_AlItems().push().getKey();

        //Adding Item to "ALL Cities and ALL Categories"
        addItem(get_DB_Ref_AlItems().child(itemKey));

        //Adding Item to "selected Category and selected City"
        addItem(get_DB_Ref_selected_Cit_Cat().child(itemKey));

        //Adding Item to "ALL Cities and selected Category"
        addItem(get_DB_Ref_AllCities().child(itemKey));

        //Adding Item to "ALL Categories and selected City"
        addItem(get_DB_Ref_AllCategories().child(itemKey));


        //
        // For current user's Items
        //

        //Adding Item to "ALL Cities and ALL Categories belong to current user"
        addItem(get_DB_Ref_CurrentUser_AllItems().child(itemKey));

        //Adding Item to "selected Category and selected City belong to current user"
        addItem(get_DB_Ref_CurrentUser_selected_Cit_Cat().child(itemKey));

        //Adding Item to "ALL Cities and selected Category belong to current user"
        addItem(get_DB_Ref_CurrentUser_AllCities().child(itemKey));


        //Adding Item to "ALL Categories and selected City belong to current user"
        addItem(get_DB_Ref_CurrentUser_AllCategories().child(itemKey));





    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_KEY_BITMAP,bitmap);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        bitmap=((Bitmap) savedInstanceState.getParcelable(BUNDLE_KEY_BITMAP));
        if(bitmap!=null)
            mItemImage.setImageBitmap(bitmap);


    }

    @Override
    protected void onStart() {
        super.onStart();


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

    public Instant now() {
        return Instant.now();
    }



    public String getCurrentDate() {

        Date date= Calendar.getInstance().getTime();
        DateFormat formatter= SimpleDateFormat.getDateInstance();

        return formatter.format(date);
    }
}
