package mohammedyouser.com.mustaemalaleppo.Domain;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


public class SetUpContactInfo_Activity extends AppCompatActivity implements View.OnClickListener {
   
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPhoneNumberField;
    private ImageButton img_btn_userImage;

    private DatabaseReference db_root_users;
    private StorageReference mStorageReference;


    private Uri uri_userImage;
    private Uri download_uri;


    private Bitmap bitmap;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_contact_info);

        FirebaseAuth auth = FirebaseAuth.getInstance();
       userID= auth.getCurrentUser().getUid();

        mStorageReference=FirebaseStorage.getInstance().getReference();

        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);
        db_root_users.keepSynced(true);



        mNameField =     findViewById(R.id.editText_displayName);
        mEmailField =    findViewById(R.id.editText_email);
        mPhoneNumberField =    findViewById(R.id.editText_phone_number);

        img_btn_userImage =       findViewById(R.id.img_btn_user_image);
        Button mSave = findViewById(R.id.button_setUp_ContactInfo);
        Button mIgnore = findViewById(R.id.button_setUp_ignore);

        mSave.setOnClickListener(this);
        mIgnore.setOnClickListener(this);
        img_btn_userImage.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialContactInfoFields();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        if (savedInstanceState != null) {
            bitmap=((Bitmap) savedInstanceState.getParcelable(BUNDLE_KEY_BITMAP));
        }
        if(bitmap!=null)
            img_btn_userImage.setImageBitmap(bitmap);
    }

   
  
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_setUp_ContactInfo:
            {
                setUpContactInfo(getFinalStorageReference());
            }
            break;

            case R.id.button_setUp_ignore:
            {
                finish();
            }
            break;
            case R.id.img_btn_user_image:


                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start( this);

                break;
        }


    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri_userImage=  result.getUri();
                setImage_circle(this,uri_userImage,1,img_btn_userImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView)
    {
        Glide.with(context)
                .load(imageURL)
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(thumbnail)
/*
                .diskCacheStrategy(DiskCacheStrategy.ALL)
*/
                .into(imageView)


        ;


    }
    private void setUpContactInfo(final StorageReference storageReference) {


        if (TextUtils.isEmpty(getUserName_fromField()) || TextUtils.isEmpty(getUserEmail_fromField()) || TextUtils.isEmpty(getUserPhoneNumber_fromField())) {
            Toast.makeText(this, R.string.message_error_empty_field, Toast.LENGTH_SHORT).show();

        } else {
            showProgressDialog(this,getString(R.string.message_info_UPDATING_CONTACT_INFO),getString(R.string.message_info_PLEASE_WAIT));
            // First upload User image to firebase
            if (uri_userImage == null) {
                uri_userImage = Uri.EMPTY;

            }
try {
    storageReference.putFile(uri_userImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Toast.makeText(getBaseContext(), R.string.message_info_uploaded_ok, Toast.LENGTH_SHORT).show();
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    download_uri=uri;
                }
            });

            // Second save user name and the URL of user image to firebase
            storeUserInfoToFirebase();

            hideProgressDialog();
                finish();

        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            hideProgressDialog();
            Toast.makeText(getBaseContext(), e.toString() + " " + R.string.message_info_upload_error, Toast.LENGTH_LONG).show();

        }
    });
}
catch (Exception e)
{

    Toast.makeText(getBaseContext(), e.toString() , Toast.LENGTH_LONG).show();

}
finally {
    storeUserInfoToFirebase();

    hideProgressDialog();
        finish();
}
        }

    }
    private StorageReference getFinalStorageReference() {


        return mStorageReference.child(PATH_USERS).child(userID).child(PATH_USER_IMAGE);
    }

    private String getUserName_fromField() {

        return String.valueOf(mNameField.getText());
    }
    private String getUserEmail_fromField() {

        return  String.valueOf(mEmailField.getText());

    }
    private String getUserPhoneNumber_fromField() {
        return   String.valueOf(mPhoneNumberField.getText());




    }
    private Uri getUserImage_fromView() {
        if(download_uri!=null)
        return download_uri;

        else
        {
            return uri_userImage;
        }



    }

    private void initialContactInfoFields() {

        setUserName_fromDB( userID);
        setUserEmail_fromDB( userID);
        setUserPhoneNumber_fromDB( userID);
        setUserImage_fromDB(userID);
    }

    private String setUserName_fromDB(final String userID) {

        final String[] userName = {new String()};
        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    userName[0] = (String) dataSnapshot.child(userID).child(PATH_USER_NAME).getValue();

                mNameField.setText(userName[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userName[0];
    }
    private String setUserEmail_fromDB(final String userID) {

        final String[] userEmail = {new String()};
        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userID).child(PATH_USER_EMAIL).getValue()!=null) {
                    userEmail[0] = (String) dataSnapshot.child(userID).child(PATH_USER_EMAIL).getValue();
                    mEmailField.setText(userEmail[0]);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userEmail[0];


    }
    private String setUserImage_fromDB(final String userID) {

        final String[] userImage = {new String()};
        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(userID).child(PATH_USER_IMAGE).getValue()!=null) {
                    userImage[0] = (String) dataSnapshot.child(userID).child(PATH_USER_IMAGE).getValue();
                    setImage_circle(getBaseContext(),Uri.parse(userImage[0]),1,img_btn_userImage);
                    uri_userImage= Uri.parse(userImage[0]);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userImage[0];

    }
    private String setUserPhoneNumber_fromDB(final String userID) {
        final String[] userPhoneNumber = {new String()};
        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userID).child(PATH_USER_PHONE_NUMBER).getValue()!=null) {
                    userPhoneNumber[0] = (String) dataSnapshot.child(userID).child(PATH_USER_PHONE_NUMBER).getValue();
                    mPhoneNumberField.setText( userPhoneNumber[0] );


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userPhoneNumber[0];


    }

    private void storeUserInfoToFirebase() {

        db_root_users.child(userID).child(PATH_USER_NAME).setValue(getUserName_fromField());
        db_root_users.child(userID).child(PATH_USER_IMAGE).setValue(getUserImage_fromView());
        db_root_users.child(userID).child(PATH_USER_EMAIL).setValue(getUserEmail_fromField());
        db_root_users.child(userID).child(PATH_USER_PHONE_NUMBER).setValue(getUserPhoneNumber_fromField());


    }






    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
