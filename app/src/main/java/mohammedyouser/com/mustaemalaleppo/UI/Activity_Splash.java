package mohammedyouser.com.mustaemalaleppo.UI;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jrummyapps.android.animations.Technique;

import java.util.Locale;

import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_LANGUAGE;

public class Activity_Splash extends AppCompatActivity {
    private TextView mtext1;
    private TextView mtext2;
    private TextView mtext3;
    private TextView mtext4;
    private TextView mTV_label;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras()!=null) {
            Log.d("ATAG", "Splash onCreate: !=null");
            if (getIntent().getExtras().containsKey(INTENT_KEY_LANGUAGE)) {
                changeLanguage(getIntent().getStringExtra(INTENT_KEY_LANGUAGE));
            }
            else {
                changeLanguage(LocaleHelper.getLocale(this,Resources.getSystem().getConfiguration().locale.getLanguage()));

            }
        }else{
            Log.d("ATAG", "Splash onCreate: ==null");

            changeLanguage(LocaleHelper.getLocale(this,Resources.getSystem().getConfiguration().locale.getLanguage()));

        }

        setContentView(R.layout.activity_splash);

        mtext1 = (TextView) findViewById(R.id.textView_thanks_1);
        mtext2 = (TextView) findViewById(R.id.textView_thanks_2);

        mtext3 = (TextView) findViewById(R.id.textView_motivation1);
        mtext4 = (TextView) findViewById(R.id.textView_motivation2);
        mTV_label = findViewById(R.id.tv_label_my_app);

        // play activity_single_item bounce animation on activity_single_item view
        Technique.BOUNCE_IN_UP.playOn(mtext1);
        Technique.SLIDE_IN_LEFT.playOn(mtext2);

        Technique.BOUNCE_IN_UP.playOn(mtext3);
        Technique.SLIDE_IN_LEFT.playOn(mtext3);

        Technique.BOUNCE_IN_UP.playOn(mtext4);
        Technique.SLIDE_IN_LEFT.playOn(mtext4);

        Technique.FADE_IN.playOn(mTV_label);
        Technique.FADE_OUT.playOn(mTV_label);
        Technique.FLASH.playOn(mTV_label);

        Technique.SLIDE_IN_RIGHT.playOn(mtext3);


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                Intent intent = new Intent(Activity_Splash.this, Activity_Ineed_Ihave.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(Activity_Splash.this, Activity_Sign_Up_Phone_Number.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }


    private void changeLanguage(String lan) {
        if (!lan.equals("null")) {
            Locale locale = new Locale(lan);
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLayoutDirection(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

    }


}
