package com.example.unser_hoersaal.model;

import java.util.List;

public class Message {
    public String messageText;
    public Long time;


    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Message(String messageText, Long time) {
        this.messageText = messageText;
        this.time = time;

    }

    public Long getTime() {
        return time;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
