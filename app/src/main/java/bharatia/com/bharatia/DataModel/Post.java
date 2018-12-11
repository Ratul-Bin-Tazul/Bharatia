package bharatia.com.bharatia.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("PostID")
    @Expose
    private String postID;
    @SerializedName("UserID")
    @Expose
    private String userID;
    @SerializedName("Room_No")
    @Expose
    private String roomNo;
    @SerializedName("Size")
    @Expose
    private String size;
    @SerializedName("Price")
    @Expose
    private String price;
    @SerializedName("Area")
    @Expose
    private String area;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("Lat")
    @Expose
    private String lat;
    @SerializedName("Lon")
    @Expose
    private String lon;
    @SerializedName("Availability")
    @Expose
    private String availability;
    @SerializedName("Phone_No")
    @Expose
    private String phoneNo;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Type1")
    @Expose
    private String type1;
    @SerializedName("Type2")
    @Expose
    private String type2;
    @SerializedName("CoverPhoto")
    @Expose
    private String coverPhoto;
    @SerializedName("Date")
    @Expose
    private String date;

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Post(){}

    public Post(String postID, String userID, String roomNo, String size, String price, String area, String address, String description, String lat, String lon, String availability, String phoneNo, String email, String type1, String type2, String coverPhoto, String date) {
        this.postID = postID;
        this.userID = userID;
        this.roomNo = roomNo;
        this.size = size;
        this.price = price;
        this.area = area;
        this.address = address;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.availability = availability;
        this.phoneNo = phoneNo;
        this.email = email;
        this.type1 = type1;
        this.type2 = type2;
        this.coverPhoto = coverPhoto;
        this.date = date;
    }
}