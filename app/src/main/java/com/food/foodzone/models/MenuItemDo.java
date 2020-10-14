package com.food.foodzone.models;

public class MenuItemDo extends BaseDo {

    public String itemId = "";
    public String itemName = "";
    public double itemPrice;
    public String itemDescription = "";
    public String itemImage;
    public boolean isAvailable;

    public MenuItemDo() {}

    public MenuItemDo(String itemId, String itemName, double itemPrice, String itemDescription, String itemImage, boolean isAvailable) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemDescription = itemDescription;
        this.itemImage = itemImage;
        this.isAvailable = isAvailable;
    }
}
