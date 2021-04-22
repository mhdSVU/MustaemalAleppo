package mohammedyouser.com.mustaemalaleppo.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {


    private  String mState;
    private  String mCategory;
    private  String mCity;

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

    private  String ID;
    private  String mValue="";
    private String mTitle="";
    private String mTopic="";
    private String mDateTime="";

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

    public static Creator<Notification> getCREATOR() {
        return CREATOR;
    }

    private String mUserName;

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

    public Notification(String mState, String mCategory, String mCity, String ID, String mValue, String mTitle, String mTopic, String mDateTime, String mUserName) {
        this.mState = mState;
        this.mCategory = mCategory;
        this.mCity = mCity;
        this.ID = ID;
        this.mValue = mValue;
        this.mTitle = mTitle;
        this.mTopic = mTopic;
        this.mDateTime = mDateTime;
        this.mUserName = mUserName;
    }

    protected Notification(Parcel in) {
        mTitle = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
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
}