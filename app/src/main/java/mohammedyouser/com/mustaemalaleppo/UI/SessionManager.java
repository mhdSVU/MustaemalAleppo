package mohammedyouser.com.mustaemalaleppo.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;

public class SessionManager {

    private static SessionManager sInstance;

    public static final String SHP_KEY_PHONE_NUMBER = "shp_phoneNumber";
    public static final String SHP_KEY_PASSWORD = "shp_password";

    public static final String SESSION_NAME_REMEMBER_ME = "rememberMe";
    public static final String SHP_KEY_ISREMEMBERME = "isrememberMe";


    private SharedPreferences shpref_user_session;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context, String sessionName) {
        shpref_user_session = context.getSharedPreferences(sessionName, context.MODE_PRIVATE);
        editor = shpref_user_session.edit();
    }

    public static synchronized SessionManager getInstance(Context context, String sessionName) {
        if(sInstance == null) sInstance = new SessionManager(context ,sessionName);
        return sInstance;
    }

    public void create_RememberMe_Session(String phoneNumber, String password) {
        editor.putBoolean(SHP_KEY_ISREMEMBERME, true);
        editor.putString(SHP_KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(SHP_KEY_PASSWORD, password);
        editor.commit();
        Log.d(TAG, "create_RememberMe_Session: " + phoneNumber);
        Log.d(TAG, "create_RememberMe_Session: " + password);
        Log.d(TAG, "get_RememberMe_Session: " + shpref_user_session.getString(SHP_KEY_PHONE_NUMBER, null));


    }

    public void delete_RememberMe_Session() {
        editor.putBoolean(SHP_KEY_ISREMEMBERME, true);
        editor.putString(SHP_KEY_PHONE_NUMBER, "");
        editor.putString(SHP_KEY_PASSWORD, "");
        editor.commit();
    }

    public HashMap<String, String> get_RememberMe_Session() {
        HashMap<String, String> map_user_data = new HashMap<>();
        map_user_data.put(SHP_KEY_PHONE_NUMBER, shpref_user_session.getString(SHP_KEY_PHONE_NUMBER, null));
        map_user_data.put(SHP_KEY_PASSWORD, shpref_user_session.getString(SHP_KEY_PASSWORD, null));
        return map_user_data;
    }

    public void createSession(String key, String value) {
        editor.putString(key, value);
        editor.commit();

    }

    public HashMap<String, String> getSession(String key) {
        HashMap<String, String> map_user_data = new HashMap<>();
        map_user_data.put(key, shpref_user_session.getString(key, null));
        return map_user_data;
    }


    public void createLoginSession(String phoneNumber, String password) {
        editor.putString(SHP_KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(SHP_KEY_PASSWORD, password);

    }

    public boolean isRememberMeSession() {
        return shpref_user_session.getBoolean(SHP_KEY_ISREMEMBERME, false);
    }
}
