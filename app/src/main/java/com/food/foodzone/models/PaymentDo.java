package com.food.foodzone.models;

public class PaymentDo extends BaseDo {

    public String paymentId = "";
    public String paymentType = "";
    public String personId = "";
    public String personName = "";
    public String cardNumber = "";
    public String expDate = "";
    public String cardCvv = "";
    public double amount;

    public PaymentDo() {}

    public PaymentDo(String paymentId, String paymentType, String personId, String personName, String cardNumber, String expDate, String cardCvv, double amount) {
        this.paymentId = paymentId;
        this.paymentType = paymentType;
        this.personId = personId;
        this.personName = personName;
        this.cardNumber = cardNumber;
        this.expDate = expDate;
        this.cardCvv = cardCvv;
        this.amount = amount;
    }


}
