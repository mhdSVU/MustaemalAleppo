package mohammedyouser.com.mustaemalaleppo.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jrummyapps.android.animations.Technique;

import java.util.concurrent.TimeUnit;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.STATE_CODE_SENT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.STATE_INITIALIZED;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.STATE_VERIFY_FAILED;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.STATE_VERIFY_SUCCESS;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Reset_Password_Verify_Phone_Number#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Reset_Password_Verify_Phone_Number extends DialogFragment implements View.OnClickListener {

    public Bundle bundle;
    ///


    // [START declare_auth]
    private FirebaseAuth auth;
    // [END declare_auth]


    // [START declare_db]

    // [END declare_db]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerificationCallbacks;

    //  private ViewGroup mPhoneNumberViews;
    // private ViewGroup mResendViews;
    //private TextView mStatusText;


    private TextView m_tv_AskForResend;
    private ProgressBar m_pb_Verification;

    private EditText m_et_Verification;

    private Button m_btn_Verify;
    private Button m_btn_Resend;

    private DatabaseReference db_root;
    private DatabaseReference db_root_users;

    private String m_str_userPhoneNumber;
    private String m_str_userName;

    private Button m_btn_Modify;
    private StorageReference mStorageReference;

    private Uri m_uri_user_img;
    private String m_str_user_img;

    private TextView m_tv_verification_status;
    private Button m_btn_review_phone_number;
    private Button m_btn_Cancel;
    private FirebaseUser currentUser;

    ///

    ///

    public Fragment_Reset_Password_Verify_Phone_Number() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bundle_user_account_info The bundle created in the Sign up Activity, that holds user account info
     * @return A new instance of fragment Fragment_Phone_Number_Verify.
     */
    public static Fragment_Reset_Password_Verify_Phone_Number newInstance(Bundle bundle_user_account_info) {

        Fragment_Reset_Password_Verify_Phone_Number fragment = new Fragment_Reset_Password_Verify_Phone_Number();
        fragment.setArguments(bundle_user_account_info);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        doFirebaseRefsInitializations();
        doUserAccountInfoInitializations();
        setCancelable(false);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);


        return inflater.inflate(R.layout.fragment_verify_phone_number_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        m_tv_verification_status = (TextView) view.findViewById(R.id.et_details);
        m_et_Verification = (EditText) view.findViewById(R.id.et_verify_code);
        m_tv_AskForResend = (TextView) view.findViewById(R.id.tv_ask_for_resend);
        m_btn_Verify = (Button) view.findViewById(R.id.btn_verify);
        m_btn_Resend = (Button) view.findViewById(R.id.btn_resend_code_phone);
        m_btn_review_phone_number = (Button) view.findViewById(R.id.btn_review_phoneNumber);
        m_btn_Cancel = (Button) view.findViewById(R.id.btn_cancel_verification);
        m_pb_Verification = (ProgressBar) view.findViewById(R.id.pb_verify);

        m_pb_Verification.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);


        // Assign click listeners
        m_btn_Verify.setOnClickListener(this);
        m_btn_Resend.setOnClickListener(this);
        m_btn_review_phone_number.setOnClickListener(this);
        m_btn_Cancel.setOnClickListener(this);

        // [START initialize_auth]
        auth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        ////////////

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = auth.getCurrentUser();
        updateUI(currentUser);

/*

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STR_TAG_FRAGMENT_IN_VERIFICAION, mVerificationInProgress);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

 /*       doFirebaseRefsInitializations();
        doUserAccountInfoInitializations();*/
        mVerificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter activity_single_item verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

              /*  if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    //.setError(getString(R.string.message_error_invalid_number));
                    m_tv_verification_status.setText(getString(R.string.message_error_invalid_number));
                    hideViews(m_btn_Verify, m_et_Verification, m_pb_Verification);
                    showViews(m_btn_review_phone_number);


                    // [END_EXCLUDE]
                } else*/
                handleSignInFailed(e);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct activity_single_item credential
                // by combining the code with activity_single_item verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
//        startPhoneNumberVerification(userPhoneNumber_1);

        // [START_EXCLUDE]
    /*    if (savedInstanceState != null) {
            mVerificationInProgress = savedInstanceState.getBoolean(STR_TAG_FRAGMENT_IN_VERIFICAION);
            // Do something with value if needed
        }*/
        if ((!mVerificationInProgress) /*&& (savedInstanceState == null)*//*&& validatePhoneNumber()*/) {
            startPhoneNumberVerification(m_str_userPhoneNumber);
        }
        // [END_EXCLUDE]
        //start verification
    /*    if (!validatePhoneNumber()) {
            return;
        }*/
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        Log.d(TAG, "startPhoneNumberVerification: ");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                requireActivity(),               // Activity (for callback binding)
                mVerificationCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        startPasswordResetActivity(auth.getCurrentUser().getUid());
                        getFragmentManager().beginTransaction().remove(this).commit();
                    } else {
                        handleSignInFailed(task.getException());

                    }
                });
    }

    private void handleSignInFailed(Exception e) {
        m_pb_Verification.setVisibility(View.GONE);
        // If sign in fails, display a message to the user.
        if (e instanceof FirebaseNetworkException) {
            showSnackBar(requireActivity(), getString(R.string.message_error_network_connection));

            updateUI(STATE_NETWORK_FAILED);
            return;

        }
        if (e instanceof FirebaseAuthWeakPasswordException) {
            showSnackBar(requireActivity(), getString(R.string.message_error_password_weak));
            updateUI(STATE_FAILED_INVALID_PASSWORD);
            return;

        }
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            showSnackBar(requireActivity(), getString(R.string.message_error_phone_number));
            updateUI(STATE_FAILED_INVALID_PHONE_NUMBER);
            return;

        }
        if (e instanceof FirebaseAuthActionCodeException) {
            // The verification code entered was invalid
            // [START_EXCLUDE silent]
            m_et_Verification.setError(getString(R.string.message_error_invalid_code));
            showSnackBar(requireActivity(), getString(R.string.message_error_invalid_code));
            updateUI(STATE_VERIFY_FAILED);
            return;

            // [END_EXCLUDE]
        }
        if (e instanceof FirebaseTooManyRequestsException) {
            // The SMS quota for the project has been exceeded
            // [START_EXCLUDE]
            Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.message_error_too_many_requests),
                    Snackbar.LENGTH_SHORT).show();
            // [END_EXCLUDE]`
            updateUI(STATE_FAILED_TOO_MANY_REQUESTS);
            return;

        } /*else {
            showSnackBar(getActivity(), getString(R.string.message_error_network_connection));

            updateUI(STATE_NETWORK_FAILED);
        }*/
        // Show activity_single_item message and update the UI
        // [START_EXCLUDE]
        // [END_EXCLUDE]

        //Most likely VPN problem
        showSnackBar(requireActivity(), getString(R.string.message_error_network_connection));

        updateUI(STATE_NETWORK_FAILED);
        return;

    }


    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mVerificationCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [END sign_in_with_phone]


    private void doUserAccountInfoInitializations() {
        m_str_userPhoneNumber = getUserAccountInfoFromBundle("userPhoneNumber");
        m_str_userName = getUserAccountInfoFromBundle("userUserDisplayName");
        m_str_user_img = getUserAccountInfoFromBundle("userImageUri");
        if (m_uri_user_img != null) {
            m_uri_user_img = Uri.parse(m_str_user_img);

        } else {
            m_uri_user_img = Uri.EMPTY;

        }
    }

    private void doFirebaseRefsInitializations() {
        auth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);
        db_root_users.keepSynced(true);
    }

    private String getUserAccountInfoFromBundle(String key) {
        if (getArguments() != null)
            if (getArguments().containsKey(key)) {
                return getArguments().getString(key);
            }
        return null;
    }

    private void startPasswordResetActivity(String userID) {
        startActivity(new Intent(getActivity(), Activity_ForgetPassword_Reset_Password.class)
                .putExtra(INTENT_KEY_USER_ID, userID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        getActivity().finish();

    }

    private void updateUI(int uiState) {
        updateUI(uiState, auth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGN_IN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                //enableViews(mStartButton, mPhoneNumberField);
                hideViews(m_btn_Verify, m_et_Verification, m_btn_Resend, m_tv_AskForResend, m_btn_review_phone_number);

                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
           /*     if(user==null){
                    hideViews(m_pb_Verification);
                    showViews(m_btn_Modify);
                    m_tv_Details.setText(R.string.message_error_sign_up);

                }*/
                enableViews(m_btn_Verify, m_et_Verification, m_btn_Resend, m_tv_AskForResend);
                showViews(m_btn_Verify, m_et_Verification, m_btn_Resend, m_tv_AskForResend, m_btn_review_phone_number);
                hideViews(m_pb_Verification);
                m_tv_verification_status.setText(R.string.message_info_ok_status_code_sent);

                m_tv_verification_status.setBackgroundColor(getResources().getColor(android.R.color.white));
                m_tv_verification_status.setTextColor(getResources().getColor(R.color.colorAccent));

                break;
            case STATE_FAILED_INVALID_PHONE_NUMBER:
                m_tv_verification_status.setText(R.string.message_error_invalid_number);
                hideViews(m_pb_Verification);
                showViews(m_btn_review_phone_number);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                showViews(m_btn_Verify, m_et_Verification);
                enableViews(m_btn_Verify, m_et_Verification);
                hideViews(m_pb_Verification, m_btn_review_phone_number);
                m_tv_verification_status.setText(R.string.message_info_ok_status_verification);
                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        m_et_Verification.setText(cred.getSmsCode());
                    } else {
                        m_et_Verification.setText(R.string.instant_validation);
                    }
                }

                break;
            case STATE_VERIFY_FAILED:
                // No-op, handled by sign-in check
                m_tv_verification_status.setText(R.string.message_info_error_status_verification);
                showViews(m_btn_review_phone_number, m_btn_Resend, m_tv_AskForResend);
                hideViews(m_pb_Verification);
                break;
            case STATE_SIGN_IN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
            case STATE_NETWORK_FAILED:
                m_tv_verification_status.setText(R.string.message_error_network_connection_short);
                hideViews(m_pb_Verification, m_btn_review_phone_number, m_btn_Resend, m_tv_AskForResend, m_btn_Verify, m_et_Verification);

                break;
            case STATE_FAILED_TOO_MANY_REQUESTS:
                m_tv_verification_status.setText(R.string.message_error_quota_exceeded);
                hideViews(m_pb_Verification, m_btn_review_phone_number, m_btn_Resend, m_tv_AskForResend, m_btn_Verify, m_et_Verification);

                break;
            case STATE_FAILED_INVALID_PASSWORD:
                m_tv_verification_status.setText(R.string.message_error_password_weak);
                hideViews(m_pb_Verification, m_btn_review_phone_number, m_btn_Resend, m_tv_AskForResend, m_btn_Verify, m_et_Verification);

                break;
            case STATE_FAILED_ALREADY_REGISTERED_USER:
                m_tv_verification_status.setText(R.string.message_error_already_registered_short);
                hideViews(m_pb_Verification, m_btn_Resend, m_tv_AskForResend, m_btn_Verify, m_et_Verification);

                break;

        }
    }


    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    private void hideViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    private void showViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

        Technique.PULSE.playOn(view);

        switch (view.getId()) {

            case R.id.button_start_verification:
                startPhoneNumberVerification(m_str_userPhoneNumber);
                break;
            case R.id.btn_verify:
                String code = m_et_Verification.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    m_et_Verification.setError(getString(R.string.message_error_empty_field));
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.btn_resend_code_phone:
                resendVerificationCode(m_str_userPhoneNumber, mResendToken);
                break;
            case R.id.btn_review_phoneNumber:
                dismiss();
                break;
            case R.id.btn_cancel_verification:
                dismiss();
                break;


        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}