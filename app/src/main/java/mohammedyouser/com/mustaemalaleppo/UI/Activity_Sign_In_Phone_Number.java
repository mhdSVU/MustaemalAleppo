package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

import mohammedyouser.com.mustaemalaleppo.Domain.Fragment_Dialog_VPN_Alert;
import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


public class Activity_Sign_In_Phone_Number extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "l";
    private EditText m_et_phoneNumber;
    private EditText m_et_password;
    private CheckBox m_chb_remember_me;
    private TextInputLayout m_til_password;


    private DatabaseReference db_root_users;
    private String userID;


    public Bundle bundle;
    private TextView m_tv_sign_up;
    private Button m_btn_sign_in;
    private CountryCodePicker m_cpp;

    private ProgressDialog m_pd;
    private SessionManager sessionManager;
    private FirebaseAuth auth;
    private TextView m_tv_forget_password;
    private Integer user_reports_count_in;
    private TextInputLayout m_til_phone_number;


    @Override
    protected void onStart() {
        super.onStart();
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()),this);
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
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()),this);

        setContentView(R.layout.activity__sign__in__phone__number);

        m_til_password = findViewById(R.id.til_password);
        m_til_phone_number = findViewById(R.id.til_et_phoneNumber);
        m_et_phoneNumber =  findViewById(R.id.et_phoneNumber);
        m_et_password = findViewById(R.id.et_password);
        m_btn_sign_in =  findViewById(R.id.btn_sign_in);
        m_tv_sign_up = findViewById(R.id.btn_create_new_account);
        m_tv_forget_password =  findViewById(R.id.tv_forget_password);
        m_cpp =  findViewById(R.id.ccp);
        m_chb_remember_me =  findViewById(R.id.chb_remember_me);

        m_btn_sign_in.setOnClickListener(this);
        m_tv_sign_up.setOnClickListener(this);
        m_tv_forget_password.setOnClickListener(this);

        initializeUserInfo();

        doFirebaseInitializations();

        confirm_download_VPN();


    }

    private void confirm_download_VPN() {
      /*  Fragment fragment = getSupportFragmentManager().findFragmentByTag("VPN_Alert_Fragment");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        Fragment_Dialog_VPN_Alert fragmentDialogVPNAlert = new Fragment_Dialog_VPN_Alert();
        fragmentDialogVPNAlert.show(getFragmentManager(), "VPN_Alert_Fragment");*/
        showDialogFragment(new Fragment_Dialog_VPN_Alert(), "frg_vpn");
    }

    public void showDialogFragment(DialogFragment newFragment, String tag) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        // save transaction to the back stack
        ft.addToBackStack("dialog");
        newFragment.show(ft, tag);
        getSupportFragmentManager().executePendingTransactions();
    }


    private void restore_RememberMe_Session() {
        if (sessionManager.isRememberMeSession()) {
            if (!String.valueOf(sessionManager.get_RememberMe_Session().get(SessionManager.SHP_KEY_PHONE_NUMBER)).equals("null") && String.valueOf(sessionManager.get_RememberMe_Session().get(SessionManager.SHP_KEY_PHONE_NUMBER)).length() >= 4)
                m_et_phoneNumber.setText(String.valueOf(sessionManager.get_RememberMe_Session().get(SessionManager.SHP_KEY_PHONE_NUMBER)).substring(4));
            m_et_password.setText(String.valueOf(sessionManager.get_RememberMe_Session().get(SessionManager.SHP_KEY_PASSWORD)));
        }
    }

    private void initializeUserInfo() {
        m_et_phoneNumber.setText(getValueFromIntent(INTENT_KEY_USER_PHONE_NUMBER));
    }

    private String getValueFromIntent(String key) {
        if (getIntent() != null) {
            if (getIntent().hasExtra(key)) {
                return Objects.requireNonNull(getIntent().getExtras()).getString(key);
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
            case R.id.btn_create_new_account:
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
        startActivity(new Intent(this, Activity_ForgetPassword_Enter_Number.class));
        finish();

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
            if (!m_chb_remember_me.isChecked()) {
                sessionManager.delete_RememberMe_Session();

            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(phone_number + "@m.org", password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            handleSignInSuccess(FirebaseAuth.getInstance().getUid());
                        } else {
                            handleSignInFailed(task);
                            Log.d(TAG, "signInWithPhoneNumberAndPassword: unsuccessful " +task.getException());
                        }
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "signInWithPhoneNumberAndPassword: failure  " + e.getMessage()));
        }
    }


    private void handleSignInSuccess(String userID) {
        hideProgressDialog(m_pd);
        // finish();
        checkUserReportsCount(userID);
    }

    private void handleSignInFailed(Task<AuthResult> task) {
        hideProgressDialog(m_pd);
        // If sign in fails, display a message to the user.
        if (!task.isSuccessful()) {
            try {
                throw Objects.requireNonNull(task.getException());
            } catch (FirebaseAuthInvalidUserException e) {
         /*       m_tv_sign_in_result.setError("Invalid Phone Number");
                m_tv_sign_in_result.requestFocus();
                m_tv_sign_in_result.setText("Invalid Phone Number");*/
                showSnackBar(this, getString(R.string.message_error_phone_number));
            } catch (FirebaseAuthInvalidCredentialsException e) {
              /*  m_tv_sign_in_result.setError("Invalid Password");
                m_tv_sign_in_result.requestFocus();
                m_tv_sign_in_result.setText("Invalid Password");*/
                showSnackBar(this, getString(R.string.message_error_password));


            } catch (FirebaseNetworkException e) {

                showSnackBar_with_action(this, getString(R.string.message_error_network_connection),getString(R.string.title_nav_download_vpn));

            } catch (Exception e) {
                Log.e(LOG_TAG, Objects.requireNonNull(e.getMessage()));
                showSnackBar_with_action(this, getString(R.string.message_error_network_connection),getString(R.string.title_nav_download_vpn));


            }

        }
    }


    private DatabaseReference get_UserAccountInfo_RootRef() {
        return db_root_users;
        // return db_root_users;
    }


    private void startSignUpActivity() {
        startActivity(new Intent(this, Activity_Sign_Up_Phone_Number.class).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT ));

    }


    private void checkUserReportsCount(String userID) {
        db_root_users.child(userID).child(PATH_REPORTS_COUNT_IN).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot_user_reports_count_in) {

                    user_reports_count_in = snapshot_user_reports_count_in.getValue(Integer.class);
                if (user_reports_count_in != null) {
                    Log.d(TAG, "onDataChange: user_reports_count_in"+user_reports_count_in);
                    if (userReportsCountsIsAcceptable(user_reports_count_in)) {
                        startActivity(new Intent(Activity_Sign_In_Phone_Number.this, Activity_Ineed_Ihave.class)
                                .putExtra(INTENT_KEY_USER_ID, userID));
                    } else {
                        auth.signOut();
                        showSnackBar(Activity_Sign_In_Phone_Number.this, getString(R.string.message_error_sign_in_reported_user));
                    }
                } else {
                    startActivity(new Intent(Activity_Sign_In_Phone_Number.this, Activity_Ineed_Ihave.class)
                            .putExtra(INTENT_KEY_USER_ID, userID));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    private boolean userReportsCountsIsAcceptable(Integer user_reports_count_in) {
        if(user_reports_count_in!=null)
        return user_reports_count_in < MAX_ALLOWED_REPORTS_COUNT;
        return true;
    }


    private String getContentOfView(View view) {
        return String.valueOf(((EditText) view).getText()).trim();
    }

    private boolean validateInput_UserAccountInfo() {

        return checkError(m_et_phoneNumber) && checkError(m_et_password);
    }

    private boolean checkError(View view) {
        boolean validationState = true;

        if (view.getId() == R.id.et_phoneNumber) {
            if (TextUtils.isEmpty(getContentOfView(view))) {
                (m_til_phone_number).setError(getString(R.string.message_error_empty_field));
                (view).requestFocus();
                validationState = false;
            }

        }
        if (view.getId() == R.id.et_password) {
            if (TextUtils.isEmpty(getContentOfView(view))) {
                m_til_password.setError(getString(R.string.message_error_empty_field));
                (view).requestFocus();

                validationState = false;
            }
        }


        return validationState;
    }


    public  void showSnackBar_with_action(Activity activity, String message, String action) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);

        Snackbar.make(rootView, message, BaseTransientBottomBar.LENGTH_LONG).setAction(action, v -> {
            if (action.equals(activity.getString(R.string.title_nav_download_vpn))) {

                confirm_download_VPN();
            }
        }).show();
    }


}