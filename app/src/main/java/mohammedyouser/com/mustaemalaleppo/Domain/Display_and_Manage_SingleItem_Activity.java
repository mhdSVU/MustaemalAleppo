package mohammedyouser.com.mustaemalaleppo.Domain;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.threeten.bp.Instant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mohammedyouser.com.mustaemalaleppo.Data.MyViewHolder;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


public class Display_and_Manage_SingleItem_Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG="A";
    private Uri downloadUri;
    private Bitmap bitmap;
    private Uri itemImageUri;

    private MyViewHolder viewHolder;
    private String userID;
    private String userEmail;
    private String userPhoneNumber;


    private String selectedItem_UserID;
    private DatabaseReference selectedItem_DB_Ref;
    private DatabaseReference selectedItem_User_DB_Ref;
    private TextView mSingleItemLabel;

    private DatabaseReference finalDatabaseReference;

    private StorageReference storageReference;

    private DatabaseReference db_root_items;
    private DatabaseReference db_root_users;
    private DatabaseReference db_root_items_users;


    private String selectedCity;
    private String selectedCategory;

    private ImageButton img_btn_itemImage;

    private boolean edit_state = false;
    private File myDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_item_recyclerview_row);

        doMainInitializations();

        initialize_User_LocalDir();
    }

    private void doMainInitializations() {
        initializeAuthenticationRef();
        initializeDatabaseRefs();
        initializeViews();
        initializeStorageRef();
        initializeListeners();
    }

    private void initialize_User_LocalDir() {
        String root = Environment.getExternalStorageDirectory().toString();
        if (getItemStateFlag())
            myDir = new File(root + "/Pictures" + "/MyStuffApp/" + PATH_INEED);
        else if (!(getItemStateFlag())) {
            myDir = new File(root + "/Pictures" + "/MyStuffApp/" + PATH_IHAVE);

        }

        if (!(myDir.exists()))
            myDir.mkdirs();


    }

    private void initializeViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mSingleItemLabel = findViewById(R.id.textView_label_single_item);
        View mRootView = findViewById(R.id.rootView);
        viewHolder = new MyViewHolder(mRootView);


        final String item_State = getIntent().getStringExtra(INTENT_KEY__PATH_STATE);

        setUpLabel(item_State);


        populateWithSelectedItem(selectedItem_DB_Ref);


    }

    private void initializeListeners() {

        if (selectedItem_UserID != null) {
            selectedItem_User_DB_Ref = db_root_users.child(selectedItem_UserID);
            get_selectedItem_userPhoneNumber(selectedItem_User_DB_Ref);
            get_selectedItem_userEmail(selectedItem_User_DB_Ref);

        }

        ImageButton img_btn_call = findViewById(R.id.imageButton_user_phoneCall);
        ImageButton img_btn_send_email = findViewById(R.id.imageButton_user_send_email);
        img_btn_itemImage = findViewById(R.id.img_btn_itemImage);

        img_btn_call.setOnClickListener(this);

        img_btn_send_email.setOnClickListener(this);

        img_btn_itemImage.setOnClickListener(this);


        viewHolder.itemView.findViewById(R.id.button_confirm_modifications).setOnClickListener(this);

        viewHolder.itemView.findViewById(R.id.button_dismiss_modifications).setOnClickListener(this);

    }

    private void setUpLabel(String item_state) {

        if (item_state.equals(INTENT_VALUE__STATEVALUE_IHAVE)) {
            mSingleItemLabel.setText(getString(R.string.label_item_single_desc_for_sale));


        } else if (item_state.equals(INTENT_VALUE__STATEVALUE_INEED)) {
            mSingleItemLabel.setText(getString(R.string.label_item_single_desc_wanted));

        }
    }

    private void populateWithSelectedItem(DatabaseReference selectedItem_db_ref) {
        Log.d(TAG, "populateWithSelectedItem: "+String.valueOf(selectedItem_db_ref));
        if (selectedItem_db_ref != null) {

            selectedItem_db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    viewHolder.setItemTitle((String) dataSnapshot.child(PATH_ITEM_TITLE).getValue());
                    viewHolder.setItemPrice((String) dataSnapshot.child(PATH_ITEM_PRICE).getValue());
                    viewHolder.setItemCity((String) dataSnapshot.child(PATH_ITEM_CITY).getValue());
                    viewHolder.setItemCategory((String) dataSnapshot.child(PATH_ITEM_CATEGORY).getValue());
                    viewHolder.setItemDateAndTime((String) dataSnapshot.child(PATH_ITEM_DATE_AND_TIME).getValue());
                    viewHolder.setItemImage(Display_and_Manage_SingleItem_Activity.this, (String) dataSnapshot.child(PATH_ITEM_IMAGE).getValue());
                    viewHolder.setItemDetails((String) dataSnapshot.child(PATH_ITEM_DETAILS).getValue());

                    //
                    selectedCity = (String) dataSnapshot.child(PATH_ITEM_CITY).getValue();
                    selectedCategory = (String) dataSnapshot.child(PATH_ITEM_CATEGORY).getValue();
                    //

                    selectedItem_UserID = (String) dataSnapshot.child(PATH_ITEM_USER_ID).getValue();

                    if (selectedItem_UserID != null) {
                        selectedItem_User_DB_Ref = db_root_users.child(selectedItem_UserID);
                        get_selectedItem_userPhoneNumber(selectedItem_User_DB_Ref);
                        get_selectedItem_userEmail(selectedItem_User_DB_Ref);

                        selectedItem_User_DB_Ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                viewHolder.setUserName((String) dataSnapshot.child(PATH_USER_NAME).getValue());
                                viewHolder.setUserImage(getBaseContext(), (String) dataSnapshot.child(PATH_USER_IMAGE).getValue());

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private DatabaseReference getSelectedItem_DB_Ref(String item_ID, String item_State, String item_City, String item_Category) {

        if (item_City.equals(PATH_ALL_CITIES) && item_Category.equals(PATH_ALL_CATEGORIES)) {

            selectedItem_DB_Ref = db_root_items.child(item_State).child(PATH_ALL_ITEMS).child(item_ID);

        } else {

            selectedItem_DB_Ref = db_root_items.child(item_State).child(item_City).child(item_Category).child(item_ID);
        }


        return selectedItem_DB_Ref;
    }

    private String get_selectedItem_userPhoneNumber(DatabaseReference selectedItem_User_DB_Ref) {
        if (selectedItem_User_DB_Ref == null) {
            return null;
        }
        selectedItem_User_DB_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userPhoneNumber = (String) dataSnapshot.child(PATH_USER_PHONE_NUMBER).getValue();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userPhoneNumber;
    }

    private String get_selectedItem_userEmail(DatabaseReference selectedItem_User_DB_Ref) {
        if(selectedItem_User_DB_Ref==null){
            return null;
        }
        selectedItem_User_DB_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userEmail = (String) dataSnapshot.child(PATH_USER_EMAIL).getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userEmail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_item, menu);

        if (!(userID.equals(selectedItem_UserID))) {
            menu.findItem(R.id.action_remove_item).setVisible(false);
            menu.findItem(R.id.action_edit_item).setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_remove_item:

                confirmRemovingItem();

                break;

            case R.id.action_edit_item:

                showModifyWidgets();

                break;

            case android.R.id.home:

                finish();
                break;

            case R.id.action_save_item:
                saveItemImageToGallery();

                break;

        }

        return true;
    }

    private void initializeAuthenticationRef() {

        //Firebase Authentication initialization
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        userID = mAuth.getCurrentUser().getUid();

    }

    private void initializeStorageRef() {
        //Firebase Storage initialization
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(CODE_FIREBASE_STORAGE_REF);
    }

    private void initializeDatabaseRefs() {

        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();

        db_root_items = db_root.child(PATH_ITEMS);
        db_root_users = db_root.child(PATH_USERS);
        db_root_items_users = db_root.child(PATH_ITEMS_USERS);

        final String item_ID = getIntent().getExtras().getString(INTENT_KEY_ITEM_ID);

        final String item_State = getIntent().getExtras().getString(INTENT_KEY__PATH_STATE);
        final String item_Category = getIntent().getExtras().getString(INTENT_KEY_ITEM_CAT);
        final String item_City = getIntent().getExtras().getString(INTENT_KEY_ITEM_CITY);


        setUpLabel(item_State);

        if (item_ID != null && item_State != null && item_Category != null) {

            // getting the DB_Ref of the "selectedItem"  by providing the path data i.e. item_ID,item_State,item_City and item_Category.
            selectedItem_DB_Ref = getSelectedItem_DB_Ref(item_ID, item_State, item_City, item_Category);

        }


    }

    private DatabaseReference getDB_Ref_selected_Cit_Cat() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                finalDatabaseReference = db_root_items.child(PATH_INEED).child(selectedCity).child(selectedCategory);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                finalDatabaseReference = db_root_items.child(PATH_IHAVE).child(selectedCity).child(selectedCategory);


            }
        }
        return finalDatabaseReference;
    }

    private DatabaseReference getDB_Ref_AllCities() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                finalDatabaseReference = db_root_items.child(PATH_INEED).child(PATH_ALL_CITIES).child(selectedCategory);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                finalDatabaseReference = db_root_items.child(PATH_IHAVE).child(PATH_ALL_CITIES).child(selectedCategory);


            }
        }
        return finalDatabaseReference;
    }

    private DatabaseReference getDB_Ref_AllCategories() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                finalDatabaseReference = db_root_items.child(PATH_INEED).child(selectedCity).child(PATH_ALL_CATEGORIES);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                finalDatabaseReference = db_root_items.child(PATH_IHAVE).child(selectedCity).child(PATH_ALL_CATEGORIES);


            }
        }
        return finalDatabaseReference;
    }

    private DatabaseReference getDB_Ref_AlItems() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                finalDatabaseReference = db_root_items.child(PATH_INEED).child(PATH_ALL_ITEMS);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                finalDatabaseReference = db_root_items.child(PATH_IHAVE).child(PATH_ALL_ITEMS);


            }
        }
        return finalDatabaseReference;
    }

    private DatabaseReference getDB_Ref_CurrentUser_selected_Cit_Cat() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                finalDatabaseReference = db_root_items_users.child(PATH_INEED).child(selectedCity).child(selectedCategory).child(PATH_USERS_IDS).child(userID);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                finalDatabaseReference = db_root_items_users.child(PATH_IHAVE).child(selectedCity).child(selectedCategory).child(PATH_USERS_IDS).child(userID);


            }
        }
        return finalDatabaseReference;
    }

    private DatabaseReference getDB_Ref_CurrentUser_AllCities() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                finalDatabaseReference = db_root_items_users.child(PATH_INEED).child(PATH_ALL_CITIES).child(selectedCategory).child(PATH_USERS_IDS).child(userID);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                finalDatabaseReference = db_root_items_users.child(PATH_IHAVE).child(PATH_ALL_CITIES).child(selectedCategory).child(PATH_USERS_IDS).child(userID);


            }
        }
        return finalDatabaseReference;
    }

    private DatabaseReference getDB_Ref_CurrentUser_AllCategories() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                finalDatabaseReference = db_root_items_users.child(PATH_INEED).child(selectedCity).child(PATH_ALL_CATEGORIES).child(PATH_USERS_IDS).child(userID);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                finalDatabaseReference = db_root_items_users.child(PATH_IHAVE).child(selectedCity).child(PATH_ALL_CATEGORIES).child(PATH_USERS_IDS).child(userID);


            }
        }
        return finalDatabaseReference;
    }

    private DatabaseReference getDB_Ref_CurrentUser_AllItems() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                finalDatabaseReference = db_root_items_users.child(PATH_INEED).child(PATH_ALL_ITEMS).child(PATH_USERS_IDS).child(userID);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                finalDatabaseReference = db_root_items_users.child(PATH_IHAVE).child(PATH_ALL_ITEMS).child(PATH_USERS_IDS).child(userID);


            }
        }
        return finalDatabaseReference;
    }


    //remove start
    ///
    //
    private void removeItemDataFromFirebase(StorageReference storageReference) {
        //TODO: Remove item file
        showProgressDialog(this, getString(R.string.message_info_DELETING), getString(R.string.message_info_PLEASE_WAIT));
        // Remove item from all  database references
        removeItem_From_All_DB_Refs();

        hideProgressDialog();
        finish();


    }

    private void removeItemDataFromFirebase_ALLState(StorageReference storageReference) {

        //TODO: Remove item file
        showProgressDialog(this, getString(R.string.message_info_DELETING), getString(R.string.message_info_PLEASE_WAIT));
        // Remove item from  needed  database references


        String item_ID = getIntent().getExtras().getString(INTENT_KEY_ITEM_ID);

        //Removing Item from "ALL Cities and ALL Categories"
        removeItem(getDB_Ref_AlItems().child(item_ID));

        //Removing Item from "ALL Cities and ALL Categories belong to current user"
        removeItem(getDB_Ref_CurrentUser_AllItems().child(item_ID));


        hideProgressDialog();
        finish();


    }

    public void removeItem_From_All_DB_Refs() {

        String item_ID = getIntent().getExtras().getString(INTENT_KEY_ITEM_ID);

        //Removing Item from "ALL Cities and ALL Categories"
        removeItem(getDB_Ref_AlItems().child(item_ID));

        //Removing Item from "selected Category and selected City"
        removeItem(getDB_Ref_selected_Cit_Cat().child(item_ID));

        //Removing Item from "ALL Cities and selected Category"
        removeItem(getDB_Ref_AllCities().child(item_ID));

        //Removing Item from "ALL Categories and selected City"
        removeItem(getDB_Ref_AllCategories().child(item_ID));


        //
        // For current user's Items
        //

        //Removing Item from "ALL Cities and ALL Categories belong to current user"
        removeItem(getDB_Ref_CurrentUser_AllItems().child(item_ID));

        //Removing Item from "selected Category and selected City belong to current user"
        removeItem(getDB_Ref_CurrentUser_selected_Cit_Cat().child(item_ID));

        //Removing Item from "ALL Cities and selected Category belong to current user"
        removeItem(getDB_Ref_CurrentUser_AllCities().child(item_ID));


        //Removing Item from "ALL Categories and selected City belong to current user"
        removeItem(getDB_Ref_CurrentUser_AllCategories().child(item_ID));


        Toast.makeText(Display_and_Manage_SingleItem_Activity.this, getString(R.string.message_info_item_remove), Toast.LENGTH_SHORT).show();
        finish();
    }

    public void removeItem_From_Corresponding_DB_Refs() {


        if (selectedCity.equals(PATH_ALL_CITIES) && selectedCategory.equals(PATH_ALL_CATEGORIES)) {

            removeItemDataFromFirebase_ALLState(getImgStorageRef());

        } else {

            removeItemDataFromFirebase(getImgStorageRef());
        }
    }

    private void confirmRemovingItem() {
        FragmentManager fragmentManager = getFragmentManager();


        Fragment fragment = fragmentManager.findFragmentByTag("removeItemFragment");
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }


        RemoveItem_AlertDialogFragment removeItemAlertDialogFragment = new RemoveItem_AlertDialogFragment();
        removeItemAlertDialogFragment.show(fragmentManager, "removeItemFragment");


    }

    public void removeItem(DatabaseReference databaseReference) {

        databaseReference.removeValue();

    }


    //remove end
    ///
    //


    //modify start
    ///
    //

    public void modifyItem_In_All_DB_Refs() {
        String item_ID = getIntent().getExtras().getString(INTENT_KEY_ITEM_ID);

        //Removing Item from "ALL Cities and ALL Categories"
        modifyItem(getDB_Ref_AlItems().child(item_ID));

        //Removing Item from "selected Category and selected City"
        modifyItem(getDB_Ref_selected_Cit_Cat().child(item_ID));

        //Removing Item from "ALL Cities and selected Category"
        modifyItem(getDB_Ref_AllCities().child(item_ID));

        //Removing Item from "ALL Categories and selected City"
        modifyItem(getDB_Ref_AllCategories().child(item_ID));


        //
        // For current user's Items
        //

        //Removing Item from "ALL Cities and ALL Categories belong to current user"
        modifyItem(getDB_Ref_CurrentUser_AllItems().child(item_ID));

        //Removing Item from "selected Category and selected City belong to current user"
        modifyItem(getDB_Ref_CurrentUser_selected_Cit_Cat().child(item_ID));

        //Removing Item from "ALL Cities and selected Category belong to current user"
        modifyItem(getDB_Ref_CurrentUser_AllCities().child(item_ID));


        //Removing Item from "ALL Categories and selected City belong to current user"
        modifyItem(getDB_Ref_CurrentUser_AllCategories().child(item_ID));


    }

    private void modifyItem(DatabaseReference databaseReference) {

        databaseReference.child(PATH_ITEM_TITLE).setValue(viewHolder.getItemTitle());
        databaseReference.child(PATH_ITEM_PRICE).setValue(viewHolder.getItemPrice());
        databaseReference.child(PATH_ITEM_DETAILS).setValue(viewHolder.getItemDetails());
        databaseReference.child(PATH_ITEM_DATE_AND_TIME).setValue(getCurrentDate());
        databaseReference.child(PATH_ITEM_IMAGE).setValue(String.valueOf(downloadUri));


    }

    public void modifyItem_In_Corresponding_DB_Refs() {

        if (selectedCity.equals(PATH_ALL_CITIES) && selectedCategory.equals(PATH_ALL_CATEGORIES)) {


            modifyItemDataInFirebase_ALLState(getImgStorageRef());

        } else {

            modifyItemDataInFirebase(getImgStorageRef());
        }

    }

    private void modifyItemDataInFirebase(final StorageReference storageReference) {

        showProgressDialog(this, getString(R.string.message_info_MODIFYING), getString(R.string.message_info_PLEASE_WAIT));

//First upload selected image

        if (itemImageUri == null) {
            itemImageUri = Uri.EMPTY;
        }


        storageReference.putFile(itemImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Display_and_Manage_SingleItem_Activity.this, getResources().getString(R.string.message_info_modified_ok), Toast.LENGTH_SHORT).show();

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUri = uri;
                    }
                });
                //Secondly  Modify item in all  database references
                modifyItem_In_All_DB_Refs();


                hideProgressDialog();
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Display_and_Manage_SingleItem_Activity.this, getResources().getString(R.string.message_info_modifing_error) + "\n" + e.toString(), Toast.LENGTH_LONG).show();


            }
        });


    }

    private void modifyItemDataInFirebase_ALLState(final StorageReference storageReference) {


        showProgressDialog(this, getString(R.string.message_info_MODIFYING), getString(R.string.message_info_PLEASE_WAIT));

//First upload selected image

        if (itemImageUri == null) {
            itemImageUri = Uri.EMPTY;
        }


        storageReference.putFile(itemImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Display_and_Manage_SingleItem_Activity.this, getResources().getString(R.string.message_info_modified_ok), Toast.LENGTH_SHORT).show();

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUri = uri;
                    }
                });


                String item_ID = getIntent().getExtras().getString(INTENT_KEY_ITEM_ID);

                //Modifying Item in "ALL Cities and ALL Categories"
                modifyItem(getDB_Ref_AlItems().child(item_ID));

                //Modifying Item in "ALL Cities and ALL Categories belong to current user"
                modifyItem(getDB_Ref_CurrentUser_AllItems().child(item_ID));


                hideProgressDialog();
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Display_and_Manage_SingleItem_Activity.this, getResources().getString(R.string.message_info_modifing_error) + "\n" + e.toString(), Toast.LENGTH_LONG).show();


            }
        });

    }

    private void showModifyWidgets() {

        edit_state = true;

        viewHolder.itemView.findViewById(R.id.ll_item_modify).setVisibility(View.VISIBLE);

        viewHolder.itemView.findViewById(R.id.et_item_price).setEnabled(true);
        viewHolder.itemView.findViewById(R.id.et_item_title).setEnabled(true);
        viewHolder.itemView.findViewById(R.id.et_item_details).setEnabled(true);

        viewHolder.itemView.findViewById(R.id.et_item_price).setBackground(getDrawable(R.drawable.background_edit_text_enabled));
        viewHolder.itemView.findViewById(R.id.et_item_title).setBackground(getDrawable(R.drawable.background_edit_text_enabled));
        viewHolder.itemView.findViewById(R.id.et_item_details).setBackground(getDrawable(R.drawable.background_edit_text_enabled));
        img_btn_itemImage.setBackground(getDrawable(R.drawable.background_edit_text_enabled));


    }

    private void hideModifyWidgets() {

        edit_state = false;
        viewHolder.itemView.findViewById(R.id.ll_item_modify).setVisibility(View.INVISIBLE);

        viewHolder.itemView.findViewById(R.id.et_item_price).setEnabled(false);
        viewHolder.itemView.findViewById(R.id.et_item_title).setEnabled(false);
        viewHolder.itemView.findViewById(R.id.et_item_details).setEnabled(false);

        viewHolder.itemView.findViewById(R.id.et_item_price).setBackground(getDrawable(R.drawable.background_edit_text_disabled));
        viewHolder.itemView.findViewById(R.id.et_item_title).setBackground(getDrawable(R.drawable.background_edit_text_disabled));
        viewHolder.itemView.findViewById(R.id.et_item_details).setBackground(getDrawable(R.drawable.background_edit_text_disabled));
        img_btn_itemImage.setBackground(getDrawable(R.drawable.background_edit_text_disabled));


    }
    // modify end
    ///
    //

    private StorageReference getImgStorageRef() {
        StorageReference imgStorageRef = storageReference.child(PATH_STORAGE_USERS_PICTURES).child(userID);

        return imgStorageRef;
    }

    private void setItemImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);

            img_btn_itemImage.setImageBitmap(smallBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton_user_phoneCall: {
                String phoneNumber = get_selectedItem_userPhoneNumber(selectedItem_User_DB_Ref);


                if (phoneNumber != null) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                            "tel", phoneNumber, null));
                    startActivity(phoneIntent);
                } else {
                    Toast.makeText(this, getString(R.string.message_info_error_no_phone_number), Toast.LENGTH_LONG).show();
                }

            }

            break;
            case R.id.imageButton_user_send_email: {

                String userEmail = get_selectedItem_userEmail(selectedItem_User_DB_Ref);


                String[] addresses = {userEmail};
                if (addresses[0] != null) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", addresses[0], null));
                    String subject = ((EditText) viewHolder.itemView.findViewById(R.id.et_item_title)).getText().toString();


                    emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses[0]);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_email_to_item_user, subject));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.default_val_content_email_to_item_user, subject));
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.label_email_to_item_user)));
                } else {
                    Toast.makeText(this, getString(R.string.message_info_error_no_email), Toast.LENGTH_LONG).show();
                }

            }

            break;

            case R.id.button_confirm_modifications: {

                hideModifyWidgets();

                //modify item with the new values

                modifyItem_In_Corresponding_DB_Refs();


            }
            break;

            case R.id.button_dismiss_modifications: {

                hideModifyWidgets();

                //reset item with its original values
                populateWithSelectedItem(selectedItem_DB_Ref);


            }
            break;
            case R.id.img_btn_itemImage:

                if (edit_state) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                } else {

                    openItemImageInGallery(saveItemImageToGallery());

                }


                break;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                itemImageUri = resultUri;
                setItemImage(itemImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                error.printStackTrace();
            }
        }

    }

    public Instant now() {
        return Instant.now();
    }

    public String getCurrentDate() {

        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = SimpleDateFormat.getDateInstance();

        return formatter.format(date);
    }

    public String saveItemImageToGallery() {
        final String item_ID = getIntent().getExtras().getString(INTENT_KEY_ITEM_ID);

        String fname = viewHolder.getItemTitle() + "_" + viewHolder.getItemPrice() + "_" + item_ID + ".jpg";
        File file = new File(myDir, fname);

        bitmap = drawableToBitmap(img_btn_itemImage.getDrawable());
        if (bitmap != null) {
            Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);
            if (!(file.exists())) {
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    smallBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                //itemImageStoragePath = MediaStore.Images.Media.insertImage(getContentResolver(), smallBitmap, viewHolder.getItemTitle() + "_" + viewHolder.getItemPrice(), viewHolder.getItemDetails());
                Toast.makeText(Display_and_Manage_SingleItem_Activity.this, getString(R.string.message_info_item_saved_ok), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(Display_and_Manage_SingleItem_Activity.this, getString(R.string.message_info_item_saved_already), Toast.LENGTH_SHORT).show();

            }

        }
        return file.getPath();

    }


    public void openItemImageInGallery(String itemImageStoragePath) {
        if (itemImageStoragePath != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + itemImageStoragePath), "image/*");
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.message_info_error_no_image), Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable != null) {
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    public boolean getItemStateFlag() {
        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {
                return true;


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {

                return false;


            }


        }

        return true;
    }
}

