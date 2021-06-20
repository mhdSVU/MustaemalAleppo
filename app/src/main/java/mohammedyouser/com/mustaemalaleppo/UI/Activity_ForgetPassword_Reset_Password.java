package mohammedyouser.com.mustaemalaleppo.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USER_PASSWORD;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.adjustLanguage;

public class Activity_ForgetPassword_Reset_Password extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "Activity_ForgetPassword_Reset_Password";
    private EditText m_et_password;
    private EditText m_et_password_confirm;


    private DatabaseReference db_root_users;
    private Button m_btn_reset_confirm;
    private Button m_btn_reset_cancel;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()),this);

        setContentView(R.layout.activity__reset__password);

        m_et_password = findViewById(R.id.et_password);
        m_et_password_confirm = findViewById(R.id.et_password_confirm);
        m_btn_reset_confirm = findViewById(R.id.btn_reset_confirm);
        m_btn_reset_cancel = findViewById(R.id.btn_reset_cancel);

        m_btn_reset_confirm.setOnClickListener(this);
        m_btn_reset_cancel.setOnClickListener(this);

        doFirebaseInitializations();
    }

    private void doFirebaseInitializations() {
        DatabaseReference db_root;
        auth = FirebaseAuth.getInstance();
        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);
        db_root_users.keepSynced(true);
    }

    private boolean validateInput_UserAccountInfo() {

        boolean c3 = checkError(m_et_password);
        boolean c4 = checkError(m_et_password_confirm);
        return c3 && c4;
    }

    private boolean checkError(View view) {
        boolean validationState = true;

        if (view.getId() == R.id.et_password) {
            if (TextUtils.isEmpty(getContentOfView(view))) {
                ((EditText) view).setError(getString(R.string.message_error_empty_field));
                view.requestFocus();
                validationState = false;
            }

            if (!getContentOfView(view).equals(getContentOfView(m_et_password_confirm))) {
                ((EditText) view).setError(getString(R.string.message_error_password_doesnt_match));
                view.requestFocus();

                validationState = false;
            }

        }
        if (view.getId() == R.id.et_password_confirm) {
            if (TextUtils.isEmpty(getContentOfView(view))) {
                ((EditText) view).setError(getString(R.string.message_error_empty_field));
                view.requestFocus();

                validationState = false;

            }
            if (!getContentOfView(view).equals(getContentOfView(m_et_password_confirm))) {
                ((EditText) view).setError(getString(R.string.message_error_password_doesnt_match));
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

    private String getContentOfView(View view) {


        return String.valueOf(((EditText) view).getText()).trim();
    }

    @Override
    public void onClick(View v) {
        boolean inputValid;
        switch (v.getId()) {

            case R.id.btn_reset_confirm: {
                 inputValid = validateInput_UserAccountInfo();
                if (inputValid) {
                resetUserPassword(auth.getCurrentUser(), String.valueOf(m_et_password.getText()));}
            }

            break;
         /*   case R.id.ccp: {

            }
            break;*/
            case R.id.btn_reset_cancel:
                auth.signOut();
                startSignInActivity();
                break;

        }


    }


    private void startSignInActivity() {
        startActivity(new Intent(this, Activity_Sign_In_Phone_Number.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void resetUserPassword(FirebaseUser currentUser, String newPassword) {
        currentUser.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storeNewPasswordToFirebase(currentUser.getUid(), newPassword);
                        startPassWordUpdatedActivity();
                    }
                }).addOnFailureListener(e -> {

        });
    }


    private void storeNewPasswordToFirebase(String userID, String newPassword) {

            get_UserAccountInfo_RootRef(userID).child(PATH_USER_PASSWORD).setValue(newPassword);
    }

    private DatabaseReference get_UserAccountInfo_RootRef(String userID) {
        return db_root_users.child(userID);
    }

    private void startPassWordUpdatedActivity() {
        auth.signOut();
        startActivity(new Intent(this, Activity_ForgetPassword_SuccessfulReset.class)
               .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        auth.signOut();
    }


}