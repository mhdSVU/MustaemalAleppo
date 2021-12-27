package mohammedyouser.com.mustaemalaleppo.Data;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import mohammedyouser.com.mustaemalaleppo.R;

public class ViewHolder_Child_Subscription extends ChildViewHolder {

    public TextView m_tv_child;
    public LinearLayout ll_listChild;
    public View itemView_child;
    public ImageView m_img_subscription_category;
    public ImageView m_img_subscription_city;
    public TextView m_tv_timeAgo;
    public TextView m_tv_id;
    public TextView m_tv_topic;


    public ViewHolder_Child_Subscription(View itemView_child) {
        super(itemView_child);
        this.itemView_child = itemView_child;

        ll_listChild = (LinearLayout) itemView_child.findViewById(R.id.ll_listChild);
        m_tv_child = (TextView) itemView_child.findViewById(R.id.listChildItem);

        m_img_subscription_category = itemView_child.findViewById(R.id.img_subscription_category);
        m_img_subscription_city = itemView_child.findViewById(R.id.img_subscription_city);

        m_tv_timeAgo = itemView_child.findViewById(R.id.tv_timeAgo);
        m_tv_id = itemView_child.findViewById(R.id.tv_id);
        m_tv_topic = itemView_child.findViewById(R.id.tv_topic);

    }

    public void onBind(String Sousdoc) {
        m_tv_child.setText(Sousdoc);

    }


}