package mohammedyouser.com.mustaemalaleppo.Data;

/**
 * Created by mohammed_youser on 8/12/2017.
 */

public class Item {


    private String itemTitle;
    private String itemPrice;
    private String itemCategory;
    private String itemCountry;//TODO
    private String itemCity;
    private String itemDateandTime;
    private String itemImage;
    private String itemDetails;


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private double latitude;
    private double longitude;

    private String userID;//TODO

    //ToDo:
    private int price_evaluations_high;
    private int price_evaluations_appropriate;
    private int price_evaluations_low;


    public Item() {

    }

    public Item(String itemTitle, String itemPrice, String itemCategory, String itemCountry, String itemCity, String itemDateandTime, String itemImage, String itemDetails,
                String userImage, String userName, int price_evaluations_high,
                int price_evaluations_appropriate, int price_evaluations_low, String userID) {
        this.itemTitle = itemTitle;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;
        this.itemCountry = itemCountry;
        this.itemCity = itemCity;
        this.itemDateandTime = itemDateandTime;
        this.itemImage = itemImage;
        this.itemDetails = itemDetails;

        this.price_evaluations_high = price_evaluations_high;
        this.price_evaluations_appropriate = price_evaluations_appropriate;
        this.price_evaluations_low = price_evaluations_low;
        this.userID = userID;

    }


    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getItemCity() {
        return itemCity;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemDateandTime() {
        return itemDateandTime;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getItemDetails() {
        return itemDetails;
    }
}

