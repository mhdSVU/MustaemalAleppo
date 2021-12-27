package mohammedyouser.com.mustaemalaleppo.Data;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.CommonUtility;
import mohammedyouser.com.mustaemalaleppo.UI.Fragment_Dialog_Remove_Alert;

import static mohammedyouser.com.mustaemalaleppo.Data.ViewHolder_Item_Display_Edit.TAG;


public class Adapter_ExpandableRecycler__Subscriptions extends
        ExpandableRecyclerViewAdapter<ViewHolder_Parent, ViewHolder_Child_Subscription> {

    public View mGroupView;
    public View mChildView;
    private Context mContext;

    public Adapter_ExpandableRecycler__Subscriptions(List<TopicMainCategory> groups, Context context) {
        super(groups);
        mContext = context;
    }

    @Override
    public ViewHolder_Parent onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent, parent, false);
        mGroupView = view;
        return new ViewHolder_Parent(view);
    }

    @Override
    public ViewHolder_Child_Subscription onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row_item_child_subscription, parent, false);
        mChildView = view;
        return new ViewHolder_Child_Subscription(view);
    }

    @Override
    public void onBindChildViewHolder(ViewHolder_Child_Subscription holder, int flatPosition, ExpandableGroup group, int childIndex) {

        final Topic topic = ((TopicMainCategory) group).getItems().get(childIndex);
        String topicCategory = topic.getmTitle().split(mContext.getString(R.string.in))[0].trim();
        String topicCity = topic.getmTitle().split(mContext.getString(R.string.in))[1].trim();
        Log.d(TAG, "onBindChildViewHolder: " + topicCategory + " " + topicCity);
        holder.onBind(topic.getmTitle());
        final String titleChild = group.getTitle();
        holder.ll_listChild.setOnLongClickListener(view -> {
            confirmRemoveAlert(mContext.getString(R.string.tag_fragment_remove),
                    topic.getmTopicState(),
                    topicCity,
                    topicCategory

            );
            // notifyItemRemoved(flatPosition);


            return false;
        });

        set_Subscription_Image(holder, topicCategory, topicCity);


    }

    @Override
    public void onBindGroupViewHolder(ViewHolder_Parent holder, int flatPosition, final ExpandableGroup group) {
       boolean firstClick=true;
        holder.setParentTitle(group);
       /* holder.itemView.setOnClickListener(v ->
                ((ImageView)holder.itemView.findViewById(R.id.img_parent_arrow))
                        .setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_baseline_keyboard_arrow_up_24)));
*/

        if (group.getItems() == null) {
            holder.listGroup.setOnClickListener(view -> {

                Toast toast = Toast.makeText(mContext, group.toString(), Toast.LENGTH_SHORT);
                toast.show();
            });

        }
    }


    private void set_Subscription_Image(ViewHolder_Child_Subscription viewHolderChild, String topicCategory, String topicCity) {
        Uri uri_city = Uri.parse("android.resource://" + CommonUtility.CommonConstants.PACKAGE_NAME + "/drawable/" + topicCity.toLowerCase());

        if (topicCity.equals("All-Cities")) {
            uri_city = Uri.parse("android.resource://" + CommonUtility.CommonConstants.PACKAGE_NAME + "/drawable/" + "allcities");

        }

        Uri uri_category = Uri.parse("android.resource://" + CommonUtility.CommonConstants.PACKAGE_NAME + "/drawable/" + topicCategory.toLowerCase());

        if (topicCategory.equals("All-Categories")) {
            uri_category = Uri.parse("android.resource://" + CommonUtility.CommonConstants.PACKAGE_NAME + "/drawable/" + "miscellaneous");

        }
        if (topicCategory.equals("Real-Estates")) {
            uri_category = Uri.parse("android.resource://" + CommonUtility.CommonConstants.PACKAGE_NAME + "/drawable/" + "realestates");

        }
        if (topicCategory.equals("Electronic-Devices")) {
            uri_category = Uri.parse("android.resource://" + CommonUtility.CommonConstants.PACKAGE_NAME + "/drawable/" + "electronicdevices");

        }
        if (topicCategory.equals("Laptops-PCs")) {
            uri_category = Uri.parse("android.resource://" + CommonUtility.CommonConstants.PACKAGE_NAME + "/drawable/" + "laptopspcs");

        }
        if (topicCategory.equals("Services-Jobs")) {
            uri_category = Uri.parse("android.resource://" + CommonUtility.CommonConstants.PACKAGE_NAME + "/drawable/" + "servicesjobs");

        }
        if (topicCategory.equals("Mobile-Phones")) {
            uri_category = Uri.parse("android.resource://" + CommonUtility.CommonConstants.PACKAGE_NAME + "/drawable/" + "mobilephones");

        }

        if (uri_category != null) {
            setImage_circle(mContext, uri_category, 0.3f, viewHolderChild.m_img_subscription_city);
        }
        if (uri_city != null) {
            setImage_circle(mContext, uri_city, 0.3f, viewHolderChild.m_img_subscription_category);
        }

    }


    private void setImage_circle(Context context, Uri imageURL, float thumbnail, ImageView imageView) {
        Glide.with(context).load(imageURL)//TODO
                .thumbnail(thumbnail)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }


    public void confirmRemoveAlert(String tag, String itemState, String itemCity, String itemCategory) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.


        Fragment_Dialog_Remove_Alert remove_alert_fragment = Fragment_Dialog_Remove_Alert.newInstance(itemState, itemCity, itemCategory);

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


}

