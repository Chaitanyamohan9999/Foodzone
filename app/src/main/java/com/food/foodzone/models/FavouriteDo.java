package com.food.foodzone.models;

public class FavouriteDo extends BaseDo {

    public String postId = "";
    public String favouriteId = "";
    public String userId = "";

    public FavouriteDo(){}

    public FavouriteDo(String favouriteId, String postId, String userId){
        this.favouriteId = favouriteId;
        this.postId = postId;
        this.userId = userId;
    }
}
