package mohammedyouser.com.mustaemalaleppo.Data;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class FavoriteTopic extends ExpandableGroup<FavoriteItem> {

    public void setTitle(String title) {
        this.title = title;
    }

    String title;

    @Override
    public String getTitle() {
        return title;
    }

    public FavoriteTopic(String title, List<FavoriteItem> favoriteItems) {
        super(title, favoriteItems);
        this.title=title;
    }

}