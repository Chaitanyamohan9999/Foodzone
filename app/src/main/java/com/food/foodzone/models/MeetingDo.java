package com.food.foodzone.models;


public class MeetingDo extends BaseDo {

    public String meetingId = "";
    public String userId = "";
    public String meetingDate = "";
    public String meetingTime = "";
    public String meetingDescription = "";
    public MeetingDo(){}

    public MeetingDo(String meetingId, String userId, String meetingDate, String meetingTime, String meetingDescription){
        this.meetingId = meetingId;
        this.userId = userId;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.meetingDescription = meetingDescription;
    }
}
