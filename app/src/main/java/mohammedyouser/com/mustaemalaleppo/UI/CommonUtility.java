package mohammedyouser.com.mustaemalaleppo.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mohammed_youser on 12/7/2017.
 */

public  class CommonUtility {
    public static class CommonConstants {

        public static final String TAG = "TAG";
        public static String COUNTRY_NAME_SYR= "Syria";


        public static String ITEM_ID = "item_ID";
        public static String INTENT_KEY_ITEM_ID = "item_ID";
        public static String INTENT_KEY_ITEM_CAT = "item_Category";
        public static String INTENT_KEY_ITEM_CITY = "item_City";
        public static String INTENT_KEY_ACTIVITY_TAG = "activityName";


        public static String PATH_IHAVE = "Ihave";
        public static String PATH_INEED = "Ineed";
        public static String PATH_ITEMS = "Items";
        public static String PATH_ITEMS_USERS = "Items_users";
        public static String PATH_ALL_CITIES = "All Cities";
        public static String PATH_ALL_CATEGORIES = "All Categories";

        public static final String PATH_ALL_ITEMS ="All Items" ;


        public static String PATH_USERS_IDS ="userIDs" ;
        public static String PATH_USER_NAME= "userName";
        public static String PATH_USER_EMAIL = "userEmail";
        public static String PATH_USER_PHONE_NUMBER = "userPhoneNumber";
        public static String PATH_USER_IMAGE= "userImage";
        public static String PATH_USERS= "Users";

        public static String PATH_ITEM_TITLE = "itemTitle";
        public static String PATH_ITEM_PRICE = "itemPrice";
        public static String PATH_ITEM_CITY =   "itemCity";
        public static String PATH_ITEM_COUNTRY = "itemCountry";
        public static String PATH_ITEM_CATEGORY =    "itemCategory";
        public static String PATH_ITEM_DATE_AND_TIME =  "itemDateandTime";
        public static String PATH_ITEM_DATE_AND_TIME_REVERSE =  "itemDateandTime_reverse";
        public static String PATH_ITEM_IMAGE =      "itemImage";
        public static String PATH_ITEM_DETAILS=      "itemDetails";
        public static String PATH_ITEM_USER_ID=      "itemUserID";



        public static String PATH_STORAGE_USERS_PICTURES="UsersPics";



        public static String INTENT_KEY__STATELABEL = "state label";
        public static String INTENT_VALUE__STATELABEL_INEED ="Add new \"Ineed\" Item";
        public static String INTENT_VALUE__STATELABEL_IHAVE = "Add new \"Ihave\" Item";

        public static String INTENT_KEY_USER_ID = "userID";
        public static String INTENT_KEY_USER_NAME = "userName";
        public static String INTENT_KEY_USER_IMAGE  = "userImage";
        public static String INTENT_KEY_USER_PHONE_NUMBER = "userPhone";
        public static String INTENT_KEY_USER_EMAIL = "userEmail";
        public static String INTENT_KEY__PATH_STATE = "PathState";


        public static String INTENT_KEY__STATEVALUE = "state";
        public static String INTENT_VALUE__STATEVALUE_INEED = "INEED";
        public static String INTENT_VALUE__STATEVALUE_IHAVE = "IHAVE";




        public static String BUNDLE_KEY_BITMAP= "bitmap";

        public static String ASSETS_FILE_NAME_CITIES= "my_cities";
        public static String ASSETS_FILE_NAME_CATEGORIES= "my_categories";

        public static String CODE_ENCODE_SYSTEM_UTF_8= "UTF-8";
        public static String CODE_FIREBASE_ID_TOKEN = "970940998859-jro8b29lmk9fd1ljp7kn57e2c4esen4p.apps.googleusercontent.com";
        public static String CODE_FIREBASE_STORAGE_REF= "gs://mustaemalaleppo.appspot.com/";
        private static ProgressDialog mProgressDialog;

        public static final String FRIENDLY_ENGAGE_TOPIC = "cars";


        /**
         * This function is used to create and show the dialog
         *
         *
         */

        public static void showProgressDialog(Context context, String title, String message) {

            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(message);
            try
            {
                if (mProgressDialog != null)

                mProgressDialog = ProgressDialog.show(context, title, message, false,true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


        /**
         * This function is used to dismiss the dialog
         *
         *
         */
        public static void hideProgressDialog() {
            try
            {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
            catch (Exception e)
            {

                e.printStackTrace();
            }
        }


    }

}
