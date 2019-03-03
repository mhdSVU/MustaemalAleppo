package mohammedyouser.com.mustaemalaleppo.Device;

import android.os.AsyncTask;


/**
 * Created by mohammed_youser on 1/21/2018.
 */


public   class CheckConnectionStateTask extends AsyncTask<String,String,Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        return NetworkUtil.isInternetAvailable(params[0], 53, 1000);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        NetworkUtil.isConnected = result;
    }

}