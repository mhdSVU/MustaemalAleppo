package mohammedyouser.com.mustaemalaleppo.Data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Locale;

import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Fragment_AddAlert_Dialog;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

public class Activity_User_Subscriptions extends AppCompatActivity implements View.OnClickListener, Fragment_AddAlert_Dialog.OnFragmentInteractionListener {


    private RecyclerView mRecyclerView;

    private DatabaseReference db_root_items;
    private DatabaseReference db_root_items_users;
    private DatabaseReference db_root_user_topics;
    private DatabaseReference db_root;
    private DatabaseReference db_root_userIDs_notifications;
    private DatabaseReference db_root_tokens_notifications;


    private String userID;
    public Bundle bundle;
    public ProgressBar mProgress;

    private ViewModel_Item viewModel;
    private TextView m_tv_no_content;
    private FloatingActionButton m_fab_add_alert;

    private String[] categoriesData;
    private String[] citiesData;
    private String[] categoriesLocale;
    private String[] citiesLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()));

        setContentView(R.layout.activity__user__subscriptions);
        setUpToolBar();

        viewModel = new ViewModelProvider(this).get(ViewModel_Item.class);


        mProgress = findViewById(R.id.progressBar1);
        mProgress.setVisibility(View.VISIBLE);


        mProgress.setIndeterminate(true);
        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        setUpAuthentication();

        initialDatabaseRefs();

        initialDataArrays();

        create_userID_tokens_notifications_list(PATH_INEED);
        create_userID_tokens_notifications_list(PATH_IHAVE);

        m_fab_add_alert = findViewById(R.id.fab_add_alert);
        m_tv_no_content = findViewById(R.id.tv_no_content);
        mRecyclerView = findViewById(R.id.recycler_Expand_ineed);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_fab_add_alert.setOnClickListener(this);

        //reading data from firebase
        db_root_user_topics.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showProgressDialog(getBaseContext(), getString(R.string.title_nav_adding_alert), getString(R.string.message_info_adding_alert));

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

                                String[] topicCityCat_ = topicTitle.split(getString(R.string.underScore));

                                topicList.add(new Topic(getCategory_locale(topicCityCat_[3]) + getString(R.string.in) + getCity_locale(topicCityCat_[2]), topicCategory_key));

                                Log.d(TAG, "onDataChange: " + topicCategory_key);

                            }

                            if (topicCategory_key.equals(PATH_INEED)) {
                                if (topicList.size() == 0) {
                                    topicMainCategoryList.clear();
                                } else {
                                    topicMainCategoryList.add(new TopicMainCategory(getString(R.string.title_subscribtion_ineed), topicList));

                                }

                            } else {
                                if (topicList.size() == 0) {
                                    topicMainCategoryList.clear();
                                } else {
                                    topicMainCategoryList.add(new TopicMainCategory(getString(R.string.title_subscribtion_ihave), topicList));

                                }

                            }


                            Adapter_ExpandableRecycler__Subscriptions adapter = new Adapter_ExpandableRecycler__Subscriptions(topicMainCategoryList, Activity_User_Subscriptions.this);

                            mRecyclerView.setAdapter(adapter);

                            updateUI_If_No_Content(topicMainCategoryList.size());
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to read value. " + databaseError.getMessage());


            }
        });
        hideProgressBar(mProgress);
        mProgress.setVisibility(View.GONE);

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

        db_root_user_topics = db_root.child(PATH_USERS_TOPICS).child(userID);

        db_root_userIDs_notifications = db_root.child(PATH_USERIDS_NOTIFICATIONS);
        db_root_tokens_notifications = db_root.child(PATH_TOKENS_NOTIFICATIONS);

    }

    private void create_userID_tokens_notifications_list(String itemKind) {
        db_root.child(PATH_USERSIDs_TOKENS).child(userID).child(PATH_TOKENS).addListenerForSingleValueEvent(new ValueEventListener() {
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

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(getString(R.string.title_user_subscriptions));
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fab_add_alert) {
            show_Add_Alert_Dialog();
        }
    }

    private void show_Add_Alert_Dialog() {

        showDialogFragment(new Fragment_AddAlert_Dialog(), "AddAlertDialog");
/*
        getSupportFragmentManager().setFragmentResultListener(BUNDLE_KEY_ADD_ALERT, this, (requestKey, bundle) -> {
            if (bundle.getBoolean(BUNDLE_KEY_ADD_ALERT)) {
                showProgressDialog(this,getString(R.string.title_nav_adding_alert),getString(R.string.message_info_adding_alert));
                Log.d(TAG, "show_Add_Alert_Dialog: ");
                create_userID_tokens_notifications_list(PATH_IHAVE);
                create_userID_tokens_notifications_list(PATH_INEED);
                hideProgressBar(mProgress);
            }
        });
*/
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // myAdapter.stopListening();

    }


    private void updateUI_If_No_Content(long topicsCount) {
        if (topicsCount == 0) {
            Log.d(TAG, "updateUI_If_No_Content: topicsCount == 0");
            m_tv_no_content.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "updateUI_If_No_Content: topicsCount != 0  " + topicsCount);

            m_tv_no_content.setVisibility(View.GONE);

        }


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private String getCategory_locale(String category) {

        for (int i = 0; i < categoriesData.length; i++) {
            if (categoriesData[i].equals(category))
                return categoriesLocale[i];
        }

        return "";
    }

    private String getCity_locale(String city) {

        for (int i = 0; i < citiesData.length; i++) {
            if (citiesData[i].equals(city))
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