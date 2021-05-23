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

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REMOVE_ALERT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REMOVE_NOTIFICATION;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REPORT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_NOTIFICATIONS_COUNT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOKENS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOKENS_NOTIFICATIONS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERIDS_NOTIFICATIONS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERSIDs_TOKENS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_FORGET_ME;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_REMOVE_ALERT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_REMOVE_NOTIFICATION;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;

public class Fragment_Dialog_Remove_Notification extends DialogFragment {

    private View fragment_view;

    private String userID;
    public Bundle bundle;

    private DatabaseReference db_root_topics_tokens;
    private DatabaseReference db_root_tokens_topics;
    private DatabaseReference db_root_topics_users;
    private DatabaseReference db_root_users_topics;
    private LayoutInflater mInflator;
    private DatabaseReference db_root_tokens_notifications;
    private DatabaseReference db_root_usersIDs_notifications;
    private DatabaseReference db_root_usersIDs_tokens;
    private DatabaseReference db_root_users;
    private String notificationValue;
    private Bundle result;


    public Fragment_Dialog_Remove_Notification() {
        // Required empty public constructor
    }

    public static Fragment_Dialog_Remove_Notification newInstance(String param1, String param2, String param3) {

        Bundle bundle = new Bundle();

        bundle.putString("state", param1);
        bundle.putString("topic", param2);
        bundle.putString("notificationID", param3);


        Fragment_Dialog_Remove_Notification fragment_remove__alert_dialog = new Fragment_Dialog_Remove_Notification();

        fragment_remove__alert_dialog.setArguments(bundle);

        return fragment_remove__alert_dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        return new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.title_fragment_delete_notification))
                .setMessage(R.string.messag_info_fragment_notification_delete)
                .setPositiveButton(R.string.btn_delete, (dialog, id) ->
                        {
                            removeNotification(getArguments().getString("state"),
                                    getArguments().getString("topic"),
                                    getArguments().getString("notificationID"));

                            result = new Bundle();
                            result.putBoolean(BUNDLE_KEY_REMOVE_NOTIFICATION, true);
                            // The child fragment needs to still set the result on its parent fragment manager
                            getParentFragmentManager().setFragmentResult(REQUEST_KEY_REMOVE_NOTIFICATION, result);
                        }


                )


                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                    result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_REMOVE_ALERT, false);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY_REMOVE_ALERT, result);
                    Toast.makeText(getActivity(), getString(R.string.message_info_notification_preserved), Toast.LENGTH_SHORT).show();
                })

                /*    scrollView.addView(view);
                    view.addView(scrollView);*/
                .create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        fragment_view = inflater.inflate(android.R.layout.select_dialog_item, container, true);

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

        db_root_tokens_notifications = db_root.child(PATH_TOKENS_NOTIFICATIONS);
        db_root_usersIDs_notifications = db_root.child(PATH_USERIDS_NOTIFICATIONS);
        db_root_usersIDs_tokens = db_root.child(PATH_USERSIDs_TOKENS);
        db_root_users = db_root.child(PATH_USERS);

    }

    private void removeNotification(String state, final String topic, String notificationID) {
        decreaseUserNotificationsCount(state,topic,notificationID);
        removeTopic_From_DB_tokens_notifications(state, topic, notificationID);
        remove_Topic_from_DB_userIDs_notifications(state, topic, notificationID);
    }


    private void removeTopic_From_DB_tokens_notifications(String state, String topic, String notificationID) {
        db_root_usersIDs_tokens.child(userID).child(PATH_TOKENS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_userID_tokens) {
                for (final DataSnapshot snapshot_userID_token : snapshot_userID_tokens.getChildren()) {
                    db_root_tokens_notifications.child(snapshot_userID_token.getKey())
                            .child(state).child(topic).child(notificationID).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void remove_Topic_from_DB_userIDs_notifications(String state, String topic, String notificationID) {
        db_root_usersIDs_notifications.child(userID).child(state).child(topic).child(notificationID).removeValue();
        Log.d(TAG, "remove notif: " + state + " " + topic + " " + notificationID);


    }

    private void decreaseUserNotificationsCount(String state, String topic, String notificationID) {
        db_root_usersIDs_notifications.child(userID).child(state).child(topic).child(notificationID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(String.valueOf(snapshot.getValue()).equals("true")){// decrease only if notification has not been opened yet
                    db_root_users.child(userID).child(PATH_NOTIFICATIONS_COUNT).runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Integer notifications_count = 0;
                            if (currentData.getValue() == null) {
                                return Transaction.success(currentData);
                            }

                            notifications_count = Integer.parseInt(String.valueOf(currentData.getValue()));
                            currentData.setValue(--notifications_count);

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @com.google.firebase.database.annotations.Nullable DataSnapshot currentData) {
                            if (committed) {
                                // unique key saved
                                Log.d("TAG", "onComplete: " + "unique key saved3");
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



