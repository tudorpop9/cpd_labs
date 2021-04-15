package com.cpd.entity;

import com.cpd.enums.UserType;

public class User {
    private static int idCounter = 0;
    private UserType userType;
    private long userId;
    private String name;

    public User(String name, UserType userType) {
        this.userId = idCounter++;
        this.userType = userType;
        this.name = name;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
