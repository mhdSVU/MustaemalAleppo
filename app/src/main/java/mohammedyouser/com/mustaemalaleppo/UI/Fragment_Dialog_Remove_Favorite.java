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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REMOVE_FAVORITE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOKENS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_TOKENS_FAVORITES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERSIDs_TOKENS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS_FAVORITES;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_REMOVE_FAVORITE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;

public class Fragment_Dialog_Remove_Favorite extends DialogFragment {

    private View fragment_view;

    private String userID;
    public Bundle bundle;

    private DatabaseReference db_root_topics_tokens;
    private DatabaseReference db_root_tokens_topics;
    private DatabaseReference db_root_topics_users;
    private DatabaseReference db_root_users_topics;
    private LayoutInflater mInflator;
    private DatabaseReference db_root_tokens_favorites;
    private DatabaseReference db_root_usersIDs_favorites;
    private DatabaseReference db_root_usersIDs_tokens;
    private DatabaseReference db_root_users;
    private Bundle result;


    public Fragment_Dialog_Remove_Favorite() {
        // Required empty public constructor
    }

    public static Fragment_Dialog_Remove_Favorite newInstance(String param1, String param2, String param3) {

        Bundle bundle = new Bundle();

        bundle.putString("state", param1);
        bundle.putString("topic", param2);
        bundle.putString("favoriteID", param3);


        Fragment_Dialog_Remove_Favorite fragment_remove__alert_dialog = new Fragment_Dialog_Remove_Favorite();

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
                .setTitle(getResources().getString(R.string.title_fragment_delete_favorite))
                .setMessage(R.string.messag_info_fragment_favorite_delete)
                .setPositiveButton(R.string.btn_delete, (dialog, id) ->
                        {
                            removeFavoriteItem(getArguments().getString("state"),
                                    getArguments().getString("topic"),
                                    getArguments().getString("favoriteID"));

                            result = new Bundle();
                            result.putBoolean(BUNDLE_KEY_REMOVE_FAVORITE, true);
                            // The child fragment needs to still set the result on its parent fragment manager
                            getParentFragmentManager().setFragmentResult(REQUEST_KEY_REMOVE_FAVORITE, result);
                        }


                )


                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                    result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_REMOVE_FAVORITE, false);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY_REMOVE_FAVORITE, result);
                    Toast.makeText(getActivity(), getString(R.string.message_info_favorite_preserved), Toast.LENGTH_SHORT).show();
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

        db_root_tokens_favorites = db_root.child(PATH_TOKENS_FAVORITES);
        db_root_usersIDs_favorites = db_root.child(PATH_USERS_FAVORITES);
        db_root_usersIDs_tokens = db_root.child(PATH_USERSIDs_TOKENS);
        db_root_users = db_root.child(PATH_USERS);

    }

    private void removeFavoriteItem(String state, final String topic, String favoriteID) {
     //   decreaseUserNotificationsCount(state,topic,favoriteID);
        removeTopic_From_DB_tokens_favorites(state, topic, favoriteID);
        remove_Topic_from_DB_userIDs_favorites(state, topic, favoriteID);
    }


    private void removeTopic_From_DB_tokens_favorites(String state, String topic, String favoriteID) {
        db_root_usersIDs_tokens.child(userID).child(PATH_TOKENS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_userID_tokens) {
                for (final DataSnapshot snapshot_userID_token : snapshot_userID_tokens.getChildren()) {
                    db_root_tokens_favorites.child(snapshot_userID_token.getKey())
                            .child(state).child(topic).child(favoriteID).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void remove_Topic_from_DB_userIDs_favorites(String state, String topic, String favoriteID) {
        db_root_usersIDs_favorites.child(userID).child(state).child(topic).child(favoriteID).removeValue();
        Log.d(TAG, "remove favorite: " + state + " " + topic + " " + favoriteID);


    }

/*
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
*/

}



