package com.food.foodzone.models;

public class MenuItemDo extends BaseDo {

    public String itemId = "";
    public String itemCategory = "";
    public String itemName = "";
    public double itemPrice;
    public String itemDescription = "";
    public String itemImage = "";
    public boolean isAvailable;
    public int quantity;
    public boolean isCategory;

    public MenuItemDo() {}

    public MenuItemDo(String itemId, String itemCategory, String itemName, double itemPrice, String itemDescription, String itemImage, boolean isAvailable, int quantity) {
        this.itemId = itemId;
        this.itemCategory = itemCategory;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemDescription = itemDescription;
        this.itemImage = itemImage;
        this.isAvailable = isAvailable;
        this.quantity = quantity;
    }

}
