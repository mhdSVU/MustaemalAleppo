package mohammedyouser.com.mustaemalaleppo.Domain;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
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

import mohammedyouser.com.mustaemalaleppo.Data.ViewHolder_Item_Display_Edit;
import mohammedyouser.com.mustaemalaleppo.Device.Activity_Maps_Item_Location;
import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Fragment_Dialog_Report_User;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.adjustLanguage;


public class
Activity_Display_Modify_Remove_Item extends AppCompatActivity implements
        View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {


    private static final String TAG_FRAGMENT_ITEM_REMOVE = "removeItemFragment";
    private static final int REQUEST_READ_PERMISSION_ITEM_SHARE = 700;
    private boolean edit_state = false;
    private File myDir;

    private Uri uri_itemImg;
    private Bitmap bitmap;

    private ViewHolder_Item_Display_Edit viewHolder;
    private TextView m_tv_ItemStateLabel;
    private ImageButton img_btn_itemImage;


    private String userID;
    private String item_ID;
    private String item_city;
    private String item_category;
    private String item_userID;
    private String item_userEmail;
    private String item_user_PhoneNumber;
    private String notificationValue;


    private DatabaseReference item_user_DB_Ref;
    private DatabaseReference item_DB_Ref;
    private DatabaseReference db_ref_final;
    private DatabaseReference db_ref_items;
    private DatabaseReference db_ref_users;
    private DatabaseReference db_ref_currentUser_items;
    private DatabaseReference db_root_usersIDs_notifications;
    private DatabaseReference db_root_tokens_notifications;
    private DatabaseReference db_root;
    private DatabaseReference db_root_users_favorites;


    private StorageReference storageReference_currentUser;

    private ValueEventListener selectedItem_db_ref_listener;
    private ValueEventListener selectedItem_db_ref_listener_img;
    private String str_uri_itemImg;
    private ImageButton img_btn_call;
    private ImageButton img_btn_send_sms;
    private ImageButton img_btn_whatsapp;
    private ImageButton img_btn_share;
    private ImageButton img_btn_report;
    private ImageButton img_btn_save;
    private CheckBox cb_favorite;

    private Button img_btn_show_on_map;
    private ValueEventListener selectedItem_db_ref_listener_itemImage;
    private boolean mItemFound = true;
    private double itemLat;
    private double itemLong;
    private Uri itemImageUriShare;
    private String itemState;
    private TextView mItemUserName;
    private DatabaseReference db_Ref_SelectedItem;
    Long notifications_count = 0L;
    private String item_topic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()), this);

        setContentView(R.layout.activity_display_modify_remove_item);

        doMainInitializations();

    }


    private DatabaseReference reactToIntent(Bundle bundle) {
        //check for nullity
        if (bundle == null) return null;
        if (bundle.containsKey(INTENT_KEY_SOURCE)) {
            db_Ref_SelectedItem = get_SelectedItem_DB_Ref(
                    bundle.getString(INTENT_KEY_ITEM_ID),
                    bundle.getString(INTENT_KEY__PATH_STATE));
            Log.d(TAG, "reactToIntent: " + bundle.getString(INTENT_KEY_ITEM_ID));
            db_Ref_SelectedItem.runTransaction(new Transaction.Handler() {
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
                return null;
            }

            Log.d(TAG, "reactToIntent: INTENT_KEY_SOURCE ");
            populateWithSelectedItem(get_SelectedItem_DB_Ref(
                    bundle.getString(INTENT_KEY_ITEM_ID),
                    bundle.getString(INTENT_KEY__PATH_STATE)));

            decrementUserNotificationsCount(
                    bundle.getString(INTENT_KEY__PATH_STATE),
                    bundle.getString(INTENT_KEY_TOPIC),
                    bundle.getString(INTENT_KEY_NOTIFICATION_ID));
            setNotificationFlagOpened(
                    bundle.getString(INTENT_KEY__PATH_STATE),
                    bundle.getString(INTENT_KEY_TOPIC),
                    bundle.getString(INTENT_KEY_NOTIFICATION_ID));

        } else {
            db_Ref_SelectedItem = get_SelectedItem_DB_Ref(
                    bundle.getString(INTENT_KEY_ITEM_ID),
                    bundle.getString(INTENT_KEY__PATH_STATE));
            populateWithSelectedItem(db_Ref_SelectedItem);
        }

        return db_Ref_SelectedItem;
    }

    private void update_UI_no_content() {
        findViewById(R.id.fl_main_subscription).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_no_content).setVisibility(View.VISIBLE);
        Log.d(TAG, "update_UI_no_content: ");

    }

    private void setNotificationFlagOpened(String state, String topicID, String notificationID) {
        db_root_usersIDs_notifications.child(userID).child(state).child(topicID).child(notificationID).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() != null) {
                    currentData.setValue(false);
                    Log.d(TAG, "doTransaction:1 " + notifications_count);

                    db_root_usersIDs_notifications.child(userID).child(PATH_NOTIFICATIONS_COUNT).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            if (currentData.getValue() != null) {
                                Log.d(TAG, "doTransaction:2 " + notifications_count);

                                notifications_count = currentData.getValue(Long.class);
                                if (notifications_count != null) {
                                    notifications_count = notifications_count - 1;
                                } else {
                                    notifications_count = 0L;

                                }
                                currentData.setValue(notifications_count);
                                Log.d(TAG, "doTransaction:3 " + notifications_count);
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

    private void decreaseCount(String notificationValue) {
        if (notificationValue.equals("true")) {
            db_root.child(PATH_USERS).child(userID).child(PATH_NOTIFICATIONS_COUNT).runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Integer notifications_count;
                    if (currentData.getValue() == null) {
                        return Transaction.success(currentData);
                    }

                    notifications_count = currentData.getValue(Integer.class);
                    currentData.setValue(--notifications_count);

                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @com.google.firebase.database.annotations.Nullable DataSnapshot currentData) {
                    if (committed) {
                        // unique key saved
                        Log.d("TAG", "onComplete: " + "unique key saved3");
                    } else {
                        // unique key already exists
                    }
                }
            });
        }

    }

    private void

    decrementUserNotificationsCount(String state, String topic, String notificationID) {
        db_root_usersIDs_notifications.child(userID).child(state).child(topic).child(notificationID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationValue = String.valueOf(snapshot.getValue());
                        decreaseCount(notificationValue);
                        Log.d(TAG, "notificationValue: " + notificationValue);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void populateWithSelectedItem(DatabaseReference selectedItem_db_ref) {

        findViewById(R.id.fl_main_subscription).setVisibility(View.GONE);
        findViewById(R.id.tv_no_content).setVisibility(View.GONE);

        setUpViews();
        str_uri_itemImg = get_ItemImage_finalFilePath();

        if (selectedItem_db_ref != null) {

            selectedItem_db_ref_listener = selectedItem_db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    viewHolder.setItemTitle((String) dataSnapshot.child(PATH_ITEM_TITLE).getValue());
                    viewHolder.setItemPrice((String) dataSnapshot.child(PATH_ITEM_PRICE).getValue());
                    viewHolder.setItemCity((String) dataSnapshot.child(PATH_ITEM_CITY).getValue());
                    viewHolder.setItemCategory((String) dataSnapshot.child(PATH_ITEM_CATEGORY).getValue());

                    if (!String.valueOf(dataSnapshot.child(PATH_ITEM_DATE_AND_TIME_REVERSE).getValue()).equals("null")) {
                        viewHolder.setItemDateAndTime(TimeAgo.getTimeAgo(-(dataSnapshot.child(PATH_ITEM_DATE_AND_TIME_REVERSE).getValue(Long.class)), Activity_Display_Modify_Remove_Item.this));
                    }

                    if (!String.valueOf(dataSnapshot.child(PATH_ITEM_DETAILS).getValue()).equals("null")) {
                        viewHolder.setItemDetails((String) dataSnapshot.child(PATH_ITEM_DETAILS).getValue());
                    } else {
                        viewHolder.setItemDetails(getString(R.string.tv_details_default_val));
                    }




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
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                viewHolder.setUserName((String) dataSnapshot.child(PATH_USER_NAME).getValue());
                                viewHolder.setUserImage(getBaseContext(), (String) dataSnapshot.child(PATH_USER_IMAGE).getValue());

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            selectedItem_db_ref_listener_img = selectedItem_db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PATH_ITEM_IMAGE).getValue() != null) {
                        Uri uri_itemImg = Uri.parse((String) dataSnapshot.child(PATH_ITEM_IMAGE).getValue());
                        Glide.with(getBaseContext())
                                .load(uri_itemImg).override(img_btn_itemImage.getWidth())
                                .into(img_btn_itemImage);
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void openItemImage(DatabaseReference selectedItem_db_ref) {
        if (selectedItem_db_ref != null) {

            selectedItem_db_ref_listener_itemImage = selectedItem_db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    openItemImageInGallery(String.valueOf(dataSnapshot.child(PATH_ITEM_IMAGE).getValue()));
                    selectedItem_db_ref.removeEventListener(selectedItem_db_ref_listener_itemImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
        reactToIntent(getIntent().getExtras());
        initializeDatabaseRefs();
        initializeStorageRef();
        initialize_item_loc_value();
        setUpToolBar();
        initialize_item_state_label_value(db_Ref_SelectedItem);
        initialFavoriteCheckBox(cb_favorite, item_ID, itemState);

    }

    private void initialize_item_state_label_value(DatabaseReference db_Ref_SelectedItem) {
        mItemUserName = findViewById(R.id.textView_username);
        m_tv_ItemStateLabel = findViewById(R.id.tv_item_state_lable);

        db_Ref_SelectedItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                db_ref_users.child(String.valueOf(dataSnapshot.child(PATH_ITEM_USER_ID).getValue())).child(PATH_USER_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot_userName) {
                        if (itemState.equals(PATH_IHAVE)) {

                            m_tv_ItemStateLabel.setText(getString(R.string.item_share_state_ihave, String.valueOf(snapshot_userName.getValue())));


                        } else {
                            m_tv_ItemStateLabel.setText(getString(R.string.item_share_state_ineed, String.valueOf(snapshot_userName.getValue())));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void initialize_item_loc_value() {
        db_Ref_SelectedItem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue()).equals("0")) ||
                        (String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue()).equals("0"))
                        || ((String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue()).equals("null")) ||
                        (String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue()).equals("null")))) {
                    //  Toast.makeText(getBaseContext(), getString(R.string.message_error_item_no_address), Toast.LENGTH_SHORT).show();
                    return;
                }
                if ((String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue()).equals("null")) || (String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue())).equals("null")) {
                    //  Toast.makeText(getBaseContext(), getString(R.string.message_error_item_no_address), Toast.LENGTH_SHORT).show();
                    return;
                }

                itemLat = Double.parseDouble(String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue()));
                itemLong = Double.parseDouble(String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initialize_User_LocalDir() {

        String root = String.valueOf(getFilesDir());

        if (is_IHAVE_State())
            myDir = new File(root + "/Pictures/" + getString(R.string.my_app_name) + getString(R.string.forward_slash) + PATH_IHAVE);

        else if (!(is_IHAVE_State())) {
            myDir = new File(root + "/Pictures/" + getString(R.string.my_app_name) + getString(R.string.forward_slash) + PATH_INEED);


        }

        if (!(myDir.exists()))
            myDir.mkdirs();


    }

    private void setUpToolBar() {

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setUpViews() {
        View mRootView = findViewById(R.id.rootView);
        viewHolder = new ViewHolder_Item_Display_Edit(mRootView, this);

        //setUpLabel(item_State);

        if (item_userID != null) {
            item_user_DB_Ref = db_ref_users.child(item_userID);
            get_selectedItem_userPhoneNumber(item_user_DB_Ref);
            get_selectedItem_userEmail(item_user_DB_Ref);

        }

        img_btn_call = findViewById(R.id.imageButton_user_phoneCall);
        img_btn_send_sms = findViewById(R.id.imageButton_user_send_sms);
        img_btn_whatsapp = findViewById(R.id.imageButton_user_whatsapp);
        img_btn_share = findViewById(R.id.img_btn_item_share);
        img_btn_report = findViewById(R.id.img_btn_item_report);
        img_btn_save = findViewById(R.id.img_btn_item_save);
        cb_favorite = findViewById(R.id.cb_favorite);
        img_btn_itemImage = findViewById(R.id.img_btn_itemImage);
        img_btn_show_on_map = findViewById(R.id.img_btn_show_on_map);

        img_btn_call.setOnClickListener(this);
        img_btn_send_sms.setOnClickListener(this);
        img_btn_itemImage.setOnClickListener(this);
        img_btn_whatsapp.setOnClickListener(this);
        img_btn_share.setOnClickListener(this);
        img_btn_report.setOnClickListener(this);
        img_btn_save.setOnClickListener(this);
        cb_favorite.setOnClickListener(this);
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

        db_root_usersIDs_notifications = db_root.child(PATH_USERIDS_NOTIFICATIONS);
        db_root_tokens_notifications = db_root.child(PATH_TOKENS_NOTIFICATIONS);
        db_root_users_favorites = db_root.child(PATH_USERS_FAVORITES);


        item_ID = getItemID();

        final String item_State = getIntent().getExtras().getString(INTENT_KEY__PATH_STATE);
        final String item_Category = getIntent().getExtras().getString(INTENT_KEY_ITEM_CAT);
        final String item_City = getIntent().getExtras().getString(INTENT_KEY_ITEM_CITY);


        //setUpLabel(item_State);

        if (item_ID != null && item_State != null && item_Category != null) {

            // getting the DB_Ref of the "selectedItem"  by providing the path data i.e. item_ID,item_State,item_City and item_Category.
            item_DB_Ref = get_SelectedItem_DB_Ref(item_ID, item_State);

        }
        itemState = getIntent().getExtras().getString(INTENT_KEY__PATH_STATE);

        item_topic = form_selected_Topic(itemState, item_City, item_Category);
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
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(INTENT_KEY_ITEM_ID))
                Log.d(TAG, "getItemID: " + getIntent().getExtras().getString(INTENT_KEY_ITEM_ID));
            return getIntent().getExtras().getString(INTENT_KEY_ITEM_ID);
        }
        return null;
    }

// Initialization end
    //

    ///////////////////////////////////////////////////////////////////
//...getting DB references start
    private DatabaseReference getDB_Ref_selected_Cit_Cat() {


        if (getIntent() != null && getIntent().getExtras() != null) {

            if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_INEED)) {

                db_ref_final = db_ref_items.child(PATH_INEED).child(item_city).child(item_category);


            } else if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_IHAVE)) {


                db_ref_final = db_ref_items.child(PATH_IHAVE).child(item_city).child(item_category);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_AllCities() {

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_INEED)) {

                db_ref_final = db_ref_items.child(PATH_INEED).child(PATH_ALL_CITIES).child(item_category);


            } else if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_IHAVE)) {


                db_ref_final = db_ref_items.child(PATH_IHAVE).child(PATH_ALL_CITIES).child(item_category);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_AllCategories() {


        if (getIntent() != null && getIntent().getExtras() != null) {
            if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_INEED)) {

                db_ref_final = db_ref_items.child(PATH_INEED).child(item_city).child(PATH_ALL_CATEGORIES);


            } else if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_IHAVE)) {


                db_ref_final = db_ref_items.child(PATH_IHAVE).child(item_city).child(PATH_ALL_CATEGORIES);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_AlItems() {


        if (getIntent() != null && getIntent().getExtras() != null) {
            if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_INEED)) {


                db_ref_final = db_ref_items.child(PATH_INEED).child(PATH_ALL_ITEMS);
                Log.d(TAG, "getDB_Ref_AlItems 11: " + db_ref_final);


            } else if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_IHAVE)) {


                db_ref_final = db_ref_items.child(PATH_IHAVE).child(PATH_ALL_ITEMS);
                Log.d(TAG, "getDB_Ref_AlItems 22: " + db_ref_final);


            }
        }
        Log.d(TAG, "getDB_Ref_AlItems: 33 " + db_ref_final);

        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_CurrentUser_selected_Cit_Cat() {


        if (getIntent() != null && getIntent().getExtras() != null) {
            if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_INEED)) {

                db_ref_final = db_ref_currentUser_items.child(PATH_INEED).child(item_city).child(item_category).child(PATH_USERS_IDS).child(userID);


            } else if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_IHAVE)) {


                db_ref_final = db_ref_currentUser_items.child(PATH_IHAVE).child(item_city).child(item_category).child(PATH_USERS_IDS).child(userID);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_CurrentUser_AllCities() {

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_INEED)) {

                db_ref_final = db_ref_currentUser_items.child(PATH_INEED).child(PATH_ALL_CITIES).child(item_category).child(PATH_USERS_IDS).child(userID);


            } else if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_IHAVE)) {


                db_ref_final = db_ref_currentUser_items.child(PATH_IHAVE).child(PATH_ALL_CITIES).child(item_category).child(PATH_USERS_IDS).child(userID);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_CurrentUser_AllCategories() {


        if (getIntent() != null && getIntent().getExtras() != null) {
            if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_INEED)) {

                db_ref_final = db_ref_currentUser_items.child(PATH_INEED).child(item_city).child(PATH_ALL_CATEGORIES).child(PATH_USERS_IDS).child(userID);


            } else if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_IHAVE)) {


                db_ref_final = db_ref_currentUser_items.child(PATH_IHAVE).child(item_city).child(PATH_ALL_CATEGORIES).child(PATH_USERS_IDS).child(userID);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference getDB_Ref_CurrentUser_AllItems() {


        if (getIntent() != null && getIntent().getExtras() != null) {
            if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_INEED)) {

                db_ref_final = db_ref_currentUser_items.child(PATH_INEED).child(PATH_ALL_ITEMS).child(PATH_USERS_IDS).child(userID);


            } else if (Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_IHAVE)) {


                db_ref_final = db_ref_currentUser_items.child(PATH_IHAVE).child(PATH_ALL_ITEMS).child(PATH_USERS_IDS).child(userID);


            }
        }
        return db_ref_final;
    }

    private DatabaseReference get_SelectedItem_DB_Ref(String item_ID, String item_State) {

        db_root = FirebaseDatabase.getInstance().getReference();
        db_ref_items = db_root.child(PATH_ITEMS);
        db_root_usersIDs_notifications = db_root.child(PATH_USERIDS_NOTIFICATIONS);

        return item_DB_Ref = db_ref_items.child(item_State).child(PATH_ALL_ITEMS).child(item_ID);

    }

    //...getting DB references end
//

    //...getting Storage references start
//

    private StorageReference getImgStorageRef() {

        return storageReference_currentUser.child(PATH_STORAGE_USERS_PICTURES).child(userID).child(Objects.requireNonNull(getItemID()));
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
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION_ITEM_OPEN_IMG);
            } else {
                upload_itemImg_and_data();
            }
        } else {
            upload_itemImg_and_data();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_PERMISSION_ITEM_MODIFY: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms", "perms granted");
                    
                    upload_itemImg_and_data();

                } else {
                    Log.d("perms", "perms denied");
                    Toast.makeText(this, "Permissions Needed!", Toast.LENGTH_SHORT).show();


                }
            }
            break;
            case REQUEST_READ_PERMISSION_ITEM_OPEN_IMG: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms", "perms granted");

                    openItemImage(item_DB_Ref);

                } else {
                    Log.d("perms", "perms denied");
                                       Toast.makeText(this, "Permissions Needed!", Toast.LENGTH_SHORT).show();



                }
            }
            break;
            case REQUEST_READ_PERMISSION_ITEM_SHARE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms", "perms granted");

                    shareItemImage(get_Item_Description_for_Share(itemState));


                } else {
                    Log.d("perms", "perms denied");
                                       Toast.makeText(this, "Permissions Needed!", Toast.LENGTH_SHORT).show();



                }
            }
            break;
            case REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms", "perms granted");
                    showItemOnMap();

                } else {
                    Log.d("perms", "perms denied");
                                       Toast.makeText(this, "Permissions Needed!", Toast.LENGTH_SHORT).show();



                }
            }
            break;
        }
   /*     if (requestCode == REQUEST_READ_PERMISSION_ITEM_MODIFY &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("perms", "perms granted");

            upload_itemImg_and_data();

        } else if (requestCode == REQUEST_READ_PERMISSION_ITEM_OPEN_IMG &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("perms", "perms granted");

            openItemImage(item_DB_Ref);

        } else if (requestCode == REQUEST_READ_PERMISSION_ITEM_SHARE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("perms", "perms granted");

            shareItemImage(get_Item_Description_for_Share(itemState));

        } else if (requestCode == REQUEST_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("perms", "perms granted");

            showItemOnMap();

        } else if (requestCode == REQUEST_LOCATION &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            Log.d("perms", "perms granted");

            Toast.makeText(this, "Location permission needed!", Toast.LENGTH_SHORT).show();

        }*/
       /* else {
            Log.d("perms", "perms not granted");

            Toast.makeText(Activity_Display_Modify_Remove_Item.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();

        }*/
    }


    private void upload_itemImg_and_data() {
        if (uri_itemImg != null) {
            item_DB_Ref.removeEventListener(selectedItem_db_ref_listener_img);
            showProgressDialog(this, getString(R.string.message_info_MODIFYING), getString(R.string.message_info_PLEASE_WAIT));

            getImgStorageRef().putFile(uri_itemImg)
                    .addOnSuccessListener(taskSnapshot -> getImgStorageRef().getDownloadUrl().addOnSuccessListener(this::manage_modifyItem_state))
                    .addOnFailureListener(e ->
                            Toast.makeText(Activity_Display_Modify_Remove_Item.this, getResources().getString(R.string.message_info_modifing_error) + getString(R.string.new_line) + e.toString(), Toast.LENGTH_LONG).show())
                    .addOnCompleteListener(task -> hideProgressDialog());
        } else {
            manage_modifyItem_state(null);
        }


    }


    //Entry point modification method
    public void manage_modifyItem_state(Uri uri_itemImg_download) {// main method for 'save' modification button
        itemImageUriShare = uri_itemImg_download;
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

        //Removing Item from "ALL Cities and ALL Categories"
        removeItem_from_single_DB_Ref(getDB_Ref_AlItems().child(item_ID));

        //Removing Item from "ALL Cities and ALL Categories belong to current user"
        removeItem_from_single_DB_Ref(getDB_Ref_CurrentUser_AllItems().child(item_ID));


        hideProgressDialog();
        finish();


    }

    public void removeItem_from_all_DB_Ref() {
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


        Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ITEM_REMOVE);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }


        Fragment_Dialog_RemoveItem_Alert removeItemAlertDialogFragment = new Fragment_Dialog_RemoveItem_Alert();
        removeItemAlertDialogFragment.show(fragmentManager, TAG_FRAGMENT_ITEM_REMOVE);


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
        Technique.PULSE.playOn(v);
        switch (v.getId()) {
            case R.id.imageButton_user_phoneCall: {
                String phoneNumber = get_selectedItem_userPhoneNumber(item_user_DB_Ref);


                if (phoneNumber != null) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                            getString(R.string.scheme), phoneNumber, null));
                    startActivity(phoneIntent);
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

                String subject = String.valueOf(((EditText) viewHolder.itemView.findViewById(R.id.et_item_title)).getText());
                String content = getString(R.string.default_val_content_email_to_item_user, subject);
                String str_msg_sms = getString(R.string.title_email_to_item_user) + content;


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + get_selectedItem_userPhoneNumber(item_user_DB_Ref)));
                intent.putExtra("sms_body", str_msg_sms);
                startActivity(intent);

            }
            break;
            case R.id.imageButton_user_whatsapp: {

                String subject = String.valueOf(((EditText) viewHolder.itemView.findViewById(R.id.et_item_title)).getText());
                String str_msg_watsapp_ = getString(R.string.title_email_to_item_user) + getString(R.string.default_val_content_email_to_item_user, subject);
                contact_via_Whatsapp(get_selectedItem_userPhoneNumber(item_user_DB_Ref), str_msg_watsapp_);

            }
            break;
            case R.id.img_btn_item_share: {

                manageShareItemImagePerm();
            }
            break;
            case R.id.cb_favorite: {

                add_to_favorite(v, item_ID, itemState);
            }
            break;
            case R.id.img_btn_item_report: {

                confirm_report_user(item_userID);
            }
            break;
            case R.id.img_btn_item_save: {

                saveItem();
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
                manage_ShowMap_Perm();

                break;

        }


    }

    private void showItemOnMap() {
        Technique.PULSE.playOn(img_btn_show_on_map);
        item_DB_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Intent intent = new Intent(Activity_Display_Modify_Remove_Item.this, Activity_Maps_Item_Location.class);
                if ((String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue()).equals("0")) ||
                        (String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue()).equals("0"))
                        || ((String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue()).equals("null")) ||
                        (String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue()).equals("null")))) {
                    Toast.makeText(getBaseContext(), getString(R.string.message_error_item_no_address), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Objects.equals(String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue()), ("null")) || (Objects.equals(String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue()), ("null")))) {
                    Toast.makeText(getBaseContext(), getString(R.string.message_error_item_no_address), Toast.LENGTH_SHORT).show();
                    return;
                }

                itemLat = Double.parseDouble(String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue()));
                itemLong = Double.parseDouble(String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue()));

                intent.putExtra(INTENT_KEY_ITEM_LAT, Double.parseDouble(String.valueOf(snapshot.child(PATH_ITEM_LAT).getValue())));
                intent.putExtra(INTENT_KEY_ITEM_LONG, Double.parseDouble(String.valueOf(snapshot.child(PATH_ITEM_LONG).getValue())));
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveItem() {
        saveItemImageToGallery(
                get_ScaledBitmap_from_drawable(img_btn_itemImage.getDrawable()),
                viewHolder.getItemTitle() + getString(R.string.underScore) + viewHolder.getItemPrice(),
                viewHolder.getItemDetails());
    }

    private void initialFavoriteCheckBox(View view, String item_ID, String itemState) {

        getFavoriteItemRef(item_ID, itemState).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null) {
                    ((CheckBox) view).setChecked(false);
                    Log.d(TAG, "doTransaction: null");
                    return Transaction.success(currentData);
                }

                ((CheckBox) view).setChecked(true);
                Log.d(TAG, "doTransaction:not null");

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    // unique key saved
                    Log.d(TAG, "onComplete: " + "unique key saved");
                } else {
                    // unique key already exists
                }
            }
        });

    }

    private void add_to_favorite(View view, String item_ID, String itemState) {

        getFavoriteItemRef(item_ID, itemState).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(true);
                    ((CheckBox) view).setChecked(true);
                    Log.d(TAG, "doTransaction: set");
                    return Transaction.success(currentData);
                }

                ((CheckBox) view).setChecked(false);
                getFavoriteItemRef(item_ID, itemState).removeValue();
                Log.d(TAG, "doTransaction: remove");

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    // unique key saved
                    Log.d(TAG, "onComplete: " + "unique key saved");
                } else {
                    // unique key already exists
                }
            }
        });

    }

    private DatabaseReference getFavoriteItemRef(String item_ID, String itemState) {
        return db_root_users_favorites.child(userID).child(itemState).child(item_topic).child(item_ID);
    }

    private String form_selected_Topic(String itemState, String itemCity, String itemCategory) {

        // In cloud: topic="Items"+"_"+itemKind+"_"+itemCity+"_"+itemCategory;
        String topic = "Items" + "_" + itemState + "_" + itemCity + "_" + itemCategory;
        Log.d("TAG", "generatedTopic: " + topic);
        return topic;
    }

    private String form_item_fileName(String itemState, String itemTitle, String itemPrice) {
        return itemState + "_" + itemTitle + "_" + itemPrice;
    }


    private String get_Item_Description_for_Share(String itemState) {
        String[] s = String.valueOf(m_tv_ItemStateLabel.getText()).split(itemState);
        String uri_loc = "http://maps.google.com/maps?saddr=Current%20Location&daddr=" + itemLat + "," + itemLong;
        return getString(R.string.item_share_intro) + " " +

                s[0] + " " +

                "\n" +
                getString(R.string.in) + " " + item_city + ": " +
                "\n" + ((EditText) viewHolder.itemView.findViewById(R.id.et_item_title)).getText() +
                "\n" +
                getString(R.string.item_share_price) + " " + ((EditText) viewHolder.itemView.findViewById(R.id.et_item_price)).getText() +
                "\n" +
                getString(R.string.item_share_loc) + " " + uri_loc +
                "\n\n" +
                getString(R.string.item_share_call) + "\n" + item_user_PhoneNumber;
    }


    private void contact_via_Whatsapp(String item_userPhoneNumber, String item_data) {


        try {
            PackageManager packageManager = getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + item_userPhoneNumber + "&text=" + URLEncoder.encode(item_data, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
            Log.e("ERROR WHATSAPP", e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
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
                uri_itemImg = result.getUri();
                setItemImage(uri_itemImg);
                img_btn_itemImage.layout(0, 0, 0, 0);
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
        getMenuInflater().inflate(R.menu.menu_manage_item, menu);

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


        }

        return true;
    }

    private void confirm_report_user(String item_userID) {
        Log.d(TAG, "confirm_report_user: A " + item_userID);
        Fragment_Dialog_Report_User fragment_dialog_report_user = new Fragment_Dialog_Report_User();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_ITEM_USER_ID, item_userID);
        fragment_dialog_report_user.setItem_userID(item_userID);
        showDialogFragment(fragment_dialog_report_user, "frg_report_user");
        getSupportFragmentManager().setFragmentResultListener(REQUEST_KEY_REPORT, this, (requestKey, result) -> {
            if (result.getBoolean(BUNDLE_KEY_REPORT)) {
                Toast.makeText(this, getString(R.string.message_info_report_user), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDialogFragment(DialogFragment newFragment, String tag) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        androidx.fragment.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        // save transaction to the back stack
        ft.addToBackStack("dialog");
        newFragment.show(ft, tag);
        getSupportFragmentManager().executePendingTransactions();
    }

    private void saveItemImageToGallery(Bitmap scaledBitmap, String itemImage_fileName, String description) {
        MediaStore.Images.Media.insertImage(getContentResolver(), scaledBitmap, itemImage_fileName, description);
        Toast.makeText(this, getString(R.string.message_info_item_saved_ok), Toast.LENGTH_LONG).show();
        Log.d(TAG, "saveItemImageToGallery: ");

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
/*
        outState.putString(INTENT_KEY_ITEM_IMG_URI, String.valueOf(uri_itemImg));
*/
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
  /*      if (savedInstanceState.containsKey(INTENT_KEY_ITEM_IMG_URI)) {
            uri_itemImg = Uri.parse(savedInstanceState.getString(INTENT_KEY_ITEM_IMG_URI));
            setImage_circle(this, uri_itemImg, 0.3f, img_btn_itemImage);
        }*/
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
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION_ITEM_MODIFY);
            } else {

                openItemImage(item_DB_Ref);

            }
        } else {
            openItemImage(item_DB_Ref);

        }
    }

    private void manage_ShowMap_Perm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {

                showItemOnMap();

            }
        } else {
            showItemOnMap();

        }
    }

    private void manageShareItemImagePerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION_ITEM_SHARE);
            } else {
                shareItemImage(get_Item_Description_for_Share(itemState));
            }
        } else {
            shareItemImage(get_Item_Description_for_Share(itemState));

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

    public File get_ItemImage_finalFilePath_for_Share() {

        File itemImageFile = create_ItemImage_File();
        if (itemImageFile.exists()) {
            return itemImageFile;
        }

        Bitmap scaledBitmap = get_ScaledBitmap_from_drawable(img_btn_itemImage.getDrawable());
        if (scaledBitmap == null) return null;

        compressBitmap(itemImageFile, scaledBitmap);
        return itemImageFile;
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
            return Bitmap.createScaledBitmap(bitmap, 640, 480, true);

        }
        return bitmap;
    }

    private File create_ItemImage_File() {
        final String item_ID = Objects.requireNonNull(getIntent().getExtras()).getString(INTENT_KEY_ITEM_ID);
        String itemImage_fileName = viewHolder.getItemTitle() +
                getString(R.string.underScore) + viewHolder.getItemPrice() + getString(R.string.underScore) + item_ID + ".jpg";
        return new File(myDir, itemImage_fileName);
    }

    public void openItemImageInGallery(String itemImage_fileName) {

        try {
            if (itemImage_fileName == null) {
                Toast.makeText(this, getString(R.string.message_info_error_no_image), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(itemImage_fileName), "image/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

        } catch (
                Throwable throwable) {
            Log.d(TAG, "openItemImageInGallery: " + throwable.getMessage());

        }

    }


    public void shareItemImage(String textToShare) {
        if (get_ItemImage_finalFilePath_for_Share() == null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_TEXT, textToShare);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            startActivity(Intent.createChooser(intent, getString(R.string.item_share_chooser_title)));
        } else {
            Uri fileURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", get_ItemImage_finalFilePath_for_Share());
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, fileURI);
            intent.putExtra(Intent.EXTRA_TEXT, textToShare);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            startActivity(Intent.createChooser(intent, getString(R.string.item_share_chooser_title)));
        }


       /* try {

            // if (itemImage_FileName.exists()) {
            if (uri_itemImg != null) {

                // Uri fileURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", itemImage_FileName);

                //     Log.d(TAG, "shareItemImage: " + fileURI);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri_itemImg);
                intent.putExtra(Intent.EXTRA_TEXT, textToShare);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                startActivity(Intent.createChooser(intent, getString(R.string.item_share_chooser_title)));


            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT, textToShare);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(Intent.createChooser(intent, getString(R.string.item_share_chooser_title)));
            }
        } catch (Throwable throwable) {
            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();

        } finally {}*/


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

        viewHolder.itemView.findViewById(R.id.et_item_price).setBackground(ContextCompat.getDrawable(Activity_Display_Modify_Remove_Item.this, R.drawable.background_edit_text_enabled));
        viewHolder.itemView.findViewById(R.id.et_item_title).setBackground(ContextCompat.getDrawable(Activity_Display_Modify_Remove_Item.this, R.drawable.background_edit_text_enabled));
        viewHolder.itemView.findViewById(R.id.et_item_details).setBackground(ContextCompat.getDrawable(Activity_Display_Modify_Remove_Item.this, R.drawable.background_edit_text_enabled));

        viewHolder.itemView.findViewById(R.id.tv_item_update_img).setVisibility(View.VISIBLE);
        viewHolder.itemView.findViewById(R.id.tv_item_update_img).setBackground(ContextCompat.getDrawable(Activity_Display_Modify_Remove_Item.this, R.drawable.background_edit_text_enabled));


    }

    private void hideModifyWidgets() {

        edit_state = false;
        viewHolder.itemView.findViewById(R.id.ll_item_modify).setVisibility(View.INVISIBLE);

        viewHolder.itemView.findViewById(R.id.et_item_price).setEnabled(false);
        viewHolder.itemView.findViewById(R.id.et_item_title).setEnabled(false);
        viewHolder.itemView.findViewById(R.id.et_item_details).setEnabled(false);

        viewHolder.itemView.findViewById(R.id.et_item_price).setBackground(ContextCompat.getDrawable(Activity_Display_Modify_Remove_Item.this, R.drawable.background_tv));
        viewHolder.itemView.findViewById(R.id.et_item_title).setBackground(ContextCompat.getDrawable(Activity_Display_Modify_Remove_Item.this, R.drawable.background_tv));
        viewHolder.itemView.findViewById(R.id.et_item_details).setBackground(ContextCompat.getDrawable(Activity_Display_Modify_Remove_Item.this, R.drawable.background_input));
        img_btn_itemImage.setBackground(ContextCompat.getDrawable(Activity_Display_Modify_Remove_Item.this, R.drawable.background_edit_text_disabled));

        viewHolder.itemView.findViewById(R.id.tv_item_update_img).setVisibility(View.INVISIBLE);


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

    public boolean is_IHAVE_State() {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(INTENT_KEY__STATE)) {
            return Objects.equals(getIntent().getExtras().getString(INTENT_KEY__STATE), INTENT_VALUE__STATE_IHAVE);
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                item_user_PhoneNumber = (String) dataSnapshot.child(PATH_USER_PHONE_NUMBER).getValue();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return item_user_PhoneNumber;
    }


    private String get_selectedItem_userEmail(DatabaseReference selectedItem_User_DB_Ref) {
        if (selectedItem_User_DB_Ref == null) {
            return null;
        }
        selectedItem_User_DB_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                item_userEmail = (String) dataSnapshot.child(PATH_USER_EMAIL).getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return item_userEmail;
    }

}













