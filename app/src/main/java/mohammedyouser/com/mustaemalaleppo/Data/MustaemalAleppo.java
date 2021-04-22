package mohammedyouser.com.mustaemalaleppo.Data;

import android.content.Intent;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import mohammedyouser.com.mustaemalaleppo.Device.ConnectivityChangeReceiverListener;
import mohammedyouser.com.mustaemalaleppo.Device.ConnectivityChangeReceiver;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Ineed_Ihave_Activity;
import mohammedyouser.com.mustaemalaleppo.UI.SplashActivity;

import static mohammedyouser.com.mustaemalaleppo.Device.ConnectivityUtility.CheckConnectivity;
import static mohammedyouser.com.mustaemalaleppo.Device.ConnectivityUtility.isConnected;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_USER_PHONE_NUMBER;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.hideProgressDialog;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.showProgressDialog;

/**
 * Created by mohammed_youser on 8/29/2017.
 */

public class MustaemalAleppo extends MultiDexApplication implements ConnectivityChangeReceiverListener {

    @Override
    public void onCreate() {
        super.onCreate();
        // register connection status listener
        setConnectionListener(this);

        CheckConnectivity();

        if (!FirebaseApp.getApps(this).isEmpty()) {
         //   FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            String userID;
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
       if (userID != null)
            startMainActivity("",userID );
        }


        Intent splashActivityIntent;
        splashActivityIntent = new Intent(MustaemalAleppo.this, SplashActivity.class);
        startActivity(splashActivityIntent);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder1.build());

        AndroidThreeTen.init(this);

    }

    public void setConnectionListener(ConnectivityChangeReceiverListener listener) {
        ConnectivityChangeReceiver.connectivityChangeReceiver_Listener = listener;
    }

    @Override
    public void onConnectivityChanged() {
        CheckConnectivity();

        if (!isConnected) {

            //show a No Internet Alert or Dialog
            showProgressDialog(this, getString(R.string.title_network_con_not_found), getString(R.string.message_info_network_con_check));

        } else {

            // dismiss the dialog or refresh the activity
            hideProgressDialog();
        }
    }


    // TODO use Java 8 Time API instead
    private static ZoneId geZoneId() {
        return ZoneId.systemDefault();

    }

    public static ZonedDateTime getDate(@NonNull String isoDateString) {
        return ZonedDateTime.parse(isoDateString).withZoneSameInstant(MustaemalAleppo.geZoneId());
    }

    public static String formatDate(@NonNull String format, @NonNull String isoDateString) {
        return DateTimeFormatter.ofPattern(format).format(getDate(isoDateString));
    }
/*TODO:
TimeZone.setDefault(TimeZone.getDefault());
DateConverter.setShowLocalTime(true);
    ZonedDateTime dateInLocalTimeZone = DateConverter.getDate(dateInLocalTimeZone);

dateInLocalTimeZone.toString();  //2017-11-09T20:38:56+05:30[Asia/Kolkata]
*/

    //
    String date = "2017-03-17T14:00:00+08:00";
    String formattedString = formatDate("dd MM YYYY hh:mm:ss a", date);

    private void startMainActivity(String phone_number, String userID) {

        startActivity(new Intent(this, Ineed_Ihave_Activity.class)
                .putExtra(INTENT_KEY_USER_ID, userID)
                .putExtra(INTENT_KEY_USER_PHONE_NUMBER, phone_number));

    }

}
