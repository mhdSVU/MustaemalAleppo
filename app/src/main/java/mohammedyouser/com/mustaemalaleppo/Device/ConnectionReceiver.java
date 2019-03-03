package mohammedyouser.com.mustaemalaleppo.Device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mohammedyouser.com.mustaemalaleppo.ConnectionReceiverListener;


/**
 * Created by mohammed_youser on 1/18/2018.
 */

public class ConnectionReceiver extends BroadcastReceiver {
    public static ConnectionReceiverListener connectionReceiverListener;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (connectionReceiverListener != null) {
            connectionReceiverListener.onNetworkConnectionStatusChanged();
           
        }


    }




}

