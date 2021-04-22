package mohammedyouser.com.mustaemalaleppo.Domain;


public class ItemNotification {
    private String itemTitle;
    private String itemCity;
    private String itemCategory;
    private String itemID;
    private String notificationID;

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemCity(String itemCity) {
        this.itemCity = itemCity;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemCity() {
        return itemCity;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemID() {
        return itemID;
    }

    public ItemNotification(String notificationID, String itemTitle, String itemCity, String itemCategory, String itemID) {
        this.itemTitle = itemTitle;
        this.itemCity = itemCity;
        this.itemCategory = itemCategory;
        this.itemID = itemID;
    }
}
