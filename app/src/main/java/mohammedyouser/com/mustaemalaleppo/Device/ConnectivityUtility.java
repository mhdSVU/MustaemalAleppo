package mohammedyouser.com.mustaemalaleppo.Device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by mohammed_youser on 1/18/2018.
 */

//This class ConnectivityUtility: Executing a task that checks isInternetAvailable( via CheckConnectivityStatus)
public class ConnectivityUtility {
    //TODO copy for later use
/*    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0, NETWORK_STAUS_WIFI = 1, NETWORK_STATUS_MOBILE = 2;*/
    public static boolean isConnected;


    public static void CheckConnectivity() {
        Task_Check_ConnectionState taskCheckConnectionState = new Task_Check_ConnectionState();
        taskCheckConnectionState.execute("8.8.8.8");

    }

    public static boolean isInternetAvailable(String address, int port, int timeoutMs) {
        Socket sock = new Socket();
        try {

            SocketAddress sockaddr = new InetSocketAddress(address, port);

            sock.connect(sockaddr, timeoutMs); // This will block no more than timeoutMs
            sock.close();

            return true;

        } catch (IOException e) {
            try {
                sock.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
    }

 /*   public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = ConnectivityUtility.getConnectivityStatus(context);
        int status = 0;
        if (conn == ConnectivityUtility.TYPE_WIFI) {
            status = NETWORK_STAUS_WIFI;
        } else if (conn == ConnectivityUtility.TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == ConnectivityUtility.TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }
*/ //TODO copy for later use

    private static class Task_Check_ConnectionState extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            return isInternetAvailable(params[0], 53, 1000);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            ConnectivityUtility.isConnected = result;
        }

    }

}