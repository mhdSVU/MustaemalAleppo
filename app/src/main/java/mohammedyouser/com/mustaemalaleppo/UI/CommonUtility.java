package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import mohammedyouser.com.mustaemalaleppo.Data.Category;
import mohammedyouser.com.mustaemalaleppo.Data.City;
import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

/**
 * Created by mohammed_youser on 12/7/2017.
 */

public class CommonUtility {
    public static class CommonConstants {


        public static final int STATE_INITIALIZED = 1;
        public static final int STATE_CODE_SENT = 2;
        public static final int STATE_VERIFY_FAILED = 3;
        public static final int STATE_VERIFY_SUCCESS = 4;
        public static final int STATE_SIGN_IN_FAILED = 5;
        public static final int STATE_SIGN_IN_SUCCESS = 6;
        public static final int STATE_NETWORK_FAILED = 7;
        public static final int STATE_FAILED_TOO_MANY_REQUESTS = 8;
        public static final int STATE_FAILED_INVALID_PHONE_NUMBER = 9;
        public static final int STATE_FAILED_INVALID_PASSWORD = 10;
        public static final int STATE_FAILED_ALREADY_REGISTERED_USER = 11;

        public static final String STR_TAG_FRAGMENT_IN_VERIFICAION = "fragment_in_Verificaion";


        public static final int GPS_REQUEST = 1001;

        public static final String TAG = "ATAG";
        public static final String PACKAGE_NAME = "mohammedyouser.com.mustaemalaleppo";
        public static final int REQUEST_READ_PERMISSION_ITEM_MODIFY = 786;
        public static final int REQUEST_READ_PERMISSION_ITEM_OPEN_IMG = 787;
        public static final int REQUEST_LOCATION = 789;

        public static final String TAG2 = "TAG";
        public static final String BUNDLE_KEY_REQUEST_LOCATION_UPDATES = "request_loc_update";
        public static final String BUNDLE_KEY_ADD_ALERT = "request_add_alert";
        public static final String BUNDLE_KEY_REQUEST_LOCATION_UPDATE = "request_loc_update";
        public static final String BUNDLE_KEY_DOWNLOAD_VPN = "bundle_download_vpn";
        public static final String BUNDLE_KEY_FORGET_ME = "forget_me";
        public static final String BUNDLE_KEY_REMOVE_ALERT = "remove_alert";
        public static final String REQUEST_KEY_REMOVE_ALERT = "request_remove_alert";
        public static final String BUNDLE_KEY_REMOVE_FAVORITE = "remove_favorite";
        public static final String REQUEST_KEY_REMOVE_FAVORITE = "request_remove_favorite";
        public static final String BUNDLE_KEY_REMOVE_NOTIFICATION = "remove_notification";
        public static final String REQUEST_KEY_REMOVE_NOTIFICATION = "request_remove_notification";
        public static final String REQUEST_KEY_FORGET_ME = "request_forget_me";
        public static final String REQUEST_KEY_DOWNLOAD_VPN = "request_download_vpn";
        public static final String KEY_REQUEST_REMOVE_ALERT = "KEY_REQUEST_REMOVE_ALERT";
        public static final String BUNDLE_KEY_REPORT = "report";
        public static final String REQUEST_KEY_REPORT = "request_report";
        public static final String BUNDLE_KEY_ITEM_USER_ID = "item_userID";


        public static final String PATH_USER_LOCATION_LAT = "userLocLat";
        public static final String PATH_USER_LOCATION_LONG = "userLocLong";
        public static final String PATH_USER_ADDRESS = "userAdress";
        public static final String PATH_NOTIFICATIONS_COUNT = "userNotificationsCount";
        public static final String PATH_REPORTS_COUNT_IN = "reports_count_in";
        public static final String PATH_REPORTS_COUNT_IN_INAPPROPRIATE = "reports_count_in_inappropriate";
        public static final String PATH_REPORTS_COUNT_IN_MISLEADING = "reports_count_in_misleading";
        public static final String PATH_REPORTS_COUNT_IN_VIOLENCE = "reports_count_in_violence";
        public static final String PATH_REPORTS_COUNT_OUT = "reports_count_out";
        public static final String PATH_REPORTS = "Reports";
        public static final int MAX_ALLOWED_REPORTS_COUNT = 10;
        public static final int MAX_ALLOWED_REPORTS_COUNT_OUT = 10;
        public static final String PATH_REPORTED_USERS = "reported_users";

        public static String COUNTRY_NAME_SYR = "Syria";


        public static String PATH_IHAVE = "Ihave";
        public static String PATH_INEED = "Ineed";
        public static String PATH_ITEMS = "Items";


        public static String PATH_USER_ITEMS = "User_Items";

        public static String PATH_ALL_CITIES = "All-Cities";
        public static String PATH_ALL_CATEGORIES = "All-Categories";

        public static final String PATH_ALERT_STATE_INEED = "Ineed";
        public static final String PATH_ALERT_STATE_IHAVE = "Ihave";


        public static  String PATH_ALL_ITEMS = "All-Items";

        public static String PATH_ALL_ITEMS_TURKEY = "All-Items_Turkey";
        public static String PATH_ALL_ITEMS_EGYPT = "All-Items_Egypt";
        public static String PATH_ALL_ITEMS_QATAR = "All-Items_Qatar";
        public static String PATH_ALL_ITEMS_UAE = "All-Items_UAE";

        public static final String PATH_USERS_FAVORITES = "UsersIDs_Favorites";
        public static String PATH_TOKENS_FAVORITES = "Tokens_Favorites";
        public static String PATH_TOPICS_TOKENS = "TopicsIDs_Tokens";
        public static String PATH_TOKENS_TOPICS = "Tokens_TopicsIDs";
        public static String PATH_TOPICS_USERS = "TopicsIDs_UsersIDs";
        public static String PATH_USERS_TOPICS = "UsersIDs_TopicsIDs";
        public static String PATH_USERSIDs_TOKENS = "UsersIDs_Tokens";
        public static String PATH_TOKENS_USERS = "Tokens_UsersIDs";
        public static String PATH_USERIDS_NOTIFICATIONS = "UsersIDs_Notifications";
        public static String PATH_TOKENS_NOTIFICATIONS = "Tokens_Notifications";
        public static String PATH_TOPICS_NAMES = "Topics Names";
        public static String PATH_MAX_PRICE = "max_price";
        public static String PATH_MIN_PRICE = "min_price";
        public static String PATH_MAX_DISTANCE = "max_distance";


        public static String PATH_USERS_IDS = "userIDs";
        public static String PATH_USER_NAME = "userName";
        public static String PATH_USER_EMAIL = "userEmail";
        public static String PATH_USER_PHONE_NUMBER = "userPhoneNumber";
        public static String PATH_USER_PASSWORD = "userPassword";
        public static String PATH_USER_IMAGE = "userImage";
        public static String PATH_USER_LOCATION = "userLocation";
        public static String PATH_USERS = "Users";

        public static String PATH_ITEM_TITLE = "itemTitle";
        public static String PATH_ITEM_PRICE = "itemPrice";
        public static String PATH_ITEM_CITY = "itemCity";
        public static String PATH_ITEM_COUNTRY = "itemCountry";
        public static String PATH_ITEM_CATEGORY = "itemCategory";
        public static String PATH_ITEM_DATE_AND_TIME = "itemDateandTime";
        public static String PATH_ITEM_DATE_AND_TIME_REVERSE = "itemDateandTime_reverse";
        public static String PATH_ITEM_IMAGE = "itemImage";
        public static String PATH_ITEM_DETAILS = "itemDetails";
        public static String PATH_ITEM_USER_ID = "itemUserID";
        public static String PATH_ITEM_USER_NAME = "itemUserName";
        public static String PATH_ITEM_LAT = "itemLat";
        public static String PATH_ITEM_LONG = "itemLong";
        public static String PATH_TOKENS = "tokens";
        public static String PATH_TOKEN = "token";
        public static String PATH_USER_ID = "UserID";

        public static String PATH_FLAG_OPENED = "OpenedFlag";
        public static String PATH_NOTIFICATION_ITEM_ID = "notification_item_id";


        public static String PATH_STORAGE_USERS_PICTURES = "UsersPics";


        public static String INTENT_KEY__STATELABEL = "state label";
        public static String INTENT_VALUE__STATELABEL_INEED = "Add new \"Ineed\" Item";
        public static String INTENT_VALUE__STATELABEL_IHAVE = "Add new \"Ihave\" Item";

        public static String INTENT_KEY_USER_ID = "userID";
        public static String INTENT_KEY_LANGUAGE = "lan";
        // public static String INTENT_KEY_USER_NAME = "userName";
        public static String INTENT_KEY_USER_IMAGE = "userImage";
        //   public static String INTENT_KEY_USER_PHONE_NUMBER = "userPhone";
        public static String INTENT_KEY_USER_EMAIL = "userEmail";
        public static String INTENT_KEY__PATH_STATE = "state";
        public static String INTENT_KEY_TOPIC = "KEY_TOPIC";
        public static String INTENT_KEY_NOTIFICATION_ID = "notificationID";
        public static String INTENT_KEY_ITEM_LAT = "item_lat";
        public static String INTENT_KEY_ITEM_LONG = "item_long";


        public static String INTENT_KEY_ITEM_ID = "item_ID";
        public static String INTENT_KEY_ITEM_CAT = "item_Category";
        public static String INTENT_KEY_ITEM_CITY = "item_City";
        public static String INTENT_KEY_ACTIVITY_TAG = "activityName";
        public static String INTENT_KEY_SOURCE = "activityName";
        public static String INTENT_KEY_ITEM_IMG_URI = "item_img_uri";
        public static String INTENT_KEY_USER_IMAGE_URI = "userImageUri";
        public static String INTENT_KEY_USER_PHONE_NUMBER = "userPhoneNumber";
        public static String INTENT_KEY_USER_PASSWORD = "userPassword";
        public static String INTENT_KEY_USER_NAME = "userUserDisplayName";


        public static String INTENT_KEY__STATE = "state";
        public static String INTENT_VALUE__STATE_INEED = "Ineed";
        public static String INTENT_VALUE__STATE_IHAVE = "Ihave";

        public static String INTENT_VALUE_SOURCE_NOTIFICATION = "notification";


        public static String BUNDLE_KEY_BITMAP = "bitmap";

        public static String ASSETS_FILE_NAME_CITIES = "my_cities";
        public static String ASSETS_FILE_NAME_CATEGORIES = "my_categories";

        public static String CODE_ENCODE_SYSTEM_UTF_8 = "UTF-8";
        public static String CODE_FIREBASE_ID_TOKEN = "970940998859-jro8b29lmk9fd1ljp7kn57e2c4esen4p.apps.googleusercontent.com";
        public static String CODE_FIREBASE_STORAGE_REF = "gs://mustaemalaleppo.appspot.com/";
        public static ProgressDialog mProgressDialog;

        public static final String FRIENDLY_ENGAGE_TOPIC = "cars";

        public static boolean calledAlready = false;


        /**
         * This function is used to create and show the dialog
         */

        public static void showProgressDialog(Context context, String title, String message) {

            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), (dialog, which) -> mProgressDialog.dismiss());
            //   mProgressDialog.addContentView(new View(context,)); shap as abackground with alpha 1 or 0 // TODO
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawableResource(R.drawable.background_fragment_phone_verification);

            try {
                if (mProgressDialog != null)

                    mProgressDialog = ProgressDialog.show(context, title, message, false, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static ProgressDialog showProgressDialog(Context context, String title, String message, String l) {

            mProgressDialog = new ProgressDialog(context);
            //   mProgressDialog.addContentView(new View(context,)); //shape as a background with alpha 1 or 0 // TODO
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(message);
            mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            mProgressDialog.getWindow().setBackgroundDrawableResource(R.drawable.background_fragment_phone_verification);


            try {
                if (mProgressDialog != null)

                    mProgressDialog = ProgressDialog.show(context, title, message, false, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mProgressDialog;
        }


        /**
         * This function is used to dismiss the dialog
         */
        public static void hideProgressDialog() {
            if (mProgressDialog != null) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                } catch (Exception e) {

                    e.printStackTrace();
                    Log.d("hideProgressDialog", e.toString());
                }
            }
        }

        public static void hideProgressDialog(ProgressDialog mProgressDialog) {
            if (mProgressDialog != null) {
                try {

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                } catch (Exception e) {

                    e.printStackTrace();
                    Log.d("hideProgressDialog", e.toString());
                }
            }
        }

        public static void hideProgressBar(ProgressBar progressBar) {
            try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (progressBar.isAnimating())
                        progressBar.setVisibility(View.GONE);
                }
            } catch (Exception e) {

                e.printStackTrace();
                Log.d("hideProgressDialog", e.toString());
            }
        }

        public static void showSnackBar(Activity activity, String message) {
            View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, message, BaseTransientBottomBar.LENGTH_LONG).show();
        }


        public static void adjustLanguage(String lan, Context context) {
            if (!lan.equals("null")) {

/*
            LocaleHelper.setLocale(this, lan);
*/
                Locale locale = new Locale(lan);
                Locale.setDefault(locale);
                Configuration config = context.getResources().getConfiguration();
                config.setLayoutDirection(locale);
                config.locale = locale;
                context.getResources().updateConfiguration(config,
                        context.getResources().getDisplayMetrics());
            }

        }


        public static String[] get_Cities_array_data(Context context) {
            //build data citiesNames
            // Reading json file from assets dir
            String json = null;
            try {
                InputStream inputStream = context.getAssets().open(ASSETS_FILE_NAME_CITIES);
                byte[] buffer = new byte[context.getAssets().open(ASSETS_FILE_NAME_CITIES).available()];
                inputStream.read(buffer);
                inputStream.close();
                json = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();

            }
            //from json String to cities object
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            City[] cities = gson.fromJson(json, City[].class);
//TODO use copy method instead
            //creating the CountryNames array from cities array
            String[]  CitiesNames = new String[cities.length];
            for (int i = 0; i < cities.length; i++) {
                CitiesNames[i] = cities[i].getName();
            }
            return CitiesNames;
        }

        public static String[] get_Categories_array_data(Context context) {
            // Reading json file from assets dir
            String json = null;
            try {
                InputStream is = context.getAssets().open("my_categories");
                byte[] buffer = new byte[context.getAssets().open("my_categories").available()];
                is.read(buffer);
                is.close();
                json = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();

            }
            //from json String to  Categories object
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Category[] categories = gson.fromJson(json, Category[].class);

            //creating the CountryNames array from cities array
            String[]  CategoriesNames = new String[categories.length];
            for (int i = 0; i < categories.length; i++) {
                CategoriesNames[i] = categories[i].getName();
            }
            return CategoriesNames;
        }

        public static String[] get_Cities_array_locale(Context context) {
            if (LocaleHelper.getLocale(context, "en").equals("ar")) {
                // Reading json file from assets dir
                String json_ar = null;
                try {
                    InputStream inputStream = context.getAssets().open("my_cities_ar");
                    byte[] buffer = new byte[context.getAssets().open("my_cities_ar").available()];
                    inputStream.read(buffer);
                    inputStream.close();
                    json_ar = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
                //from json String to cities object
                GsonBuilder gsonBuilder_ar = new GsonBuilder();
                Gson gson_ar = gsonBuilder_ar.create();
                City[] cities = gson_ar.fromJson(json_ar, City[].class);

                //creating the CountryNames array from cities array
                String[] citiesNames_locale = new String[cities.length];
                for (int i = 0; i < cities.length; i++) {
                    citiesNames_locale[i] = cities[i].getName();
                }
                return citiesNames_locale;


            } else {
                return get_Cities_array_data(context);
            }


        }


        public static String[] get_Categories_array_locale(Context context) {
            if (LocaleHelper.getLocale(context, "en").equals("ar")) {
                // Reading json file from assets dir
                String json_ar = null;
                try {
                    InputStream inputStream = context.getAssets().open("my_categories_ar");
                    byte[] buffer = new byte[context.getAssets().open("my_categories_ar").available()];
                    inputStream.read(buffer);
                    inputStream.close();
                    json_ar = new String(buffer, CODE_ENCODE_SYSTEM_UTF_8);
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
                //from json String to cities object
                GsonBuilder gsonBuilder_ar = new GsonBuilder();
                Gson gson_ar = gsonBuilder_ar.create();
                Category[] categories = gson_ar.fromJson(json_ar, Category[].class);

                //creating the CountryNames array from cities array
                String[] categoriesNames_locale = new String[categories.length];
                for (int i = 0; i < categories.length; i++) {
                    categoriesNames_locale[i] = categories[i].getName();
                }
                Log.d(TAG, "get_Categories_array_locale: ar");
                return categoriesNames_locale;
            } else {
                Log.d(TAG, "get_Categories_array_locale: en");

                return get_Categories_array_data(context);
            }


        }

    }

}
