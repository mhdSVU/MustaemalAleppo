package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

import mohammedyouser.com.mustaemalaleppo.Domain.VPN_AlertDialogFragment;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


public class Activity_Sign_In_Phone_Number extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "l";
    private EditText m_et_phoneNumber;
    private EditText m_et_password;
    private CheckBox m_chb_remember_me;

    private DatabaseReference db_root_users;
    private String userID;


    public Bundle bundle;
    private TextView m_tv_sign_up;
    private TextView m_tv_userName_welcome;
    private Button m_btn_sign_in;
    private CountryCodePicker m_cpp;

    private ProgressDialog m_pd;
    private SessionManager sessionManager;
    private FirebaseAuth auth;
    private TextView m_tv_forget_password;
    private TextView m_tv_sign_in_result;


    @Override
    protected void onStart() {
        super.onStart();
        sessionManager = new SessionManager(this, SessionManager.SESSION_NAME_REMEMBER_ME);
        restore_RememberMe_Session();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__sign__in__phone__number);

        m_et_phoneNumber = (EditText) findViewById(R.id.et_phoneNumber);
        m_et_password = (EditText) findViewById(R.id.et_password);
        m_btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
        m_tv_sign_up = (TextView) findViewById(R.id.tv_sign_up);
        m_tv_sign_in_result = (TextView) findViewById(R.id.tv_sign_in_result);
        m_tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
        m_tv_userName_welcome = (TextView) findViewById(R.id.tv_userName_welcome);
        m_cpp = (CountryCodePicker) findViewById(R.id.ccp);
        m_chb_remember_me = (CheckBox) findViewById(R.id.chb_remember_me);

        m_btn_sign_in.setOnClickListener(this);
        m_tv_sign_up.setOnClickListener(this);
        m_tv_forget_password.setOnClickListener(this);

        initializeUserInfo();

        doFirebaseInitializations();

        alertVPN();


    }

    private void alertVPN() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("VPN_Alert_Fragment");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        VPN_AlertDialogFragment vpn_alertDialogFragment = new VPN_AlertDialogFragment();
        vpn_alertDialogFragment.show(getFragmentManager(), "VPN_Alert_Fragment");


    }

    private void restore_RememberMe_Session() {
        if (sessionManager.isRememberMeSession()) {
            m_et_phoneNumber.setText(String.valueOf(sessionManager.get_RememberMe_Session().get(SessionManager.SHP_KEY_PHONE_NUMBER)).substring(4));
            m_et_password.setText(String.valueOf(sessionManager.get_RememberMe_Session().get(SessionManager.SHP_KEY_PASSWORD)));
        }
    }

    private void initializeUserInfo() {
        ;
        m_et_phoneNumber.setText(getValuefromIntent(INTENT_KEY_USER_PHONE_NUMBER));
        m_tv_userName_welcome.setText("Wellcome, " + getValuefromIntent(INTENT_KEY_USER_NAME) + "yuo have successfully created your account.\n Please use your phone number and password to sign in.");
        //userID = (Objects.requireNonNull(getIntent().getExtras()).getString(INTENT_KEY_USER_ID));
    }

    private String getValuefromIntent(String key) {
        if (getIntent() != null) {
            if (getIntent().hasExtra(key)) {
                return getIntent().getExtras().getString(key);
            }
        }
        return null;
    }

    private void doFirebaseInitializations() {

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        DatabaseReference db_root;
        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);
        auth = FirebaseAuth.getInstance();
        db_root_users.keepSynced(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sign_up:
                startSignUpActivity();
                break;
            case R.id.btn_sign_in:

                signInWithPhoneNumberAndPassword(getFinalPhoneNumber(), getContentOfView(m_et_password));
                break;
            case R.id.tv_forget_password:
                startForgetPasswordActivity();

                break;
        }
    }

    private void startForgetPasswordActivity() {
        startActivity(new Intent(this, Activity_Forget_Password_Enter_Number.class));

    }

    private String getFinalPhoneNumber() {
        if ((getContentOfView(m_et_phoneNumber).length()) == 0) {
            return null;
        }

        String selectedCountryCode = m_cpp.getSelectedCountryCode();
        String phoneNumberWithoutZero = getContentOfView(m_et_phoneNumber);

        if (getContentOfView(m_et_phoneNumber).charAt(0) == '0') {
            phoneNumberWithoutZero = getContentOfView(m_et_phoneNumber).substring(1);
        }

        return "+" + selectedCountryCode + phoneNumberWithoutZero;
    }

    private void signInWithPhoneNumberAndPassword(String phone_number, String password) {
        //  checkConnectivity(); //TODO
        boolean input_valid = validateInput_UserAccountInfo();
        if (input_valid) {
            m_pd = showProgressDialog(this, "", getString(R.string.message_info_SIGNING_IN), "");
            if (m_chb_remember_me.isChecked()) {
                sessionManager.create_RememberMe_Session(getFinalPhoneNumber(), getContentOfView(m_et_password));

            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(phone_number + "@m.org", password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            handleSignInSuccess(FirebaseAuth.getInstance().getUid());
                        } else {
                            handleSignInFailed(task);
                            Log.d(TAG, "signInWithPhoneNumberAndPassword: unsuccessful " + String.valueOf(task.getException()));
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "signInWithPhoneNumberAndPassword: failure  " + String.valueOf(e.getMessage()));


                    });
        }
    }


    private void handleSignInSuccess(String userID) {
        hideProgressDialog(m_pd);
        // finish();
        startMainActivity(userID);
    }

    private void handleSignInFailed(Task<AuthResult> task) {
        hideProgressDialog(m_pd);
        // If sign in fails, display a message to the user.
        if (!task.isSuccessful()) {
            try {
                throw Objects.requireNonNull(task.getException());
            } catch (FirebaseAuthInvalidUserException e) {
                m_tv_sign_in_result.setError("Invalid Phone Number");
                m_tv_sign_in_result.requestFocus();
                m_tv_sign_in_result.setText("Invalid Phone Number");
            } catch (FirebaseAuthInvalidCredentialsException e) {
                m_tv_sign_in_result.setError("Invalid Password");
                m_tv_sign_in_result.requestFocus();
                m_tv_sign_in_result.setText("Invalid Password");

            } catch (FirebaseNetworkException e) {
                Snackbar.make(this.findViewById(R.id.cl_sign_in_phone), getString(R.string.message_error_network_connection),
                        Snackbar.LENGTH_SHORT).show();
                m_tv_sign_in_result.setText(getString(R.string.message_error_network_connection));


            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());

            }
            Log.w(LOG_TAG, "signInWithEmail:failed", task.getException());
            Toast.makeText(this, getString(R.string.message_error_sign_in) + "\n" + getString(R.string.message_error_network_connection) + ", and/or VPN app status!",
                    Toast.LENGTH_SHORT).show();
            //updateUI(null);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //moveTaskToBack(true);
    }

    private DatabaseReference get_UserAccountInfo_RootRef() {
        return db_root_users;
        // return db_root_users;
    }


    private void startSignUpActivity() {
        startActivity(new Intent(this, Activity_Sign_Up_Phone_Number.class));

    }

    private void startMainActivity(String userID) {

        startActivity(new Intent(this, Ineed_Ihave_Activity.class)
                .putExtra(INTENT_KEY_USER_ID, userID));


    }

    private String getContentOfView(View view) {
        return String.valueOf(((EditText) view).getText()).trim();
    }

    private boolean validateInput_UserAccountInfo() {

        return checkError(m_et_phoneNumber) && checkError(m_et_password);
    }

    private boolean checkError(View view) {
        boolean validationState = true;

        if (view.getId() == R.id.et_password) {
            if (TextUtils.isEmpty(getContentOfView(view))) {
                ((EditText) view).setError(getString(R.string.message_error_empty_field));
                view.requestFocus();
                validationState = false;
            }

            if (TextUtils.getTrimmedLength(getContentOfView(view)) < 6) {
                ((EditText) view).setError(getString(R.string.message_error_too_short));
                view.requestFocus();
                validationState = false;

            }


        }
        if (TextUtils.isEmpty(getContentOfView(view))) {
            ((EditText) view).setError(getString(R.string.message_error_empty_field));
            view.requestFocus();
            validationState = false;
        }
        return validationState;
    }


}