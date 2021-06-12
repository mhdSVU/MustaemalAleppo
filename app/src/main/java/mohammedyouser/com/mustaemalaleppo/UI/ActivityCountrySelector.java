package mohammedyouser.com.mustaemalaleppo.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;

public class ActivityCountrySelector extends AppCompatActivity {
    private static final String SELECTED_COUNTRY = "ActivityCountrySelector.Country";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_selector);
    }
    private static void setSelectedCountry(Context context, String country) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_COUNTRY, country);
        editor.apply();
        Log.d(TAG, "persist: "+preferences.getString(SELECTED_COUNTRY, "Syria"));

    }

    public static String getSelectedCountry(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG, "getLocale: "+preferences.getString(SELECTED_COUNTRY, "Syria"));
        return preferences.getString(SELECTED_COUNTRY, language);
    }
}