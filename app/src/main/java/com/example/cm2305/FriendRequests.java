package com.example.cm2305;

import android.content.Context;




public class FriendRequests {


    private String Email;
    private String Name;


    public FriendRequests() {
    }

    public FriendRequests(String Name, String Email) {

        this.Name = Name;
        this.Email = Email;

    }


    public String getEmail() {
        return Email;
    }


    public String getName() {
        return Name;
    }



}