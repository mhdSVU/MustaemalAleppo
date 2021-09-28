package mohammedyouser.com.mustaemalaleppo.Data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

import mohammedyouser.com.mustaemalaleppo.Domain.Activity_Display_Modify_Remove_Item;
import mohammedyouser.com.mustaemalaleppo.Domain.TimeAgo;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Fragment_Dialog_Remove_Notification;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REMOVE_ALERT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REMOVE_NOTIFICATION;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_NOTIFICATION_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_SOURCE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_TOPIC;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__STATE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_VALUE_SOURCE_NOTIFICATION;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_IHAVE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_REMOVE_ALERT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_REMOVE_NOTIFICATION;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;

public class Adapter_ExpandableRecycler__Notifications extends
        ExpandableRecyclerViewAdapter<ViewHolder_Parent, ViewHolder_Child> {
    private Context mContext;
    private String mItemState;
    private Notification notification;
    private String notification_timeAgo = "";
    private ArrayList<Notification> notifications_list ;
    private List<NotificationTopic> groups_notificationTopicList ;
    private TextView m_ll_no_content;
    private TextView m_tv_label_notifications_ihave;
    private TextView m_tv_label_notifications_ineed;


    public Adapter_ExpandableRecycler__Notifications(String mItemState, ArrayList<Notification> notifications, List<NotificationTopic> groups, Context context, View view1, View view2, View view3) {
        super(groups);
        mContext = context;
        this.mItemState = mItemState;
        this.notifications_list = notifications;
        this.groups_notificationTopicList = groups;
        this.m_tv_label_notifications_ihave = (TextView) view1;
        this.m_tv_label_notifications_ineed = (TextView) view2;
        this.m_ll_no_content = (TextView) view3;


    }

    @Override
    public ViewHolder_Parent onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent, parent, false);
        return new ViewHolder_Parent(view);
    }

    @Override
    public void onBindGroupViewHolder(ViewHolder_Parent viewHolderGroup, int flatPosition, final ExpandableGroup group) {
        viewHolderGroup.setParentTitle(group);
    }

    @Override
    public ViewHolder_Child onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_notification, parent, false);
        return new ViewHolder_Child(view);
    }

    @Override
    public void onBindChildViewHolder(ViewHolder_Child viewHolderChild, int flatPosition, ExpandableGroup group, int childIndex) {

        final Notification notification = ((NotificationTopic) group).getItems().get(childIndex);
        viewHolderChild.onBind(notification.getmTitle());

        viewHolderChild.itemView_child.setOnClickListener(view -> mContext.startActivity(getNotificationIntent((Notification) group.getItems().get(childIndex))));
        viewHolderChild.itemView_child.setOnLongClickListener(view -> {
            confirmDeleteNotification(childIndex,((NotificationTopic) group).getItems(),((Notification) group.getItems().get(childIndex)).getmState(), notifications_list.get(childIndex).getmTopic(), notifications_list.get(childIndex).getID(), groups_notificationTopicList.size());
            return false;
        });

        set_Notification_BackgroundColor(viewHolderChild, notification);
        set_Notification_Title(viewHolderChild, notification);
        set_Notification_Image(viewHolderChild, notification);
        set_Notification_TimeAgo(viewHolderChild, notification);

    }

    private void set_Notification_BackgroundColor(ViewHolder_Child viewHolderChild, Notification notification) {
        if (notification.getmValue() != null) {
            if (notification.getmValue().equals("false")) {
                viewHolderChild.ll_listChild.setBackgroundColor(mContext.getColor(R.color.grey_300));
            } else {
                viewHolderChild.ll_listChild.setBackgroundColor(mContext.getColor(R.color.green_light));
            }
        }

    }

    private void set_Notification_Title(ViewHolder_Child viewHolderChild, Notification notification) {
        String notification_title = mContext.getString(
                R.string.title_notification_place_holders,
                notification.getmUserName(),
                mContext.getString(R.string.space),
                getAddState(notification.getmState()),
                notification.getmTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolderChild.m_tv_child.setText(Html.fromHtml(notification_title, Html.FROM_HTML_MODE_LEGACY));
        } else {
            viewHolderChild.m_tv_child.setText(notification_title);

        }
    }

    private void set_Notification_Image(ViewHolder_Child viewHolderChild, Notification notification) {

        if (notification.getmImgURL() != null) {
           // setImage_circle(mContext, Uri.parse(notification.getmImgURL()), 0.3f, viewHolderChild.m_imgView_userImage);
           setImage_circle(mContext, Uri.parse(notification.getmImgURL()), 0.3f, viewHolderChild.m_imgView_itemImage);
            setImage_circle(mContext, Uri.parse(notification.getmImgURL()), 0.3f, viewHolderChild.m_imgView_userImage_2);

        }
    }

    private void set_Notification_TimeAgo(ViewHolder_Child viewHolderChild, Notification notification) {

        if (notification.getmDateTime() != null) {
            if (!notification.getmDateTime().equals("null")) {
                notification_timeAgo = TimeAgo.getTimeAgo(-Long.parseLong(notification.getmDateTime()),mContext);
            }
        }
        viewHolderChild.m_tv_timeAgo.setText(notification_timeAgo);
    }


    private Intent getNotificationIntent(Notification notification) {
        Intent intent = new Intent(mContext, Activity_Display_Modify_Remove_Item.class);

        intent.putExtra(INTENT_KEY_SOURCE, INTENT_VALUE_SOURCE_NOTIFICATION);

        intent.putExtra(INTENT_KEY_ITEM_ID, notification.getID());
        intent.putExtra(INTENT_KEY_NOTIFICATION_ID, String.valueOf(notification.getID()));

        intent.putExtra(INTENT_KEY_TOPIC, notification.getmTopic());
        intent.putExtra(INTENT_KEY__STATE, notification.getmState());

        return intent;
    }

    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView) {
        Glide.with(context).load(imageURL)//TODO
                .thumbnail(thumbnail)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    private String getAddState(String notificationState) {
        if (notificationState.equals(PATH_IHAVE)) {
            return mContext.getString(R.string.notification_add_state_ihave);
        } else {
            return mContext.getString(R.string.notification_add_state_ineed);

        }

    }

    public void confirmDeleteNotification(int position,List<Notification>notifications,String state, final String topic, String notificationID, long topicsCount) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.


        Fragment_Dialog_Remove_Notification remove_alert_fragment = Fragment_Dialog_Remove_Notification.newInstance(state, topic, notificationID);

        FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity) mContext).getSupportFragmentManager().findFragmentByTag("DeleteNotification");
        if (prev != null) {
            ft.remove(prev);
        }
        // save transaction to the back stack
        ft.addToBackStack("DeleteNotification");
        remove_alert_fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "DeleteNotification");
        ((AppCompatActivity) mContext).getSupportFragmentManager().executePendingTransactions();

        ((AppCompatActivity) mContext).getSupportFragmentManager().setFragmentResultListener(REQUEST_KEY_REMOVE_NOTIFICATION, ((AppCompatActivity) mContext), (requestKey, result) -> {
            if(result.getBoolean(BUNDLE_KEY_REMOVE_NOTIFICATION)){
                notifications.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notifications.size());
                updateUI_If_No_Content(topicsCount);

            }
        });



    }

    private void updateUI_If_No_Content(long topicsCount) {
        if (topicsCount == 0) {
            Log.d(TAG, "updateUI_If_No_Content: topicsCount == 0");
            m_ll_no_content.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "updateUI_If_No_Content: topicsCount != 0  " + topicsCount);

            m_ll_no_content.setVisibility(View.GONE);
            m_tv_label_notifications_ihave.setVisibility(View.VISIBLE);
            m_tv_label_notifications_ineed.setVisibility(View.VISIBLE);


        }


    }


}

