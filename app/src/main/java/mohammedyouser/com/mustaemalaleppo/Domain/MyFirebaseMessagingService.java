package mohammedyouser.com.mustaemalaleppo.Domain;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Ineed_Ihave_Activity;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.*;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private static final String MESSAGE_BODY_ITEM_KIND = "body_itemKind";
    private static final String MESSAGE_BODY_ITEM_CITY = "body_itemCity";
    private static final String MESSAGE_BODY_ITEM_CATEGORY = "body_itemCategory";
    private static final String MESSAGE_BODY_ITEM_ID = "body_itemId";
    private String itemKind;
    private String itemKindHint="Item for sale";;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        /////

        ////
// ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            itemKind=String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_KIND));
            if(itemKind.equals(PATH_INEED)){
                itemKindHint="Item needed";
            }
            getSelectedItemData(formAddedItemReference(remoteMessage));



            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //TODO   scheduleJob();
            } else {
                // Handle message within 10 seconds
                //TODO  handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
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
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance()
                .subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        //TODO  sendRegistrationToServer(token);
    }


    private DatabaseReference formAddedItemReference(RemoteMessage remoteMessage) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(PATH_ITEMS)
                .child(String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_KIND)))
                .child(String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_CITY)))
                .child(String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_CATEGORY)))
                .child(String.valueOf(remoteMessage.getData().get(MESSAGE_BODY_ITEM_ID)));

        return databaseReference;
    }

    private void getSelectedItemData(DatabaseReference selectedItem_db_ref) {

        Log.d(TAG, "getSelectedItemData: " + String.valueOf(selectedItem_db_ref));
        if (selectedItem_db_ref != null) {

            selectedItem_db_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String itemTitle = (String) dataSnapshot.child(PATH_ITEM_TITLE).getValue();
                    final String itemCity=((String) dataSnapshot.child(PATH_ITEM_CITY).getValue());
                    final String itemCategory=((String) dataSnapshot.child(PATH_ITEM_CATEGORY).getValue());

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            sendNotification(itemTitle,itemCity,itemCategory);


                        }
                    });


                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendNotification(String itemTitle,String itemCity,String itemCategory) {
        Intent intent = new Intent(getApplicationContext(), Ineed_Ihave_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        long[] vib = new long[]{500, 2000, 500, 2000};
        // Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pushnotify);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "f")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(itemKindHint+" in "+itemCity)
                .setContentText(itemCategory+": " +itemTitle)
                .setVibrate(vib)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());


        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
