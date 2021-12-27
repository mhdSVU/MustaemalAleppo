package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOKENS_TOPICS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOPICS_TOKENS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOPICS_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS_TOPICS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG2;

public class Fragment_RemoveNotifications_Dialog extends DialogFragment {

    private View fragment_view;

    private String userID;
    public Bundle bundle;

    private DatabaseReference db_root_topics_tokens;
    private DatabaseReference db_root_tokens_topics;
    private DatabaseReference db_root_topics_users;
    private DatabaseReference db_root_users_topics;
    private LayoutInflater mInflator;


    public Fragment_RemoveNotifications_Dialog() {
        // Required empty public constructor
    }

    public static Fragment_RemoveNotifications_Dialog newInstance(String param1, String param2, String param3) {
        Bundle bundle= new Bundle();
        bundle.putString("topicState",param1);
        bundle.putString("topicCity",param2);
        bundle.putString("topicCategory",param3);
        Fragment_RemoveNotifications_Dialog fragment_remove_alert_dialog= new Fragment_RemoveNotifications_Dialog();
        fragment_remove_alert_dialog.setArguments(bundle);
        return fragment_remove_alert_dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        return new MaterialAlertDialogBuilder(getActivity(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(getResources().getString(R.string.title_unsubscribe))
                .setMessage(R.string.title_fragment_remove_alert)
                .setPositiveButton(R.string.remove_alert, (dialog, id) ->
                        removeTopicAlert(getArguments().getString("topicState"), form_selected_Topic(
                                getArguments().getString("topicState"),
                                getArguments().getString("topicCity"),
                              getArguments().getString("topicCategory"))))

                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                    Toast.makeText(getActivity(), getString(R.string.message_info_notification_preserved), Toast.LENGTH_SHORT).show();
                })

                /*    scrollView.addView(view);
                    view.addView(scrollView);*/
                .create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        fragment_view =  inflater.inflate(android.R.layout.select_dialog_item, container, true);

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
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    }






    private View setUpDialogViews() {

        mInflator = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflator.inflate(R.layout.fragment_add_alert, null);


        return view;
    }

    private String form_selected_Topic(String itemState,String itemCity, String itemCategory) {

        // In cloud: topic="Items"+"_"+itemKind+"_"+itemCity+"_"+itemCategory;
        String topic = "Items" + "_" + itemState + "_" + itemCity + "_" + itemCategory;
        Log.d(TAG2, "generatedTopic: " + topic);
        return topic;
    }

    private void removeTopicAlert(String m_alertState, final String selectedTopic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(selectedTopic).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG2, "onComplete: Failed");
            }
            removeTopic_From_DB_tokens_topics(m_alertState, selectedTopic);
            remove_Topic_from_DB_userIDs_topics(m_alertState, selectedTopic);
            Log.d(TAG2, "onComplete: Successful");

        });
        Log.d(TAG, "removeTopicAlert: unsubscribe: "+selectedTopic);
    }

    private void removeTopic_From_DB_tokens_topics(String m_alertState, String selectedTopic) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    db_root_topics_tokens.child(m_alertState).child(selectedTopic).child(token).removeValue();
                    db_root_tokens_topics.child(token).child(m_alertState).child(selectedTopic).removeValue();


                });

    }

    private void remove_Topic_from_DB_userIDs_topics(String m_alertState, String selectedTopic) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    db_root_topics_users.child(m_alertState).child(selectedTopic).child(userID).removeValue();
                    db_root_users_topics.child(userID).child(m_alertState).child(selectedTopic).removeValue();


                });

    }


}



