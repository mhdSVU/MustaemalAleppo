package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


public class Fragment_Dialog_Report_User extends DialogFragment {
    private boolean is_already_reportedUsers = false;
    private boolean reached_max_reportedUsers = false;

    public void setItem_userID(String item_userID) {
        this.item_userID = item_userID;
    }

    private String item_userID;
    private View fragment_view;
    private View dialogView;
/*
    private Fragment_AddAlert_Dialog.OnFragmentInteractionListener mListener;
*/


    private DatabaseReference db_root_users;
    private DatabaseReference db_root;


    private String userID_reported;
    private String userID;

    private LayoutInflater mInflator;


    public Bundle bundle;

    private RadioButton m_rb_inappropriate;
    private RadioButton m_rb_misleading;
    private RadioButton m_rb_violence;

    private String m_reporter_feedBack;
    private String m_reported_user_phone_number;

    private Integer reports_count_out = 0;
    private Integer reports_count_in = 0;
    private Integer reports_count_in_inappropriate = 0;
    private Integer reports_count_in_misleading = 0;
    private Integer reports_count_in_violence = 0;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogView = setUpDialogViews();

        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.title_report_user))
                .setMessage(getString(R.string.tv_report_label))
                .setPositiveButton(getString(R.string.btn_report), (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_REPORT, true);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY_FORGET_ME, result);
                    check_reporting_validation(item_userID, dialogView);
                    Log.d(TAG, "onCreateDialog:F " + item_userID);
                })
                .setNegativeButton(getString(R.string.btn_cancel), (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(BUNDLE_KEY_REPORT, false);
                    // The child fragment needs to still set the result on its parent fragment manager
                    getParentFragmentManager().setFragmentResult(REQUEST_KEY_REPORT, result);
                    Toast.makeText(getActivity(), getActivity().getString(R.string.message_info_no_report), Toast.LENGTH_SHORT).show();
                })

                .setView(dialogView)
                .create();


    }

    private View setUpDialogViews() {

        mInflator = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflator.inflate(R.layout.fragment_report, null);

        return view;
    }

    private void reportUser(View view) {


        increase_ReportsCount_ReporterUser();
        increase_ReportsCount_ReportedUser(item_userID);
        increase_count_on_feedback(view, item_userID);
        add_to_reportedUsers(item_userID);

    }

    private void check_reporting_validation(String item_userID, View view) {
        db_root_users.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_reporter_user) {
                for (DataSnapshot dataSnapshot_reported_user : snapshot_reporter_user.child(PATH_REPORTED_USERS).getChildren()) {
                    if (String.valueOf(dataSnapshot_reported_user.getValue()).equals(item_userID)) {
                        is_already_reportedUsers = true;

                    }
                    if (!(snapshot_reporter_user.child(PATH_REPORTS_COUNT_OUT).getValue(Integer.class) < MAX_ALLOWED_REPORTS_COUNT_OUT)) {
                        reached_max_reportedUsers = true;
                    }
                }
                if (!is_already_reportedUsers && !reached_max_reportedUsers) {
                    reportUser(view);
                    Toast.makeText(requireActivity(), requireActivity().getString(R.string.message_info_report_user), Toast.LENGTH_SHORT).show();

                } else if (is_already_reportedUsers) {
                    Toast.makeText(requireActivity(), requireActivity().getString(R.string.message_error_reported_already), Toast.LENGTH_SHORT).show();
                } else if (reached_max_reportedUsers) {
                    Toast.makeText(requireActivity(), requireActivity().getString(R.string.message_error_reached_max), Toast.LENGTH_SHORT).show();

                } else if (reached_max_reportedUsers && is_already_reportedUsers) {
                    Toast.makeText(requireActivity(), requireActivity().getString(R.string.message_error_reported_already), Toast.LENGTH_SHORT).show();
                    Toast.makeText(requireActivity(), requireActivity().getString(R.string.message_error_reached_max), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void add_to_reportedUsers(String item_userID) {
        db_root_users.child(userID).child(PATH_REPORTED_USERS).push().setValue(item_userID);
    }


    private void increase_ReportsCount_ReporterUser() {

        db_root_users.child(userID).child(PATH_REPORTS_COUNT_OUT).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null) {
                    reports_count_out = 0;
                    currentData.setValue(++reports_count_out);
                    reports_count_out = currentData.getValue(Integer.class);
                    return Transaction.success(currentData);
                }

                reports_count_out = currentData.getValue(Integer.class);
                currentData.setValue(++reports_count_out);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @com.google.firebase.database.annotations.Nullable DataSnapshot currentData) {
                if (committed) {
                    // unique key saved
                    Log.d(TAG, "onComplete: " + "unique key saved3");
                } else {
                    // unique key already exists
                }
            }
        });

    }


    private void increase_ReportsCount_ReportedUser(String userID_reported) {
        if (userID_reported != null) {
            db_root_users.child(userID_reported).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot_user) {

                    snapshot_user.child(PATH_REPORTS_COUNT_IN).getRef().runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            if (currentData.getValue() == null) {
                                reports_count_in = 0;
                                currentData.setValue(++reports_count_in);
                                reports_count_in = currentData.getValue(Integer.class);
                                return Transaction.success(currentData);
                            }

                            reports_count_in = currentData.getValue(Integer.class);
                            currentData.setValue(++reports_count_in);

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @com.google.firebase.database.annotations.Nullable DataSnapshot currentData) {
                            if (committed) {
                                // unique key saved
                                Log.d(TAG, "onComplete: " + "unique key saved3");
                            } else {
                                // unique key already exists
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }

            });
        }
    }

    private void increase_count_on_feedback(View view, String userID_reported) {
        if (get_reporterUser_feedback(view).equals(getContext().getString(R.string.rb_inappropriate_content))) {
            increase_count_inappropriate(userID_reported);

            return;
        }
        if (get_reporterUser_feedback(view).equals(getContext().getString(R.string.rb_misleading_fraud))) {
            increase_count_misleading(userID_reported);

            return;
        }
        if (get_reporterUser_feedback(view).equals(getContext().getString(R.string.rb_violence_bad_communication))) {
            increase_count_violence(userID_reported);

            return;
        }
    }

    private String get_reporterUser_feedback(View view) {
        m_rb_inappropriate = (RadioButton) view.findViewById(R.id.rb_inappropriate_content);
        m_rb_misleading = (RadioButton) view.findViewById(R.id.rb_misleading_fraud);
        m_rb_violence = (RadioButton) view.findViewById(R.id.rb_violence_bad_communication);
        if (m_rb_inappropriate.isChecked()) {
            m_reporter_feedBack = String.valueOf(m_rb_inappropriate.getText());


        } else if (m_rb_misleading.isChecked()) {
            m_reporter_feedBack = String.valueOf(m_rb_misleading.getText());


        } else if (m_rb_violence.isChecked()) {
            m_reporter_feedBack = String.valueOf(m_rb_violence.getText());


        }

        return m_reporter_feedBack;
    }

    private void increase_count_violence(String userID_reported) {
        db_root_users.child(userID_reported).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_user) {
                snapshot_user.child(PATH_REPORTS_COUNT_IN_VIOLENCE).getRef().runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        if (currentData.getValue() == null) {
                            reports_count_in_violence = 0;
                            currentData.setValue(++reports_count_in_violence);
                            reports_count_in_violence = currentData.getValue(Integer.class);
                            return Transaction.success(currentData);
                        }

                        reports_count_in_violence = currentData.getValue(Integer.class);
                        currentData.setValue(++reports_count_in_violence);

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @com.google.firebase.database.annotations.Nullable DataSnapshot currentData) {
                        if (committed) {
                            // unique key saved
                            Log.d(TAG, "onComplete: " + "unique key saved3");
                        } else {
                            // unique key already exists
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void increase_count_misleading(String userID_reported) {
        db_root_users.child(userID_reported).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_user) {

                snapshot_user.child(PATH_REPORTS_COUNT_IN_MISLEADING).getRef().runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        if (currentData.getValue() == null) {
                            reports_count_in_misleading = 0;
                            currentData.setValue(++reports_count_in_misleading);
                            reports_count_in_misleading = currentData.getValue(Integer.class);
                            return Transaction.success(currentData);
                        }

                        reports_count_in_misleading = currentData.getValue(Integer.class);
                        currentData.setValue(++reports_count_in_misleading);

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @com.google.firebase.database.annotations.Nullable DataSnapshot currentData) {
                        if (committed) {
                            // unique key saved
                            Log.d(TAG, "onComplete: " + "unique key saved3");
                        } else {
                            // unique key already exists
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void increase_count_inappropriate(String userID_reported) {
        db_root_users.child(userID_reported).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_user) {

                snapshot_user.child(PATH_REPORTS_COUNT_IN_INAPPROPRIATE).getRef().runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        if (currentData.getValue() == null) {
                            reports_count_in_inappropriate = 0;
                            currentData.setValue(++reports_count_in_inappropriate);
                            reports_count_in_inappropriate = currentData.getValue(Integer.class);
                            return Transaction.success(currentData);
                        }

                        reports_count_in_inappropriate = currentData.getValue(Integer.class);
                        currentData.setValue(++reports_count_in_inappropriate);

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @com.google.firebase.database.annotations.Nullable DataSnapshot currentData) {
                        if (committed) {
                            // unique key saved
                            Log.d(TAG, "onComplete: " + "unique key saved3");
                        } else {
                            // unique key already exists
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        fragment_view = (FrameLayout) inflater.inflate(R.layout.fragment_report, container, true);

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
 /*       if (context instanceof Fragment_AddAlert_Dialog.OnFragmentInteractionListener) {
            mListener = (Fragment_AddAlert_Dialog.OnFragmentInteractionListener) context;


        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

    }

    @Override
    public void onDetach() {
        super.onDetach();
/*
        mListener = null;
*/
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
        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);


    }

}
