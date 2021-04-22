package mohammedyouser.com.mustaemalaleppo.Data;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import mohammedyouser.com.mustaemalaleppo.R;

public class MyParentViewHolder extends GroupViewHolder {

    public TextView listGroup;

    public MyParentViewHolder(View itemView) {
        super(itemView);
        listGroup = (TextView) itemView.findViewById(R.id.listParent);
    }

    public void setParentTitle(ExpandableGroup<Notification> group) {
        listGroup.setText(group.getTitle());
    }


}