package mohammedyouser.com.mustaemalaleppo.Domain;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.hideProgressDialog;


public class Activity_About_App extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private ImageView img_btn_userImage;

    private Uri uri_developer_img_static ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        mNameField = findViewById(R.id.editText_displayName);
        mEmailField = findViewById(R.id.editText_email);

        img_btn_userImage = findViewById(R.id.img_btn_developer_image);


        setSupportActionBar(findViewById(R.id.toolBar));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      //  setImage_circle(this, uri_developer_img_static, 1, img_btn_userImage);


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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


}



/*Location Handling End*/

