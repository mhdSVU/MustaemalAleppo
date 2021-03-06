package mohammedyouser.com.mustaemalaleppo.Domain;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import mohammedyouser.com.mustaemalaleppo.Data.ItemNotification;
import mohammedyouser.com.mustaemalaleppo.LocaleHelper;
import mohammedyouser.com.mustaemalaleppo.R;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;


public class Service_FirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private static final String MESSAGE_BODY_ITEM_KIND = "body_itemKind";
    private static final String MESSAGE_BODY_ITEM_CITY = "body_itemCity";
    private static final String MESSAGE_BODY_ITEM_CATEGORY = "body_itemCategory";
    private static final String MESSAGE_BODY_ITEM_ID = "body_itemId";
    private static final String MESSAGE_BODY_ITEM_PRICE = "body_itemPrice";
    private static final String MESSAGE_BODY_ITEM_TITLE = "body_itemTitle";
    private static final String GROUP_KEY_INEED = "mohammedyouser.com.mustaemalaleppo.Ineed";
    private static final String GROUP_KEY_IHAVE = "mohammedyouser.com.mustaemalaleppo.Ihave";
    int SUMMARY_ID_INEED = 0;
    int SUMMARY_ID_IHAVE = 1;

    private String itemState;
    private String itemStateHint;

    private String userID;
    public Bundle bundle;

    private DatabaseReference db_root_items;
    private DatabaseReference db_root_users_tokens_notifications;
    private DatabaseReference db_root_users_topics;
    private DatabaseReference db_root_tokens_topics;
    private DatabaseReference db_root_users;

    private int hCode;

    long notifications_count = 0L;

    private String[] categoriesData;
    private String[] citiesData;
    private String[] categoriesLocale;
    private String[] citiesLocale;


    @Override
    public void onCreate() {
        super.onCreate();
        adjustLanguage(LocaleHelper.getLocale(this, Resources.getSystem().getConfiguration().locale.getLanguage()), this);

        //  itemStateHint = getString(R.string.item_notification_state_hint_ihave);
        initialDatabaseRefs();
        initialDataArrays(Service_FirebaseMessagingService.this);
        setUpAuthentication();
    }

    public Service_FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            initialItemKind(remoteMessage);

            LogMsgData(remoteMessage);
            // checkMessageDataWithPreferences(remoteMessage);

            hCode = getTopicDataFromMessage_itemID(remoteMessage).hashCode();

            manageNotifications(remoteMessage);


            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //TODO   scheduleJob();
            } else {
                // Handle message within 10 seconds
                //TODO  handleNow();
            }


        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Once a token is generated, we subscribe to topic.


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        //TODO  sendRegistrationToServer(token);
    }


    //It is refreshed in 4 situation:
    //
    //App deletes Instance ID
    //
    //App is restored on a new device
    //
    //User uninstalls/reinstall the app
    //
    //User clears app data


    private void setUpAuthentication() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        }
    }

    private void initialDatabaseRefs() {

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();


        db_root_items = db_root.child(PATH_ITEMS);
        db_root_users = db_root.child(PATH_USERS);

        db_root_users_tokens_notifications = db_root.child(PATH_TOKENS_NOTIFICATIONS);

        db_root_users_topics = db_root.child(PATH_USERS_TOPICS);
        db_root_tokens_topics = db_root.child(PATH_TOKENS_TOPICS);


    }


    private void initialItemKind(RemoteMessage remoteMessage) {
        itemState = getTopicDataFromMessage_itemState(remoteMessage);
        if (itemState.equals(PATH_INEED)) {
            itemStateHint = getString(R.string.item_notification_state_hint_ineed);
        } else {
            itemStateHint = getString(R.string.item_notification_state_hint_ihave);


        }
    }

    private void LogMsgData(RemoteMessage remoteMessage) {

        Log.d(TAG, "onMessageReceived: " + " state " + getTopicDataFromMessage_itemState(remoteMessage) + " title " +
                getTopicDataFromMessage_itemTitle(remoteMessage) +
                getTopicDataFromMessage_itemCategory(remoteMessage)
                + "\t price " + getTopicDataFromMessage_itemPrice(remoteMessage) +
                getTopicDataFromMessage_itemID(remoteMessage));
    }

    private void checkMessageDataWithPreferences(String message_itemPrice, String message_itemTitle, RemoteMessage remoteMessage) {
        if (message_itemTitle.equals("null")) {
            Log.d(TAG, "checkMessageDataWithPreferences: null message_itemTitle " + message_itemTitle);
            return;
        }
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }
            // Get new FCM registration token
            String token = task.getResult();


            db_root_users_topics.child(userID).child(itemState).child(getTopicValueFromMessage(remoteMessage)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot_topic) {
                    //Check for Price preference
                    if (isInvalidPrice(String.valueOf(snapshot_topic.child(PATH_MAX_PRICE).getValue()),
                            String.valueOf(snapshot_topic.child(PATH_MIN_PRICE).getValue()),
                            message_itemPrice)) return;

                    //Check for Distance preference
                    get_ItemID_Ref(remoteMessage).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot_item) {
                            //fetch item and user location
                            db_root_users.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot_currentUser) {
                                    float itemDistance;
                                    if (!String.valueOf(snapshot_item.child(PATH_ITEM_LAT).getValue()).equals("0") &&
                                            !String.valueOf(snapshot_item.child(PATH_ITEM_LONG).getValue()).equals("0")
                                            &&
                                            !String.valueOf(snapshot_item.child(PATH_ITEM_LAT).getValue()).equals("null") &&
                                            !String.valueOf(snapshot_item.child(PATH_ITEM_LONG).getValue()).equals("null")
                                            &&
                                            !String.valueOf(snapshot_currentUser.child(PATH_USER_LOCATION_LAT).getValue()).equals("0") &&
                                            !String.valueOf(snapshot_currentUser.child(PATH_USER_LOCATION_LONG).getValue()).equals("0")
                                            &&

                                            !String.valueOf(snapshot_currentUser.child(PATH_USER_LOCATION_LAT).getValue()).equals("null") &&
                                            !String.valueOf(snapshot_currentUser.child(PATH_USER_LOCATION_LONG).getValue()).equals("null") &&

                                            !String.valueOf(snapshot_topic.child(PATH_MAX_DISTANCE).getValue()).equals("null")) {

                                        itemDistance = getDistanceBetweenTwoPoints(
                                                Double.parseDouble(String.valueOf(snapshot_item.child(PATH_ITEM_LAT).getValue())),
                                                Double.parseDouble(String.valueOf(snapshot_item.child(PATH_ITEM_LONG).getValue())),
                                                Double.parseDouble(String.valueOf(snapshot_currentUser.child(PATH_USER_LOCATION_LAT).getValue())),
                                                Double.parseDouble(String.valueOf(snapshot_currentUser.child(PATH_USER_LOCATION_LONG).getValue())));


                                        //fetch topic max distance
                                        //Compare item distance to topic max distance
                                        if ((itemDistance / 1000) > Double.parseDouble(String.valueOf(snapshot_topic.child(PATH_MAX_DISTANCE).getValue()))) { //return;
                                        }
                                    }


                                    //adding notification_data_to_DB
                                    db_root_users_tokens_notifications.child(token).child(itemState).child(getTopicValueFromMessage(remoteMessage))
                                            .child(getTopicDataFromMessage_itemID(remoteMessage)).runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                            if (currentData.getValue() == null) {
                                                currentData.setValue(true);
                                                //sendNotification(create_notification_from_msg(remoteMessage, message_itemTitle), remoteMessage);
                                                sendNotification(create_notification_from_msg(remoteMessage, String.valueOf(snapshot_item.child(PATH_ITEM_TITLE).getValue())), remoteMessage);


                                                return Transaction.success(currentData);
                                            }


                                            return Transaction.abort();
                                        }

                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                            if (committed) {
                                                // unique key saved
                                                Log.d(TAG, "onComplete: " + "unique key saved");
                                            } else {
                                                // unique key already exists
                                            }
                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void manageNotifications(RemoteMessage remoteMessage) {
        setUpAuthentication();
        if (userID.equals(null)) return;//Disable notifications when user is signed out
        get_ItemID_Ref(remoteMessage).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                      /*  if (String.valueOf(snapshot.child(PATH_ITEM_USER_ID).getValue()).equals(userID)) {
                            return;
                        }*/
                checkMessageDataWithPreferences(String.valueOf(snapshot.child(PATH_ITEM_PRICE).getValue()),
                        String.valueOf(snapshot.child(PATH_ITEM_TITLE).getValue()), remoteMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private DatabaseReference get_ItemID_Ref(RemoteMessage remoteMessage) {
        return db_root_items.child(getTopicDataFromMessage_itemState(remoteMessage))
                .child(getTopicDataFromMessage_itemCity(remoteMessage))
                .child(getTopicDataFromMessage_itemCategory(remoteMessage))
                .child(getTopicDataFromMessage_itemID(remoteMessage));
    }

    private ItemNotification create_notification_from_msg(RemoteMessage remoteMessage, String itemTitle) {

        return new ItemNotification(getTopicDataFromMessage_itemID(remoteMessage), itemTitle,
                getTopicDataFromMessage_itemCity(remoteMessage),
                getTopicDataFromMessage_itemCategory(remoteMessage),
                getTopicDataFromMessage_itemID(remoteMessage));

/*
            Log.d(TAG, "getSelectedItemData: " + String.valueOf(added_Item_db_ref));
        if (added_Item_db_ref != null) {

            added_Item_db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String itemID = (String) dataSnapshot.getKey();
                    final String itemTitle = (String) dataSnapshot.child(PATH_ITEM_TITLE).getValue();
                    final String itemCity = ((String) dataSnapshot.child(PATH_ITEM_CITY).getValue());
                    final String itemCategory = ((String) dataSnapshot.child(PATH_ITEM_CATEGORY).getValue());

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> sendNotification(itemTitle, itemCity, itemCategory, itemID));


                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/
    }

    private void sendNotification(ItemNotification itemNotification, RemoteMessage remoteMessage) {
        increaseUserNotificationsCount();

        long[] vib = new long[]{500, 2000, 500, 2000};
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        createNotificationChanel(remoteMessage, notificationManager, vib);

        NotificationCompat.Builder notificationBuilder = buildNotification(remoteMessage, itemNotification, vib);


        group_and_notify_Notifications(remoteMessage, notificationManager, notificationBuilder);


    }

    private NotificationCompat.Builder buildNotification(RemoteMessage remoteMessage, ItemNotification itemNotification, long[] vib) {
        // Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pushnotify);
        String contentTitle;
        itemState = getTopicDataFromMessage_itemState(remoteMessage);
        if (itemState.equals(PATH_INEED)) {
            contentTitle = getString(R.string.item_notification_state_hint_ineed, getCity_locale(itemNotification.getItemCity()));
            Log.d(TAG, "buildNotification: " + contentTitle);
        } else {
            contentTitle = getString(R.string.item_notification_state_hint_ihave, getCity_locale(itemNotification.getItemCity()));
            Log.d(TAG, "buildNotification: " + contentTitle);
        }

        return new NotificationCompat.Builder(getApplicationContext(), getTopicDataFromMessage_itemState(remoteMessage))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(contentTitle)
                .setContentText(getCategory_locale(itemNotification.getItemCategory()) + ": " + itemNotification.getItemTitle())
                .setVibrate(vib)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setChannelId(getTopicDataFromMessage_itemState(remoteMessage))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(getPendingIntent(itemNotification, remoteMessage));
    }

    private String getItemStateHint(RemoteMessage remoteMessage) {
        itemState = getTopicDataFromMessage_itemState(remoteMessage);
        if (itemState.equals(PATH_INEED)) {
            itemStateHint = getString(R.string.item_notification_state_hint_ineed);
        } else {
            itemStateHint = getString(R.string.item_notification_state_hint_ihave);

        }
        return itemStateHint;
    }

    private void createNotificationChanel(RemoteMessage remoteMessage, NotificationManagerCompat notificationManager, long[] vib) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(getTopicDataFromMessage_itemState(remoteMessage), "n_channel", NotificationManager.IMPORTANCE_MAX);
            itemState = getTopicDataFromMessage_itemState(remoteMessage);
            if (itemState.equals(PATH_INEED)) {
                itemStateHint = getString(R.string.item_notification_state_hint_ineed);
            } else {
                itemStateHint = getString(R.string.item_notification_state_hint_ihave);

            }
            notificationChannel.setDescription(itemStateHint);
            notificationChannel.setName(itemStateHint);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(vib);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void group_and_notify_Notifications(RemoteMessage remoteMessage, NotificationManagerCompat notificationManager, NotificationCompat.Builder notificationBuilder) {
        NotificationCompat.Builder notificationSummaryIneedBuilder = new NotificationCompat.Builder(getApplicationContext(), getTopicDataFromMessage_itemState(remoteMessage))

                .setContentTitle(getItemStateHint(remoteMessage))
                //set content text to support devices running API level < 24
                .setContentText(getString(R.string.item_notification_extra_ineed))
                .setSmallIcon(R.drawable.ic_ineed)
                //build summary info into InboxStyle template
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("")
                        .addLine("")
                        .setBigContentTitle(getString(R.string.item_notification_style_title))
                        .setSummaryText(getString(R.string.item_notification_extra_ineed)))

                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(getTopicDataFromMessage_itemState(remoteMessage))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setGroup(GROUP_KEY_INEED)
                .setGroupSummary(true);

        NotificationCompat.Builder notificationSummaryIhaveBuilder = new NotificationCompat.Builder(getApplicationContext(), getTopicDataFromMessage_itemState(remoteMessage))

                .setContentTitle(getItemStateHint(remoteMessage))
                //set content text to support devices running API level < 24
                .setContentText(getString(R.string.item_notification_extra_ihave))
                .setSmallIcon(R.drawable.ic_ihave)
                //build summary info into InboxStyle template
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("")
                        .addLine("")
                        .setBigContentTitle(getString(R.string.item_notification_style_title))
                        .setSummaryText(getString(R.string.item_notification_extra_ihave)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId(getTopicDataFromMessage_itemState(remoteMessage))
                .setLargeIcon(Activity_Display_Modify_Remove_Item.get_ScaledBitmap_from_drawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher)))
                .setGroup(GROUP_KEY_IHAVE)
                .setGroupSummary(true);


        if (itemState.equals(PATH_INEED)) {
            notificationBuilder.setGroup(GROUP_KEY_INEED);
            notificationManager.notify(hCode, notificationBuilder.build());
            notificationManager.notify(SUMMARY_ID_INEED, notificationSummaryIneedBuilder.build());
        }
        if (itemState.equals(PATH_IHAVE)) {
            notificationBuilder.setGroup(GROUP_KEY_IHAVE);
            notificationManager.notify(hCode, notificationBuilder.build());
            notificationManager.notify(SUMMARY_ID_IHAVE, notificationSummaryIhaveBuilder.build());
        }
    }

    private PendingIntent getPendingIntent(ItemNotification itemNotification, RemoteMessage remoteMessage) {
        Intent intent = new Intent(getApplicationContext(), Activity_Display_Modify_Remove_Item.class);

        intent.putExtra(INTENT_KEY_SOURCE, INTENT_VALUE_SOURCE_NOTIFICATION);
        intent.putExtra(INTENT_KEY_ITEM_ID, itemNotification.getItemID());
        intent.putExtra(INTENT_KEY_TOPIC, getTopicValueFromMessage(remoteMessage));
        intent.putExtra(INTENT_KEY__PATH_STATE, itemState);
        intent.putExtra(INTENT_KEY_NOTIFICATION_ID, getTopicDataFromMessage_itemID(remoteMessage));

        intent.putExtra(INTENT_KEY_ITEM_CAT, itemNotification.getItemCategory());
        intent.putExtra(INTENT_KEY_ITEM_CITY, itemNotification.getItemCity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(getApplicationContext(),
                hCode /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private float getDistanceBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween(lat1, lon1,
                lat2, lon2, distance);

        return distance[0] / 1000;
    }

    private boolean isInvalidPrice(String db_maxPrice, String db_minPrice, String message_itemPrice) {
        if (db_maxPrice == null && db_minPrice != null) {
            return Double.parseDouble(message_itemPrice) < Double.parseDouble(db_minPrice);
        }
        if (db_minPrice == null && db_maxPrice != null) {
            return Double.parseDouble(message_itemPrice) > Double.parseDouble(db_maxPrice);
        }
        if (db_minPrice == null && db_maxPrice == null) {
            return false;
        }

        if (db_maxPrice.equals("0") || db_minPrice.equals("0") || message_itemPrice.equals("0"))
            return false;
        if (db_maxPrice.equals("null") || db_minPrice.equals("null") || message_itemPrice.equals("null"))
            return false;

        return Double.parseDouble(message_itemPrice) > Double.parseDouble(db_maxPrice) ||
                Double.parseDouble(message_itemPrice) < Double.parseDouble(db_minPrice);
    }

    private void increaseUserNotificationsCount() {

        db_root_users.child(userID).child(PATH_NOTIFICATIONS_COUNT).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer notifications_count = 0;
                if (currentData.getValue() == null) {
                    currentData.setValue(++notifications_count);
                    return Transaction.success(currentData);
                }

                notifications_count = currentData.getValue(Integer.class);
                currentData.setValue(++notifications_count);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@com.google.firebase.database.annotations.Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    // unique key saved
                    Log.d(TAG, "onComplete: " + "unique key saved3");
                } else {
                    // unique key already exists
                }
            }
        });

    }


    private DatabaseReference form_AddedItem_Ref(RemoteMessage remoteMessage) {

        return FirebaseDatabase.getInstance().getReference()
                .child(PATH_ITEMS)
                .child(String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_KIND)))
                .child(String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_CITY)))
                .child(String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_CATEGORY)))
                .child(String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_ID)));
    }


    private String getTopicValueFromMessage(RemoteMessage remoteMessage) {
        return "Items" + "_" + remoteMessage.getData().get(MESSAGE_BODY_ITEM_KIND) +
                "_" + remoteMessage.getData().get(MESSAGE_BODY_ITEM_CITY) +
                "_" + remoteMessage.getData().get(MESSAGE_BODY_ITEM_CATEGORY);
    }

    private String getTopicDataFromMessage_itemID(RemoteMessage remoteMessage) {

        return String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_ID));
    }

    private String getTopicDataFromMessage_itemCity(RemoteMessage remoteMessage) {

        return String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_CITY));
    }

    private String getTopicDataFromMessage_itemCategory(RemoteMessage remoteMessage) {

        return String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_CATEGORY));
    }

    private String getTopicDataFromMessage_itemState(RemoteMessage remoteMessage) {

        return String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_KIND));
    }

    private String getTopicDataFromMessage_itemPrice(RemoteMessage remoteMessage) {

        return String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_PRICE));
    }

    private String getTopicDataFromMessage_itemTitle(RemoteMessage remoteMessage) {

        return String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_TITLE));
    }

    private void initialDataArrays(Context context) {
        categoriesData = get_Categories_array_data(context);
        citiesData = get_Cities_array_data(context);
        categoriesLocale = get_Categories_array_locale(context);
        citiesLocale = get_Cities_array_locale(context);
    }

    private String getCategory_locale(String category) {

        for (int i = 0; i < categoriesData.length; i++) {
            if (categoriesData[i].equals(category))
                return categoriesLocale[i];
        }

        return "";
    }

    private String getCity_locale(String city) {

        for (int i = 0; i < citiesData.length; i++) {
            if (citiesData[i].equals(city))
                return citiesLocale[i];
        }

        return "";
    }

     /*  .setContentTitle(itemStateHint + getString(R.string.in) + getCategory_locale(itemNotification.getItemCity()))
            .setContentText(getCategory_locale(itemNotification.getItemCategory()) + ": " + itemNotification.getItemTitle())*/


}
/*
*
*
*   private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            serviceChannel.setVibrationPattern(new long[]{ 0 });
            serviceChannel.enableVibration(true);
            serviceChannel.enableLights(false);
            serviceChannel.setSound(null, null);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
*
*
*
*
*
*
* */