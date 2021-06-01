package mohammedyouser.com.mustaemalaleppo.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import mohammedyouser.com.mustaemalaleppo.R;

public class Activity_ForgetPassword_SuccessfulReset extends AppCompatActivity implements View.OnClickListener {

    private Button m_btn_sign_in_proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_successful_reset);
       m_btn_sign_in_proceed=(Button) findViewById(R.id.btn_sign_in_proceed);
       m_btn_sign_in_proceed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sign_in_proceed:
                startSignInActivity();

                break;
        }
    }

    private void startSignInActivity() {

        startActivity(new Intent(this,Activity_Sign_In_Phone_Number.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
    private void startMainActivity() {
        startActivity(new Intent(this, Activity_Ineed_Ihave.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }
}