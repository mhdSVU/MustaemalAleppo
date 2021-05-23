package mohammedyouser.com.mustaemalaleppo.Data;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class NotificationTopic extends ExpandableGroup<Notification> {

    public void setTitle(String title) {
        this.title = title;
    }

    String title;

    @Override
    public String getTitle() {
        return title;
    }

    public NotificationTopic(String title, List<Notification> notifications) {
        super(title, notifications);
        this.title=title;
    }

}