package mohammedyouser.com.mustaemalaleppo.Data;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ALL_ITEMS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_IHAVE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_INEED;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEMS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEM_DATE_AND_TIME_REVERSE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEM_TITLE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEM_USER_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEM_USER_NAME;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOKENS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOKENS_FAVORITES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOKENS_NOTIFICATIONS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERSIDs_TOKENS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS_FAVORITES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USER_IMAGE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.hideProgressBar;

public class Activity_User_Favorites extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView mRecyclerView_ineed;
    private RecyclerView mRecyclerView_ihave;

    private DatabaseReference db_root;
    private DatabaseReference db_root_userIDs_favorites;
    private DatabaseReference db_root_tokens_favorites;


    private String userID;

    public Bundle bundle;
    public ProgressBar mProgress;
    private LinearLayout m_ll_no_content;
    private String mFavoriteTitle;
    private String mFavoriteDateTime;
    private String mFavoriteUserName;
    private String mFavoriteUserImage;
    private FavoriteItem mFavorite;
    private TextView m_tv_label_favorites_ihave;
    private TextView m_tv_label_favorites_ineed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__user__favorites);

        setUpToolBar();
        setUpViews();




        setUpAuthentication();

        initialDatabaseRefs();

      /*  create_userID_notifications_list(PATH_INEED);
        create_userID_notifications_list(PATH_IHAVE);*/

        fetchUserFavoriteItems(PATH_IHAVE);
        fetchUserFavoriteItems(PATH_INEED);

        updateUI_If_No_Content();
    }

    private void setUpViews() {
        mProgress = findViewById(R.id.progressBar1);
       // mProgress.setVisibility(View.VISIBLE);


        mProgress.setIndeterminate(true);
        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        //Define recycleview
        m_ll_no_content = findViewById(R.id.ll_no_content);
        mRecyclerView_ihave = findViewById(R.id.recycler_Expand_ihave);
        mRecyclerView_ineed = findViewById(R.id.recycler_Expand_ineed);
        mRecyclerView_ihave.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView_ineed.setLayoutManager(new LinearLayoutManager(this));
        m_tv_label_favorites_ihave = findViewById(R.id.tv_label_favorites_ihave);
        m_tv_label_favorites_ineed = findViewById(R.id.tv_label_favorites_ineed);
    }

    @Override
    protected void onStart() {
        super.onStart();
        create_userID_favorites_list(PATH_IHAVE);
        create_userID_favorites_list(PATH_INEED);
    }

    private void setUpAuthentication() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        }
    }

    private void initialDatabaseRefs() {
        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_userIDs_favorites = db_root.child(PATH_USERS_FAVORITES);
        db_root_tokens_favorites = db_root.child(PATH_TOKENS_FAVORITES);


    }

    private void create_userID_favorites_list(String itemState) {
        Log.d(TAG, "create_userID_tokens_notifications_list: ");
        //fetching Notifications topics and notifications from Users_Tokens
        db_root.child(PATH_USERSIDs_TOKENS).child(userID).child(PATH_TOKENS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot_userID_tokens) {
                        for (DataSnapshot snapshot_userID_token : snapshot_userID_tokens.getChildren()) {

                            db_root_tokens_favorites.child(snapshot_userID_token.getKey()).child(itemState)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot_topics) {
                                            for (DataSnapshot snapshot_topic : snapshot_topics.getChildren()) {
                                                //Adding "Notifications topics" to UsersIDS_Notifications
                                                db_root_userIDs_favorites.child(userID).child(itemState)
                                                        .child(snapshot_topic.getKey()).runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        if (currentData.getValue() == null) {
                                                            currentData.setValue(true);
                                                            snapshot_topic.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot_favorite_topic) {
                                                                    for (DataSnapshot snapshot_favoriteItemID : snapshot_favorite_topic.getChildren()) {
                                                                        //Adding "Notifications" to UsersIDS_Notifications


                                                                        db_root_userIDs_favorites.child(userID).child(itemState).child(snapshot_topic.getKey())
                                                                                // db_root_userIDs_notifications.child(userID).child(itemState).child(snapshot_favorite_topic.getKey())
                                                                                .child(snapshot_favoriteItemID.getKey()).runTransaction(new Transaction.Handler() {
                                                                            @NonNull
                                                                            @Override
                                                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                                                if (currentData.getValue() == null) {
                                                                                    //  currentData.setValue(snapshot_favoriteItemID.getValue());
                                                                                    currentData.setValue(true);
                                                                                    Log.d(TAG, "doTransaction: " + (snapshot_favoriteItemID.getValue()));


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
                                                                public void onDataChange(@NonNull DataSnapshot snapshot_favorite_topic) {
                                                                    for (DataSnapshot snapshot_favoriteItemID : snapshot_favorite_topic.getChildren()) {
                                                                        db_root_userIDs_favorites.child(userID).child(itemState).child(snapshot_topic.getKey()).child(snapshot_favoriteItemID.getKey()).runTransaction(new Transaction.Handler() {
                                                                            @NonNull
                                                                            @Override
                                                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                                                if (currentData.getValue() == null) {
                                                                                    currentData.setValue(snapshot_favoriteItemID.getValue());


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

    private void fetchUserFavoriteItems(String itemState) {
        //notificationTopicList.clear();
        // notificationList.clear();

        db_root_userIDs_favorites.child(userID).child(itemState)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot_favorites_topics) {

                        final ArrayList<FavoriteTopic> favoriteTopicList = new ArrayList<>();

                        for (final DataSnapshot snapshot_favorite_topic : snapshot_favorites_topics.getChildren()) {
                            final ArrayList<FavoriteItem> favoriteItemsList = new ArrayList<>();

                            final String topicKey = String.valueOf(snapshot_favorite_topic.getKey());

                            snapshot_favorite_topic.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot_favorite_topic) {
                                    for (DataSnapshot snapshot_favorite : snapshot_favorite_topic.getChildren()) {
                                        final String favoriteKey = String.valueOf(snapshot_favorite.getKey());
                                        final String favoriteValue = String.valueOf(snapshot_favorite.getValue());

                                        String[] topicCityCat_ = String.valueOf(snapshot_favorite_topic.getKey()).split("_");
                                        FirebaseDatabase.getInstance().getReference()
                                                .child(PATH_ITEMS).child(itemState).child(PATH_ALL_ITEMS).child(favoriteKey).
                                                addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot_item) {

                                                        db_root.child(PATH_USERS).child(String.valueOf(snapshot_item.child(PATH_ITEM_USER_ID).getValue())).
                                                                addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot_userItem) {

                                                                        mFavoriteTitle = String.valueOf(snapshot_item.child(PATH_ITEM_TITLE).getValue());
                                                                        mFavoriteDateTime = String.valueOf(snapshot_item.child(PATH_ITEM_DATE_AND_TIME_REVERSE).getValue());
                                                                        mFavoriteUserName = String.valueOf(snapshot_item.child(PATH_ITEM_USER_NAME).getValue());
                                                                        mFavoriteUserImage = String.valueOf(snapshot_userItem.child(PATH_USER_IMAGE).getValue());

                                                                        Log.d(TAG, "onDataChange:1     mNotificationTitle,\n" +
                                                                                "                                                topicKey,\n" +
                                                                                "                                               mNotificationDateTime,\n" +
                                                                                "                                                mNotificationUserName,\n" +
                                                                                "                                             mNotificationUserImage))" + mFavoriteTitle + " " + topicKey + " " +
                                                                                mFavoriteDateTime + " " +
                                                                                mFavoriteUserName + " " +
                                                                                mFavoriteUserImage);

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                        mFavorite = new FavoriteItem(itemState, topicCityCat_[3],
                                                                topicCityCat_[2],
                                                                favoriteKey,
                                                                favoriteValue,
                                                                String.valueOf(snapshot_item.child(PATH_ITEM_TITLE).getValue()),
                                                                topicKey,
                                                                String.valueOf(snapshot_item.child(PATH_ITEM_DATE_AND_TIME_REVERSE).getValue()),
                                                                String.valueOf(snapshot_item.child(PATH_ITEM_USER_NAME).getValue()),
                                                                String.valueOf(mFavoriteUserImage));

                                                        favoriteItemsList.add(mFavorite);


                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                    }

                                    String[] topicCityCat_ = topicKey.split(getString(R.string.underScore));

                              /*      if (favoriteItemsList.size() != 0) {
                                        favoriteTopicList.add(new NotificationTopic(topicCityCat_[3] + getString(R.string.in) + topicCityCat_[2], favoriteItemsList));

                                    }*/
                                    favoriteTopicList.add(new FavoriteTopic(topicCityCat_[3] + getString(R.string.in) + topicCityCat_[2], favoriteItemsList));

                                    if (itemState.equals(PATH_INEED)) {
                                        Adapter_ExpandableRecycler__Favorites adapter_ineed = new Adapter_ExpandableRecycler__Favorites(itemState, favoriteItemsList, favoriteTopicList, Activity_User_Favorites.this, m_tv_label_favorites_ihave, m_tv_label_favorites_ineed, m_ll_no_content);

                                        mRecyclerView_ineed.setAdapter(adapter_ineed);
                                        adapter_ineed.notifyDataSetChanged();

                                    } else {
                                        Adapter_ExpandableRecycler__Favorites adapter_ihave = new Adapter_ExpandableRecycler__Favorites(itemState, favoriteItemsList, favoriteTopicList, Activity_User_Favorites.this, m_tv_label_favorites_ihave, m_tv_label_favorites_ineed, m_ll_no_content);
                                        mRecyclerView_ihave.setAdapter(adapter_ihave);

                                        adapter_ihave.notifyDataSetChanged();

                                    }
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

    private void updateUI_If_No_Content() {
        db_root_userIDs_favorites.child(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.hasChildren()) {

                                m_ll_no_content.setVisibility(View.GONE);
                                m_tv_label_favorites_ihave.setVisibility(View.VISIBLE);
                                m_tv_label_favorites_ineed.setVisibility(View.VISIBLE);
                                mProgress.setVisibility(View.GONE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


}