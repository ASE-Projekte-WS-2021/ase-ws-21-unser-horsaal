package com.example.unserhoersaal.model;

/** Model for a meeting. */
public class MeetingsModel {


  private String creationTimeInput;
  private String eventTimeInput;
  private Long creationTime;
  private String creatorId;
  private String description;
  private Long eventTime;
  private String title;
  private String key;

  public MeetingsModel() {
    //needed for Firebase
  }

  public String getCreationTimeInput() {
    return creationTimeInput;
  }

  public void setCreationTimeInput(String creationTimeInput) {
    this.creationTimeInput = creationTimeInput;
  }

  public String getEventTimeInput() {
    return eventTimeInput;
  }

  public void setEventTimeInput(String eventTimeInput) {
    this.eventTimeInput = eventTimeInput;
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
