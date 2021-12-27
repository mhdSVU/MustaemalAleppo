package mohammedyouser.com.mustaemalaleppo.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class FavoriteItem implements Parcelable {


    private String mState;
    private String mCategory;
    private String mCity;
    private String ID;
    private String mValue = "";
    private String mTitle = "";
    private String mTopic = "";
    private String mDateTime = "";
    private String mUserName;
    private String mImgURL_item = "";
    private String mImgURL_user = "";

    public FavoriteItem(String mState, String mCategory, String mCity, String ID, String mValue, String mTitle, String mTopic,
                        String mDateTime, String mUserName, String mImgURL_item, String mImgURL_user) {
        this.mState = mState;
        this.mCategory = mCategory;
        this.mCity = mCity;
        this.ID = ID;
        this.mValue = mValue;
        this.mTitle = mTitle;
        this.mTopic = mTopic;
        this.mDateTime = mDateTime;
        this.mUserName = mUserName;
        this.mImgURL_item = mImgURL_item;
        this.mImgURL_user = mImgURL_user;
    }

    public void setmValue(String mValue) {
        this.mValue = mValue;
    }

    public String getmValue() {
        return mValue;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }


    public void setmTopic(String mTopic) {
        this.mTopic = mTopic;
    }

    public void setmDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmTopic() {
        return mTopic;
    }

    public String getmDateTime() {
        return mDateTime;
    }

    public String getmUserName() {
        return mUserName;
    }

    public static Creator<FavoriteItem> getCREATOR() {
        return CREATOR;
    }


    public void setTopic(String mTopic) {
        this.mTopic = mTopic;
    }

    public String getTopic() {
        return mTopic;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmState() {
        return mState;
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmCity() {
        return mCity;
    }

    protected FavoriteItem(Parcel in) {
        mTitle = in.readString();
    }

    public static final Creator<FavoriteItem> CREATOR = new Creator<FavoriteItem>() {
        @Override
        public FavoriteItem createFromParcel(Parcel in) {
            return new FavoriteItem(in);
        }

        @Override
        public FavoriteItem[] newArray(int size) {
            return new FavoriteItem[size];
        }
    };

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String Title) {
        this.mTitle = Title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
    }


    public String getmImgURL_item() {
        return mImgURL_item;
    }

    public String getmImgURL_user() {
        return mImgURL_user;
    }


    public void setmImgURL_item(String mImgURL_item) {
        this.mImgURL_item = mImgURL_item;
    }

    public void setmImgURL_user(String mImgURL_user) {
        this.mImgURL_user = mImgURL_user;
    }
}