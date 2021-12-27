package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_AddAlert_Dialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_AddAlert_Dialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_AddAlert_Dialog extends DialogFragment {

    private View fragment_view;
    private Fragment_AddAlert_Dialog.OnFragmentInteractionListener mListener;

    private String userID;
    public Bundle bundle;

    private DatabaseReference db_root_topics_tokens;
    private DatabaseReference db_root_tokens_topics;
    private DatabaseReference db_root_topics_users;
    private DatabaseReference db_root_users_topics;
    private LayoutInflater mInflator;
    private String kind = PATH_IHAVE;
    private double m_dbl_max_price = 1000f;
    private double m_dbl_min_price = 100f;
    private EditText m_et_max_price;
    private EditText m_et_min_price;
    private RadioButton m_rb_ineed;
    private RadioButton m_rb_ihave;
    private String m_alertState = PATH_ALERT_STATE_IHAVE;
    private EditText m_et_max_distance;
    private double m_dbl_max_distance;
    private DatabaseReference db_root_topics_names;
    private double[] prices;
    private String topic;
    private View dialogView;

    private String[] categoriesData;
    private String[] citiesData;
    private String[] categoriesLocale;
    private String[] citiesLocale;
    private AlertDialog alertDialog;


    public Fragment_AddAlert_Dialog() {
        // Required empty public constructor
    }

    public static Fragment_AddAlert_Dialog newInstance(String param1, String param2) {
        return new Fragment_AddAlert_Dialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dialogView = setUpDialogViews();

        return alertDialog = new MaterialAlertDialogBuilder(getActivity(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                /*.setTitle(getResources().getString(R.string.title_subscribe))
                              //  .setMessage(R.string.title_fragment_add_alert)
                               .setPositiveButton(R.string.add_alert, (dialog, which) -> {
                                   topic = form_UserSelection_Topic(dialogView);
                                   prices = createTopicAlert(dialogView, topic);
                                   Bundle result = new Bundle();
                                   result.putBoolean(BUNDLE_KEY_ADD_ALERT, true);
                                   // The child fragment needs to still set the result on its parent fragment manager
                                   getParentFragmentManager().setFragmentResult(BUNDLE_KEY_ADD_ALERT, result);
                                   Toast.makeText(getActivity(), getString(R.string.message_ifo_add_alert_added), Toast.LENGTH_SHORT).show();

                               })
                               .setNegativeButton(R.string.cancel, (dialog, id) -> {
                                   // User cancelled the dialog
                                   Toast.makeText(getActivity(), getString(R.string.message_ifo_add_alert_no), Toast.LENGTH_SHORT).show();
                               })*/


                /*    scrollView.addView(view);
                    view.addView(scrollView);*/
                .setView(dialogView)
                .create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        fragment_view = (FrameLayout) inflater.inflate(R.layout.fragment_add_alert, container, true);

        return fragment_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpAuthentication();
        initialDatabaseRefs();
        initialDataArrays();
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    private void setUpAuthentication() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        }
    }

    private void initialDatabaseRefs() {
        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();

        db_root_topics_users = db_root.child(PATH_TOPICS_USERS);
        db_root_users_topics = db_root.child(PATH_USERS_TOPICS);

        db_root_topics_tokens = db_root.child(PATH_TOPICS_TOKENS);
        db_root_tokens_topics = db_root.child(PATH_TOKENS_TOPICS);

        db_root_topics_names = db_root.child(PATH_TOPICS_NAMES);

    }


    private double getMinPrice(View view) {
        m_et_min_price = (EditText) view.findViewById(R.id.et_min_price);
        if (String.valueOf(m_et_min_price.getText()).equals("")) {
            return 0;
        }
        return m_dbl_min_price = Double.parseDouble(String.valueOf(m_et_min_price.getText()));

    }

    private double getMaxPrice(View view) {
        m_et_max_price = (EditText) view.findViewById(R.id.et_max_price);
        if (String.valueOf(m_et_max_price.getText()).equals("")) {
            return 0;
        }
        return m_dbl_max_price = Double.parseDouble(String.valueOf(m_et_max_price.getText()));

    }

    private double getMaxDistance(View view) {
        m_et_max_distance = view.findViewById(R.id.et_max_distance);
        if (TextUtils.isEmpty(String.valueOf(m_et_max_distance.getText()))) {
            return 0;
        }
        return m_dbl_max_distance = Double.parseDouble(String.valueOf(m_et_max_distance.getText()));

    }

    private void setAlertState(View view) {
        m_rb_ineed = (RadioButton) view.findViewById(R.id.rb_ineed);
        m_rb_ihave = (RadioButton) view.findViewById(R.id.rb_ihave);
        if (m_rb_ineed.isChecked()) {
            m_alertState = PATH_ALERT_STATE_INEED;
            kind = PATH_INEED;
        }


    }

    private void setUpSpinnerCategories(View view) {
        final Spinner spinner_categories = (Spinner) view.findViewById(R.id.spinner_categories2);
        //creating arrayAdapter_cities for the spinner_cities
        ArrayAdapter<String> arrayAdapter_categories = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, get_Categories_array_locale(getContext()));
        //   getResources().getStringArray(R.array.array_categories));

        arrayAdapter_categories.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner_categories.setAdapter(arrayAdapter_categories);
    }

    private void setUpSpinnerCities(View view) {
        final Spinner spinner_cities = (Spinner) view.findViewById(R.id.spinner_cities2);
        //creating arrayAdapter_cities for the spinner_cities
        ArrayAdapter<String> arrayAdapter_cities = new ArrayAdapter<>(requireContext(),
                R.layout.support_simple_spinner_dropdown_item, get_Cities_array_locale(getContext()));
        //  getResources().getStringArray(R.array.array_cities));

        arrayAdapter_cities.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner_cities.setAdapter(arrayAdapter_cities);
    }

    private View setUpDialogViews() {

        mInflator = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = mInflator.inflate(R.layout.fragment_add_alert, null);

        setUpSpinnerCities(dialogView);
        setUpSpinnerCategories(dialogView);
        setUpButtons(dialogView);

        return dialogView;
    }

    private void setUpButtons(View dialogView) {
        dialogView.findViewById(R.id.customPositiveButton).setOnClickListener((v) -> {
            topic = form_UserSelection_Topic(this.dialogView);
            prices = createTopicAlert(this.dialogView, topic);
            Bundle result = new Bundle();
            result.putBoolean(BUNDLE_KEY_ADD_ALERT, true);
            // The child fragment needs to still set the result on its parent fragment manager
            getParentFragmentManager().setFragmentResult(BUNDLE_KEY_ADD_ALERT, result);
            alertDialog.dismiss();
            Toast.makeText(getActivity(), getString(R.string.message_ifo_add_alert_added), Toast.LENGTH_SHORT).show();
        });
        dialogView.findViewById(R.id.customNegativeButton).setOnClickListener((v) -> {

            alertDialog.dismiss();
            Toast.makeText(getActivity(), getString(R.string.message_ifo_add_alert_no), Toast.LENGTH_SHORT).show();
        });
    }

    private String form_UserSelection_Topic(View view) {
        setAlertState(view);
        int city_position = ((Spinner) view.findViewById(R.id.spinner_cities2)).getSelectedItemPosition();
        int category_position = ((Spinner) view.findViewById(R.id.spinner_categories2)).getSelectedItemPosition();
        String city = citiesData[city_position];
        String category = categoriesData[category_position];

        // In cloud: topic="Items"+"_"+itemKind+"_"+itemCity+"_"+itemCategory;
        String topic = "Items" + "_" + kind + "_" + city + "_" + category;
        Log.d(TAG2, "generatedTopic: " + topic);
        return topic;
    }

    private double[] createTopicAlert(View view, final String selectedTopic) {

        FirebaseMessaging.getInstance().subscribeToTopic(selectedTopic).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG2, "subscribeToTopic onComplete: Failed");
                //   retrySubscribe(topic,prices);


            }
            storeTopicDataToDB_tokens_topics(m_alertState, selectedTopic, getMaxPrice(view), getMinPrice(view), getMaxDistance(view));
            storeTopicDataToDB_userIDs_topics(m_alertState, selectedTopic, getMaxPrice(view), getMinPrice(view), getMaxDistance(view));
            Log.d(TAG2, "subscribeToTopic onComplete: Successful");

        });
        return new double[]{getMaxPrice(view), getMinPrice(view)};
    }

    public void retrySubscribe(String topic, double[] prices) {
        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, getActivity().getString(R.string.message_error_add_alert), BaseTransientBottomBar.LENGTH_LONG).setAction(getActivity().getString(R.string.btn_retry), v -> {
            createTopicAlert(dialogView, topic);
        }).show();


    }

    private void storeTopicNamesToDB(String selectedTopic, double m_dbl_max_price, double m_dbl_min_price, double m_dbl_max_distance) {

        db_root_topics_names.child(m_alertState).child(selectedTopic).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null) {
                    //  currentData.setValue(true);
                    currentData.child(PATH_MAX_PRICE).setValue(m_dbl_max_price);
                    currentData.child(PATH_MIN_PRICE).setValue(m_dbl_min_price);
                    currentData.child(PATH_MAX_DISTANCE).setValue(m_dbl_max_distance);
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

    private void storeTopicDataToDB_tokens_topics(String m_alertState, String selectedTopic, double m_dbl_max_price, double m_dbl_min_price, double m_dbl_max_distance) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    db_root_topics_tokens.child(m_alertState).child(selectedTopic).child(token).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            if (currentData.getValue() == null) {
                                currentData.child(PATH_MAX_PRICE).setValue(m_dbl_max_price);
                                currentData.child(PATH_MIN_PRICE).setValue(m_dbl_min_price);
                                currentData.child(PATH_MAX_DISTANCE).setValue(m_dbl_max_distance);
                                return Transaction.success(currentData);
                            }
                            currentData.child(PATH_MAX_PRICE).setValue(m_dbl_max_price);
                            currentData.child(PATH_MIN_PRICE).setValue(m_dbl_min_price);
                            currentData.child(PATH_MAX_DISTANCE).setValue(m_dbl_max_distance);
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
                    db_root_tokens_topics.child(token).child(m_alertState).child(selectedTopic).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            if (currentData.getValue() == null) {
                                //currentData.setValue(true);
                                currentData.child(PATH_MAX_PRICE).setValue(m_dbl_max_price);
                                currentData.child(PATH_MIN_PRICE).setValue(m_dbl_min_price);
                                currentData.child(PATH_MAX_DISTANCE).setValue(m_dbl_max_distance);
                                return Transaction.success(currentData);
                            }
                            currentData.child(PATH_MAX_PRICE).setValue(m_dbl_max_price);
                            currentData.child(PATH_MIN_PRICE).setValue(m_dbl_min_price);
                            currentData.child(PATH_MAX_DISTANCE).setValue(m_dbl_max_distance);
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


                });

    }

    private void storeTopicDataToDB_userIDs_topics(String m_alertState, String selectedTopic, double m_dbl_max_price, double m_dbl_min_price, double m_dbl_max_distance) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    db_root_topics_users.child(m_alertState).child(selectedTopic).child(userID).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            if (currentData.getValue() == null) {
                                currentData.child(PATH_MAX_PRICE).setValue(m_dbl_max_price);
                                currentData.child(PATH_MIN_PRICE).setValue(m_dbl_min_price);
                                currentData.child(PATH_MAX_DISTANCE).setValue(m_dbl_max_distance);
                                return Transaction.success(currentData);
                            }
                            currentData.child(PATH_MAX_PRICE).setValue(m_dbl_max_price);
                            currentData.child(PATH_MIN_PRICE).setValue(m_dbl_min_price);
                            currentData.child(PATH_MAX_DISTANCE).setValue(m_dbl_max_distance);
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
                    db_root_users_topics.child(userID).child(m_alertState).child(selectedTopic).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            if (currentData.getValue() == null) {
                                //currentData.setValue(true);
                                currentData.child(PATH_MAX_PRICE).setValue(m_dbl_max_price);
                                currentData.child(PATH_MIN_PRICE).setValue(m_dbl_min_price);
                                currentData.child(PATH_MAX_DISTANCE).setValue(m_dbl_max_distance);
                                return Transaction.success(currentData);
                            }
                            currentData.child(PATH_MAX_PRICE).setValue(m_dbl_max_price);
                            currentData.child(PATH_MIN_PRICE).setValue(m_dbl_min_price);
                            currentData.child(PATH_MAX_DISTANCE).setValue(m_dbl_max_distance);
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


                });

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
        categoriesData = get_Categories_array_data(getContext());
        citiesData = get_Cities_array_data(getContext());
        categoriesLocale = get_Categories_array_locale(getContext());
        citiesLocale = get_Cities_array_locale(getContext());
    }

}



