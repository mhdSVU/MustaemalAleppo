package mohammedyouser.com.mustaemalaleppo.Data;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import mohammedyouser.com.mustaemalaleppo.ConnectionReceiverListener;
import mohammedyouser.com.mustaemalaleppo.Device.ConnectionReceiver;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.SplashActivity;

import static mohammedyouser.com.mustaemalaleppo.Device.NetworkUtil.CheckNetworkConnectionStatus;
import static mohammedyouser.com.mustaemalaleppo.Device.NetworkUtil.isConnected;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.hideProgressDialog;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.showProgressDialog;

/**
 * Created by mohammed_youser on 8/29/2017.
 */

public class MustaemalAleppo extends MultiDexApplication implements  ConnectionReceiverListener  {

    @Override
    public void onCreate() {
        super.onCreate();
        // register connection status listener
        setConnectionListener(this);

        CheckNetworkConnectionStatus();

        if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }


        Intent splashActivityIntent;
        splashActivityIntent=new Intent(MustaemalAleppo.this, SplashActivity.class);
        startActivity(splashActivityIntent);

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        AndroidThreeTen.init(this);


    }




    public void setConnectionListener(ConnectionReceiverListener listener) {
        ConnectionReceiver.connectionReceiverListener = listener;
    }
    @Override
    public void onNetworkConnectionStatusChanged() {
        CheckNetworkConnectionStatus();

        if(!isConnected) {

            //show a No Internet Alert or Dialog
            showProgressDialog(this,getString(R.string.title_network_con_not_found),getString(R.string.message_info_network_con_check));

        }else{

            // dismiss the dialog or refresh the activity
            hideProgressDialog();
        }
    }

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

}
