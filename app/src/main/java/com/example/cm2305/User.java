package com.example.cm2305;
import android.content.Context;
public class User {


    private String CurrentCords;
    private String Name;
    private String DangerLevel;
    private String journeyStatus;
    private String ETA;

    public User() {
    }

    public User(String CurrentCords, String Name, String dangerLevel, String journeyStatus) {
        this.CurrentCords = CurrentCords;
        this.Name = Name;
        this.DangerLevel = dangerLevel;
        this.journeyStatus = journeyStatus;
    }



    public String getCurrentCords() {
        return CurrentCords;
    }


    public String getName() {
        return Name;
    }
    public String getDangerLevel() {
        return DangerLevel;
    }
    public String getJourneyStatus() {
        return journeyStatus;
    }


}