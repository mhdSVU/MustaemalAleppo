package mohammedyouser.com.mustaemalaleppo.UI;

import android.Manifest;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import mohammedyouser.com.mustaemalaleppo.Data.Activity_User_Favorites;
import mohammedyouser.com.mustaemalaleppo.Data.Activity_User_Notifications;
import mohammedyouser.com.mustaemalaleppo.Data.Activity_User_Subscriptions;
import mohammedyouser.com.mustaemalaleppo.Domain.Activity_About_App;
import mohammedyouser.com.mustaemalaleppo.Domain.Activity_About_Developer;
import mohammedyouser.com.mustaemalaleppo.Domain.Activity_Add_Item;
import mohammedyouser.com.mustaemalaleppo.Data.Category;
import mohammedyouser.com.mustaemalaleppo.Data.City;
import mohammedyouser.com.mustaemalaleppo.Domain.ActivityProfile;
import mohammedyouser.com.mustaemalaleppo.Domain.Fragment_Dialog_SignOut_Alert_2;
import mohammedyouser.com.mustaemalaleppo.Domain.Fragment_Dialog_VPN_Alert;
import mohammedyouser.com.mustaemalaleppo.Domain.Fragment_UpdateLocation_Alert_Dialog;
import mohammedyouser.com.mustaemalaleppo.Device.GpsUtils;
import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;


import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.adjustLanguage;

public class Activity_Ineed_Ihave_CurrentUser extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        View.OnClickListener,
        Fragment_Ineed.OnFragmentInteractionListener,
        Fragment_Ihave.OnFragmentInteractionListener,
        Fragment_AddAlert_Dialog.OnFragmentInteractionListener, AdapterView.OnItemSelectedListener {

    private ImageView m_imgView_user_nav;
    private TextView m_tv_userName, m_tv_userName_welcome, m_tv_userPhoneNumber;
    private LinearLayout m_ll_welcome;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Adapter_FragmentPagerAdapter_Ineed_Ihave myFragmentPagerAdapter;

    private SearchView searchView;
    private SearchManager searchManager;

    private City[] cities;

    private Category[] categories;

    private FirebaseAuth auth;


    private DatabaseReference db_root_users;

    private String selectedCategory = PATH_ALL_CATEGORIES;
    private String selectedCity = PATH_ALL_CITIES;
    private String userID;

    private CollapsingToolbarLayout collapseLayout;

    private Toolbar toolbar;

    private String userName;
    private String userImage;
    private String userEmail;
    private String userPhoneNumber;
    private String userAddress;

    private String custom_criteria = PATH_ITEM_DATE_AND_TIME;
    private ImageView m_imageView_user;

    private static final int REQUEST_PERMISSION_LOCATION_GET = 787;
    private boolean isGPS = false;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = true;
    private TextView m_tv_location;
    private ProgressBar m_pb_location;
    public static double current_User_Lat;
    public static double current_User_Long;
    public static boolean locationUpdatedConfirm = false;
    private ImageView imageView_city;
    private LinearLayout m_ll_app;
    private ViewPager m_viewPager;
    private DrawerLayout drawer;
    private FrameLayout m_fl_notificationView;
    private TextView m_tv_notifications_count;
    private Spinner spinner_cities;
    private Spinner spinner_categories;
    private String[] CitiesNames;
    private String[] CategoriesNames;
    private String[] CitiesNames_locale;
    private String[] CategoriesNames_locale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()),this);

        setContentView(R.layout.activity_ineed__ihave);

        setUpAuthentication();

        doMainInitializations();

        adjustCurrentPage();


    }

    private void adjustCurrentPage() {
        if (getIntent().hasExtra(INTENT_KEY__STATE)) {
            if (getIntent().getStringExtra(INTENT_KEY__STATE).equals(PATH_INEED)) {
                viewPager.setCurrentItem(0);

            } else {
                viewPager.setCurrentItem(1);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        } else {
            Intent intent = new Intent(this, Activity_Sign_Up_Phone_Number.class);
            startActivity(intent);
        }
        loadNavHeader();
        initializeUserNotificationCount();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeUserNotificationCount();
    }

    private void doMainInitializations() {
        /*Apps that rely on the Play Services SDK should always check the device for
        a compatible Google Play services APK before accessing Google Play services features.
        It is recommended to do this in two places:
        in the main activity's onCreate() method, and in its onResume() method.*/
        //TODO isGooglePlayServicesAvailable(Context context)
        //If the device doesn't have a compatible version of Google Play services
        //TODO GoogleApiAvailability.makeGooglePlayServicesAvailable()
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        collapseLayout = findViewById(R.id.collapse);
        m_imageView_user = findViewById(R.id.imageView_user);
        m_tv_userName_welcome = findViewById(R.id.tv_userName);
        m_viewPager = findViewById(R.id.viewPager);


        spinner_cities = (Spinner) findViewById(R.id.spinner_cities);
        spinner_categories = (Spinner) findViewById(R.id.spinner_categories);

        spinner_cities.setOnItemSelectedListener(this);
        spinner_categories.setOnItemSelectedListener(this);

        ((Spinner) findViewById(R.id.spinner_categories)).setSelection(0);
        ((Spinner) findViewById(R.id.spinner_cities)).setSelection(0);

        m_ll_welcome = findViewById(R.id.ll_welcome);
        m_ll_app = findViewById(R.id.ll_app);


        ((Spinner) findViewById(R.id.spinner_categories)).setSelection(0);
        ((Spinner) findViewById(R.id.spinner_cities)).setSelection(0);
        m_ll_app.setVisibility(View.GONE);
        m_ll_welcome.setVisibility(View.VISIBLE);


        initialDatabaseRefs();

        setUpMyNavigationDrawer();


        initialCities();
        initialCategories();

        setUpMyViewPager();
        tabLayout.setupWithViewPager(viewPager);
        setUpMyTabLayout();


    }

    private void customizeUserBackground() {


        // Loading profile image
        if (userImage != null) {
        /*    try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.parse(userImage));
                collapseLayout.setBackground(Drawable.createFromStream(inputStream, userImage));
            } catch (FileNotFoundException e) {
                collapseLayout.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.aleppo_castle,this.getTheme()));
            }*/
            Glide.with(this).load(Uri.parse(userImage)).into((ImageView) collapseLayout.findViewById(R.id.imageView_user));

        }
    }

    private void setUpAuthentication() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();

        } else {
            Intent intent = new Intent(this, Activity_Sign_Up_Phone_Number.class);
            startActivity(intent);
        }

    }


    private void initialDatabaseRefs() {

        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference db_root_items = db_root.child(PATH_ITEMS);
        db_root_users = db_root.child(PATH_USERS);
    }

    private void setUpMyNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(userName);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // initial nav menu header data
        initialNavHeader();

        // load nav menu header data
        loadNavHeader();
    }

    private void initialNavHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Navigation view header
        View navHeader = navigationView.getHeaderView(0);
        m_tv_userName = (TextView) navHeader.findViewById(R.id.tv_userName);
        m_tv_userPhoneNumber = (TextView) navHeader.findViewById(R.id.tv_userPhoneNumber);
        m_imgView_user_nav = (ImageView) navHeader.findViewById(R.id.imageView_userImage);

        m_tv_location = navHeader.findViewById(R.id.tv_location);
        m_pb_location = navHeader.findViewById(R.id.pb_location);
        m_tv_location.setOnClickListener(this);

        m_imgView_user_nav.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_white_24dp));

    }


    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
    }

    private void setUpMyViewPager() {
        ArrayList<String> tabs = new ArrayList<>();
        tabs.add(getString(R.string.title_ineed));
        tabs.add(getString(R.string.title_ihave));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((TextView) tabLayout.getTabAt(position).getCustomView().findViewById(R.id.android_R_id_text1)).setTextColor(getResources().getColor(R.color.selcted_tab));

                if (position == 0) {
                    ((ImageView) tabLayout.getTabAt(position).getCustomView().findViewById(R.id.android_R_id_icon)).setImageDrawable(getDrawable(R.drawable.ic_ineed));

                    populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);

                } else {

                    ((ImageView) tabLayout.getTabAt(position).getCustomView().findViewById(R.id.android_R_id_icon)).setImageDrawable(getDrawable(R.drawable.ic_ihave));

                    populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);

                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (searchView != null && !(searchView.isIconified())) {
                    searchView.onActionViewCollapsed();

               /*     toolbar.setBackgroundColor(getColor(R.color.transparent));
                    m_ll_app.setVisibility(View.VISIBLE);
                    m_tv_app_title.setVisibility(View.VISIBLE);
                    m_tv_app_title.setText(getString(R.string.my_app_name));*/

                    populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);


                }

            }
        });
        myFragmentPagerAdapter = new Adapter_FragmentPagerAdapter_Ineed_Ihave(getSupportFragmentManager(), tabs.size(), tabs);
        viewPager.setAdapter(myFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }


    private void setUpMyTabLayout() {

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.selcted_tab));
        tabLayout.setTabTextColors(getResources().getColor(R.color.normal), getResources().getColor(R.color.selcted_tab));


        tabLayout.getTabAt(0).setCustomView(R.layout.tap_layout_i_need);
        tabLayout.getTabAt(1).setCustomView(R.layout.tap_layout_i_have);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                ((TextView) tab.getCustomView().findViewById(R.id.android_R_id_text1)).setTextColor(getResources().getColor(R.color.selcted_tab));


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.android_R_id_text1)).setTextColor(getResources().getColor(R.color.normal));
                if (tab.getPosition() == 0) {
                    ((ImageView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.android_R_id_icon)).setImageDrawable(getDrawable(R.drawable.icons_empty_box_24));

                } else {

                    ((ImageView) tabLayout.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.android_R_id_icon)).setImageDrawable(getDrawable(R.drawable.icons_full_box_24));


                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void loadNavHeader() {
        userAddress = get_UserAddress_from_DB(userID);
        userName = setUserName(userID);
        userImage = setUserImage(userID);
        userPhoneNumber = setUserPhoneNumber(userID);

    }

    private String get_UserAddress_from_DB(final String userID) {
        db_root_users.child(userID).child(PATH_USER_ADDRESS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_userAddress) {
                if (!String.valueOf(snapshot_userAddress.getValue()).equals("null")) {
                    m_pb_location.setVisibility(View.GONE);
                    m_tv_location.setVisibility(View.VISIBLE);
                    m_tv_location.setText(String.valueOf(snapshot_userAddress.getValue()));

                } else {
                    m_pb_location.setVisibility(View.GONE);
                    m_tv_location.setVisibility(View.VISIBLE);
                    m_tv_location.setText(getString(R.string.message_info_error_no_location_click_to_update_loc));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return userAddress;
    }

    public void populateCurrentFragment(String custom_criteria, String selectedCity, String selectedCategory) {

        try {
            ArrayList<String> tabs = new ArrayList<>();
            tabs.add(getString(R.string.title_ineed));
            tabs.add(getString(R.string.title_ihave));

            Log.d("populateCurrentFragment", "populateCurrentFragment: ");
            // showProgressDialog(this, getString(R.string.message_info_LOADING_DATA), getString(R.string.message_info_PLEASE_WAIT));
            int position = tabLayout.getSelectedTabPosition();
            Fragment current_fragment = myFragmentPagerAdapter.getFragment(position);
            viewPager.setCurrentItem(position);
            if (current_fragment instanceof Fragment_Ineed) {
                Log.d(TAG, "populateCurrentFragment: current Ineed");

                Query customQuery = ((Fragment_Ineed) current_fragment).createCustomQuery(this, selectedCity, selectedCategory, PATH_INEED, custom_criteria);

                ((Fragment_Ineed) current_fragment).populate_ViewHolder_Item(customQuery);


            } else {
                Log.d(TAG, "populateCurrentFragment: current ihave");
                Query customQuery = ((Fragment_Ihave) current_fragment).createCustomQuery(this, selectedCity, selectedCategory, PATH_IHAVE, custom_criteria);

                ((Fragment_Ihave) current_fragment).populate_ViewHolder_Item(customQuery);


            }
            hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }


    }

    public void populateCurrentFragment_Search(String searchText, String searchCriteria) {
        showProgressDialog(this, getString(R.string.message_info_LOADING_DATA), getString(R.string.message_info_PLEASE_WAIT));
        int position = tabLayout.getSelectedTabPosition();
        //tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(position);
        Log.d(TAG, "populateCurrentFragment_Search getSelectedTabPosition: " + position);
        Fragment current_fragment = myFragmentPagerAdapter.getFragment(position);
        Log.d(TAG, "populateCurrentFragment_Search: ");
        if (current_fragment instanceof Fragment_Ineed) {
            Log.d(TAG, "populateCurrentFragment_Search:Fragment_Ineed ");
            Query customQuery = ((Fragment_Ineed) current_fragment).createCustomQuery(this, selectedCity, selectedCategory, PATH_INEED, searchCriteria);

            Query final_customQuery = customQuery.startAt(searchText).endAt(searchText + "\uf8ff");

            ((Fragment_Ineed) current_fragment).populate_ViewHolder_Item(final_customQuery);
        } else {
            Log.d(TAG, "populateCurrentFragment_Search:Fragment_Ihave ");
            Query customQuery = ((Fragment_Ihave) current_fragment).createCustomQuery(this, selectedCity, selectedCategory, PATH_IHAVE, searchCriteria);
            Query final_customQuery = customQuery.startAt(searchText).endAt(searchText + "\uf8ff");
            ((Fragment_Ihave) current_fragment).populate_ViewHolder_Item(final_customQuery);
        }
        hideProgressDialog();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent() {

        if (getIntent().getAction() != null) {

            if (getIntent().getAction().equals(Intent.ACTION_SEARCH)) {
                String query = getIntent().getStringExtra(SearchManager.QUERY);
                doSearch(query);
            }
        }

    }


    private void doSearch(String query) {
        populateCurrentFragment_Search(query, PATH_ITEM_TITLE);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!(searchView.isIconified())) {
            searchView.onActionViewCollapsed();

            toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
            m_ll_app.setVisibility(View.GONE);
            m_ll_welcome.setVisibility(View.VISIBLE);
            m_ll_welcome.setVisibility(View.VISIBLE);
            m_tv_userName.setVisibility(View.VISIBLE);
            m_imageView_user.setVisibility(View.VISIBLE);
            m_tv_userName.setText(userName);

            populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);


        } else {
            super.onBackPressed();
        }
    }

    private void initializeUserNotificationCount() {
        if (m_tv_notifications_count != null) {
            db_root_users.child(userID).child(PATH_NOTIFICATIONS_COUNT)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!(String.valueOf(snapshot.getValue()).equals("null")) && !(String.valueOf(snapshot.getValue()).equals("0"))) {
                                m_tv_notifications_count.setVisibility(View.VISIBLE);
                                m_tv_notifications_count.setText(String.valueOf(snapshot.getValue()) + "+");
                            } else {
                                ((TextView) m_fl_notificationView.findViewById(R.id.tv_notifications_count)).setVisibility(View.INVISIBLE);

                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        m_fl_notificationView = (FrameLayout) menu.findItem(R.id.action_show_notifications).getActionView();
        m_tv_notifications_count = m_fl_notificationView.findViewById(R.id.tv_notifications_count);
        initializeUserNotificationCount();

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(this);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.hint_search_item));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setOnClickListener(this);
        m_fl_notificationView.setOnClickListener(v -> startActivity(new Intent(Activity_Ineed_Ihave_CurrentUser.this, Activity_User_Notifications.class)));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify activity_single_item parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_by_title) {
            custom_criteria = PATH_ITEM_TITLE;
            populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);

            return true;
        }
        if (id == R.id.action_sort_by_price) {
            custom_criteria = PATH_ITEM_PRICE;
            populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);


            return true;
        }
        if (id == R.id.action_sort_by_date) {
            custom_criteria = PATH_ITEM_DATE_AND_TIME;
            populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);


            return true;
        }
        if (id == R.id.action_show_notifications) {

            startActivity(new Intent(this, Activity_User_Notifications.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            drawer.closeDrawer(GravityCompat.START);

        }
        if (id == R.id.nav_add_item) {
            initializeUserLocIfNeeded();

            Intent currentUserItemsIntent = new Intent(this, Activity_Add_Item.class);
            currentUserItemsIntent.putExtra(INTENT_KEY__STATE, INTENT_VALUE__STATE_IHAVE);
            currentUserItemsIntent.putExtra(INTENT_KEY__STATELABEL, INTENT_VALUE__STATELABEL_IHAVE);
            currentUserItemsIntent.putExtra(INTENT_KEY_USER_ID, userID);
            currentUserItemsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(currentUserItemsIntent);

        } else if (id == R.id.nav_my_items) {

            drawer.closeDrawer(GravityCompat.START);


        } else if (id == R.id.nav_edit_my_profile) {


            Intent setUpAccountIntent = new Intent(this, ActivityProfile.class);
            setUpAccountIntent.putExtra(INTENT_KEY_USER_ID, userID);
            startActivity(setUpAccountIntent);

        } else if (id == R.id.nav_sign_out) {
            confirmSignOut();
        } else if (id == R.id.nav_my_favorites) {
            Intent favoritesIntent = new Intent(this, Activity_User_Favorites.class);
            startActivity(favoritesIntent);

        } else if (id == R.id.nav_my_notifications) {
            Intent notificationsIntent = new Intent(this, Activity_User_Notifications.class);
            startActivity(notificationsIntent);

        } else if (id == R.id.nav_my_subscriptions) {
            Intent subscriptionsIntent = new Intent(this, Activity_User_Subscriptions.class);
            startActivity(subscriptionsIntent);

        } else if (id == R.id.nav_add_alert) {
            show_Add_Alert_Dialog();

        } else if (id == R.id.nav_download_vpn) {

            confirm_download_VPN();


        } else if (id == R.id.nav_forget_me) {

            confirm_forget_me();


        } else if (id == R.id.nav_share_app) {

            shareApp();


        } else if (id == R.id.nav_about_app) {

            startAboutAppActivity();


        } else if (id == R.id.nav_about_developer) {


            startAboutDeveloperActivity();

        } else if (id == R.id.nav_rate_app) {

            startRateAppActivity();


        } else if (id == R.id.nav_contact_us) {

            contactUs();


        } else if (id == R.id.nav_change_language_ar) {

            changeLanguage("ar");


        } else if (id == R.id.nav_change_language_en) {

            changeLanguage("en");


        }
        else if (id == R.id.nav_change_country) {

            changeCountry();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeCountry() {
        Toast.makeText(this, getString(R.string.message_error_change_country_app), Toast.LENGTH_SHORT).show();
    }

    private void startRateAppActivity() {
        Toast.makeText(this, getString(R.string.message_error_rate_app), Toast.LENGTH_SHORT).show();
    }

    private void startAboutAppActivity() {
        Intent intent = new Intent(this, Activity_About_App.class);
        startActivity(intent);

    }

    private void startAboutDeveloperActivity() {
        Intent intent = new Intent(this, Activity_About_Developer.class);
        startActivity(intent);

    }

    private void contactUs() {
        String developerEmail = "developer@ineedihave.com";
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", developerEmail, null));
        String subject = "";


        emailIntent.putExtra(Intent.EXTRA_EMAIL, developerEmail);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.chooser_title_email)));

    }


    private void shareApp() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Hey check out Ineed Ihave App at :\n https://drive.google.com/file/d/1wmEWB8SQhLFQQ4uP6voTiQBJh0XB7EYi/view?usp=sharing");
        intent.setType("text/plain");
        startActivity(intent);
    }


    @Override
    public boolean onClose() {
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        m_ll_app.setVisibility(View.GONE);
        m_ll_welcome.setVisibility(View.VISIBLE);
        m_tv_userName.setText(userName);
        populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);


        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        populateCurrentFragment_Search(query, PATH_ITEM_TITLE);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        populateCurrentFragment_Search(newText, PATH_ITEM_TITLE);

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == searchView.getId()) {
            Log.d(TAG, "onClick: search2");
            toolbar.setBackgroundColor(getResources().getColor(R.color.grey_500));
            m_ll_welcome.setVisibility(View.GONE);
        } else if (v.getId() == R.id.tv_location) {

            confirmUpdateLocation();


        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
            for (int i = 0; i < cities.length; i++) {
                CitiesNames_locale[i] = CitiesNames[i];
            }
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
            for (int i = 0; i < categories.length; i++) {
                CategoriesNames_locale[i] = CategoriesNames[i];
            }
        }


    }

    private void build_spinnerCategories_view() {
        //creating arrayAdapter_categories for the spinner_cities
        ArrayAdapter<String> arrayAdapter_categories = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, CategoriesNames_locale);
        arrayAdapter_categories.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner_categories.setAdapter(arrayAdapter_categories);
    }

    private int getCityDrawableByName(String selectedCity_Name) {//TODO: change drawables
        switch (selectedCity_Name) {
            case "Damascus":
                return R.drawable.damascus;
            case "Aleppo":
                return R.drawable.aleppo;

            case "Homs":
                return R.drawable.homs;
            case "Hama":
                return R.drawable.hama;
            case "Lattakia":
                return R.drawable.lattakia;
            case "Idleb":
                return R.drawable.idleb;
            case "Dair Zor":
                return R.drawable.allcities;
            case "Hasaka":
                return R.drawable.allcities;
            case "Daraa":
                return R.drawable.allcities;
            case "Reef Dimashque":
                return R.drawable.allcities;
            case "Tartus":
                return R.drawable.allcities;
            default:
                return R.drawable.allcities;
        }

    }

    private void confirmUpdateLocation() {
        showDialogFragment(new Fragment_UpdateLocation_Alert_Dialog(), "frg_loc");
        getSupportFragmentManager().setFragmentResultListener(BUNDLE_KEY_REQUEST_LOCATION_UPDATE, this, (requestKey, bundle) -> {
            if (bundle.getBoolean(BUNDLE_KEY_REQUEST_LOCATION_UPDATE)) {
                m_tv_location.setVisibility(View.GONE);
                m_pb_location.setVisibility(View.VISIBLE);
                m_pb_location.setVisibility(View.VISIBLE);
                prepareGPS();
                setUpLocation();
            }
        });

    }

    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView) {
        Glide.with(context).load(imageURL)
/*
                .crossFade()
*/
                .thumbnail(thumbnail)
/*
                .bitmapTransform(new CircleTransform(context))
*/.apply(RequestOptions.circleCropTransform())

/*
                .diskCacheStrategy(DiskCacheStrategy.ALL)
*/
                .into(imageView);


    }

    private String setUserName(final String userID) {


        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    userName = (String) dataSnapshot.child(userID).child(PATH_USER_NAME).getValue();
                    m_tv_userName.setText(userName);
                    m_tv_userName_welcome.setText(userName);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return userName;
    }


    private String setUserImage(final String userID) {

        db_root_users.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userImage = (String) dataSnapshot.child(userID).child(PATH_USER_IMAGE).getValue();
                // Loading profile image
                if (userImage != null) {
                    setImage_circle(getBaseContext(), Uri.parse(userImage), 0.3f, m_imgView_user_nav);
                    setImage_circle(getBaseContext(), Uri.parse(userImage), 0.3f, m_imageView_user);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userImage;

    }


    private String setUserPhoneNumber(final String userID) {

        db_root_users.child(userID).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userPhoneNumber = (String) dataSnapshot.child(PATH_USER_PHONE_NUMBER).getValue();
                m_tv_userPhoneNumber.setText(userPhoneNumber);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userPhoneNumber;


    }

    /*
    Location Start
    * */

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
                LocationServices.getFusedLocationProviderClient(Activity_Ineed_Ihave_CurrentUser.this).removeLocationUpdates(this);
                if (locationResult == null || locationResult.getLocations().size() == 0) {
                    return;
                }
                int latestLocationIndex = locationResult.getLocations().size() - 1;
                for (Location location : locationResult.getLocations()) {
                    LatLng sydney = new LatLng(-34, 151);
                    if (location != null) {

                        Activity_Ineed_Ihave_CurrentUser.GeoCoderHandler m_geocoderHandler = new Activity_Ineed_Ihave_CurrentUser.GeoCoderHandler();
                        getAddressFromLocation_GeoCoderProcessor(locationResult.getLocations().get(latestLocationIndex), getBaseContext(), m_geocoderHandler);
                        store_UserLocation_To_DB(new LatLng(locationResult.getLocations().get(latestLocationIndex).getLatitude(),

                                locationResult.getLocations().get(latestLocationIndex).getLongitude()), userID);
                        Toast.makeText(Activity_Ineed_Ihave_CurrentUser.this, getString(R.string.message_info_loc_updated), Toast.LENGTH_SHORT).show();


                    }
                }
            }


            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
        getFusedLocationProviderClient().requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());

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

    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    current_User_Lat = bundle.getDouble("lat");
                    current_User_Long = bundle.getDouble("long");
                    break;
                default:
                    locationAddress = null;
            }
            Log.e("location Address=", locationAddress);
            store_UserAddress_To_DB(locationAddress);
            get_UserAddress_from_DB(userID);


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
                        if (!address.getThoroughfare().equals("null"))
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

    private void store_UserAddress_To_DB(String locationAddress) {
        db_root_users.child(userID).child(PATH_USER_ADDRESS).setValue(String.valueOf(locationAddress));

    }

    private void store_UserLocation_To_DB(LatLng latLng, String userID) {

        db_root_users.child(userID).child(PATH_USER_LOCATION_LAT).setValue(String.valueOf(latLng.latitude));
        db_root_users.child(userID).child(PATH_USER_LOCATION_LONG).setValue(String.valueOf(latLng.longitude));

    }

    private void initializeUserLocIfNeeded() {
        db_root_users.child(userID).child(PATH_USER_LOCATION_LAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (String.valueOf(snapshot.getValue()).equals("null")) {
                    prepareGPS();
                    setUpLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void confirmSignOut() {
        FragmentManager fragmentManager = getFragmentManager();


        android.app.Fragment fragment = fragmentManager.findFragmentByTag("signOut");
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }


        Fragment_Dialog_SignOut_Alert_2 fragmentSignOutAlertDialog = new Fragment_Dialog_SignOut_Alert_2();
        fragmentSignOutAlertDialog.show(fragmentManager, "signOut");


    }

    private void show_Add_Alert_Dialog() {
        Bundle bundle = new Bundle();
        if (tabLayout.getSelectedTabPosition() == 0) {
            bundle.putString("kind", "Ineed");

        } else {
            bundle.putString("kind", "Ihave");

        }

        Fragment_AddAlert_Dialog add_alert_fragment = new Fragment_AddAlert_Dialog();
        add_alert_fragment.setArguments(bundle);
        add_alert_fragment.show(getSupportFragmentManager(), "AddAlertDialog");
    }

    private void confirm_forget_me() {
        showDialogFragment(new Fragment_Dialog_Forget_Me(), "frg_forget_me");
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


    public void SignOut() {
      /*  auth.signOut();
        finish();
        Intent intent = new Intent(Ineed_Ihave_Activity.this, Activity_Sign_In_Phone_Number.class);
        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
        showProgressDialog(Activity_Ineed_Ihave_CurrentUser.this, getString(R.string.sign_out), getString(R.string.message_info_signing_out));
        FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener(task -> {
            auth.signOut();
            finish();
            Intent intent = new Intent(Activity_Ineed_Ihave_CurrentUser.this, Activity_Sign_In_Phone_Number.class);
            //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            hideProgressDialog();
            startActivity(intent);

        });
        FirebaseMessaging.getInstance().deleteToken().addOnFailureListener(task -> {
            Toast.makeText(Activity_Ineed_Ihave_CurrentUser.this, getString(R.string.message_error_sign_out), Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
/*
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
*/
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_PERMISSION_LOCATION_GET:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perms_location", "perms granted");
                    setUpLocation();

                } else {
                    Log.d("perms_location", "perms not granted");
                    m_pb_location.setVisibility(View.GONE);
                    m_tv_location.setVisibility(View.VISIBLE);

                    Toast.makeText(Activity_Ineed_Ihave_CurrentUser.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();


                }
                break;
        }

    }

    /*
    location End
    * */


    private void confirm_download_VPN() {
      /*  Fragment fragment = getSupportFragmentManager().findFragmentByTag("VPN_Alert_Fragment");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        Fragment_Dialog_VPN_Alert fragmentDialogVPNAlert = new Fragment_Dialog_VPN_Alert();
        fragmentDialogVPNAlert.show(getFragmentManager(), "VPN_Alert_Fragment");*/
        showDialogFragment(new Fragment_Dialog_VPN_Alert(), "frg_vpn");
    }

    private void changeLanguage(String lan) {
        LocaleHelper.setLocale(this, lan);
        startActivity(new Intent(this, Activity_Splash.class).putExtra(INTENT_KEY_LANGUAGE, lan));
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.spinner_cities) {
            Log.d(TAG, "onItemSelected:cities current");
            if (((TextView) view) != null)
                //selectedCity = String.valueOf(((TextView) view).getText());
                selectedCity = CitiesNames[position];

            Log.d(TAG, "current onItemSelected:cities  " + selectedCity);


            populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);

            // int selectedCity_Code=cities[adapterView.getSelectedItemPosition()].getCode();

            //   collapseLayout.setTitle(getString(R.string.collapsing_toolbar,selectedCity_Name));
            m_viewPager.setBackground(ContextCompat.getDrawable(getBaseContext(), getCityDrawableByName(selectedCity)));
        }
        if (parent.getId() == R.id.spinner_categories) {
            if (((TextView) view) != null)
                //   selectedCategory = String.valueOf(((TextView) view).getText());
                selectedCategory = CategoriesNames[position];
            Log.d(TAG, "current onItemSelected:categories " + selectedCategory);

            populateCurrentFragment(custom_criteria, selectedCity, selectedCategory);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}



