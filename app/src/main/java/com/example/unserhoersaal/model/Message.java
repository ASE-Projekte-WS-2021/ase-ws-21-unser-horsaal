package com.example.unserhoersaal.model;

/**Class represent the message model.**/

public class Message {
  public String messageText;
  public Long time;

  /**Default class constructor.**/

  public Message() {
    // Default constructor required for calls to DataSnapshot.getValue(User.class)
  }

  /**Class constructor.**/

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
