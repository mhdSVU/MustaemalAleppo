package mohammedyouser.com.mustaemalaleppo.Data;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class TopicMainCategory extends ExpandableGroup<Topic> {



    public TopicMainCategory(String title, List<Topic> topics) {
        super(title, topics);
    }

}