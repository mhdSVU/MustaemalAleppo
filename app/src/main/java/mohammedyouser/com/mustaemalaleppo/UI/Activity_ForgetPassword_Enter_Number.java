package mohammedyouser.com.mustaemalaleppo.UI;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import mohammedyouser.com.mustaemalaleppo.Domain.Fragment_Dialog_VPN_Alert;
import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.adjustLanguage;

public class Activity_ForgetPassword_Enter_Number extends AppCompatActivity implements View.OnClickListener {
    private static final String STR_TAG_FRAGMENT_VERIFY_RESET = "fragment_phone_number_verify_tag";

    private CountryCodePicker m_cpp;

    private DatabaseReference db_root_users;
    private FirebaseAuth auth;

    private final Uri uri_user_img = null;

    private Fragment_Reset_Password_Verify_Phone_Number fragment_phone_number_verify;
    private Button m_btn_reset_start;
    private EditText m_et_phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()), this);
        setContentView(R.layout.activity__forget__password__enter__number);

        m_et_phoneNumber = findViewById(R.id.et_phoneNumber);
        m_btn_reset_start = findViewById(R.id.btn_reset_start);
        m_cpp = findViewById(R.id.ccp);

        m_btn_reset_start.setOnClickListener(this);
        m_cpp.setOnClickListener(this);

        doFirebaseInitializations();
        if (savedInstanceState != null) {
            fragment_phone_number_verify = (Fragment_Reset_Password_Verify_Phone_Number) getSupportFragmentManager().findFragmentByTag(STR_TAG_FRAGMENT_VERIFY_RESET);
        } else {
            fragment_phone_number_verify = new Fragment_Reset_Password_Verify_Phone_Number();
        }
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


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void doFirebaseInitializations() {
        DatabaseReference db_root;
        db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);
        db_root_users.keepSynced(true);
        auth = FirebaseAuth.getInstance();

    }


    private void show_Phone_Number_Verify_Fragment_Dialog(Bundle bundle) {

        boolean validationState = validateInput_UserAccountInfo();
        if (validationState) {
            fragment_phone_number_verify = Fragment_Reset_Password_Verify_Phone_Number.newInstance(createBundle_UserAccountInfo());

            fragment_phone_number_verify.show(getSupportFragmentManager(), STR_TAG_FRAGMENT_VERIFY_RESET);
        } else {
            Toast.makeText(this, getString(R.string.message_info_error_information_missing), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateInput_UserAccountInfo() {


        return checkError(m_et_phoneNumber);
    }

    private boolean checkError(View view) {
        boolean validationState = true;

        if (TextUtils.isEmpty(getContentOfView(view))) {
            ((EditText) view).setError(getString(R.string.message_error_empty_field));
            view.requestFocus();

            validationState = false;

        }
      /*  if (auth.hasUserWithPhoneNumber(getFinalPhoneNumber())) {
            ((EditText) view).setError(getString(R.string.message_error_no_user_exists));
            view.requestFocus();

            validationState = false;

        }*/

        return validationState;
    }

    private Bundle createBundle_UserAccountInfo() {

        Bundle bundle = new Bundle();

        bundle.putString("userPhoneNumber", getFinalPhoneNumber());

        return bundle;
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

    private String getContentOfView(View view) {

        return String.valueOf(((EditText) view).getText()).trim();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reset_start) {
            show_Phone_Number_Verify_Fragment_Dialog(createBundle_UserAccountInfo());
        }


    }


    private void startSignInActivity() {
        startActivity(new Intent(this, Activity_Sign_In_Phone_Number.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }


}