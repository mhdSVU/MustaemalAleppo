package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jrummyapps.android.animations.Technique;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import mohammedyouser.com.mustaemalaleppo.Domain.Add_NewItem_Activity;
import mohammedyouser.com.mustaemalaleppo.Domain.Category;
import mohammedyouser.com.mustaemalaleppo.Domain.City;
import mohammedyouser.com.mustaemalaleppo.Domain.SetUpContactInfo_Activity;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

public class Ineed_Ihave_Activity extends AppCompatActivity  implements
        NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        View.OnClickListener,
        Ineed_Fragment.OnFragmentInteractionListener,
        Ihave_Fragment.OnFragmentInteractionListener,
        Add_Alert_Fragment.OnFragmentInteractionListener


{

    private ImageView imgView_UserImage;
    private TextView txtName, txtEmail;

    private String userName;
    private String userImage;
    private String userEmail;
    private String userPhoneNumber;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private My_FragmentPagerAdapter_Ineed_Ihave myFragmentPagerAdapter;

    private SearchView searchView;
    private SearchManager searchManager;

    private City[] cities;

    private Category[] categories;

    private FirebaseAuth auth;
    private String userID;


    private DatabaseReference db_root_users;

    //TODO
    private String selectedCategory;
    private String selectedCity_Name;

    private Toolbar toolbar;
    private ImageButton img_btn_refresh;
    
    private String custom_criteria=PATH_ITEM_DATE_AND_TIME_REVERSE;

    private CollapsingToolbarLayout collapseLayout;

    private ImageView imageView_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ineed__ihave);

        setUpAuthentication();

        doMainInitializations();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void doMainInitializations() {
        /*Apps that rely on the Play Services SDK should always check the device for
        a compatible Google Play services APK before accessing Google Play services features.
        It is recommended to do this in two places:
        in the main activity's onCreate() method, and in its onResume() method.*/
        //TODO isGooglePlayServicesAvailable(Context context)
        //If the device doesn't have a compatible version of Google Play services
        //TODO GoogleApiAvailability.makeGooglePlayServicesAvailable()

        searchManager= (SearchManager) getSystemService(SEARCH_SERVICE);
        tabLayout= findViewById(R.id.tabLayout);
        viewPager= findViewById(R.id.viewPager);
        collapseLayout= findViewById(R.id.collapse);
        imageView_city= findViewById(R.id.imageView_city);

        img_btn_refresh=findViewById(R.id.img_btn_refresh);
        img_btn_refresh.setOnClickListener(this);

        ((Spinner)findViewById(R.id.spinner_categories)).setSelection(0);
        ((Spinner)findViewById(R.id.spinner_cities)).setSelection(0);


        initialDatabaseRefs();

        setUpMyNavigationDrawer();


        initialCities();
        initialCategories();

        setUpMyViewPager();
        tabLayout.setupWithViewPager(viewPager);
        setUpMyTabLayout();


    }
    private void setUpAuthentication() {
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            userID=auth.getCurrentUser().getUid();}
        else {
            Intent intent=new Intent(this,AuthActivity.class);
            startActivity(intent);
        }

    }




    private void initialDatabaseRefs() {

        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);
    }


    private void setUpMyNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Navigation view header
        View navHeader = navigationView.getHeaderView(0);
        txtName =  navHeader.findViewById(R.id.textView_userName);
        txtEmail = navHeader.findViewById(R.id.textView_adding_state);
        imgView_UserImage =  navHeader.findViewById(R.id.imageView_userImage);
        ImageView imgLogo = navHeader.findViewById(R.id.imageView_logo);

        // initial nav menu header data
        initialNavHeader();

        // load nav menu header data
        loadNavHeader();



    }

    private void initialNavHeader() {
        userName=(getString(R.string.default_val_user_name));
        userImage =String.valueOf(Uri.EMPTY);
        imgView_UserImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_white_24dp));
        userEmail =(getString(R.string.default_val_user_email));
        userPhoneNumber=(getString(R.string.default_val_user_phone_number));

    }


    private void initialCities() {

        // gaining activity_single_item reference for spinner_cities
        Spinner spinner_cities = (Spinner) findViewById(R.id.spinner_cities);

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
        arrayAdapter_cities.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_cities.setAdapter(arrayAdapter_cities);


        // set listener for the spinner_cities...Done!
        spinner_cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCity_Name=cities[adapterView.getSelectedItemPosition()].getName();
                // int selectedCity_Code=cities[adapterView.getSelectedItemPosition()].getCode();

                //   collapseLayout.setTitle(getString(R.string.collapsing_toolbar,selectedCity_Name));
                imageView_city.setImageDrawable(getBaseContext().getDrawable(getCityDrawableByName(selectedCity_Name)));


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initialCategories() {

        // gaining activity_single_item reference for spinner_categories
        Spinner spinner_categories = (Spinner) findViewById(R.id.spinner_categories);

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
        arrayAdapter_categories.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner_categories.setAdapter(arrayAdapter_categories);

        // set listener for the spinner_cities...Done!
        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = categories[adapterView.getSelectedItemPosition()].getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
    }


    private void setUpMyViewPager() {
        ArrayList<String> tabs=new ArrayList<>();
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
                    ((ImageView) tabLayout.getTabAt(position).getCustomView().findViewById(R.id.android_R_id_icon)).setImageDrawable(getDrawable(R.drawable.icons_empty_box_24_red));

                } else {

                    ((ImageView) tabLayout.getTabAt(position).getCustomView().findViewById(R.id.android_R_id_icon)).setImageDrawable(getDrawable(R.drawable.icons_full_box_24_red));


                }



            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if  (searchView!=null &&!(searchView.isIconified()) )
                {
                    searchView.onActionViewCollapsed();
                    toolbar.setBackgroundColor(getResources().getColor(R.color.fui_transparent));


                    populateCurrentFragment(custom_criteria);


                }

            }
        });
        myFragmentPagerAdapter =new My_FragmentPagerAdapter_Ineed_Ihave(getSupportFragmentManager(),tabs.size(),tabs);
        viewPager.setAdapter(myFragmentPagerAdapter);



    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser()!=null){
            userID=auth.getCurrentUser().getUid();}
        else {
            Intent intent=new Intent(this,AuthActivity.class);
            startActivity(intent);
        }
    }

    private void setUpMyTabLayout() {

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.selcted_tab));
        tabLayout.setTabTextColors(getResources().getColor(R.color.normal),getResources().getColor(R.color.selcted_tab));


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
        final String userID = auth.getCurrentUser().getUid();

        userName = setUserName(userID);
        userEmail = setUserEmail(userID);
        userImage = setUserImage(userID);
        userPhoneNumber = setUserPhoneNumber(userID);

        txtName.setText(userName);
        txtEmail.setText(userEmail);

        // Loading profile image
        if (userImage != null) {
            setImage_circle(this, Uri.parse(userImage), 0.3f, imgView_UserImage);
        }
    }

    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView)
    {
        Glide.with(context).load(imageURL)//TODO
               /* .crossFade()*/
                .thumbnail(thumbnail)
                .apply(RequestOptions.circleCropTransform())

/*
                .bitmapTransform(new CircleTransform(context))
*/
/*
                .diskCacheStrategy(DiskCacheStrategy.ALL)
*/
                .into(imageView)


        ;


    }

    public void populateCurrentFragment(String custom_criteria) {
        showProgressDialog(this,getString(R.string.message_info_LOADING_DATA),getString(R.string.message_info_PLEASE_WAIT));

         int position = tabLayout.getSelectedTabPosition();
        Fragment current_fragment = myFragmentPagerAdapter.getFragment(position);

        if (position == 0) {
            Query customQuery = ((Ineed_Fragment) current_fragment).createCustomQuery(this, PATH_INEED,custom_criteria );

            ((Ineed_Fragment) current_fragment).populateMyViewHolder(customQuery);


        } else {
            Query customQuery = ((Ihave_Fragment) current_fragment).createCustomQuery(this, PATH_IHAVE, custom_criteria);

            ((Ihave_Fragment) current_fragment).populateMyViewHolder(customQuery);



        }
        hideProgressDialog();




    }

    public void populateCurrentFragment_Search(String searchText, String searchCriteria) {
        showProgressDialog(this,getString(R.string.message_info_LOADING_DATA),getString(R.string.message_info_PLEASE_WAIT));

        int position = tabLayout.getSelectedTabPosition();
        Fragment current_fragment = myFragmentPagerAdapter.getFragment(position);

        if (position == 0) {
            Query customQuery = ((Ineed_Fragment) current_fragment).createCustomQuery(this, PATH_INEED, searchCriteria);

            Query final_customQuery = customQuery.startAt(searchText).endAt(searchText+"\uf8ff");

            ((Ineed_Fragment) current_fragment).populateMyViewHolder(final_customQuery);
        } else {
            Query customQuery = ((Ihave_Fragment) current_fragment).createCustomQuery(this, PATH_IHAVE, searchCriteria);
            Query final_customQuery = customQuery.startAt(searchText).endAt(searchText+"\uf8ff");
            ((Ihave_Fragment) current_fragment).populateMyViewHolder(final_customQuery);
        }
        hideProgressDialog();

    }



   @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent();
    }




    private void handleIntent() {

        if(getIntent().getAction()!= null) {
            if (getIntent().getAction().equals(Intent.ACTION_SEARCH)) {
                String query = getIntent().getStringExtra(SearchManager.QUERY);
                doSearch(query);
            }

        }


    }

    private void doSearch(String query) {

        populateCurrentFragment_Search(query,PATH_ITEM_TITLE);

    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView= (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(this);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.hint_search_item));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setOnClickListener(this);





        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify activity_single_item parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sort_by_title) {
            custom_criteria=PATH_ITEM_TITLE;
            populateCurrentFragment(custom_criteria);
            return true;
        }
        if (id == R.id.action_sort_by_price) {
            custom_criteria=PATH_ITEM_PRICE;
            populateCurrentFragment(custom_criteria);

            return true;
        }
        if (id == R.id.action_sort_by_date) {
            custom_criteria=PATH_ITEM_DATE_AND_TIME_REVERSE;
            populateCurrentFragment(custom_criteria);

            return true;
        }


        if (id == R.id.action_add_alert) {
            //addAlert("cars");
            show_Add_Alert_Dialog();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void show_Add_Alert_Dialog() {
        Bundle bundle=new Bundle();
        if(tabLayout.getSelectedTabPosition()==0)
        {
            bundle.putString("kind","Ineed");

        }
            else
        {
            bundle.putString("kind","Ihave");

        }
        Add_Alert_Fragment add_alert_fragment=new Add_Alert_Fragment();
        add_alert_fragment.setArguments(bundle);
        add_alert_fragment.show(getSupportFragmentManager(),"AddAlertDialog");
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_ihave_item) {

            Intent currentUserItemsIntent =new Intent(this,Add_NewItem_Activity.class);
            currentUserItemsIntent.putExtra(INTENT_KEY__STATEVALUE, INTENT_VALUE__STATEVALUE_IHAVE);
            currentUserItemsIntent.putExtra(INTENT_KEY__STATELABEL, INTENT_VALUE__STATELABEL_IHAVE);
            currentUserItemsIntent.putExtra(INTENT_KEY_USER_ID,userID);
            currentUserItemsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(currentUserItemsIntent);

        } else if (id == R.id.nav_add_ineed_item) {


            Intent currentUserItemsIntent =new Intent(this,Add_NewItem_Activity.class);
            currentUserItemsIntent.putExtra(INTENT_KEY__STATEVALUE, INTENT_VALUE__STATEVALUE_INEED);
            currentUserItemsIntent.putExtra(INTENT_KEY__STATELABEL, INTENT_VALUE__STATELABEL_INEED);
            currentUserItemsIntent.putExtra(INTENT_KEY_USER_ID,userID);
            currentUserItemsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(currentUserItemsIntent);


        } else if (id == R.id.nav_my_items) {

            startCurrentUserActivity();


        } else if (id == R.id.nav_set_up_my_account) {


            Intent setUpAccountIntent =new Intent(this,SetUpContactInfo_Activity.class);
            setUpAccountIntent.putExtra(INTENT_KEY_USER_ID,userID);
            startActivity(setUpAccountIntent);

        } else if (id == R.id.nav_sign_out) {
            SignOut();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SignOut() {
        auth.signOut();
        Intent intent =new Intent(this,AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public boolean onClose() {
        toolbar.setBackgroundColor(getResources().getColor(R.color.fui_transparent));

        populateCurrentFragment(custom_criteria);
        return false;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        populateCurrentFragment_Search(query,PATH_ITEM_TITLE);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        populateCurrentFragment_Search(newText,PATH_ITEM_TITLE);
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==searchView.getId())
        {

            toolbar.setBackgroundColor(getResources().getColor(R.color.grey_500));

        } 
        
        if(v.getId()==img_btn_refresh.getId())
        {

            Technique.ROTATE_IN.playOn(img_btn_refresh);
            populateCurrentFragment(custom_criteria);
            Toast.makeText(Ineed_Ihave_Activity.this, getString(R.string.message_info_refreshed_ok), Toast.LENGTH_SHORT).show();


        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    //TODO
    private int getCityDrawableByName(String selectedCity_Name) {//TODO: change drawables
        switch (selectedCity_Name)
        {
            case "Damascus":
                return R.drawable.damascus;
            case "Aleppo":
                return R.drawable.aleppo_castle;

            case "Homs":
                return R.drawable.homs;
            case "Hama":
                return R.drawable.hama;
            case "Lattakia":
                return R.drawable.lattakia;
            case "Idleb":
                return R.drawable.idleb;
            case "Dair Zor":
                return R.drawable.all_cities;
            case "Hasaka":
                return R.drawable.all_cities;
            case "Daraa":
                return R.drawable.all_cities;
            case "Reef Dimashque":
                return R.drawable.all_cities;
            case "Tartus":
                return R.drawable.all_cities;
            default:
                return R.drawable.all_cities;
        }

    }




    private void startCurrentUserActivity() {
        Intent mainIntent =new Intent(Ineed_Ihave_Activity.this,Ineed_Ihave_Activity_CurrentUser.class);

        mainIntent.putExtra(INTENT_KEY__STATEVALUE,INTENT_VALUE__STATEVALUE_INEED);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);

    }

    private String setUserName(final String userID) {


        db_root_users.child(userID).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               userName = (String) dataSnapshot.child(PATH_USER_NAME).getValue();
                if(userName!=null)
                {
                    txtName.setText(userName);

                }
                else {
                    txtName.setText(getString(R.string.default_val_user_name));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return userName;
    }
    private String setUserEmail(final String userID) {

        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    userEmail = (String) dataSnapshot.child(userID).child(PATH_USER_EMAIL).getValue();
                txtEmail.setText(userEmail);
                if(txtEmail!=null)
                {
                    txtEmail.setText(userEmail);

                }
                else {
                    txtEmail.setText((getString(R.string.default_val_user_email)));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userEmail;


    }
    private String setUserImage(final String userID) {

        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    userImage = (String) dataSnapshot.child(userID).child(PATH_USER_IMAGE).getValue();
                // Loading profile image
                if (userImage != null) {
                    setImage_circle(getBaseContext(), Uri.parse(userImage), 0.3f, imgView_UserImage);
                }
                else {

                    imgView_UserImage.setImageDrawable(getDrawable(R.drawable.ic_account_circle_white_24dp));
                }
                           }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userImage;

    }
    private String setUserPhoneNumber(final String userID) {

        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    userPhoneNumber = (String) dataSnapshot.child(userID).child(PATH_USER_PHONE_NUMBER).getValue();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userPhoneNumber;


    }


    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if  (!(searchView.isIconified()) )
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.fui_transparent));

            searchView.onActionViewCollapsed();
            populateCurrentFragment(custom_criteria);


        }
        else
        {
            moveTaskToBack(true);
        }
    }


   /* private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected(thi);
        if (!isConnected) {
            //show a No Internet Alert or Dialog
        }
    }
   *//* showProgressDialog(
            ineed_ihave_activity,
            context.getString(R.string.title_network_con_not_found),
                        context.getString(R.string.message_info_PLEASE_WAIT));*/



}



