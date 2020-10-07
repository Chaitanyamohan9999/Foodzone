package com.food.foodzone.models;

public class AddPostDo extends BaseDo {

    public String postId = "";
    public String title = "";
    public String rent = "";
    public String address1 = "";
    public String address2 = "";
    public String zipCode = "";
    public String unitType = "";
    public String bedRooms = "";
    public String hydro = "";
    public String heating = "";
    public String parking = "";
    public String pet = "";
    public String furniture = "";
    public String wifi = "";
    public String image1Path = "";
    public String image2Path = "";
    public String image3Path = "";
    public String postBy = "";
    public String postedName = "";
    public String postedEmail = "";
    public String postPhone = "";
    public String postDate = "";
    public int postRating;

    public AddPostDo(){}

    public AddPostDo(String postId, String title, String rent, String address1, String address2, String zipCode,
                     String unitType, String bedRooms, String hydro, String heating, String parking,
                     String pet, String furniture, String wifi, String image1Path, String image2Path,
                     String image3Path, String postedName, String postBy, String postedEmail, String postPhone, String postDate, int postRating){
        this.postId             = postId;
        this.title              = title;
        this.rent               = rent;
        this.address1           = address1;
        this.address2           = address2;
        this.zipCode            = zipCode;
        this.unitType           = unitType;
        this.bedRooms           = bedRooms;
        this.hydro              = hydro;
        this.heating            = heating;
        this.parking            = parking;
        this.pet                = pet;
        this.furniture          = furniture;
        this.wifi               = wifi;
        this.image1Path         = image1Path;
        this.image2Path         = image2Path;
        this.image3Path         = image3Path;
        this.postBy             = postBy;
        this.postedName         = postedName;
        this.postedEmail        = postedEmail;
        this.postPhone          = postPhone;
        this.postDate           = postDate;
        this.postRating         = postRating;
    }
}
