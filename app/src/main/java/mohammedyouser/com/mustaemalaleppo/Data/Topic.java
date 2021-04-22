package mohammedyouser.com.mustaemalaleppo.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Topic implements Parcelable {

    private String mTitle;
    private String mTopicState;

    public String getmTopicState() {
        return mTopicState;
    }

    public void setmTopicState(String mTopicState) {
        this.mTopicState = mTopicState;
    }

    public Topic(String mTitle,String mTopicState) {
        this.mTitle = mTitle;
        this.mTopicState = mTopicState;

    }

    protected Topic(Parcel in) {
        mTitle = in.readString();
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
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