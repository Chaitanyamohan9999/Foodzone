package com.food.foodzone.models;


public class EventDo extends BaseDo {

    public String eventId = "";
    public String userId = "";
    public String eventTitle = "";
    public String eventDate = "";
    public String eventDescription = "";

    public EventDo(){}

    public EventDo(String eventId, String userId, String eventTitle, String eventDate, String eventDescription){
        this.eventId = eventId;
        this.userId = userId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
    }
}
