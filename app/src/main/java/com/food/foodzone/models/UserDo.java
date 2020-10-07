package com.food.foodzone.models;

public class UserDo extends BaseDo {

    public String userId = "";
    public String name = "";
    public String email = "";
    public String phone = "";
    public String country = "";
    public String city = "";
    public String state = "";
    public String gender = "";
    public String password = "";
    public String profilePicUrl = "";

    public UserDo(){}

    public UserDo(String userId, String name, String email, String phone, String country, String state, String city, String gender, String password, String profilePicUrl){
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.state = state;
        this.city = city;
        this.gender = gender ;
        this.password = password ;
        this.profilePicUrl = profilePicUrl;

    }
}
