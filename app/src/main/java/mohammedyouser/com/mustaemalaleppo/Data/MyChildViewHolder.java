package mohammedyouser.com.mustaemalaleppo.Data;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import mohammedyouser.com.mustaemalaleppo.R;

public class MyChildViewHolder extends ChildViewHolder {

    public TextView listChild;
    public LinearLayout ll_listChild;

    public MyChildViewHolder(View itemView) {
        super(itemView);
        listChild = (TextView) itemView.findViewById(R.id.listChild);
        ll_listChild = (LinearLayout) itemView.findViewById(R.id.ll_listChild);

    }

    public void onBind(String Sousdoc) {
        listChild.setText(Sousdoc);

    }


}