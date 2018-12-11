package bharatia.com.bharatia.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostPictureData {

    @SerializedName("PId")
    @Expose
    private String pId;
    @SerializedName("PostID")
    @Expose
    private String postID;
    @SerializedName("Link")
    @Expose
    private String link;

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}