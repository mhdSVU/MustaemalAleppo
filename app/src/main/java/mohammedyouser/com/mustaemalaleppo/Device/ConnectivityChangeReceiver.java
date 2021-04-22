package mohammedyouser.com.mustaemalaleppo.Device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by mohammed_youser on 1/18/2018.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    public static ConnectivityChangeReceiverListener connectivityChangeReceiver_Listener;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (connectivityChangeReceiver_Listener != null) {
            connectivityChangeReceiver_Listener.onConnectivityChanged();
           
        }


    }




}

