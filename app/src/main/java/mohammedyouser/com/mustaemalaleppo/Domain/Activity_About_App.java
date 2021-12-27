package mohammedyouser.com.mustaemalaleppo.Domain;


import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.adjustLanguage;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.hideProgressDialog;


public class Activity_About_App extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()),this);

        setContentView(R.layout.activity_about_app);
    }



}


