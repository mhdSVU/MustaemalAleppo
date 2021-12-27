package mohammedyouser.com.mustaemalaleppo.Domain;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jrummyapps.android.animations.Technique;

import java.net.URLEncoder;

import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.adjustLanguage;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.hideProgressDialog;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.showProgressDialog;


public class Activity_About_Developer extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_btn_developerImage;
    private ImageButton img_btn_call;
    private ImageButton img_btn_send_email;
    private ImageButton img_btn_whatsapp;


    private Uri uri_developer_img_static = Uri.parse("https://firebasestorage.googleapis.com/v0/b/mustaemalaleppo.appspot.com/o/IMG_20210601_210821.jpg?alt=media&token=1d08890e-f3c4-43d5-8dec-a9a923acf894");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()), this);

        setContentView(R.layout.activity_about_developer);

        img_btn_developerImage = findViewById(R.id.img_btn_developer_image);
        img_btn_call = findViewById(R.id.imageButton_user_phoneCall);
        img_btn_send_email = findViewById(R.id.imageButton_user_email);
        img_btn_whatsapp = findViewById(R.id.imageButton_user_whatsapp);

        img_btn_send_email.setOnClickListener(this);
        img_btn_whatsapp.setOnClickListener(this);
        img_btn_call.setOnClickListener(this);


        setImage_circle(this, uri_developer_img_static, 1, img_btn_developerImage);


    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setImage_circle(this, uri_developer_img_static, 0.3f, img_btn_developerImage);
    }


    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView) {
        imageView.setImageTintMode(null);
        Glide.with(context)
                .load(uri_developer_img_static)
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(thumbnail)
/*
                .diskCacheStrategy(DiskCacheStrategy.ALL)
*/
                .into(imageView)


        ;

        hideProgressDialog();
    }

    @Override
    public void onClick(View v) {
        Technique.PULSE.playOn(v);
        switch (v.getId()) {
            case R.id.imageButton_user_phoneCall: {
                String phoneNumber = "+963998359911";

                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                        getString(R.string.scheme), phoneNumber, null));
                startActivity(phoneIntent);

            }

            break;
            case R.id.imageButton_user_email: {

                String[] addresses = {"mohammedyousersawwas@gmail.com"};
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", addresses[0], null));

                emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses[0]);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "From Ineed Ihave App- Developer Contacting");
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.default_val_content_email_to_item_user, "subject"));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.chooser_title_email)));


            }
            break;
            case R.id.imageButton_user_whatsapp: {

                contact_via_Whatsapp("+963998359911", "");

            }


        }


    }

    private void contact_via_Whatsapp(String item_userPhoneNumber, String item_data) {


        try {
            PackageManager packageManager = getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + item_userPhoneNumber + "&text=" + URLEncoder.encode(item_data, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e) {
            Log.e("ERROR WHATSAPP", e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


}


