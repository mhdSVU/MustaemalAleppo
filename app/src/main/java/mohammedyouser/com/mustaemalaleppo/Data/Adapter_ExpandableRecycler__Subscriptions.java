package mohammedyouser.com.mustaemalaleppo.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import mohammedyouser.com.mustaemalaleppo.R;
import mohammedyouser.com.mustaemalaleppo.UI.Fragment_Dialog_Remove_Alert;


public class Adapter_ExpandableRecycler__Subscriptions extends
        ExpandableRecyclerViewAdapter<ViewHolder_Parent, ViewHolder_Child> {

    public View mGroupView;
    public View mChildView;
    private Context mContext;

    public Adapter_ExpandableRecycler__Subscriptions(List<TopicMainCategory> groups, Context context){
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
    public ViewHolder_Child onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        mChildView = view;
        return new ViewHolder_Child(view);
    }

    @Override
    public void onBindChildViewHolder(ViewHolder_Child holder, int flatPosition, ExpandableGroup group, int childIndex) {

        final Topic topic = ((TopicMainCategory) group).getItems().get(childIndex);
        holder.onBind(topic.getmTitle());
        final String titleChild = group.getTitle();
        holder.ll_listChild.setOnLongClickListener(view -> {
            confirmRemoveAlert(mContext.getString(R.string.tag_fragment_remove),
                    topic.getmTopicState(),
                    topic.getmTitle().split(mContext.getString(R.string.in))[1],
                    topic.getmTitle().split(mContext.getString(R.string.in))[0].trim()

                    );
          // notifyItemRemoved(flatPosition);

            return false;
        });

    }

    @Override
    public void onBindGroupViewHolder(ViewHolder_Parent holder, int flatPosition, final ExpandableGroup group) {
        holder.setParentTitle(group);

        if (group.getItems() == null) {
            holder.listGroup.setOnClickListener(view -> {

                Toast toast = Toast.makeText(mContext, group.toString(), Toast.LENGTH_SHORT);
                toast.show();
            });

        }
    }

    public void confirmRemoveAlert(String tag, String itemState, String itemCity,String itemCategory) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.


        Fragment_Dialog_Remove_Alert remove_alert_fragment = Fragment_Dialog_Remove_Alert.newInstance(itemState, itemCity,itemCategory);

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

