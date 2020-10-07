package com.food.foodzone.models;

public class SupportDo extends BaseDo {

    public String supportId = "";
    public String userId = "";
    public String comments = "";
    public String commentDate = "";

    public SupportDo(){}

    public SupportDo(String supportId, String userId, String comments, String commentDate){
        this.supportId = supportId;
        this.userId    = userId;
        this.comments  = comments;
        this.commentDate = commentDate;
    }
}
