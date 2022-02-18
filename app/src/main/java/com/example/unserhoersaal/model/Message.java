package com.example.unserhoersaal.model;

/**Class represent the message model.**/
public class Message {

  private static final String TAG = "Message";

  private String messageText;
  private Long time;

  /**Default class constructor.**/
  public Message() {
    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
  }

  /**Class constructor.**/
  public Message(String messageText, Long time) {
    this.messageText = messageText;
    this.time = time;
  }

  /** Getter. */
  public Long getTime() {
    return this.time;
  }

  public String getMessageText() {
    return this.messageText;
  }

  /** Setter. */
  public void setMessageText(String messageText) {
    this.messageText = messageText;
  }

  public void setTime(Long time) {
    this.time = time;
  }
}
