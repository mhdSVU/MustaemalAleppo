package mohammedyouser.com.mustaemalaleppo.Data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mohammedyouser.com.mustaemalaleppo.Domain.Activity_Display_Modify_Remove_Item;
import mohammedyouser.com.mustaemalaleppo.Domain.TimeAgo;
import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Fragment_Dialog_Remove_Favorite;

import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REMOVE_ALERT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.BUNDLE_KEY_REMOVE_FAVORITE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_ITEM_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_NOTIFICATION_ID;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_SOURCE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY_TOPIC;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_KEY__STATE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.INTENT_VALUE_SOURCE_NOTIFICATION;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.PATH_IHAVE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_REMOVE_ALERT;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.REQUEST_KEY_REMOVE_FAVORITE;
import static mohammedyouser.com.mustaemalaleppo.UI.CommonUtility.CommonConstants.TAG;

public class Adapter_ExpandableRecycler__Favorites extends
        ExpandableRecyclerViewAdapter<ViewHolder_Parent, ViewHolder_Child> {
    private Context mContext;
    private String mItemState;
    private String notification_timeAgo = "";
    private ArrayList<FavoriteItem> favorites_list;
    private List<FavoriteTopic> groups_favoriteTopicList;
    private TextView m_ll_no_content;
    private TextView m_tv_label_favorites_ihave;
    private TextView m_tv_label_favorites_ineed;



    public Adapter_ExpandableRecycler__Favorites(String mItemState, ArrayList<FavoriteItem> favoriteItems, List<FavoriteTopic> groups, Context context, View view1, View view2, View view3) {
        super(groups);
        mContext = context;
        this.mItemState = mItemState;
        this.favorites_list = favoriteItems;
        this.groups_favoriteTopicList = groups;
        this.m_tv_label_favorites_ihave = (TextView) view1;
        this.m_tv_label_favorites_ineed = (TextView) view2;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new ViewHolder_Child(view);
    }

    @Override
    public void onBindChildViewHolder(ViewHolder_Child viewHolderChild, int flatPosition, ExpandableGroup group, int childIndex) {

        final FavoriteItem favoriteItem = ((FavoriteTopic) group).getItems().get(childIndex);
        viewHolderChild.onBind(favoriteItem.getmTitle());

        viewHolderChild.itemView_child.setOnClickListener(view -> mContext.startActivity(getFavoriteItemIntent((FavoriteItem) group.getItems().get(childIndex))));
        viewHolderChild.itemView_child.setOnLongClickListener(view -> {
            confirmDeleteFavoriteItem(
                    childIndex,
                    ((FavoriteTopic) group).getItems(),
                    ((FavoriteItem) group.getItems().get(childIndex)).getmState(),
                    favorites_list.get(childIndex).getmTopic(),
                    favorites_list.get(childIndex).getID(),
                    groups_favoriteTopicList.size());
            return false;
        });

        set_FavoriteItem_BackgroundColor(viewHolderChild, favoriteItem);
        set_FavoriteItem_Title(viewHolderChild, favoriteItem);
        set_FavoriteItem_Image(viewHolderChild, favoriteItem);
        set_FavoriteItem_TimeAgo(viewHolderChild, favoriteItem);

    }

    private void set_FavoriteItem_BackgroundColor(ViewHolder_Child viewHolderChild, FavoriteItem favoriteItem) {
        if (favoriteItem.getmValue() != null) {
            if (favoriteItem.getmValue().equals("false")) {
                viewHolderChild.ll_listChild.setBackgroundColor(mContext.getColor(R.color.grey_300));
            } else {
                viewHolderChild.ll_listChild.setBackgroundColor(mContext.getColor(R.color.green_light));
            }
        }

    }

    private void set_FavoriteItem_Title(ViewHolder_Child viewHolderChild, FavoriteItem favoriteItem) {
        String favoriteItem_title = mContext.getString(
                R.string.title_notification_place_holders,
                favoriteItem.getmUserName(),
                mContext.getString(R.string.space),
                getAddState(favoriteItem.getmState()),
                favoriteItem.getmTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolderChild.m_tv_child.setText(Html.fromHtml(favoriteItem_title, Html.FROM_HTML_MODE_LEGACY));
        } else {
            viewHolderChild.m_tv_child.setText(favoriteItem_title);

        }
    }

    private void set_FavoriteItem_Image(ViewHolder_Child viewHolderChild, FavoriteItem favoriteItem) {

        if (favoriteItem.getmImgURL() != null) {
            setImage_circle(mContext, Uri.parse(favoriteItem.getmImgURL()), 0.3f, viewHolderChild.m_imgView_userImage);

        }
    }

    private void set_FavoriteItem_TimeAgo(ViewHolder_Child viewHolderChild, FavoriteItem favoriteItem) {

        if (favoriteItem.getmDateTime() != null) {
            if (!favoriteItem.getmDateTime().equals("null")) {
                notification_timeAgo = TimeAgo.getTimeAgo(-Long.parseLong(favoriteItem.getmDateTime()),mContext);
            }
        }
        viewHolderChild.m_tv_timeAgo.setText(notification_timeAgo);
    }


    private Intent getFavoriteItemIntent(FavoriteItem favoriteItem) {
        Intent intent = new Intent(mContext, Activity_Display_Modify_Remove_Item.class);

        intent.putExtra(INTENT_KEY_SOURCE, INTENT_VALUE_SOURCE_NOTIFICATION);

        intent.putExtra(INTENT_KEY_ITEM_ID, favoriteItem.getID());
        intent.putExtra(INTENT_KEY_NOTIFICATION_ID, String.valueOf(favoriteItem.getID()));

        intent.putExtra(INTENT_KEY_TOPIC, favoriteItem.getmTopic());
        intent.putExtra(INTENT_KEY__STATE, favoriteItem.getmState());

        return intent;
    }

    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView) {
        Glide.with(context).load(imageURL)//TODO
                .thumbnail(thumbnail)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    private String getAddState(String favoriteItemState) {
        if (favoriteItemState.equals(PATH_IHAVE)) {
            return mContext.getString(R.string.notification_add_state_ihave);
        } else {
            return mContext.getString(R.string.notification_add_state_ineed);

        }

    }

    public void confirmDeleteFavoriteItem(int position, List<FavoriteItem>favoriteItems, String state, final String topic, String favoriteItemID, long topicsCount) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.


        Fragment_Dialog_Remove_Favorite remove_alert_fragment = Fragment_Dialog_Remove_Favorite.newInstance(state, topic, favoriteItemID);

        FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity) mContext).getSupportFragmentManager().findFragmentByTag("DeleteNotification");
        if (prev != null) {
            ft.remove(prev);
        }
        // save transaction to the back stack
        ft.addToBackStack("DeleteNotification");
        remove_alert_fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "DeleteNotification");
        ((AppCompatActivity) mContext).getSupportFragmentManager().executePendingTransactions();

        ((AppCompatActivity) mContext).getSupportFragmentManager().setFragmentResultListener(REQUEST_KEY_REMOVE_FAVORITE, ((AppCompatActivity) mContext), (requestKey, result) -> {
            if(result.getBoolean(BUNDLE_KEY_REMOVE_FAVORITE)){
                favoriteItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, favoriteItems.size());
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
            m_tv_label_favorites_ihave.setVisibility(View.VISIBLE);
            m_tv_label_favorites_ineed.setVisibility(View.VISIBLE);


        }


    }


}

