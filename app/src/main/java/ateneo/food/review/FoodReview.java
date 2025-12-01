package ateneo.food.review;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FoodReview extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    // Get the UserID and attach it to the FoodReview object so that each food review has an associated User ID
    // This allows us to only load the food reviews that user has
    private String userOwnerId;
    private String foodImagePath;
    private String title;
    private String stall;
    private Float price;
    private String note;
    private Boolean favorite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserOwnerId() {
        return userOwnerId;
    }

    public void setUserOwnerId(String userOwnerId) {
        this.userOwnerId = userOwnerId;
    }

    public String getFoodImagePath() {
        return foodImagePath;
    }

    public void setFoodImagePath(String foodImagePath) {
        this.foodImagePath = foodImagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStall() {
        return stall;
    }

    public void setStall(String stall) {
        this.stall = stall;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "FoodReview{" +
                "id='" + id + '\'' +
                ", userOwnerId='" + userOwnerId + '\'' +
                ", foodImagePath=" + foodImagePath +
                ", title='" + title + '\'' +
                ", stall='" + stall + '\'' +
                ", price=" + price +
                ", comment='" + note + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
