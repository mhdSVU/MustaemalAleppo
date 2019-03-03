package mohammedyouser.com.mustaemalaleppo.UI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mohammedyouser.com.mustaemalaleppo.R;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

public class SignInActivity_EmlPass extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignIn;
    public FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;


    private DatabaseReference db_root_users;

    private String userName;
    private String userImage;
    private String userEmail;
    private String userPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in_eml_pss);

        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        buttonSignIn = findViewById(R.id.button_signIn);

        initialUserInfo();

        auth = FirebaseAuth.getInstance();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {

                    startActivity(new Intent(SignInActivity_EmlPass.this,Ineed_Ihave_Activity.class));
                }

            }
        };

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            }
        });
        initialAuthentication();
        initialDatabaseReferences();

        initialToolBar();

    }

    private void initialToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initialAuthentication() {
        auth=FirebaseAuth.getInstance();

    }

    private void initialDatabaseReferences() {
        db_root_users = FirebaseDatabase.getInstance().getReference().child("Users");

    }



    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }

    public void signIn(final String email, String pasword) {


        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.message_error_empty_field));
        }
        if (TextUtils.isEmpty(pasword)){

            editTextPassword.setError(getString(R.string.message_error_empty_field));
    }



        if (!(TextUtils.isEmpty(email))&& !(TextUtils.isEmpty(pasword)))
        {
            showProgressDialog(this,getString(R.string.message_info_Authenticating),getString(R.string.message_info_PLEASE_WAIT));
            auth.signInWithEmailAndPassword(email, pasword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    hideProgressDialog();
                    if(!task.isSuccessful())
                    {
                        Toast.makeText(SignInActivity_EmlPass.this,R.string.message_error_sign_in, Toast.LENGTH_LONG).show();
                    }

                    else if(task.isSuccessful())
                    {

                        hideProgressDialog();
                        startMainActivity(getBaseContext());
                    }

                }
            });
        }

    }

    private String getUserName(final String userID) {


        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userID).child(PATH_USER_NAME).getValue()!=null) {
                    userName = (String) dataSnapshot.child(userID).child(PATH_USER_NAME).getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return userName;
    }
    private String getUserEmail(final String userID) {

        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userID).child(PATH_USER_EMAIL).getValue()!=null) {
                    userEmail =(String) dataSnapshot.child(userID).child(PATH_USER_EMAIL).getValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userEmail;


    }
    private String getUserImage(final String userID) {

        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userID).child(PATH_USER_IMAGE).getValue()!=null) {
                    userImage = (String)dataSnapshot.child(userID).child(PATH_USER_IMAGE).getValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userImage;

    }
    private String getUserPhoneNumber(final String userID) {

        db_root_users.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userID).child(PATH_USER_PHONE_NUMBER).getValue()!=null) {
                    userPhoneNumber = (String) dataSnapshot.child(userID).child(PATH_USER_PHONE_NUMBER).getValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userPhoneNumber;


    }
    private void initialUserInfo() {
        userName=(getString(R.string.default_val_user_name));
        userImage =String.valueOf(Uri.EMPTY);
        userEmail =(getString(R.string.default_val_user_email));
        userPhoneNumber=(getString(R.string.default_val_user_phone_number));

    }






    private void startMainActivity( Context context) {
        final String  userID= auth.getCurrentUser().getUid();

        Intent mainIntent =new Intent(context,Ineed_Ihave_Activity.class);

        mainIntent.putExtra(INTENT_KEY__STATEVALUE,INTENT_VALUE__STATEVALUE_INEED);

        mainIntent.putExtra(INTENT_KEY_USER_NAME, getUserName(userID));
        mainIntent.putExtra(INTENT_KEY_USER_PHONE_NUMBER, getUserEmail(userID));
        mainIntent.putExtra(INTENT_KEY_USER_IMAGE, getUserImage(userID));
        mainIntent.putExtra(INTENT_KEY_USER_PHONE_NUMBER,getUserPhoneNumber(userID));

        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
