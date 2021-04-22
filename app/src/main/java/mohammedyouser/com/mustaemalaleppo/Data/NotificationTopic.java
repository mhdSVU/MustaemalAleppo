package mohammedyouser.com.mustaemalaleppo.Data;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class NotificationTopic extends ExpandableGroup<Notification> {



    public NotificationTopic(String title, List<Notification> notifications) {
        super(title, notifications);
    }

}