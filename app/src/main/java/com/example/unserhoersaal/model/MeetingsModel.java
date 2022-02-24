package com.example.unserhoersaal.model;

import java.util.HashMap;

/** Model for a meeting. */
public class MeetingsModel {

  private Long creationTime;
  private String creatorId;
  private String description;
  private Long eventTime;
  private HashMap<String, Boolean> threads = new HashMap<>();
  private String title;
  private String key;

  public MeetingsModel() {
    //needed for Firebase
  }

  public Long getCreationTime() {
    return this.creationTime;
  }

  public void setCreationTime(Long creationTime) {
    this.creationTime = creationTime;
  }

  public String getCreatorId() {
    return this.creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getEventTime() {
    return this.eventTime;
  }

  public void setEventTime(Long eventTime) {
    this.eventTime = eventTime;
  }

  public HashMap<String, Boolean> getThreads() {
    return this.threads;
  }

  public void setThreads(HashMap<String, Boolean> threads) {
    this.threads = threads;
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
