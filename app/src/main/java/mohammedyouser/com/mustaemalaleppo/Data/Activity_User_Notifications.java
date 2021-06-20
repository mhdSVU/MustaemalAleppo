package mohammedyouser.com.mustaemalaleppo.Data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Locale;

import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.*;

public class Activity_User_Notifications extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView mRecyclerView_ineed;
    private RecyclerView mRecyclerView_ihave;

    private DatabaseReference db_root;
    private DatabaseReference db_root_userIDs_notifications;
    private DatabaseReference db_root_tokens_notifications;


    private String userID;

    public Bundle bundle;
    public ProgressBar mProgress;
    private TextView m_tv_no_content;
    private String mNotificationTitle;
    private String mNotificationDateTime;
    private String mNotificationUserName;
    private String mNotificationUserImage;
    private Notification mNotification;
    private TextView m_tv_label_notifications_ihave;
    private TextView m_tv_label_notifications_ineed;
    private String[] categoriesData;
    private String[] citiesData;
    private String[] categoriesLocale;
    private String[] citiesLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()));

        setContentView(R.layout.activity__user__notifications);

        setUpToolBar();
        setUpViews();


        setUpAuthentication();

        initialDatabaseRefs();
        initialDataArrays();

        create_userID_notifications_list(PATH_INEED);
        create_userID_notifications_list(PATH_IHAVE);

        fetchUserNotifications(PATH_IHAVE);
        fetchUserNotifications(PATH_INEED);

        updateUI_If_No_Content();
    }

    private void setUpViews() {
        mProgress = findViewById(R.id.progressBar1);
        // mProgress.setVisibility(View.VISIBLE);


        mProgress.setIndeterminate(true);
        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        //Define recycleview
        m_tv_no_content = findViewById(R.id.tv_no_content);
        mRecyclerView_ihave = findViewById(R.id.recycler_Expand_ihave);
        mRecyclerView_ineed = findViewById(R.id.recycler_Expand_ineed);
        mRecyclerView_ihave.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView_ineed.setLayoutManager(new LinearLayoutManager(this));
        m_tv_label_notifications_ihave = findViewById(R.id.tv_label_notifications_ihave);
        m_tv_label_notifications_ineed = findViewById(R.id.tv_label_notifications_ineed);
    }

    private void adjustLanguage(String lan) {
        if (!lan.equals("null")) {

/*
            LocaleHelper.setLocale(this, lan);
*/
            Locale locale = new Locale(lan);
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLayoutDirection(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        create_userID_notifications_list(PATH_IHAVE);
        create_userID_notifications_list(PATH_INEED);
    }

    private void setUpAuthentication() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        }
    }

    private void initialDatabaseRefs() {
        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_userIDs_notifications = db_root.child(PATH_USERIDS_NOTIFICATIONS);
        db_root_tokens_notifications = db_root.child(PATH_TOKENS_NOTIFICATIONS);


    }



    private void create_userID_notifications_list(String itemState) {
        Log.d(TAG, "create_userID_tokens_notifications_list: ");
        //fetching Notifications topics and notifications from Users_Tokens
        db_root.child(PATH_USERSIDs_TOKENS).child(userID).child(PATH_TOKENS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot_userID_tokens) {
                        for (DataSnapshot snapshot_userID_token : snapshot_userID_tokens.getChildren()) {

                            db_root_tokens_notifications.child(snapshot_userID_token.getKey()).child(itemState)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot_topics) {
                                            for (DataSnapshot snapshot_topic : snapshot_topics.getChildren()) {
                                                //Adding "Notifications topics" to UsersIDS_Notifications
                                                db_root_userIDs_notifications.child(userID).child(itemState)
                                                        .child(snapshot_topic.getKey()).runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        if (currentData.getValue() == null) {
                                                            currentData.setValue(true);
                                                            snapshot_topic.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot_notification_topic) {
                                                                    for (DataSnapshot snapshot_notificationID : snapshot_notification_topic.getChildren()) {
                                                                        //Adding "Notifications" to UsersIDS_Notifications


                                                                        db_root_userIDs_notifications.child(userID).child(itemState).child(snapshot_topic.getKey())
                                                                                // db_root_userIDs_notifications.child(userID).child(itemState).child(snapshot_notification_topic.getKey())
                                                                                .child(snapshot_notificationID.getKey()).runTransaction(new Transaction.Handler() {
                                                                            @NonNull
                                                                            @Override
                                                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                                                if (currentData.getValue() == null) {
                                                                                    //  currentData.setValue(snapshot_notificationID.getValue());
                                                                                    currentData.setValue(true);
                                                                                    Log.d(TAG, "doTransaction: " + (snapshot_notificationID.getValue()));


                                                                                    return Transaction.success(currentData);
                                                                                }

                                                                                return Transaction.abort();
                                                                            }

                                                                            @Override
                                                                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                                                                if (committed) {
                                                                                    // unique key saved
                                                                                    Log.d(TAG, "onCompletesdfsdsd: " + "unique key saved 2");
                                                                                } else {
                                                                                    // unique key already exists
                                                                                }

                                                                            }
                                                                        });

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                            return Transaction.success(currentData);
                                                        }


                                                        return Transaction.abort();
                                                    }

                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                                        if (committed) {
                                                            // unique key saved
                                                            Log.d(TAG, "onComplete: " + "unique key saved3");
                                                        } else {
                                                            // unique key already exists
                                                            snapshot_topic.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot_notification_topic) {
                                                                    for (DataSnapshot snapshot_notificationID : snapshot_notification_topic.getChildren()) {
                                                                        db_root_userIDs_notifications.child(userID).child(itemState).child(snapshot_topic.getKey()).child(snapshot_notificationID.getKey()).runTransaction(new Transaction.Handler() {
                                                                            @NonNull
                                                                            @Override
                                                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                                                if (currentData.getValue() == null) {
                                                                                    currentData.setValue(snapshot_notificationID.getValue());


                                                                                    return Transaction.success(currentData);
                                                                                }


                                                                                return Transaction.abort();
                                                                            }

                                                                            @Override
                                                                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                                                                if (committed) {
                                                                                    // unique key saved
                                                                                    Log.d(TAG, "onComplete: " + "unique key saved 1");
                                                                                } else {
                                                                                    // unique key already exists
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                });


                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void fetchUserNotifications(String itemState) {
        //notificationTopicList.clear();
        // notificationList.clear();

        db_root_userIDs_notifications.child(userID).child(itemState)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot_notifications_topics) {

                        final ArrayList<NotificationTopic> notificationTopicList = new ArrayList<>();

                        for (final DataSnapshot snapshot_notification_topic : snapshot_notifications_topics.getChildren()) {
                            final ArrayList<Notification> notificationList = new ArrayList<>();

                            final String topicKey = String.valueOf(snapshot_notification_topic.getKey());

                            snapshot_notification_topic.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot_notification_topic) {
                                    for (DataSnapshot snapshot_notification : snapshot_notification_topic.getChildren()) {
                                        final String notificationKey = String.valueOf(snapshot_notification.getKey());
                                        final String notificationValue = String.valueOf(snapshot_notification.getValue());

                                        String[] topicCityCat_ = String.valueOf(snapshot_notification_topic.getKey()).split("_");
                                        FirebaseDatabase.getInstance().getReference()
                                                .child(PATH_ITEMS).child(itemState).child(PATH_ALL_ITEMS).child(notificationKey).
                                                addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot_item) {

                                                        db_root.child(PATH_USERS).child(String.valueOf(snapshot_item.child(PATH_ITEM_USER_ID).getValue())).
                                                                addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot_userItem) {

                                                                        mNotificationTitle = String.valueOf(snapshot_item.child(PATH_ITEM_TITLE).getValue());
                                                                        mNotificationDateTime = String.valueOf(snapshot_item.child(PATH_ITEM_DATE_AND_TIME_REVERSE).getValue());
                                                                        mNotificationUserName = String.valueOf(snapshot_item.child(PATH_ITEM_USER_NAME).getValue());
                                                                        mNotificationUserImage = String.valueOf(snapshot_userItem.child(PATH_USER_IMAGE).getValue());

                                                                        Log.d(TAG, "onDataChange:1     mNotificationTitle,\n" +
                                                                                "                                                topicKey,\n" +
                                                                                "                                               mNotificationDateTime,\n" +
                                                                                "                                                mNotificationUserName,\n" +
                                                                                "                                             mNotificationUserImage))" + mNotificationTitle + " " + topicKey + " " +
                                                                                mNotificationDateTime + " " +
                                                                                mNotificationUserName + " " +
                                                                                mNotificationUserImage);

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                        mNotification = new Notification(itemState, topicCityCat_[3],
                                                                topicCityCat_[2],
                                                                notificationKey,
                                                                notificationValue,
                                                                String.valueOf(snapshot_item.child(PATH_ITEM_TITLE).getValue()),
                                                                topicKey,
                                                                String.valueOf(snapshot_item.child(PATH_ITEM_DATE_AND_TIME_REVERSE).getValue()),
                                                                String.valueOf(snapshot_item.child(PATH_ITEM_USER_NAME).getValue()),
                                                                String.valueOf(mNotificationUserImage));

                                                        notificationList.add(mNotification);


                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                    }

                                    String[] topicCityCat_ = topicKey.split(getString(R.string.underScore));

                                    notificationTopicList.add(new NotificationTopic(getCategory_locale(topicCityCat_[3] )+ getString(R.string.in) + getCity_locale(topicCityCat_[2]), notificationList));

                                    if (itemState.equals(PATH_INEED)) {
                                        Adapter_ExpandableRecycler__Notifications adapter_ineed = new Adapter_ExpandableRecycler__Notifications(itemState, notificationList, notificationTopicList, Activity_User_Notifications.this, m_tv_label_notifications_ihave, m_tv_label_notifications_ineed, m_tv_no_content);

                                        mRecyclerView_ineed.setAdapter(adapter_ineed);
                                        adapter_ineed.notifyDataSetChanged();

                                    } else {
                                        Adapter_ExpandableRecycler__Notifications adapter_ihave = new Adapter_ExpandableRecycler__Notifications(itemState, notificationList, notificationTopicList, Activity_User_Notifications.this, m_tv_label_notifications_ihave, m_tv_label_notifications_ineed, m_tv_no_content);
                                        mRecyclerView_ihave.setAdapter(adapter_ihave);

                                        adapter_ihave.notifyDataSetChanged();

                                    }
                                    hideProgressBar(mProgress);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Failed to read value
                                    Log.d(TAG, "onCancelled: Failed to read value. " + error.toException());
                                }

                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void setUpToolBar() {

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // myAdapter.stopListening();

    }

    private void updateUI_If_No_Content() {
        db_root_userIDs_notifications.child(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.hasChildren()) {

                                m_tv_no_content.setVisibility(View.GONE);
                                m_tv_label_notifications_ihave.setVisibility(View.VISIBLE);
                                m_tv_label_notifications_ineed.setVisibility(View.VISIBLE);
                                mProgress.setVisibility(View.GONE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private String getCategory_locale(String category) {

        for (int i = 0; i < categoriesData.length; i++) {
            if(categoriesData[i].equals(category))
                return categoriesLocale[i];
        }

        return "";
    }
    private String getCity_locale(String city) {

        for (int i = 0; i < citiesData.length; i++) {
            if(citiesData[i].equals(city))
                return citiesLocale[i];
        }

        return "";
    }

    private void initialDataArrays() {
        categoriesData = get_Categories_array_data(this);
        citiesData = get_Cities_array_data(this);
        categoriesLocale = get_Categories_array_locale(this);
        citiesLocale = get_Cities_array_locale(this);
    }


}