package mohammedyouser.com.mustaemalaleppo.UI;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import mohammedyouser.com.mustaemalaleppo.R;

import static android.app.Activity.RESULT_OK;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUp_fragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private View fragment_view;
    private EditText mNamefield;
    private EditText mEmailfield;
    private EditText mPasswordfield;
    private ImageButton mUserImage;

    private DatabaseReference db_root_users;
    private FirebaseAuth auth;
    private StorageReference mStorageReference;
    private StorageReference finalStorageReference;


    private Uri uri_userImage;
    private Uri download_uri=Uri.EMPTY;


    private Bitmap bitmap;
    private String userID;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof SignIn_fragment.OnFragmentInteractionListener)
        {
            mListener=(SignUp_fragment.OnFragmentInteractionListener) context;
        }
        else {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener.");
        }
    }
    public SignUp_fragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment_view=inflater.inflate(R.layout.fragment_sign_up,container,false);

        return fragment_view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onViewClickInFragment(View v) {
        if (mListener != null) {
            mListener.onFragmentInteraction(fragment_view,v.getId());
        }
    }

   /* @Override
    public void onClick(View v) {
        onViewClickInFragment(v);

    }*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        auth = FirebaseAuth.getInstance();
        mStorageReference=FirebaseStorage.getInstance().getReference();

        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();
        db_root_users = db_root.child(PATH_USERS);
        db_root_users.keepSynced(true);



        mNamefield =     view.findViewById(R.id.editText_displayName);
        mEmailfield =    view.findViewById(R.id.editText_email);
        mPasswordfield = view.findViewById(R.id.editText_password);
        Button mSignup = view.findViewById(R.id.button_signUp);
        mUserImage =        view.findViewById(R.id.img_btn_user_image);

        mSignup.setOnClickListener(this);
        mUserImage.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (savedInstanceState != null) {
            bitmap=((Bitmap) savedInstanceState.getParcelable(BUNDLE_KEY_BITMAP));
        }
        if(bitmap!=null)
            mUserImage.setImageBitmap(bitmap);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(View view,int viewId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_signUp:
            {
                signUp();

            }
            break;
            case R.id.img_btn_user_image:

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getContext(), this);

                break;
        }


    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE_GALLERY = 5;
        if(requestCode== REQUEST_CODE_GALLERY &&resultCode==RESULT_OK)
        {
            //seting Item image
            uri_userImage = data.getData();
            setUserImage(uri_userImage);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri_userImage = result.getUri();
                setUserImage(uri_userImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), "Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void setUserImage(Uri uri) {
        try {
            InputStream inputStream= Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(uri);
            bitmap=   BitmapFactory.decodeStream(inputStream);
            mUserImage.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



    private void signUp() {
        final String mName = mNamefield.getText().toString().trim();
        final String mEmail      = mEmailfield.getText().toString().trim();
        String mPassword   = mPasswordfield.getText().toString().trim();

        if (TextUtils.isEmpty(mName)) {
            mNamefield.setError(getString(R.string.message_error_empty_field));
        } if (TextUtils.isEmpty(mEmail)) {
            mEmailfield.setError(getString(R.string.message_error_empty_field));
        }
        if (TextUtils.isEmpty(mPassword)){
            mPasswordfield.setError(getString(R.string.message_error_empty_field));
        }


        if(!TextUtils.isEmpty(mName)&&
                !TextUtils.isEmpty(mEmail)&&
                !TextUtils.isEmpty(mPassword))
        {
            showProgressDialog(getContext(),getString(R.string.message_info_SIGNING_UP),getString(R.string.message_info_PLEASE_WAIT));


                    auth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        userID = auth.getCurrentUser().getUid();

                        createUserProfile(getFinalStorageReference(),userID);
                                       }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();

                }
            });




        }

        else
        {
            Toast.makeText(getActivity(), R.string.message_error_empty_field, Toast.LENGTH_SHORT).show();
        }
    }

    private void createUserProfile(final StorageReference finalStorageReference, final String userID) {

        // First upload User image to firebase
        if(uri_userImage==null)
        {
            uri_userImage=Uri.EMPTY;
        }

            finalStorageReference.putFile(uri_userImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), R.string.message_info_uploaded_ok, Toast.LENGTH_SHORT).show();
                    finalStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            download_uri=uri;
                        }
                    });


       // Second save user name, email,...and the URL of user image to firebase
                    storeUserInfoToFirebase(userID);

                    hideProgressDialog();
                    startMainActivity();



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Toast.makeText(getActivity(), e.toString()+" " + R.string.message_info_upload_error, Toast.LENGTH_LONG).show();

                }
            });
        }


    private StorageReference getFinalStorageReference() {
        if (uri_userImage != null) {

            finalStorageReference = mStorageReference.child(PATH_USERS).child(PATH_USER_IMAGE).child(userID).child(uri_userImage.toString());
        }
            return finalStorageReference;
    }



    private void storeUserInfoToFirebase(final String userID) {

        db_root_users.child(userID).child(PATH_USER_NAME).setValue(getUserName());
        db_root_users.child(userID).child(PATH_USER_IMAGE).setValue(getUserImage());
        db_root_users.child(userID).child(PATH_USER_EMAIL).setValue(getUserEmail());
        db_root_users.child(userID).child(PATH_USER_PHONE_NUMBER).setValue(getUserPhoneNumber());

    }

    private String getUserName() {

        return mNamefield.getText().toString().trim();

    }
    private String getUserEmail() {

        return  mEmailfield.getText().toString().trim();



    }
    private String getUserPhoneNumber() {

        return getString(R.string.default_val_user_phone_number);


    }

    private Uri getUserImage() {
        return download_uri;

    }


    private void startMainActivity() {

        Intent mainIntent =new Intent(getActivity(),Ineed_Ihave_Activity.class);
        mainIntent.putExtra(INTENT_KEY__STATEVALUE,INTENT_VALUE__STATEVALUE_INEED);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_KEY_BITMAP,bitmap);


    }

}
