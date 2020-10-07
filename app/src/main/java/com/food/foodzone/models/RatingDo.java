package com.food.foodzone.models;

public class RatingDo extends BaseDo {

    public String ratingId = "";
    public String postId = "";
    public String userId = "";
    public String comment = "";
    public float postRating;

    public RatingDo(){}

    public RatingDo(String ratingId, String postId, String userId, String comment, float postRating){
        this.ratingId = ratingId;
        this.postId = postId;
        this.userId = userId;
        this.comment = comment;
        this.postRating = postRating;
    }
}
