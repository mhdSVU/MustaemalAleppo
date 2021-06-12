package mohammedyouser.com.mustaemalaleppo.Domain;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.hideProgressDialog;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.showProgressDialog;


public class Activity_About_Developer extends AppCompatActivity {

    private ImageView img_btn_developerImage;

    private Uri uri_developer_img_static = Uri.parse("https://firebasestorage.googleapis.com/v0/b/mustaemalaleppo.appspot.com/o/IMG_20210601_210821.jpg?alt=media&token=1d08890e-f3c4-43d5-8dec-a9a923acf894");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_developer);

        img_btn_developerImage = findViewById(R.id.img_btn_developer_image);


        setSupportActionBar(findViewById(R.id.toolBar));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setImage_circle(this, uri_developer_img_static, 1, img_btn_developerImage);


    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setImage_circle(this, uri_developer_img_static, 0.3f, img_btn_developerImage);
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

