package mohammedyouser.com.mustaemalaleppo.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jrummyapps.android.animations.Technique;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.concurrent.TimeUnit;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_NAME;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_PHONE_NUMBER;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__STATEVALUE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_VALUE__STATEVALUE_INEED;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USER_IMAGE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USER_NAME;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USER_PASSWORD;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USER_PHONE_NUMBER;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_READ_PERMISSION_ITEM_MODIFY;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.hideProgressDialog;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account extends DialogFragment implements View.OnClickListener {

    private static final String STR_TAG_FRAGMENT_IN_VERIFICAION = "fragment_in_Verificaion";
    private String userID;
    public Bundle bundle;
    ///


    private static final String TAG = "SignInActivity_Phone";
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth auth;
    // [END declare_auth]


    // [START declare_db]

    // [END declare_db]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    //  private ViewGroup mPhoneNumberViews;
    // private ViewGroup mResendViews;
    //private TextView mStatusText;

    private TextView m_tv_Details;

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

    private Uri m_uri_user_img_download;
    private Uri m_uri_user_img;
    private String m_str_user_img;
    private CoordinatorLayout m_coordinatorLayout;
    private String newPassword;
    ///

    public Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bundle_user_account_info The bundle created in the Sign up Activity, that holds user account info
     * @return A new instance of fragment Fragment_Phone_Number_Verify.
     */
    public static Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account newInstance(Bundle bundle_user_account_info) {

        Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account fragment = new Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account();
        fragment.setArguments(bundle_user_account_info);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        doFirebaseRefsInitializations();
        doUserAccountInfoInitializations();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);

        return inflater.inflate(R.layout.fragment_reset_password_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        m_tv_Details = (TextView) view.findViewById(R.id.et_details);
        m_et_Verification = (EditText) view.findViewById(R.id.et_verify_code);
        m_tv_AskForResend = (TextView) view.findViewById(R.id.tv_ask_for_resend);
        m_btn_Verify = (Button) view.findViewById(R.id.btn_verify);
        m_btn_Resend = (Button) view.findViewById(R.id.btn_resend_code_phone);
        m_btn_Modify = (Button) view.findViewById(R.id.btn_modify_phoneNumber);
        m_pb_Verification = (ProgressBar) view.findViewById(R.id.pr_verify);
        m_coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout_reset_password);


        // Assign click listeners
        m_btn_Verify.setOnClickListener(this);
        m_btn_Resend.setOnClickListener(this);
        m_btn_Modify.setOnClickListener(this);

        // [START initialize_auth]
        auth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        ////////////

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
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
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

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
                //startResetPasswordActivity();
                // promptUsertoUpdatePassword();


            }

            private void startResetPasswordActivity() {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    //.setError(getString(R.string.message_error_invalid_number));
                    m_tv_Details.setText(getString(R.string.message_error_invalid_number));
                    hideViews(m_btn_Verify, m_et_Verification, m_pb_Verification);
                    showViews(m_btn_Modify);


                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.message_error_quota_exceeded),
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show activity_single_item message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
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

    /*private void promptUsertoUpdatePassword() {
        String oldpass = "";
        String newPass = "";
        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Snackbar snackbar_fail = Snackbar
                                        .make(m_coordinatorLayout, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                snackbar_fail.show();
                            } else {
                                Snackbar snackbar_su = Snackbar
                                        .make(m_coordinatorLayout, "Password Successfully Modified", Snackbar.LENGTH_LONG);
                                snackbar_su.show();
                            }
                        }
                    });
                } else {
                    Snackbar snackbar_su = Snackbar
                            .make(m_coordinatorLayout, "Authentication Failed", Snackbar.LENGTH_LONG);
                    snackbar_su.show();
                }
            }
        });
    }*/


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        Log.d(TAG, "startPhoneNumberVerification: ");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
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
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        //userID = auth.getCurrentUser().getUid();
                        startPasswordResetActivity();
                     //   resetUserPassword(auth.getCurrentUser(), newPassword);
                        //   createEmailToPhoneAccount(getUserAccountInfoFromBundle("userPhoneNumber"), getUserAccountInfoFromBundle("userPassword"));
                        // createUserProfile(userID);
                        //  startSignInActivity(userID, m_str_userPhoneNumber, m_str_userName, getActivity());

                    } else {
                        // Sign in failed, display activity_single_item message and update the UI
                        Log.w(TAG, "signInWithCredential:failure " + String.valueOf(task.getException().getMessage()) + getUserAccountInfoFromBundle("userPhoneNumber"));
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            // [START_EXCLUDE silent]
                            m_et_Verification.setError(getString(R.string.message_error_invalid_code));
                            // [END_EXCLUDE]
                        }
                        // [START_EXCLUDE silent]
                        // Update UI
                        updateUI(STATE_SIGNIN_FAILED);
                        // [END_EXCLUDE]

                    }
                });
    }

    private void resetUserPassword(FirebaseUser currentUser, String newPassword) {
        currentUser.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //storeNewPasswordToFirebase(auth.getCurrentUser().getUid(), newPassword);
                            startPasswordResetActivity();
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
    }

    private void createEmailToPhoneAccount(String phoneNumber, String password) {
        String email = phoneNumber + "@m.org";
        Log.d(TAG, "createEmailToPhoneAccount: " + email);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userID = auth.getCurrentUser().getUid();
                Log.d(TAG, "createEmailToPhoneAccount: " + " Successful" + userID);


                createUserProfile(userID);

                getFragmentManager().beginTransaction().remove(this).commit();
                startMainActivity();
            }
        }).addOnFailureListener(e -> {
            hideProgressDialog();
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();

        });

    }

    private void startMainActivity() {
        startActivity(new Intent(getActivity(), Activity_Sign_In_Phone_Number.class)
                .putExtra(INTENT_KEY_USER_ID, userID));
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [END sign_in_with_phone]

    private void handleSignInFailed(Exception e) {
        //  hideProgressDialog(m_pd);
        Toast.makeText(getActivity(), "Error" + e, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Unsuccessful: Signing in failed!", Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Failed! Password and phone number don't match!", Toast.LENGTH_SHORT).show();

    }

    private void handleSignInSuccess(String userID) {
        //   hideProgressDialog(m_pd);
        //  finish();
        // startMainActivity(userID);
    }


    private void createUserProfile(final String userID) {
        //  showProgressDialog(getContext(), getString(R.string.message_info_SIGNING_UP), getString(R.string.message_info_PLEASE_WAIT));
    /*    if(m_uri_user_img.equals(null)){
            m_uri_user_img=Uri.EMPTY;
        }*/
        Log.d(TAG, "createUserProfile: 1 " + userID);
/*        if (m_uri_user_img != null) {
            getFinalStorageReference(userID)
                    .putFile(m_uri_user_img)//+flag intent+uri null+frgm oriint change ,auto sms+bb20 ccp+custom pb
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> m_uri_user_img_download = uri);
                        // Second save user name, email,...and the URL of user image to firebase
                        Log.d(TAG, "createUserProfile: 2");
                        storeUserInfoToFirebase(userID, m_uri_user_img_download);
                        hideProgressDialog();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "createUserProfile: 3 "+e.getMessage());
                        hideProgressDialog();
                        Toast.makeText(getActivity(), e.toString() + " " + R.string.message_info_upload_error, Toast.LENGTH_LONG).show();

                    });
        } else {*/
        storeUserInfoToFirebase(userID, null);
        hideProgressDialog();


        // First upload User image to firebase

    }

    private void storeUserInfoToFirebase(String userID, Uri uri_user_img_download) {
  /*      if (!uri_user_img_download.equals(null)) {
            Log.d(TAG, "storeUserInfoToFirebase:1 "+String.valueOf(uri_user_img_download));
            get_UserAccountInfo_RootRef(userID).child(PATH_USER_IMAGE).setValue(uri_user_img_download);
        }*/
        get_UserAccountInfo_RootRef(userID).child(PATH_USER_NAME).setValue(getUserAccountInfoFromBundle("userUserDisplayName"));
        get_UserAccountInfo_RootRef(userID).child(PATH_USER_PHONE_NUMBER).setValue(getUserAccountInfoFromBundle("userPhoneNumber"));
        get_UserAccountInfo_RootRef(userID).child(PATH_USER_PASSWORD).setValue(getUserAccountInfoFromBundle("userPassword"));
        Log.d(TAG, "storeUserInfoToFirebase: " + userID);

    }

    private void storeNewPasswordToFirebase(String userID, String newPassword) {
        get_UserAccountInfo_RootRef(userID).child(PATH_USER_PASSWORD).setValue(getUserAccountInfoFromBundle("userPassword"));
        Log.d(TAG, "storeUserInfoToFirebase: " + userID);
        getFragmentManager().beginTransaction().remove(this).commit();
        //startPassWordUpdatedActivity();
        startPasswordResetActivity();

    }

    private void startPasswordResetActivity() {
            startActivity(new Intent(getActivity(), Activity_Reset_Password.class)
                    .putExtra(INTENT_KEY_USER_ID, userID));

    }



    private DatabaseReference get_UserAccountInfo_RootRef(String userID) {
        Log.d(TAG, "get_UserAccountInfo_RootRef: " + userID);
        return db_root_users.child(userID);
    }


    private void startSignInActivity(String userID, String userPhoneNumber, String userName, Context context) {
        Intent signInIntent = new Intent(context, Activity_Sign_In_Phone_Number.class);

        signInIntent.putExtra(INTENT_KEY__STATEVALUE, INTENT_VALUE__STATEVALUE_INEED);
        signInIntent.putExtra(INTENT_KEY_USER_ID, userID);
        signInIntent.putExtra(INTENT_KEY_USER_PHONE_NUMBER, userPhoneNumber);
        signInIntent.putExtra(INTENT_KEY_USER_NAME, userName);
        // getActivity().finish();
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signInIntent);

    }


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

    private StorageReference getFinalStorageReference(String userID) {

        return mStorageReference.child(PATH_USERS).child(PATH_USER_IMAGE).child(userID);

    }


    private void updateUI(int uiState) {
        updateUI(uiState, auth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
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
                hideViews(m_btn_Verify, m_et_Verification, m_btn_Resend, m_tv_AskForResend);

                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(m_btn_Verify, m_et_Verification, m_btn_Resend, m_tv_AskForResend, m_tv_AskForResend);
                showViews(m_btn_Verify, m_et_Verification, m_btn_Resend, m_tv_AskForResend, m_tv_AskForResend);
                hideViews(m_pb_Verification);
                m_tv_Details.setText(R.string.message_info_ok_status_code_sent);
                m_tv_Details.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case STATE_VERIFY_FAILED:
                m_tv_Details.setText(R.string.message_info_error_status_verification);
                hideViews(m_btn_Verify, m_et_Verification, m_pb_Verification, m_btn_Resend, m_tv_AskForResend);
                showViews(m_btn_Modify);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                showViews(m_btn_Verify, m_et_Verification, m_btn_Resend, m_tv_AskForResend, m_tv_AskForResend);
                enableViews(m_btn_Verify, m_et_Verification, m_btn_Resend, m_tv_AskForResend, m_tv_AskForResend);
                hideViews(m_pb_Verification);
                m_tv_Details.setText(R.string.message_info_ok_status_verification);
                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        m_et_Verification.setText(cred.getSmsCode());
                    } else {
                        m_et_Verification.setText(R.string.instant_validation);
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                m_tv_Details.setText(R.string.message_info_error_status_sign_in);
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
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
            case R.id.btn_modify_phoneNumber:
                dismiss();
                break;


        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void manage_Adding_UserImg_Perm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION_ITEM_MODIFY);
            } else {
                startCropActivity();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
/*
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
*/
        if (grantResults.length >= 2) {
            if (requestCode == REQUEST_READ_PERMISSION_ITEM_MODIFY &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("perms", "perms granted");
                //upload_itemImg_and_data();
                startCropActivity();


            } else {
                Log.d("perms", "perms not granted");

                //  Toast.makeText(Activity_Sign_Up_Phone_Number.this, getString(R.string.message_info_permission_declined), Toast.LENGTH_SHORT).show();

            }
        } else {
            //Toast.makeText(this, "No feedback from user!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCropActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getActivity());
    }


}