package com.food.foodzone.models;

public class PostImagesDo extends BaseDo {

    public String roomImgPathId = "";
    public String userId = "";
    public String postId = "";
    public String roomImagePath = "";

    public PostImagesDo(){}

    public PostImagesDo(String roomImgPathId, String userId, String postId, String roomImagePath){
        this.roomImgPathId = roomImgPathId;
        this.userId = userId;
        this.postId = postId;
        this.roomImagePath = roomImagePath;
    }
}
