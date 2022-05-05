package com.example.cm2305;
import android.content.Context;
public class User {


    private String CurrentCords;
    private String Name;
    private String DangerLevel;
    private String journeyStatus;
    private String ETA;
    private String What3Words;

    public User() {
    }

    public User(String CurrentCords, String Name, String dangerLevel, String journeyStatus, String What3Words) {
        this.CurrentCords = CurrentCords;
        this.Name = Name;
        this.DangerLevel = dangerLevel;
        this.journeyStatus = journeyStatus;
        this.What3Words = What3Words;
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
    public String getWhat3Words() {return What3Words; }

}