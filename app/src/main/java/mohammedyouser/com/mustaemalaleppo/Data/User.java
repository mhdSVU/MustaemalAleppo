
package mohammedyouser.com.mustaemalaleppo.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("userImage")
    @Expose
    private Image userImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Image getUserImage() {
        return userImage;
    }

    public void setUserImage(Image userImage) {
        this.userImage = userImage;
    }

}
