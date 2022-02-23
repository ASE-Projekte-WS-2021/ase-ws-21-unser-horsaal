package com.example.unserhoersaal.model;

import java.util.HashMap;

/** Model for a thread. */
public class ThreadModel {

  private static final String TAG = "ThreadModel";

  public boolean answered;
  public long creationTime;
  public String creatorId;
  public int likes;
  public HashMap<String, Boolean> messages = new HashMap<>();
  public String text;
  public String title;
  public String key;

  public ThreadModel() {

  }

  public boolean isAnswered() {
    return this.answered;
  }

  public void setAnswered(boolean answered) {
    this.answered = answered;
  }

  public long getCreationTime() {
    return this.creationTime;
  }

  public void setCreationTime(long creationTime) {
    this.creationTime = creationTime;
  }

  public String getCreatorId() {
    return this.creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public int getLikes() {
    return this.likes;
  }

  public void setLikes(int likes) {
    this.likes = likes;
  }

  public HashMap<String, Boolean> getMessages() {
    return this.messages;
  }

  public void setMessages(HashMap<String, Boolean> messages) {
    this.messages = messages;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String textThread) {
    this.text = textThread;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
