package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jrummyapps.android.animations.Technique;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import mohammedyouser.com.mustaemalaleppo.Data.ViewHolder_Item;
import mohammedyouser.com.mustaemalaleppo.Domain.Activity_Add_Item;
import mohammedyouser.com.mustaemalaleppo.Domain.Activity_Display_Modify_Remove_Item;
import mohammedyouser.com.mustaemalaleppo.Data.Item;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_CAT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_CITY;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__PATH_STATE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__STATELABEL;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__STATE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_VALUE__STATELABEL_IHAVE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_VALUE__STATE_IHAVE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ALL_CATEGORIES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ALL_CITIES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ALL_ITEMS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_IHAVE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_IHAVE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_INEED;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEMS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEM_CATEGORY;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEM_CITY;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS_FAVORITES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USER_ITEMS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEM_DATE_AND_TIME_REVERSE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS_IDS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG2;

/*import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import androidx.recyclerview.widget.RecyclerView;*/

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Ihave.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Ihave#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Ihave extends Fragment implements View.OnClickListener {


    private View fragment_view;

    private RecyclerView mRecyclerView;

    private DatabaseReference db_ref_items;
    private DatabaseReference db_root_items_users;

    private String userID;
    private Query query;

    public Bundle bundle;
    public ProgressBar progressBar;

    private DatabaseReference db_root_users_favorites;
    private boolean favorite_filled = false;
    private String item_topic;
    private GridLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<Item, ViewHolder_Item> mFirebaseAdapter;
    private HashMap<String, Boolean> map_favoriteItems = new HashMap<>();
    private DatabaseReference db_root_items;

    private CheckBox clickedFavoriteCb;
    private String clickedItem_ID;
    private String itemState = PATH_IHAVE;



    public Fragment_Ihave() {
        // Required empty public constructor
    }


    public static Fragment_Ihave newInstance(String param1, String param2) {
        return new Fragment_Ihave();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragment_view = (FrameLayout) inflater.inflate(R.layout.fragment_i_have, container, false);
        return fragment_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mRecyclerView = view.findViewById(R.id.recyclerView_i_have);

        progressBar = view.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);

        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        initialDatabaseRefs();

        setUpAuthentication();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = fragment_view.findViewById(R.id.recyclerView_i_have);


        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        populate_ViewHolder_Item(createCustomQuery(getActivity(), PATH_ALL_CITIES, PATH_ALL_CATEGORIES, PATH_IHAVE, PATH_ITEM_DATE_AND_TIME_REVERSE));
        mFirebaseAdapter.startListening();


    }

    @Override
    public void onResume() {
        super.onResume();
        reset_favoriteState_clickedItem();

    }


    private void reset_favoriteState_clickedItem() {
        if (clickedFavoriteCb != null && clickedItem_ID != null) {
            clickedFavoriteCb.setChecked(false);
            getRef_FavoriteItems(itemState).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot_topics) {
                    for (DataSnapshot dataSnapshot_topic : snapshot_topics.getChildren()) {
                        if (dataSnapshot_topic.hasChild(clickedItem_ID))
                            clickedFavoriteCb.setChecked(true);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public Query createCustomQuery(Activity fragmentActivity, String selectedCity, String selectedCategory, String itemState, String searchCriteria) {

        switch (fragmentActivity.getClass().getName()) {
            case "mohammedyouser.com.mustaemalaleppo.UI.Activity_Ineed_Ihave": {

                query = getAll_Items_DB_Ref(itemState, selectedCity, selectedCategory).orderByChild(searchCriteria);
            }

            break;
            case "mohammedyouser.com.mustaemalaleppo.UI.Activity_Ineed_Ihave_CurrentUser": {
                if (userID != null) {
                    query = getUsersIDs_DB_Ref(itemState, selectedCity, selectedCategory).child(PATH_USERS_IDS).child(userID).orderByChild(searchCriteria);
                }
            }
            break;
        }
        return query;
    }


    private void setUpAuthentication() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        }
    }

    private void initialDatabaseRefs() {
        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();
        db_ref_items = db_root.child(PATH_ITEMS);
        db_root_users_favorites = db_root.child(PATH_USERS_FAVORITES);
        db_root_items=db_root.child(PATH_ITEMS);
        db_root_items_users = db_root.child(PATH_USER_ITEMS);




    }


    private DatabaseReference getUsersIDs_DB_Ref(String itemState,String selectedCity,String selectedCategory){
        DatabaseReference uers_IDs_DB_Ref;
        if (selectedCity.equals(PATH_ALL_CITIES) && selectedCategory.equals(PATH_ALL_CATEGORIES)) {
            uers_IDs_DB_Ref = db_root_items_users.child(itemState).child(PATH_ALL_ITEMS);

        } else {

            uers_IDs_DB_Ref = db_root_items_users.child(itemState).child(selectedCity).child(selectedCategory);
        }

        return uers_IDs_DB_Ref;
    }

    private DatabaseReference getAll_Items_DB_Ref(String itemState, String selectedCity, String selectedCategory) {

        DatabaseReference all_Items_DB_Ref;
        if (selectedCity.equals(PATH_ALL_CITIES) && selectedCategory.equals(PATH_ALL_CATEGORIES)) {
            all_Items_DB_Ref = db_ref_items.child(itemState).child(PATH_ALL_ITEMS);

        } else {
            all_Items_DB_Ref = db_ref_items.child(itemState).child(selectedCity).child(selectedCategory);

        }

        return all_Items_DB_Ref;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify activity_single_item parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_by_title) {
            return true;
        }
        if (id == R.id.action_sort_by_price) {
            return true;
        }
        if (id == R.id.action_sort_by_date) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void populate_ViewHolder_Item(Query myQuery) {
        build_initialMap_FavoriteCbs();

        FirebaseRecyclerOptions<Item> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(myQuery, Item.class).build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Item, ViewHolder_Item>(firebaseRecyclerOptions) {
            @NonNull
            @Override
            public ViewHolder_Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_row_item, parent, false);

                return new ViewHolder_Item(view, getContext());
            }

            protected void onBindViewHolder(@NonNull ViewHolder_Item viewHolder, int position, @NonNull Item model) {

                item_topic = form_selected_Topic(PATH_IHAVE, model.getItemCity(), model.getItemCategory());
                final String item_ID = getRef(position).getKey();

                populateViews(model,item_ID,viewHolder);

                ((CheckBox) viewHolder.get_checkBox_Favorite()).setOnClickListener((View.OnClickListener) v -> {
                    Technique.PULSE.playOn(v);
                    handle_FavoriteCb_click(PATH_IHAVE,item_ID, viewHolder);
                });

     /*           ((ImageButton) viewHolder.get_imgButton_itemImage()).setOnClickListener(v -> {
                    startDisplayItemActivity(item_ID, model.getItemCity(), model.getItemCategory());
                    Log.d("TAG", "onBindViewHolder: " + item_ID + "\t" + model.getItemCity() + "\t" + model.getItemCategory());

                });*/

                viewHolder.getMyItemView().setOnClickListener(v -> {
                    clickedFavoriteCb = v.findViewById(R.id.cb_favorite);
                    clickedItem_ID = item_ID;
                    startDisplayItemActivity(item_ID, model.getItemCity(), model.getItemCategory());
                    Log.d("TAG", "onBindViewHolder: " + item_ID + "\t" + model.getItemCity() + "\t" + model.getItemCategory());

                });

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

        };

        mRecyclerView.setAdapter(mFirebaseAdapter);
        mFirebaseAdapter.startListening();

    }


    private void startDisplayItemActivity(String item_ID, String itemCity, String itemCategory) {
        Intent singleItemIntent = new Intent(getContext(), Activity_Display_Modify_Remove_Item.class);
        singleItemIntent.putExtra(INTENT_KEY_ITEM_ID, item_ID);
        singleItemIntent.putExtra(INTENT_KEY__PATH_STATE, PATH_IHAVE);
        singleItemIntent.putExtra(INTENT_KEY_ITEM_CAT, itemCategory);
        singleItemIntent.putExtra(INTENT_KEY_ITEM_CITY, itemCity);
        singleItemIntent.putExtra(INTENT_KEY__STATE, INTENT_VALUE__STATE_IHAVE);
        startActivity(singleItemIntent);
    }


    private void populateViews(Item model, String item_ID, ViewHolder_Item viewHolder) {
        viewHolder.setItemTitle(model.getItemTitle());
        viewHolder.setItemPrice(model.getItemPrice());
        viewHolder.setItemImage(getContext(), model.getItemImage());
        initial_FavoriteCbs(map_favoriteItems, item_ID, viewHolder);
    }


    private void handle_FavoriteCb_click(String itemState,String item_ID, ViewHolder_Item viewHolder) {
        db_root_items.child(itemState).child(PATH_ALL_ITEMS).child(item_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                item_topic =form_selected_Topic(itemState,
                        String.valueOf(snapshot.child(PATH_ITEM_CITY).getValue()),
                        String.valueOf(snapshot.child(PATH_ITEM_CATEGORY).getValue()));
                Log.d("GTAG", "onDataChange: "+ item_topic);
                if (!map_favoriteItems.containsKey(item_ID)) {
                    Log.d("ATAG", "onBindViewHolder: map does not contain");
                    viewHolder.get_checkBox_Favorite().setChecked(true);// for UI
                    getRef_FavoriteItem(PATH_IHAVE, item_topic, item_ID).setValue(true);//for DB
                    map_favoriteItems.put(item_ID, true);//for next check

                } else {
                    Log.d("ATAG", "onBindViewHolder: map contains");

                    viewHolder.get_checkBox_Favorite().setChecked(false);// for UI
                    getRef_FavoriteItem(PATH_IHAVE, item_topic,item_ID).removeValue();//for DB
                    map_favoriteItems.remove(item_ID);//for next check
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

    }


    private void initial_FavoriteCbs(HashMap<String, Boolean> map_favoriteItems, String item_ID, ViewHolder_Item viewHolder) {
        if (!map_favoriteItems.containsKey(item_ID)) {

            viewHolder.get_checkBox_Favorite().setChecked(false);
        } else {
            viewHolder.get_checkBox_Favorite().setChecked(true);
        }

    }

    private void build_initialMap_FavoriteCbs() {
        map_favoriteItems.clear();
        Log.d("ATAG", "populate_ViewHolder_Item: ");
        getRef_FavoriteItems(PATH_IHAVE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_topics) {

                for (DataSnapshot dataSnapshot_topic : snapshot_topics.getChildren()) {
                    for (DataSnapshot favoriteItem : dataSnapshot_topic.getChildren()) {
                        map_favoriteItems.put(favoriteItem.getKey(), true);
                        Log.d("TAGt", "onDataChange: " + favoriteItem.getKey());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private DatabaseReference getRef_FavoriteItem(String itemState, String item_topic,String item_ID) {

        return db_root_users_favorites.child(userID).child(itemState).child(item_topic).child(item_ID);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                Intent intent = new Intent(getContext(), Activity_Add_Item.class);
                intent.putExtra(INTENT_KEY__STATE, INTENT_VALUE__STATE_IHAVE);
                intent.putExtra(INTENT_KEY__STATELABEL, INTENT_VALUE__STATELABEL_IHAVE);
                intent.putExtra(INTENT_KEY_USER_ID, userID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFirebaseAdapter.stopListening();

    }

    private String form_selected_Topic(String itemState, String itemCity, String itemCategory) {

        // In cloud: topic="Items"+"_"+itemKind+"_"+itemCity+"_"+itemCategory;
        String topic = "Items" + "_" + itemState + "_" + itemCity + "_" + itemCategory;
        Log.d(TAG2, "generatedTopic: " + topic);
        return topic;
    }

    private DatabaseReference getRef_FavoriteItems(String itemState) {
        return db_root_users_favorites.child(userID).child(itemState);
    }




    public void changeRecyclerViewColumnCount() {
        ViewGroup viewGroup=getActivity().findViewById(R.id.fl_frag_ihave);
        fragment_view = (FrameLayout) getLayoutInflater().inflate(R.layout.fragment_i_have, viewGroup, false);

        mRecyclerView = fragment_view.findViewById(R.id.recyclerView_i_have);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(getContext(), 4);

        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(getContext(), 2);

        }
        mLayoutManager.setStackFromEnd(false);
        mLayoutManager.setReverseLayout(false);
        int offset = mRecyclerView.computeVerticalScrollOffset();

        //mLayoutManager.scrollToPositionWithOffset(0,-1*offset);


        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setReverseLayout(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

}

