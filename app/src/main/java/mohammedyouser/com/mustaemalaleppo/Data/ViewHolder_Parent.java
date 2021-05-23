package mohammedyouser.com.mustaemalaleppo.Data;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import mohammedyouser.com.mustaemalaleppo.R;

public class ViewHolder_Parent extends GroupViewHolder {

    public TextView listGroup;

    public ViewHolder_Parent(View itemView) {
        super(itemView);
        listGroup = (TextView) itemView.findViewById(R.id.listParent);
    }

    public void setParentTitle(ExpandableGroup<Notification> group) {
        listGroup.setText(group.getTitle());
    }


}