package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import mohammedyouser.com.mustaemalaleppo.Data.MyViewHolder;
import mohammedyouser.com.mustaemalaleppo.Domain.Add_NewItem_Activity;
import mohammedyouser.com.mustaemalaleppo.Domain.Display_and_Manage_SingleItem_Activity;
import mohammedyouser.com.mustaemalaleppo.Domain.Item;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_CAT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_CITY;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__PATH_STATE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__STATELABEL;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__STATEVALUE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_VALUE__STATELABEL_INEED;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_VALUE__STATEVALUE_IHAVE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_VALUE__STATEVALUE_INEED;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.ITEM_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ALL_CATEGORIES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ALL_CITIES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ALL_ITEMS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_IHAVE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_INEED;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEMS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEMS_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEM_DATE_AND_TIME_REVERSE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS_IDS;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Ineed_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Ineed_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Ineed_Fragment extends Fragment   implements View.OnClickListener {

    private View fragment_view;
    public ProgressBar progressBar;

    private Ineed_Fragment.OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;

    private FirebaseAuth auth;

    private DatabaseReference db_root;
    private DatabaseReference db_root_items;
    private DatabaseReference db_root_items_users;
    private DatabaseReference all_Items_DB_Ref;
    private DatabaseReference uers_IDs_DB_Ref;


    private String selectedCity;
    private String selectedCategory;
    private String userID;
    private Query query;

    public Bundle bundle;
    public FirebaseRecyclerAdapter<Item, MyViewHolder> myadapter;


    public Ineed_Fragment() {
        // Required empty public constructor
    }

    public static Ineed_Fragment newInstance(String param1, String param2) {
        return new Ineed_Fragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        fragment_view = (FrameLayout) inflater.inflate(R.layout.fragment_i_need, container, false);
        FloatingActionButton fab = (FloatingActionButton) fragment_view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mRecyclerView = fragment_view.findViewById(R.id.recyclerView_i_need);


        progressBar = (ProgressBar) fragment_view.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);


        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        initialDatabaseRefs();

        setUpAuthentication();

        return fragment_view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        selectedCategory = ((Spinner) getActivity().findViewById(R.id.spinner_categories)).getSelectedItem().toString();
        selectedCity = ((Spinner) getActivity().findViewById(R.id.spinner_cities)).getSelectedItem().toString();

        mRecyclerView = fragment_view.findViewById(R.id.recyclerView_i_need);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        populateMyViewHolder(createCustomQuery(getActivity(), PATH_INEED, PATH_ITEM_DATE_AND_TIME_REVERSE));


    }

    public Query createCustomQuery(Activity fragmentActivity, String kind, String searchCriteria) {

        switch (fragmentActivity.getClass().getName()) {
            case "mohammedyouser.com.mustaemalaleppo.UI.Ineed_Ihave_Activity": {

                selectedCategory = ((Spinner) getActivity().findViewById(R.id.spinner_categories)).getSelectedItem().toString();
                selectedCity = ((Spinner) getActivity().findViewById(R.id.spinner_cities)).getSelectedItem().toString();

                query = getAll_Items_DB_Ref(kind).orderByChild(searchCriteria);
                getAll_Items_DB_Ref(kind).keepSynced(true);
            }


            break;
            case "mohammedyouser.com.mustaemalaleppo.UI.Ineed_Ihave_Activity_CurrentUser": {
                if (userID != null) {

                    selectedCategory = ((Spinner) getActivity().findViewById(R.id.spinner_categories)).getSelectedItem().toString();
                    selectedCity = ((Spinner) getActivity().findViewById(R.id.spinner_cities)).getSelectedItem().toString();

                    query = getUsersIDs_DB_Ref(kind).child(PATH_USERS_IDS).child(userID).orderByChild(searchCriteria);
                }

                getUsersIDs_DB_Ref(kind).keepSynced(true);

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
        db_root_items = db_root.child(PATH_ITEMS);
        db_root_items_users = db_root.child(PATH_ITEMS_USERS);

    }


    private DatabaseReference getUsersIDs_DB_Ref(String kind) {
        if (selectedCity.equals(PATH_ALL_CITIES) && selectedCategory.equals(PATH_ALL_CATEGORIES)) {
            uers_IDs_DB_Ref = db_root_items_users.child(kind).child(PATH_ALL_ITEMS);

        } else {

            uers_IDs_DB_Ref = db_root_items_users.child(kind).child(selectedCity).child(selectedCategory);
        }

        return uers_IDs_DB_Ref;
    }

    private DatabaseReference getAll_Items_DB_Ref(String kind) {

        if (selectedCity.equals(PATH_ALL_CITIES) && selectedCategory.equals(PATH_ALL_CATEGORIES)) {
            all_Items_DB_Ref = db_root_items.child(kind).child(PATH_ALL_ITEMS);

        } else {
            all_Items_DB_Ref = db_root_items.child(kind).child(selectedCity).child(selectedCategory);

        }

        return all_Items_DB_Ref;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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


    public void populateMyViewHolder(Query myQuery) {

        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(myQuery, Item.class)
                        .build();
        myadapter = new FirebaseRecyclerAdapter<Item, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_row, parent, false);

                return new MyViewHolder(view);
            }

            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Item model) {

                final String item_ID = getRef(position).getKey();

                holder.setItemTitle(model.getItemTitle());
                holder.setItemPrice(model.getItemPrice());
                holder.setItemImage(getContext(), model.getItemImage());


                holder.itemView.findViewById(R.id.img_btn_itemImage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        selectedCategory = ((Spinner) getActivity().findViewById(R.id.spinner_categories)).getSelectedItem().toString();
                        selectedCity = ((Spinner) getActivity().findViewById(R.id.spinner_cities)).getSelectedItem().toString();
                        Intent singleItemIntent = new Intent(getContext(), Display_and_Manage_SingleItem_Activity.class);
                        singleItemIntent.putExtra(ITEM_ID, item_ID);
                        singleItemIntent.putExtra(INTENT_KEY__PATH_STATE, PATH_INEED);
                        singleItemIntent.putExtra(INTENT_KEY_ITEM_CAT, selectedCategory);
                        singleItemIntent.putExtra(INTENT_KEY_ITEM_CITY, selectedCity);
                        singleItemIntent.putExtra(INTENT_KEY__STATEVALUE, INTENT_VALUE__STATEVALUE_IHAVE);

                        startActivity(singleItemIntent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectedCategory = ((Spinner) getActivity().findViewById(R.id.spinner_categories)).getSelectedItem().toString();
                        selectedCity = ((Spinner) getActivity().findViewById(R.id.spinner_cities)).getSelectedItem().toString();
                        Intent singleItemIntent = new Intent(getContext(), Display_and_Manage_SingleItem_Activity.class);
                        singleItemIntent.putExtra(ITEM_ID, item_ID);
                        singleItemIntent.putExtra(INTENT_KEY__PATH_STATE, PATH_IHAVE);
                        singleItemIntent.putExtra(INTENT_KEY_ITEM_CAT, selectedCategory);
                        singleItemIntent.putExtra(INTENT_KEY_ITEM_CITY, selectedCity);
                        singleItemIntent.putExtra(INTENT_KEY__STATEVALUE, INTENT_VALUE__STATEVALUE_IHAVE);

                        startActivity(singleItemIntent);
                    }
                });

                if(progressBar!=null) {
                    progressBar.setVisibility(View.GONE);
                }
            }


        };

        mRecyclerView.setAdapter(myadapter);

            myadapter.startListening();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                Intent intent = new Intent(getContext(), Add_NewItem_Activity.class);
                intent.putExtra(INTENT_KEY__STATEVALUE, INTENT_VALUE__STATEVALUE_INEED);
                intent.putExtra(INTENT_KEY__STATELABEL, INTENT_VALUE__STATELABEL_INEED);
                intent.putExtra(INTENT_KEY_USER_ID, userID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onStart() {
        super.onStart();
        myadapter.startListening();

    }


    @Override
    public void onStop() {
        super.onStop();
        myadapter.stopListening();
    }
}



