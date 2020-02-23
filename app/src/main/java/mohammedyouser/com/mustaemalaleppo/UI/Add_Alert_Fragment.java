package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import mohammedyouser.com.mustaemalaleppo.Domain.Category;
import mohammedyouser.com.mustaemalaleppo.Domain.City;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_ITEMS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Add_Alert_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Add_Alert_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Add_Alert_Fragment extends DialogFragment {

    private View fragment_view;
    private Add_Alert_Fragment.OnFragmentInteractionListener mListener;
    private String selectedCity;
    private String selectedCategory;
    private String userID;
    public Bundle bundle;

    //Fields from II Ac
    private City[] cities;
    private Category[] categories;

    //TODO
    private String selectedCity_Name;
    private DatabaseReference db_root_items;
    private LayoutInflater mInflator;
    private String kind;
    private String city;
    private String category;

    public Add_Alert_Fragment() {
        // Required empty public constructor
    }

    public static Add_Alert_Fragment newInstance(String param1, String param2) {
        return new Add_Alert_Fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kind=String.valueOf(getArguments().get("kind"));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflator.inflate(R.layout.fragment_add_alert, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Spinner spinner_cities = (Spinner) view.findViewById(R.id.spinner_cities2);
        //creating arrayAdapter_cities for the spinner_cities
        ArrayAdapter<String> arrayAdapter_cities = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.array_cities));

        arrayAdapter_cities.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner_cities.setAdapter(arrayAdapter_cities);

        final Spinner spinner_categories= (Spinner) view.findViewById(R.id.spinner_categories2);
        //creating arrayAdapter_cities for the spinner_cities
        ArrayAdapter<String> arrayAdapter_categories = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.array_categories));

        arrayAdapter_categories.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner_categories.setAdapter(arrayAdapter_categories);

        builder.setTitle(getResources().getString(R.string.title_subscribe))
                .setMessage(R.string.title_fragment_add_alert)
                .setPositiveButton(R.string.add_alert, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        Toast.makeText(getActivity(), "Alert Added! " + spinner_cities.getSelectedItem(), Toast.LENGTH_SHORT).show();
                        city=String.valueOf(spinner_cities.getSelectedItem());
                        ;
                        Log.d(TAG, "initialCities3mm: " + String.valueOf(spinner_cities.getSelectedItem()));
                        Toast.makeText(getActivity(), "Alert Added! " + spinner_categories.getSelectedItem(), Toast.LENGTH_SHORT).show();
                        category=String.valueOf(spinner_categories.getSelectedItem());

                        createAlert(generateTopic(kind,city,category));



                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(getActivity(), "No Alert Added!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setView(view);
        // Create the AlertDialog object and return it
        return builder.create();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        fragment_view = (FrameLayout) inflater.inflate(R.layout.fragment_add_alert, container, true);
     /*   ((Button) fragment_view.findViewById(R.id.test)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Button new", Toast.LENGTH_SHORT).show();
                Log.d(TAG, " " + ((Button) fragment_view.findViewById(R.id.test)).getText());
            }
        });
        ((Button) fragment_view.findViewById(R.id.test)).getText();
        Log.d(TAG, "onCreateView: " + ((Button) fragment_view.findViewById(R.id.test)).getText());*/
        return fragment_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //doMainInitializations();

        setUpAuthentication();
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
        db_root_items = db_root.child(PATH_ITEMS);

    }


    private String generateTopic(String kind, String city, String category) {
       // In cloud: topic="Items"+"_"+itemKind+"_"+itemCity+"_"+itemCategory;
        String topic = "Items" + "_" + kind + "_" + city + "_" + category;
        Log.d(TAG, "generateTopic: "+topic);
        return topic;
    }

    private void createAlert(final String selectedTopic) {
        // retrieveCurrentClientToken();
                FirebaseMessaging.getInstance().subscribeToTopic(selectedTopic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //String msg = getString(R.string.msg_info_subscribed_ok,selectedTopic);
                        String msg = selectedTopic;

                        Activity  activity=getActivity();
                        if(activity!=null)
                        Toast.makeText(getActivity(),msg , Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Failed");
                            msg = getString(R.string.msg_info_subscribed_failed);
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    private String retrieveCurrentClientToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                    }
                });
        return null;
    }

/*
    private DatabaseReference formAddedItemReference(String remoteMessage) {
        //      In cloud:   body="Items"+"_"+itemKind+"_"+itemCity+"_"+itemCategory+"_"+itemId;
        //                                                      body: topic+"_"+itemId
     //   String lastChild=((String.valueOf(remoteMessage.getData().get("body"))).replace('_','/'));
     String lastChild=((String.valueOf(remoteMessage.getData().get("body"))).replace('_','/'));
        DatabaseReference databaseReference=   FirebaseDatabase.getInstance().getReference()

                */
/*               .child(PATH_ITEMS)
                               .child(PATH_INEED)
                               .child(PATH_ALL_CITIES)
                               .child("Cars")*//*

                //        String       topic="Items"+"_"+itemKind+"_"+itemCity+"_"+itemCategory;
                //body: topic+"_"+itemId
                .child(lastChild);
        Log.d(TAG, "formAddedItemReference:lastChild "+lastChild);
        Log.d(TAG, "formAddedItemReference :databaseReference: "+String.valueOf(databaseReference));
        return databaseReference;
    }
*/


}



