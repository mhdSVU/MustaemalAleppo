package mohammedyouser.com.mustaemalaleppo.Data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
import java.util.List;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

public class Activity_User_Notifications extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView mRecyclerView_ineed;
    private RecyclerView mRecyclerView_ihave;

    private DatabaseReference db_root;
    private DatabaseReference db_root_userIDs_notifications;
    private DatabaseReference db_root_tokens_notifications;


    private String userID;

    public Bundle bundle;
    public ProgressBar mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__user__notifications);

        setUpToolBar();

        mProgress = findViewById(R.id.progressBar1);
        mProgress.setVisibility(View.VISIBLE);


        mProgress.setIndeterminate(true);
        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        //Define recycleview
        mRecyclerView_ihave = findViewById(R.id.recycler_Expand_ihave);
        mRecyclerView_ineed = findViewById(R.id.recycler_Expand_ineed);
        mRecyclerView_ihave.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView_ineed.setLayoutManager(new LinearLayoutManager(this));

        setUpAuthentication();

        initialDatabaseRefs();

        create_userID_notifications_list(PATH_INEED);
        create_userID_notifications_list(PATH_IHAVE);


    }

    @Override
    protected void onStart() {
        super.onStart();
        //fetching notifications from firebase DB
        fetchUserNotifications();
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

    private void create_userID_notifications_list(String itemKind) {
        Log.d(TAG, "create_userID_tokens_notifications_list: ");
        //fetching Notifications topics and notifications from Users_Tokens
        db_root.child(PATH_USERS_TOKENS).child(userID).child(PATH_TOKENS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_userID_tokens) {
                for (DataSnapshot snapshot_userID_token : snapshot_userID_tokens.getChildren()) {

                    db_root_tokens_notifications.child(snapshot_userID_token.getKey()).child(itemKind)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot_topics) {
                            for (DataSnapshot snapshot_topic : snapshot_topics.getChildren()) {
                                //Adding "Notifications topics" to UsersIDS_Notifications
                                db_root_userIDs_notifications.child(userID).child(itemKind)
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

                                                           db_root_userIDs_notifications.child(userID).child(itemKind).child(snapshot_topic.getKey())
                                                       // db_root_userIDs_notifications.child(userID).child(itemKind).child(snapshot_notification_topic.getKey())
                                                                .child(snapshot_notificationID.getKey()).runTransaction(new Transaction.Handler() {
                                                            @NonNull
                                                            @Override
                                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                                if (currentData.getValue() == null) {
                                                                    //  currentData.setValue(snapshot_notificationID.getValue());
                                                                    currentData.setValue(true);
                                                                    Log.d(TAG, "doTransaction: "+(snapshot_notificationID.getValue()));


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

                                            /*db_root_tokens_notifications.child(snapshot_userID_token.getKey()).child(itemKind)
                                                    .child(snapshot_topic.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {*/



                                            return Transaction.success(currentData);
                                        }


                                        return Transaction.abort();
                                    }

                                    @Override
                                    public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                        if (committed) {
                                            // unique key saved
                                            Log.d(TAG, "onComplete: " + "unique key saved3");
                                        } else {
                                            // unique key already exists
                                            snapshot_topic.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot_notification_topic) {
                                                    for (DataSnapshot snapshot_notificationID : snapshot_notification_topic.getChildren()) {
                                                        db_root_userIDs_notifications.child(userID).child(itemKind).child(snapshot_topic.getKey()).child(snapshot_notificationID.getKey()).runTransaction(new Transaction.Handler() {
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

    private void fetchUserNotifications() {
        db_root_userIDs_notifications.child(userID).child(PATH_INEED).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_notifications_topics) {

                final List<NotificationTopic> notificationTopicList = new ArrayList<>();
                for (final DataSnapshot snapshot_notification_topic : snapshot_notifications_topics.getChildren()) {


                    final String str_notification_topic_key = String.valueOf(snapshot_notification_topic.getKey());
                    String[] topicCityCat_ = str_notification_topic_key.split("_");


                    snapshot_notification_topic.getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot_notification_topic) {
                            final List<Notification> notificationList = new ArrayList<>();
                            for (DataSnapshot snapshot_notification : dataSnapshot_notification_topic.getChildren()) {
                                final String notificationTitle = String.valueOf(snapshot_notification.getKey());

                                String[] topicCityCat_=String.valueOf(snapshot_notification_topic.getKey()).split("_");
                                FirebaseDatabase.getInstance().getReference().child(PATH_ITEMS).child(PATH_INEED)
                                        .child(PATH_ALL_ITEMS).child(String.valueOf(snapshot_notification.getKey())).
                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        notificationList.add(new Notification(PATH_INEED, topicCityCat_[3], topicCityCat_[2],snapshot_notification.getKey(),String.valueOf(snapshot_notification.getValue()),
                                                String.valueOf(snapshot.child(PATH_ITEM_TITLE).getValue()),
                                                String.valueOf(snapshot_notification_topic.getKey()),
                                                String.valueOf(snapshot.child(PATH_ITEM_DATE_AND_TIME).getValue()),
                                                String.valueOf(snapshot.child(PATH_ITEM_USER_NAME).getValue())));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                            notificationTopicList.add(new NotificationTopic( String.valueOf(snapshot_notification_topic.getKey()), notificationList));

                            DocExpandableRecyclerAdapter_Notifications adapter = new DocExpandableRecyclerAdapter_Notifications(notificationTopicList, Activity_User_Notifications.this);

                            mRecyclerView_ineed.setAdapter(adapter);
                            hideProgressBar(mProgress);


                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
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
        db_root_userIDs_notifications.child(userID).child(PATH_IHAVE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_notifications_topics) {

                final List<NotificationTopic> notificationTopicList = new ArrayList<>();
                for (final DataSnapshot snapshot_notification_topic : snapshot_notifications_topics.getChildren()) {


                    final String str_notification_topic_key = String.valueOf(snapshot_notification_topic.getKey());
                    String[] topicCityCat_ = str_notification_topic_key.split("_");


                    snapshot_notification_topic.getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot_notification_topic) {
                            final List<Notification> notificationList = new ArrayList<>();
                            for (DataSnapshot snapshot_notification : dataSnapshot_notification_topic.getChildren()) {
                                final String notificationTitle = String.valueOf(snapshot_notification.getKey());

                                String[] topicCityCat_=String.valueOf(snapshot_notification_topic.getKey()).split("_");
                                FirebaseDatabase.getInstance().getReference().child(PATH_ITEMS).child(PATH_IHAVE)
                                        .child(PATH_ALL_ITEMS).child(String.valueOf(snapshot_notification.getKey())).
                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        notificationList.add(new Notification(PATH_IHAVE, topicCityCat_[3], topicCityCat_[2],snapshot_notification.getKey(),String.valueOf(snapshot_notification.getValue()),
                                                String.valueOf(snapshot.child(PATH_ITEM_TITLE).getValue()),
                                                String.valueOf(snapshot_notification_topic.getKey()),
                                                String.valueOf(snapshot.child(PATH_ITEM_DATE_AND_TIME).getValue()),
                                                String.valueOf(snapshot.child(PATH_ITEM_USER_NAME).getValue())));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                            notificationTopicList.add(new NotificationTopic( String.valueOf(snapshot_notification_topic.getKey()), notificationList));

                            DocExpandableRecyclerAdapter_Notifications adapter = new DocExpandableRecyclerAdapter_Notifications(notificationTopicList, Activity_User_Notifications.this);

                            mRecyclerView_ihave.setAdapter(adapter);
                            hideProgressBar(mProgress);


                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
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


}