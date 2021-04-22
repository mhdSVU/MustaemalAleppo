package mohammedyouser.com.mustaemalaleppo.Domain;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jrummyapps.android.animations.Technique;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import mohammedyouser.com.mustaemalaleppo.Activity_Maps_Item_Location;
import mohammedyouser.com.mustaemalaleppo.Data.MyViewHolder;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


public class Activity_Display_Modify_Remove_Item extends AppCompatActivity implements
        View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {


    private boolean edit_state = false;
    private File myDir;

    private Uri uri_itemImg;
    private Bitmap bitmap;

    private MyViewHolder viewHolder;
    private TextView mSingleItemLabel;
    private ImageButton img_btn_itemImage;


    private String userID;

    private String item_ID;
    private DatabaseReference item_DB_Ref;
    private String item_city;
    private String item_category;
    private String item_userID;
    private DatabaseReference item_user_DB_Ref;
    private String item_userEmail;
    private String item_userPhoneNumber;

    private DatabaseReference db_ref_final;
    private DatabaseReference db_ref_items;
    private DatabaseReference db_ref_users;
    private DatabaseReference db_ref_currentUser_items;
    private DatabaseReference db_root_userIDs_notifications;
    private DatabaseReference db_root_tokens_notifications;
    private DatabaseReference db_root;


    private StorageReference storageReference_currentUser;
    private ValueEventListener selectedItem_db_ref_listener;
    private ValueEventListener selectedItem_db_ref_listener_img;
    private String str_uri_itemImg;
    private ImageButton img_btn_call;
    private ImageButton img_btn_send_sms;
    private ImageButton img_btn_whatsapp;
    private ImageButton img_btn_share;
    private ImageButton img_btn_show_on_map;
    private ValueEventListener selectedItem_db_ref_listener_itemImage;
    private Bundle bundle;
    private boolean isAvl = true;
    private BroadcastReceiver receiver;
    private LocalBroadcastManager broadcaster;
    private boolean mItemFound = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_modify_remove_item);

        doMainInitializations();

    }

    private void reactToIntent(Bundle bundle) {
        //check for nullity
        if (bundle == null) return;
        if (bundle.containsKey(INTENT_KEY_SOURCE)) {
            getSelectedItem_DB_Ref(
                    bundle.getString(INTENT_KEY_ITEM_ID),
                    bundle.getString(INTENT_KEY__PATH_STATE),
                    bundle.getString(INTENT_KEY_ITEM_CITY),
                    bundle.getString(INTENT_KEY_ITEM_CAT)).runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    if (currentData.getValue() != null) {
                        return Transaction.success(currentData);
                    }

                    mItemFound = false;
                    return Transaction.abort();
                }

                @Override
                public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    if (committed) {
                        // unique key saved
                        Log.d(TAG, "onComplete: " + "unique key saved");
                    } else {
                        // unique key already exists

                    }
                }
            });
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!mItemFound) {
                update_UI_no_content();
                return;
            }

            Log.d(TAG, "reactToIntent: INTENT_KEY_SOURCE ");
            populateWithSelectedItem(getSelectedItem_DB_Ref(
                    bundle.getString(INTENT_KEY_ITEM_ID),
                    bundle.getString(INTENT_KEY__PATH_STATE),
                    bundle.getString(INTENT_KEY_ITEM_CITY),
                    bundle.getString(INTENT_KEY_ITEM_CAT)));
            setNotificationFlagOpened(
                    bundle.getString(INTENT_KEY__PATH_STATE),
                    bundle.getString(INTENT_KEY_TOPIC),
                    bundle.getString(INTENT_KEY_NOTIFICATION_ID));

        } else {
            populateWithSelectedItem(getSelectedItem_DB_Ref(
                    bundle.getString(INTENT_KEY_ITEM_ID),
                    bundle.getString(INTENT_KEY__PATH_STATE),
                    bundle.getString(INTENT_KEY_ITEM_CITY),
                    bundle.getString(INTENT_KEY_ITEM_CAT)));
        }


    }

    private void update_UI_no_content() {
        findViewById(R.id.fl_no_content).setVisibility(View.VISIBLE);
        findViewById(R.id.fl_no_content).setBackgroundColor(getColor(R.color.grey_100));
        findViewById(R.id.ll_no_content).setVisibility(View.VISIBLE);
        Log.d(TAG, "update_UI_no_content: ");

    }

    private void setNotificationFlagOpened(String state, String topicID, String notificationID) {
        db_root_userIDs_notifications.child(userID).child(state).child(topicID).child(notificationID).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() != null) {
                    currentData.setValue(false);

                    return Transaction.success(currentData);
                }


                return Transaction.abort();
            }

            @Override
            public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    // unique key saved
                    Log.d(TAG, "onComplete: " + "unique key saved");
                } else {
                    // unique key already exists

                }
            }
        });

/*
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            db_root_tokens_notifications.child(task.getResult()).child(state).child(topicID).child(notificationID).setValue(false);

        });
*/


    }

    private void populateWithSelectedItem(DatabaseReference selectedItem_db_ref) {

        findViewById(R.id.fl_no_content).setVisibility(View.GONE);
        findViewById(R.id.ll_no_content).setVisibility(View.GONE);

        setUpViews();
        str_uri_itemImg = get_ItemImage_finalFilePath();

        if (selectedItem_db_ref != null) {

            selectedItem_db_ref_listener = selectedItem_db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    viewHolder.setItemTitle((String) dataSnapshot.child(PATH_ITEM_TITLE).getValue());
                    viewHolder.setItemPrice((String) dataSnapshot.child(PATH_ITEM_PRICE).getValue());
                    viewHolder.setItemCity((String) dataSnapshot.child(PATH_ITEM_CITY).getValue());
                    viewHolder.setItemCategory((String) dataSnapshot.child(PATH_ITEM_CATEGORY).getValue());
                    viewHolder.setItemDateAndTime((String) dataSnapshot.child(PATH_ITEM_DATE_AND_TIME).getValue());
                    //  viewHolder.setItemImage(Display_and_Manage_SingleItem_Activity.this, (String) dataSnapshot.child(PATH_ITEM_IMAGE).getValue());
                    viewHolder.setItemDetails((String) dataSnapshot.child(PATH_ITEM_DETAILS).getValue());

/*
                    Uri uri_itemImg = Uri.parse((String) dataSnapshot.child(PATH_ITEM_IMAGE).getValue());
                    Glide.with(getBaseContext())
                            .load(uri_itemImg).into(img_btn_itemImage);*/
                    //
                    item_city = (String) dataSnapshot.child(PATH_ITEM_CITY).getValue();
                    item_category = (String) dataSnapshot.child(PATH_ITEM_CATEGORY).getValue();
                    //

                    item_userID = (String) dataSnapshot.child(PATH_ITEM_USER_ID).getValue();

                    if (item_userID != null) {
                        item_user_DB_Ref = db_ref_users.child(item_userID);
                        get_selectedItem_userPhoneNumber(item_user_DB_Ref);
                        get_selectedItem_userEmail(item_user_DB_Ref);

                        item_user_DB_Ref.addValueEventListener(new ValueEventListener() {
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
            selectedItem_db_ref_listener_img = selectedItem_db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PATH_ITEM_IMAGE).getValue() != null) {
                        Uri uri_itemImg = Uri.parse((String) dataSnapshot.child(PATH_ITEM_IMAGE).getValue());
                        Glide.with(getBaseContext())
                                .load(uri_itemImg).into(img_btn_itemImage);
                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void openItemImage(DatabaseReference selectedItem_db_ref) {
        Log.d(TAG, "populateWithSelectedItem: " + String.valueOf(selectedItem_db_ref));
        if (selectedItem_db_ref != null) {

            selectedItem_db_ref_listener_itemImage = selectedItem_db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    openItemImageInGallery((String) dataSnapshot.child(PATH_ITEM_IMAGE).getValue());
                    selectedItem_db_ref.removeEventListener(selectedItem_db_ref_listener_itemImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

//
    //Initialization start
    ///

    private void doMainInitializations() {
        initialize_User_LocalDir();

        initializeAuthenticationRef();
        initializeDatabaseRefs();
        initializeStorageRef();

        setUpToolBar();


        reactToIntent(getIntent().getExtras());
    }

    private void initialize_User_LocalDir() {

        String root = String.valueOf(getFilesDir());
        if (getItemStateFlag())
            myDir = new File(root + "/Pictures" + "/MyStuffApp/" + PATH_INEED);
        else if (!(getItemStateFlag())) {
            myDir = new File(root + "/Pictures" + "/MyStuffApp/" + PATH_IHAVE);

        }

        if (!(myDir.exists()))
            myDir.mkdirs();


    }

    private void setUpToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setUpViews() {
        mSingleItemLabel = findViewById(R.id.textView_label_single_item);
        View mRootView = findViewById(R.id.rootView);
        viewHolder = new MyViewHolder(mRootView);

        //setUpLabel(item_State);

        if (item_userID != null) {
            item_user_DB_Ref = db_ref_users.child(item_userID);
            get_selectedItem_userPhoneNumber(item_user_DB_Ref);
            get_selectedItem_userEmail(item_user_DB_Ref);

        }

        img_btn_call = findViewById(R.id.imageButton_user_phoneCall);
        img_btn_send_sms = findViewById(R.id.imageButton_user_send_sms);
        img_btn_whatsapp = findViewById(R.id.imageButton_user_whatsapp);
        img_btn_share = findViewById(R.id.img_btn_itemImage_share);
        img_btn_itemImage = findViewById(R.id.img_btn_itemImage);
        img_btn_show_on_map = findViewById(R.id.img_btn_show_on_map);

        img_btn_call.setOnClickListener(this);
        img_btn_send_sms.setOnClickListener(this);
        img_btn_itemImage.setOnClickListener(this);
        img_btn_whatsapp.setOnClickListener(this);
        img_btn_share.setOnClickListener(this);
        img_btn_show_on_map.setOnClickListener(this);


        viewHolder.itemView.findViewById(R.id.button_confirm_modifications).setOnClickListener(this);

        viewHolder.itemView.findViewById(R.id.button_dismiss_modifications).setOnClickListener(this);

    }

    private void initializeAuthenticationRef() {

        //Firebase Authentication initialization
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        userID = mAuth.getCurrentUser().getUid();

    }

    private void initializeStorageRef() {
        //Firebase Storage initialization
        storageReference_currentUser = FirebaseStorage.getInstance().getReferenceFromUrl(CODE_FIREBASE_STORAGE_REF);
    }

    private void initializeDatabaseRefs() {

        db_root = FirebaseDatabase.getInstance().getReference();

        db_ref_items = db_root.child(PATH_ITEMS);
        db_ref_users = db_root.child(PATH_USERS);
        db_ref_currentUser_items = db_root.child(PATH_USER_ITEMS);

        db_root_userIDs_notifications = db_root.child(PATH_USERIDS_NOTIFICATIONS);
        db_root_tokens_notifications = db_root.child(PATH_TOKENS_NOTIFICATIONS);

        item_ID = getItemID();

        final String item_State = getIntent().getExtras().getString(INTENT_KEY__PATH_STATE);
        final String item_Category = getIntent().getExtras().getString(INTENT_KEY_ITEM_CAT);
        final String item_City = getIntent().getExtras().getString(INTENT_KEY_ITEM_CITY);


        //setUpLabel(item_State);

        if (item_ID != null && item_State != null && item_Category != null) {

            // getting the DB_Ref of the "selectedItem"  by providing the path data i.e. item_ID,item_State,item_City and item_Category.
            item_DB_Ref = getSelectedItem_DB_Ref(item_ID, item_State, item_City, item_Category);

        }


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

    private String getItemID() {
        if (getIntent() != null) {
            if (getIntent().getExtras().containsKey(INTENT_KEY_ITEM_ID))
                Log.d(TAG, "getItemID: " + getIntent().getExtras().getString(INTENT_KEY_ITEM_ID));
            return getIntent().getExtras().getString(INTENT_KEY_ITEM_ID);
        }
        Toast.makeText(this, "In the name of Allah", Toast.LENGTH_LONG).show();
        return null;
    }

// Initialization end
    //

    ///////////////////////////////////////////////////////////////////
//...getting DB references start
    private DatabaseReference getDB_Ref_selected_Cit_Cat() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                db_ref_final = db_ref_items.child(PATH_INEED).child(item_city).child(item_category);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                db_ref_final = db_ref_items.child(PATH_IHAVE).child(item_city).child(item_category);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_AllCities() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                db_ref_final = db_ref_items.child(PATH_INEED).child(PATH_ALL_CITIES).child(item_category);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                db_ref_final = db_ref_items.child(PATH_IHAVE).child(PATH_ALL_CITIES).child(item_category);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_AllCategories() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                db_ref_final = db_ref_items.child(PATH_INEED).child(item_city).child(PATH_ALL_CATEGORIES);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                db_ref_final = db_ref_items.child(PATH_IHAVE).child(item_city).child(PATH_ALL_CATEGORIES);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_AlItems() {


        if (getIntent() != null) {

            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                db_ref_final = db_ref_items.child(PATH_INEED).child(PATH_ALL_ITEMS);
                Log.d(TAG, "getDB_Ref_AlItems 11: " + db_ref_final);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                db_ref_final = db_ref_items.child(PATH_IHAVE).child(PATH_ALL_ITEMS);
                Log.d(TAG, "getDB_Ref_AlItems 22: " + db_ref_final);


            }
        }
        Log.d(TAG, "getDB_Ref_AlItems: 33 " + db_ref_final);

        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_CurrentUser_selected_Cit_Cat() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                db_ref_final = db_ref_currentUser_items.child(PATH_INEED).child(item_city).child(item_category).child(PATH_USERS_IDS).child(userID);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                db_ref_final = db_ref_currentUser_items.child(PATH_IHAVE).child(item_city).child(item_category).child(PATH_USERS_IDS).child(userID);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_CurrentUser_AllCities() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                db_ref_final = db_ref_currentUser_items.child(PATH_INEED).child(PATH_ALL_CITIES).child(item_category).child(PATH_USERS_IDS).child(userID);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                db_ref_final = db_ref_currentUser_items.child(PATH_IHAVE).child(PATH_ALL_CITIES).child(item_category).child(PATH_USERS_IDS).child(userID);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_CurrentUser_AllCategories() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                db_ref_final = db_ref_currentUser_items.child(PATH_INEED).child(item_city).child(PATH_ALL_CATEGORIES).child(PATH_USERS_IDS).child(userID);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                db_ref_final = db_ref_currentUser_items.child(PATH_IHAVE).child(item_city).child(PATH_ALL_CATEGORIES).child(PATH_USERS_IDS).child(userID);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_CurrentUser_AllItems() {


        if (getIntent() != null) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_INEED)) {

                db_ref_final = db_ref_currentUser_items.child(PATH_INEED).child(PATH_ALL_ITEMS).child(PATH_USERS_IDS).child(userID);


            } else if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {


                db_ref_final = db_ref_currentUser_items.child(PATH_IHAVE).child(PATH_ALL_ITEMS).child(PATH_USERS_IDS).child(userID);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getSelectedItem_DB_Ref(String item_ID, String item_State, String item_City, String item_Category) {

        if (item_City.equals(PATH_ALL_CITIES) && item_Category.equals(PATH_ALL_CATEGORIES)) {

            item_DB_Ref = db_ref_items.child(item_State).child(PATH_ALL_ITEMS).child(item_ID);

        } else {

            item_DB_Ref = db_ref_items.child(item_State).child(item_City).child(item_Category).child(item_ID);
        }


        return item_DB_Ref;
    }

    //...getting DB references end
//

    //...getting Storage references start
//

    private StorageReference getImgStorageRef() {

        return storageReference_currentUser.child(PATH_STORAGE_USERS_PICTURES).child(userID);
    }

    //...getting Storage references end
//


    // Modify item start
    //

    private void manageModifyItemPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION_ITEM_MODIFY);
            } else {
                upload_itemImg_and_data();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PERMISSION_ITEM_MODIFY &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("perms", "perms granted");
            upload_itemImg_and_data();

        } else if (requestCode == REQUEST_READ_PERMISSION_ITEM_CLICK_IMG &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("perms", "perms granted");
            openItemImage(item_DB_Ref);

        } else {
            Log.d("perms", "perms not granted");

            Toast.makeText(Activity_Display_Modify_Remove_Item.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();

        }
    }

    private void upload_itemImg_and_data() {
        if (uri_itemImg != null) {
            item_DB_Ref.removeEventListener(selectedItem_db_ref_listener_img);
            showProgressDialog(this, getString(R.string.message_info_MODIFYING), getString(R.string.message_info_PLEASE_WAIT));

            getImgStorageRef().putFile(uri_itemImg)
                    .addOnSuccessListener(taskSnapshot -> {
                        getImgStorageRef().getDownloadUrl().addOnSuccessListener(this::manage_modifyItem_state);

                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(Activity_Display_Modify_Remove_Item.this, getResources().getString(R.string.message_info_modifing_error) + "\n" + e.toString(), Toast.LENGTH_LONG).show())
                    .addOnCompleteListener(task -> hideProgressDialog());
        } else {
            manage_modifyItem_state(null);
        }


    }


    //Entry point modification method
    public void manage_modifyItem_state(Uri uri_itemImg_download) {// main method for 'save' modification button

        if (item_city.equals(PATH_ALL_CITIES)
                && item_category.equals(PATH_ALL_CATEGORIES)) {


            modifyItem_all_CityCat_state(uri_itemImg_download);

        } else {

            modifyItem_one_CityCat_state(uri_itemImg_download);
        }

    }

    //Two methods differs in DB Ref
    private void modifyItem_all_CityCat_state(Uri uri_itemImg_download) {////Save modifications all

        modifyItem_in_single_DB_Ref(getDB_Ref_AlItems().child(item_ID), uri_itemImg_download);

        //Modifying Item in "ALL Cities and ALL Categories belong to current user"
        modifyItem_in_single_DB_Ref(getDB_Ref_CurrentUser_AllItems().child(item_ID), uri_itemImg_download);


    }

    private void modifyItem_one_CityCat_state(Uri uri_itemImg_download) {

        modifyItem_in_all_DB_Refs(uri_itemImg_download);


    }

    //Two methods differs in number of modified items (one vs multi)
    private void modifyItem_in_single_DB_Ref(@NonNull DatabaseReference databaseReference, Uri uri_itemImg_download) {

        databaseReference.child(PATH_ITEM_TITLE).setValue(viewHolder.getItemTitle());
        databaseReference.child(PATH_ITEM_PRICE).setValue(viewHolder.getItemPrice());
        databaseReference.child(PATH_ITEM_DETAILS).setValue(viewHolder.getItemDetails());
        databaseReference.child(PATH_ITEM_DATE_AND_TIME).setValue(getCurrentDate());
        if (uri_itemImg_download != null) {
            databaseReference.child(PATH_ITEM_IMAGE).setValue(String.valueOf(uri_itemImg_download));
        }

    }

    public void modifyItem_in_all_DB_Refs(Uri uri_itemImg_download) {
        ;

        //Removing Item from "ALL Cities and ALL Categories"
        modifyItem_in_single_DB_Ref(getDB_Ref_AlItems().child(item_ID), uri_itemImg_download);

        //Removing Item from "selected Category and selected City"
        modifyItem_in_single_DB_Ref(getDB_Ref_selected_Cit_Cat().child(item_ID), uri_itemImg_download);

        //Removing Item from "ALL Cities and selected Category"
        modifyItem_in_single_DB_Ref(getDB_Ref_AllCities().child(item_ID), uri_itemImg_download);

        //Removing Item from "ALL Categories and selected City"
        modifyItem_in_single_DB_Ref(getDB_Ref_AllCategories().child(item_ID), uri_itemImg_download);


        //
        // For current user's Items
        //

        //Removing Item from "ALL Cities and ALL Categories belong to current user"
        modifyItem_in_single_DB_Ref(getDB_Ref_CurrentUser_AllItems().child(item_ID), uri_itemImg_download);

        //Removing Item from "selected Category and selected City belong to current user"
        modifyItem_in_single_DB_Ref(getDB_Ref_CurrentUser_selected_Cit_Cat().child(item_ID), uri_itemImg_download);

        //Removing Item from "ALL Cities and selected Category belong to current user"
        modifyItem_in_single_DB_Ref(getDB_Ref_CurrentUser_AllCities().child(item_ID), uri_itemImg_download);


        //Removing Item from "ALL Categories and selected City belong to current user"
        modifyItem_in_single_DB_Ref(getDB_Ref_CurrentUser_AllCategories().child(item_ID), uri_itemImg_download);
        //  hideProgressDialog();


    }

//

// Modify item end
    ///
    //


    //...remove item start
//
    private void removeItem_one_CityCat_state(StorageReference storageReference) {
        //TODO: Remove item file
        showProgressDialog(this, getString(R.string.message_info_DELETING), getString(R.string.message_info_PLEASE_WAIT));
        // Remove item from all  database references
        removeItem_from_all_DB_Ref();

        hideProgressDialog();
        finish();


    }

    private void removeItem_all_CityCat_state(StorageReference storageReference) {

        //TODO: Remove item file
        showProgressDialog(this, getString(R.string.message_info_DELETING), getString(R.string.message_info_PLEASE_WAIT));
        // Remove item from  needed  database references


        ;

        //Removing Item from "ALL Cities and ALL Categories"
        removeItem_from_single_DB_Ref(getDB_Ref_AlItems().child(item_ID));

        //Removing Item from "ALL Cities and ALL Categories belong to current user"
        removeItem_from_single_DB_Ref(getDB_Ref_CurrentUser_AllItems().child(item_ID));


        hideProgressDialog();
        finish();


    }

    public void removeItem_from_all_DB_Ref() {

        ;

        //Removing Item from "ALL Cities and ALL Categories"
        removeItem_from_single_DB_Ref(getDB_Ref_AlItems().child(item_ID));

        //Removing Item from "selected Category and selected City"
        removeItem_from_single_DB_Ref(getDB_Ref_selected_Cit_Cat().child(item_ID));

        //Removing Item from "ALL Cities and selected Category"
        removeItem_from_single_DB_Ref(getDB_Ref_AllCities().child(item_ID));

        //Removing Item from "ALL Categories and selected City"
        removeItem_from_single_DB_Ref(getDB_Ref_AllCategories().child(item_ID));


        //
        // For current user's Items
        //

        //Removing Item from "ALL Cities and ALL Categories belong to current user"
        removeItem_from_single_DB_Ref(getDB_Ref_CurrentUser_AllItems().child(item_ID));

        //Removing Item from "selected Category and selected City belong to current user"
        removeItem_from_single_DB_Ref(getDB_Ref_CurrentUser_selected_Cit_Cat().child(item_ID));

        //Removing Item from "ALL Cities and selected Category belong to current user"
        removeItem_from_single_DB_Ref(getDB_Ref_CurrentUser_AllCities().child(item_ID));


        //Removing Item from "ALL Categories and selected City belong to current user"
        removeItem_from_single_DB_Ref(getDB_Ref_CurrentUser_AllCategories().child(item_ID));


        Toast.makeText(Activity_Display_Modify_Remove_Item.this, getString(R.string.message_info_item_remove), Toast.LENGTH_SHORT).show();
        finish();
    }

    public void removeItem_from_single_DB_Ref(DatabaseReference databaseReference) {

        databaseReference.removeValue();

    }

    public void manage_removeItem_state() {


        if (item_city.equals(PATH_ALL_CITIES) && item_category.equals(PATH_ALL_CATEGORIES)) {

            removeItem_all_CityCat_state(getImgStorageRef());

        } else {

            removeItem_one_CityCat_state(getImgStorageRef());
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


    //...remove item end
//

    //... callbacks start
///
//
    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        if (edit_state) {
            hideModifyWidgets();

            //reset item with its original values
            populateWithSelectedItem(item_DB_Ref);
        } else {


            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton_user_phoneCall: {
                String phoneNumber = get_selectedItem_userPhoneNumber(item_user_DB_Ref);


                if (phoneNumber != null) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                            "tel", phoneNumber, null));
                    startActivity(phoneIntent);
                } else {
                    Toast.makeText(this, getString(R.string.message_info_error_no_phone_number), Toast.LENGTH_LONG).show();
                }

            }

            break;
            case R.id.imageButton_user_send_sms: {

              /*  String userEmail = get_selectedItem_userEmail(item_user_DB_Ref);TODO save for later


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
                }*/

                String subject = ((EditText) viewHolder.itemView.findViewById(R.id.et_item_title)).getText().toString();
                String str_msg_sms = getString(R.string.title_email_to_item_user, subject) + getString(R.string.default_val_content_email_to_item_user, subject);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + get_selectedItem_userPhoneNumber(item_user_DB_Ref)));
                intent.putExtra("sms_body", str_msg_sms);
                startActivity(intent);

            }
            break;
            case R.id.imageButton_user_whatsapp: {

                String subject = ((EditText) viewHolder.itemView.findViewById(R.id.et_item_title)).getText().toString();
                String str_msg_watsapp_ = getString(R.string.title_email_to_item_user, subject) + getString(R.string.default_val_content_email_to_item_user, subject);
                contactViaWhatsapp(get_selectedItem_userPhoneNumber(item_user_DB_Ref), str_msg_watsapp_);

            }
            break;
            case R.id.img_btn_itemImage_share: {


                shareItemImage(create_ItemImage_File(), getItemDescriptionforShare());
            }
            break;

            case R.id.button_confirm_modifications: {

                hideModifyWidgets();

                //modify item with the new values

                //modifyItem_In_Corresponding_DB_Refs();
                manageModifyItemPerm();


            }
            break;

            case R.id.button_dismiss_modifications: {

                hideModifyWidgets();

                //reset item with its original values
                populateWithSelectedItem(item_DB_Ref);


            }
            break;
            case R.id.img_btn_itemImage:

                if (edit_state) {
                    startCropActivity();
                } else {

                    manageDisplayItemImagePerm();
                }


                break;
            case R.id.img_btn_show_on_map:
                Technique.PULSE.playOn(img_btn_show_on_map);
                item_DB_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent = new Intent(Activity_Display_Modify_Remove_Item.this, Activity_Maps_Item_Location.class);
                        if ((String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue()).equals("0")) ||
                                (String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue()).equals("0"))) {
                            Toast.makeText(getBaseContext(), "Sorry, this Item has no address provided!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        intent.putExtra(INTENT_KEY_ITEM_LAT, Double.parseDouble(String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue())));
                        intent.putExtra(INTENT_KEY_ITEM_LONG, Double.parseDouble(String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue())));
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                break;

        }


    }

    private String getItemDescriptionforShare() {
        String subject = "From Ineed Ihave Stuff App: I found this item, you can have a look on it: " + ((EditText) viewHolder.itemView.findViewById(R.id.et_item_title)).getText().toString() + "\n" +

                "Its price is: " + ((EditText) viewHolder.itemView.findViewById(R.id.et_item_price)).getText().toString();
        return subject;
    }

    private void contactViaWhatsapp(String item_userPhoneNumber, String item_data) {


        try {
            PackageManager packageManager = getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + item_userPhoneNumber + "&text=" + URLEncoder.encode(item_data, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            } else {
                Toast.makeText(this, "error whatsapp", Toast.LENGTH_SHORT).show();

                // KToast.errorToast(getActivity(), getString(R.string.no_whatsapp), Gravity.BOTTOM, KToast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            Log.e("ERROR WHATSAPP", e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            //KToast.errorToast(getActivity(), getString(R.string.no_whatsapp), Gravity.BOTTOM, KToast.LENGTH_SHORT);
        }

    }


    private void startCropActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uri_itemImg = resultUri;
                setItemImage(uri_itemImg);
                Glide.with(this)
                        .load(uri_itemImg)
                        .into(img_btn_itemImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                error.printStackTrace();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_item, menu);

        if (!(userID.equals(item_userID))) {
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

                saveItemImageToGallery(
                        get_ScaledBitmap_from_drawable(img_btn_itemImage.getDrawable()),
                        viewHolder.getItemTitle() + "_" + viewHolder.getItemPrice(),
                        viewHolder.getItemDetails());

                break;

        }

        return true;
    }

    private void saveItemImageToGallery(Bitmap scaledBitmap, String itemImage_fileName, String description) {
        MediaStore.Images.Media.insertImage(getContentResolver(), scaledBitmap, itemImage_fileName, description);

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("item_img_uri", String.valueOf(uri_itemImg));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("user_img_uri")) {
            uri_itemImg = Uri.parse(savedInstanceState.getString("user_img_uri"));
            setImage_circle(this, uri_itemImg, 0.3f, img_btn_itemImage);
        }
    }


//... callbacks end
    ///
    //

    //... img manipulate start
    ///
    //

    private void manageDisplayItemImagePerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION_ITEM_CLICK_IMG);
            } else {

                openItemImage(item_DB_Ref);

            }
        }
    }

    public String get_ItemImage_finalFilePath() {

        File itemImageFile = create_ItemImage_File();
        if (itemImageFile.exists()) {
            Log.d(TAG, "get_ItemImage_finalFilePath: " + " File already exists!");
            Toast.makeText(this, getString(R.string.message_info_item_saved_already), Toast.LENGTH_SHORT).show();
            return itemImageFile.getPath();
        }

        Bitmap scaledBitmap = get_ScaledBitmap_from_drawable(img_btn_itemImage.getDrawable());
        if (scaledBitmap == null) return null;

        compressBitmap(itemImageFile, scaledBitmap);
        //str_uri_itemImg = MediaStore.Images.Media.insertImage(getContentResolver(), scaledBitmap, viewHolder.getItemTitle() + "_" + viewHolder.getItemPrice(), viewHolder.getItemDetails());
        Toast.makeText(Activity_Display_Modify_Remove_Item.this, getString(R.string.message_info_item_saved_ok), Toast.LENGTH_SHORT).show();
        return itemImageFile.getPath();
    }


    //start
       /*         int CAMERA_PERMISSION_REQUEST_CODE = 2;
                int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (result != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        Toast.makeText(getApplicationContext(), "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                    } else {
                        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PERMISSION_REQUEST_CODE);
                    }
                }*/
    //end


    private boolean compressBitmap(File itemImage_file, Bitmap bitmap) {
        try {
            FileOutputStream out = new FileOutputStream(itemImage_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return true;

        } catch (Exception e) {
            Log.d(TAG, "get_compressedBitmap_filePath: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            return false;
        }

    }

    //start
       /*         int CAMERA_PERMISSION_REQUEST_CODE = 2;
                int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (result != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        Toast.makeText(getApplicationContext(), "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                    } else {
                        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PERMISSION_REQUEST_CODE);
                    }
                }*/
    //end


    public static Bitmap get_ScaledBitmap_from_drawable(Drawable drawable) {
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
        if (bitmap != null) {
            Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);
            return smallBitmap;

        }
        return bitmap;
    }

    private File create_ItemImage_File() {
        final String item_ID = Objects.requireNonNull(getIntent().getExtras()).getString(INTENT_KEY_ITEM_ID);
        String itemImage_fileName = viewHolder.getItemTitle() + "_" + viewHolder.getItemPrice() + "_" + item_ID + ".jpg";
        return new File(myDir, itemImage_fileName);
    }

    public void openItemImageInGallery(String itemImage_fileName) {

        try {
            if (itemImage_fileName == null) {
                Toast.makeText(this, getString(R.string.message_info_error_no_image), Toast.LENGTH_SHORT).show();
                return;
            }

     /*       Uri fileURI = FileProvider.getUriForFile(
                    getApplicationContext(),
                    getPackageName() + ".provider", new File(itemImage_fileName));
*/

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            // intent.setDataAndType(fileURI, "image/*");
            intent.setDataAndType(Uri.parse(itemImage_fileName), "image/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);


        } catch (
                Throwable throwable) {
            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();

        }

    }


    public void shareItemImage(File itemImage_FileName, String textToShare) {

        try {
            if (itemImage_FileName.exists()) {

                Uri fileURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", itemImage_FileName);

                Log.d(TAG, "shareItemImage: " + String.valueOf(fileURI));

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, fileURI);
                intent.putExtra(Intent.EXTRA_TEXT, textToShare);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                /*intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(ItemDescription.this,
                        BuildConfig.APPLICATION_ID +".provider",
                        file));*/
                startActivity(Intent.createChooser(intent, "Share image via"));

                //  startActivity(intent);


            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT, textToShare);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                /*intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(ItemDescription.this,
                        BuildConfig.APPLICATION_ID +".provider",
                        file));*/
                startActivity(Intent.createChooser(intent, "Share image via"));
                // Toast.makeText(this, getString(R.string.message_info_error_no_image), Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable throwable) {
            Toast.makeText(this, throwable.getMessage() + "\n share error", Toast.LENGTH_LONG).show();

        } finally {

        }

    }


    //... img manipulate end
    ///
    //

    private void showModifyWidgets() {

        edit_state = true;

        viewHolder.itemView.findViewById(R.id.ll_item_modify).setVisibility(View.VISIBLE);

        viewHolder.itemView.findViewById(R.id.et_item_price).setEnabled(true);
        viewHolder.itemView.findViewById(R.id.et_item_title).setEnabled(true);
        viewHolder.itemView.findViewById(R.id.et_item_details).setEnabled(true);

        viewHolder.itemView.findViewById(R.id.et_item_price).setBackground(getDrawable(R.drawable.background_edit_text_enabled));
        viewHolder.itemView.findViewById(R.id.et_item_title).setBackground(getDrawable(R.drawable.background_edit_text_enabled));
        viewHolder.itemView.findViewById(R.id.et_item_details).setBackground(getDrawable(R.drawable.background_edit_text_enabled));

        viewHolder.itemView.findViewById(R.id.tv_item_update_img).setVisibility(View.VISIBLE);
        viewHolder.itemView.findViewById(R.id.tv_item_update_img).setBackground(getDrawable(R.drawable.background_edit_text_enabled));


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

        viewHolder.itemView.findViewById(R.id.tv_item_update_img).setVisibility(View.INVISIBLE);


    }


    private void setUpLabel(String item_state) {//TODO setUpLabel null view

        if (item_state.equals(INTENT_VALUE__STATEVALUE_IHAVE)) {
            mSingleItemLabel.setText(getString(R.string.label_item_single_desc_for_sale));


        } else if (item_state.equals(INTENT_VALUE__STATEVALUE_INEED)) {
            mSingleItemLabel.setText(getString(R.string.label_item_single_desc_wanted));

        }
    }


    public String getCurrentDate() {

        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = SimpleDateFormat.getDateInstance();

        return formatter.format(date);
    }

    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView) {
        Glide.with(context).load(imageURL)//TODO
                //.bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);


    }

    public boolean getItemStateFlag() {
        if (getIntent().getExtras().containsKey(INTENT_KEY__STATEVALUE)) {
            if (getIntent().getExtras().getString(INTENT_KEY__STATEVALUE).equals(INTENT_VALUE__STATEVALUE_IHAVE)) {

                return false;


            }
        }


        return true;
    }

/*    private Uri getItemImage() {
        if (uri_itemImg_download != null)
            return uri_itemImg_download;

        else {
            return uri_itemImg;
        }*/

    private String get_selectedItem_userPhoneNumber(DatabaseReference selectedItem_User_DB_Ref) {
        if (selectedItem_User_DB_Ref == null) {
            return null;
        }
        selectedItem_User_DB_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                item_userPhoneNumber = (String) dataSnapshot.child(PATH_USER_PHONE_NUMBER).getValue();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return item_userPhoneNumber;
    }


    private String get_selectedItem_userEmail(DatabaseReference selectedItem_User_DB_Ref) {
        if (selectedItem_User_DB_Ref == null) {
            return null;
        }
        selectedItem_User_DB_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                item_userEmail = (String) dataSnapshot.child(PATH_USER_EMAIL).getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return item_userEmail;
    }

}













