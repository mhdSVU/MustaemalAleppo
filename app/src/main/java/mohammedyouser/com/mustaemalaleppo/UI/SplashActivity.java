package mohammedyouser.com.mustaemalaleppo.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jrummyapps.android.animations.Technique;

import mohammedyouser.com.mustaemalaleppo.R;

public class SplashActivity extends AppCompatActivity {
private TextView mtext1;
private ImageView mImageView;
private TextView mtext2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mtext1 =(TextView) findViewById(R.id.textView_motivation1);
        mtext2=(TextView) findViewById(R.id.textView_motivation2);
        mImageView=(ImageView) findViewById(R.id.imageView_logo);

        // play activity_single_item bounce animation on activity_single_item view
        Technique.BOUNCE_IN_UP.playOn(mtext1);
        Technique.SLIDE_IN_LEFT.playOn(mtext1);

        Technique.BOUNCE_IN_UP.playOn(mtext2);
        Technique.SLIDE_IN_LEFT.playOn(mtext2);

        Technique.FADE_IN.playOn(mImageView);
        Technique.FADE_OUT.playOn(mImageView);
        Technique.FLASH.playOn(mImageView);

        Technique.SLIDE_IN_RIGHT.playOn(mtext1);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

                    Intent intent = new Intent(SplashActivity.this, Ineed_Ihave_Activity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1000);

    }
}
