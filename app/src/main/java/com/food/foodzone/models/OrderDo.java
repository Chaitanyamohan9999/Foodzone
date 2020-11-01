package com.food.foodzone.models;

import java.util.ArrayList;

public class OrderDo extends BaseDo {

    public String orderId = "";
    public String customerId = "";
    public String tableId = "";
    public int tableNumber;
    public String customerName = "";
    public String customerPhone = "";
    public String customerEmail = "";
    public String pickupTime = "";
    public double totalAmount;
    public String paymentType = "";
    public String orderType = "";
    public String orderStatus = "";
    public String orderSeen = "";
    public long reservedAt;
    public ArrayList<MenuItemDo> menuItemDos;


    public OrderDo() {}


}
