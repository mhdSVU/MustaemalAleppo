package mohammedyouser.com.mustaemalaleppo.Data;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.common.hash.HashingOutputStream;
import com.google.firebase.messaging.RemoteMessage;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import mohammedyouser.com.mustaemalaleppo.Domain.Activity_Display_Modify_Remove_Item;
import mohammedyouser.com.mustaemalaleppo.Domain.ItemNotification;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Fragment_Remove_Alert_Dialog;

import static mohammedyouser.com.mustaemalaleppo.Data.MyViewHolder.TAG;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_CAT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_CITY;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_NOTIFICATION_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_SOURCE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_TOPIC;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__PATH_STATE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_VALUE_SOURCE_NOTIFICATION;

public class DocExpandableRecyclerAdapter_Notifications extends
        ExpandableRecyclerViewAdapter<MyParentViewHolder, MyChildViewHolder> {
    private Context mContext;
    private Notification notification;


    public DocExpandableRecyclerAdapter_Notifications(List<NotificationTopic> groups, Context context) {
        super(groups);
        mContext = context;

    }

    @Override
    public MyParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent, parent, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new MyChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

        notification = ((NotificationTopic) group).getItems().get(childIndex);
        holder.onBind(notification.getmTitle());
        final String TitleChild = group.getTitle();
        holder.listChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });
        holder.listChild.setText(notification.getmUserName() + "Added " + notification.getmTitle() + "\n On " + notification.getmDateTime());
        Log.d(TAG, "onBindChildViewHolder: " + notification.getmValue());
        if (notification.getmValue().equals("false")) {
            holder.ll_listChild.setBackgroundColor(mContext.getColor(R.color.grey_300));
        } else {
            holder.ll_listChild.setBackgroundColor(mContext.getColor(R.color.green_light));
        }
        holder.ll_listChild.setOnLongClickListener(view -> {

           /* confirmRemoveAlert("removeDialogFragment", topic.getmTopicState(), topic.getmTitle().split(" ")[0],
                    topic.getmTitle().split(" ")[2]);*/
            return false;
        });
        holder.ll_listChild.setOnClickListener(view -> {
            mContext.startActivity(getNotificationIntent(notification));

           /* confirmRemoveAlert("removeDialogFragment", topic.getmTopicState(), topic.getmTitle().split(" ")[0],
                    topic.getmTitle().split(" ")[2]);*/

        });


    }

    @Override
    public void onBindGroupViewHolder(MyParentViewHolder holder, int flatPosition, final ExpandableGroup group) {
        holder.setParentTitle(group);

        if (group.getItems() == null) {
            holder.listGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

           /*         Toast toast = Toast.makeText(getApplicationContext(), group.toString(), Toast.LENGTH_SHORT);
                    toast.show();*/
                }
            });

        }
    }

    public void confirmRemoveAlert(String tag, String itemState, String itemCity, String itemCategory) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.


        Fragment_Remove_Alert_Dialog remove_alert_fragment = Fragment_Remove_Alert_Dialog.newInstance(itemState, itemCategory, itemCity);

        FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity) mContext).getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        // save transaction to the back stack
        ft.addToBackStack(tag);
        remove_alert_fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), tag);
        ((AppCompatActivity) mContext).getSupportFragmentManager().executePendingTransactions();
    }

    private Intent getNotificationIntent(Notification notification) {
        Intent intent = new Intent(mContext, Activity_Display_Modify_Remove_Item.class);

        intent.putExtra(INTENT_KEY_SOURCE, INTENT_VALUE_SOURCE_NOTIFICATION);
        intent.putExtra(INTENT_KEY_ITEM_ID,notification.getID() );
        Log.d(TAG, "getNotificationIntent: "+notification.getID());
        intent.putExtra(INTENT_KEY_TOPIC,notification.getmTopic() );
        intent.putExtra(INTENT_KEY__PATH_STATE,notification.getmState() );
        intent.putExtra(INTENT_KEY_NOTIFICATION_ID,notification.getID() );
        intent.putExtra(INTENT_KEY_ITEM_CAT, notification.getmCategory());
        intent.putExtra(INTENT_KEY_ITEM_CITY, notification.getmCity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return intent;
    }


}

