package mohammedyouser.com.mustaemalaleppo.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.util.Objects;

import mohammedyouser.com.mustaemalaleppo.Domain.VPN_AlertDialogFragment;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_USERS;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_READ_PERMISSION_ITEM_MODIFY;

public class Activity_Forget_Password_Enter_Number extends AppCompatActivity implements View.OnClickListener {
    private static final String STR_TAG_FRAGMENT_VERIFY_RESET = "fragment_phone_number_verify_tag";

    private CountryCodePicker m_cpp;

    private DatabaseReference db_root_users;
    private Uri uri_user_img = null;

    private Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account fragment_phone_number_verify;
    private Button m_btn_reset_start;
    private EditText m_et_phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__forget__password__enter__number);

        m_et_phoneNumber = findViewById(R.id.et_phoneNumber);
        m_btn_reset_start = (Button) findViewById(R.id.btn_reset_start);
        m_cpp = (CountryCodePicker) findViewById(R.id.ccp);

        m_btn_reset_start.setOnClickListener(this);
        m_cpp.setOnClickListener(this);

        doFirebaseInitializations();
        if (savedInstanceState != null) {
            fragment_phone_number_verify = (Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account) getSupportFragmentManager().findFragmentByTag(STR_TAG_FRAGMENT_VERIFY_RESET);
        } else {
            fragment_phone_number_verify = new Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account();
        }
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uri_user_img", String.valueOf(uri_user_img));
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
    }


    private void show_Phone_Number_Verify_Fragment_Dialog(Bundle bundle) {

        boolean validationState = validateInput_UserAccountInfo();
        if (validationState) {
            fragment_phone_number_verify = Fragment_Reset_Password_Phone_Number_Verify_and_Create_Account.newInstance(createBundle_UserAccountInfo());

            fragment_phone_number_verify.show(getSupportFragmentManager(), STR_TAG_FRAGMENT_VERIFY_RESET);
        } else {
            Toast.makeText(this, "Please first provide information needed!", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateInput_UserAccountInfo() {

        return checkError(m_et_phoneNumber);
    }

    private boolean checkError(View view) {
        boolean validationState = true;

        if (TextUtils.isEmpty(getContentOfView(view))) {
            ((EditText) view).setError(getString(R.string.message_error_empty_field));
            ((EditText) view).requestFocus();

            validationState = false;

        }

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
        switch (v.getId()) {

            case R.id.btn_reset_start:
                show_Phone_Number_Verify_Fragment_Dialog(createBundle_UserAccountInfo());
        }


    }


    private void startSignInActivity() {
        startActivity(new Intent(this, Activity_Sign_In_Phone_Number.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }


}