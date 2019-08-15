package com.example.bloomsday.util;

import android.app.Application;

import com.example.bloomsday.models.User;

public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}