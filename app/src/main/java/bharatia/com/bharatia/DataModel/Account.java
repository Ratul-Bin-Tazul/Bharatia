package bharatia.com.bharatia.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account{

    @SerializedName("UserID")
    @Expose
    private String userID;
    @SerializedName("First")
    @Expose
    private String first;
    @SerializedName("Last")
    @Expose
    private String last;
    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("Photo")
    @Expose
    private String photo;
    @SerializedName("Phone")
    @Expose
    private String phone;
    @SerializedName("ViewCnt")
    @Expose
    private String viewCnt;
    @SerializedName("SaveCnt")
    @Expose
    private String saveCnt;
    @SerializedName("totalPost")
    @Expose
    private String totalPost;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(String viewCnt) {
        this.viewCnt = viewCnt;
    }

    public String getSaveCnt() {
        return saveCnt;
    }

    public void setSaveCnt(String saveCnt) {
        this.saveCnt = saveCnt;
    }

    public String getTotalPost() {
        return totalPost;
    }

    public void setTotalPost(String totalPost) {
        this.totalPost = totalPost;
    }
}