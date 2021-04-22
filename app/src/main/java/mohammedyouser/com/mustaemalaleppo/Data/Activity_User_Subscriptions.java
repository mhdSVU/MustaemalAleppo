package mohammedyouser.com.mustaemalaleppo.Data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
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

import mohammedyouser.com.mustaemalaleppo.ItemViewModel;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

public class Activity_User_Subscriptions extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView mRecyclerView_ineed;
    private RecyclerView mRecyclerView_ihave;

    private DatabaseReference db_root_items;
    private DatabaseReference db_root_items_users;
    private DatabaseReference db_root_items_user_topics;
    private DatabaseReference db_root;
    private DatabaseReference db_root_userIDs_notifications;
    private DatabaseReference db_root_tokens_notifications;


    private String userID;
    public Bundle bundle;
    public ProgressBar mProgress;

    private ItemViewModel viewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__user__subscriptions);
        setUpToolBar();

        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);


        mProgress = findViewById(R.id.progressBar1);
        mProgress.setVisibility(View.VISIBLE);


        mProgress.setIndeterminate(true);
        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        setUpAuthentication();

        initialDatabaseRefs();

        create_userID_tokens_notifications_list(PATH_INEED);
        create_userID_tokens_notifications_list(PATH_IHAVE);

        //Define recycleview
        mRecyclerView_ihave = findViewById(R.id.recycler_Expand_ihave);
        mRecyclerView_ineed = findViewById(R.id.recycler_Expand_ineed);
        mRecyclerView_ihave.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView_ineed.setLayoutManager(new LinearLayoutManager(this));


        //reading data from firebase
        db_root_items_user_topics.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<TopicMainCategory> topicMainCategoryList = new ArrayList<>();
                for (final DataSnapshot topicCategory_snapshot : dataSnapshot.getChildren()) {

                    final String topicCategory_key = String.valueOf(topicCategory_snapshot.getKey());

                    topicCategory_snapshot.getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final List<Topic> topicList = new ArrayList<>();
                            //numberOnline = 0;

                            for (DataSnapshot topic : dataSnapshot.getChildren()) {
                                final String topicTitle = String.valueOf(topic.getKey());

                                String[] topicCityCat_ = topicTitle.split("_");

                                topicList.add(new Topic(topicCityCat_[3] + " in " + topicCityCat_[2],topicCategory_key));
                                Log.d(TAG, "onDataChange: "+topicCategory_key);

                            }

                            if (topicCategory_key.equals(PATH_INEED)) {
                                topicMainCategoryList.add(new TopicMainCategory("My Subscriptions for ordered items:", topicList));

                            } else {
                                topicMainCategoryList.add(new TopicMainCategory("My Subscriptions for available items:", topicList));

                            }


                            DocExpandableRecyclerAdapter_Subscriptions adapter = new DocExpandableRecyclerAdapter_Subscriptions(topicMainCategoryList, Activity_User_Subscriptions.this);

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
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to read value. " + databaseError.getMessage());


            }
        });
    }


    private void setUpAuthentication() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        }
    }

    private void initialDatabaseRefs() {
        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_items = db_root.child(PATH_ITEMS);
        db_root_items_users = db_root.child(PATH_USER_ITEMS);

        db_root_items_user_topics = db_root.child(PATH_USERS_TOPICS).child(userID);

        db_root_userIDs_notifications = db_root.child(PATH_USERIDS_NOTIFICATIONS);
        db_root_tokens_notifications = db_root.child(PATH_TOKENS_NOTIFICATIONS);

    }

    private void create_userID_tokens_notifications_list(String itemKind) {
        db_root.child(PATH_USERS_TOKENS).child(userID).child(PATH_TOKENS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_userID_tokens) {
                for (DataSnapshot snapshot_userID_token : snapshot_userID_tokens.getChildren()) {

                    db_root_tokens_notifications.child(snapshot_userID_token.getKey()).child(itemKind).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot_topics) {
                            for (DataSnapshot snapshot_topic : snapshot_topics.getChildren()) {
                                db_root_userIDs_notifications.child(userID).child(itemKind).child(snapshot_topic.getKey()).runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                        if (currentData.getValue() == null) {
                                            currentData.setValue(true);


                                            /*db_root_tokens_notifications.child(snapshot_userID_token.getKey()).child(itemKind)
                                                    .child(snapshot_topic.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {*/
                                            snapshot_topic.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot_notification_topic) {
                                                    for (DataSnapshot snapshot_notificationID : snapshot_notification_topic.getChildren()) {
                                                        db_root_userIDs_notifications.child(userID).child(itemKind)
                                                                .child(snapshot_topic.getKey()).child(snapshot_notificationID.getKey()).runTransaction(new Transaction.Handler() {
                                                            @NonNull
                                                            @Override
                                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                                if (currentData.getValue() == null) {
                                                                    currentData.setValue(true);


                                                                    return Transaction.success(currentData);
                                                                }


                                                                return Transaction.abort();
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
                                    public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                        if (committed) {
                                            // unique key saved
                                            Log.d(TAG, "onComplete: " + "unique key saved");
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