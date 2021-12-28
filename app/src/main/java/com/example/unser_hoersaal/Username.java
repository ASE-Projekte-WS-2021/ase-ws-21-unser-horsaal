package com.example.unser_hoersaal;

public class Username {

    private String username, userId;

    public Username(String username, String userId){
        this.username = username;
        this.userId = userId;
    }

    public String getUsername(){return username;}

    public String getUserId() {
        return userId;
    }
}
