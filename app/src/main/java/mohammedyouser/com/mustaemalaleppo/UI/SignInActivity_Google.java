package mohammedyouser.com.mustaemalaleppo.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mohammedyouser.com.mustaemalaleppo.R;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

public class SignInActivity_Google extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener
{


    private DatabaseReference db_root_users;

    private FirebaseAuth auth;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;

    private String userID;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in_google);

        auth = FirebaseAuth.getInstance();
        db_root_users = FirebaseDatabase.getInstance().getReference().child(PATH_USERS);
        db_root_users.keepSynced(true);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.button_sign_in_Google).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//Google started
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CODE_FIREBASE_ID_TOKEN)
                .requestEmail()
                .requestProfile()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build activity_single_item GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]




        // [START customize_button]
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.button_sign_in_Google);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // [END customize_button]
//Google ended
        signIn();

    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgressDialog(this,getString(R.string.message_info_Authenticating),getString(R.string.message_info_PLEASE_WAIT));

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            // since op done we can get the result
            GoogleSignInResult result = opr.get();

            ///Three states:

            //sate1: handle for silent right away
            handleSignInResult(result);

        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            // since op not done yet we have to wait for the result

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {

            //sate2: handle for silent but later
            handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }



    // [START signIn]
    public void signIn() {
//       showProgressDialog();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]




    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            //sate3: handle for not silent right away
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]



    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            updateUI(true);

            firebaseAuthWithGoogle(account);



        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
            hideProgressDialog();

        }
    }
    // [END handleSignInResult]




    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            userID=auth.getCurrentUser().getUid();
                            storeUserInfoToFirebase(account);
                            updateUI(true);
                            hideProgressDialog();
                            startMainActivity(account,SignInActivity_Google.this);

                        } else {
                            // If sign in fails, display activity_single_item message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            hideProgressDialog();
                            Toast.makeText(SignInActivity_Google.this, getString(R.string.message_error_sign_in_google),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(false);
                        }

                        // ...
                    }
                });
    }

    private void storeUserInfoToFirebase(GoogleSignInAccount account) {

        db_root_users.child(userID).child(PATH_USER_NAME).setValue(account.getDisplayName());
        if (account.getPhotoUrl()!=null)
       {db_root_users.child(userID).child(PATH_USER_IMAGE).setValue(account.getPhotoUrl().toString());}
       db_root_users.child(userID).child(PATH_USER_EMAIL).setValue(account.getEmail());
       db_root_users.child(userID).child(PATH_USER_PHONE_NUMBER).setValue("+963962938872");




    }

    private void startMainActivity(GoogleSignInAccount account, Context context) {

        Intent mainIntent =new Intent(context,Ineed_Ihave_Activity.class);
        mainIntent.putExtra(INTENT_KEY__STATEVALUE,INTENT_VALUE__STATEVALUE_INEED);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       startActivity(mainIntent);

    }



    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }




    private void updateUI(boolean signedIn) {
        if (signedIn) {

        } else {
            mStatusTextView.setText(R.string.message_info_signed_out);



        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_in_Google:
                signIn();
                break;
            case R.id.button_sign_out:
                signOut();
                break;
                  }
    }



}
