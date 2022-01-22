package com.example.unserhoersaal.model;

public class UserCourse {
    public String key;
    public String name;


    public UserCourse() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserCourse(String key, String name) {
        this.key = key;
        this.name = name;

    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }
}
