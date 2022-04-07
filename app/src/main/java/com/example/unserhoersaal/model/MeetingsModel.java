package com.example.unserhoersaal.model;

/** Model for a meeting. */
public class MeetingsModel {

  private static final String TAG = "MeetingsModel";

  private Long creationTime;
  private String creatorId;
  private String description;
  private Long eventTime;
  private Long eventEndTime;
  private String title;
  private String key;
  private String meetingKey;

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

  public Long getEventEndTime() {
    return this.eventEndTime;
  }

  public void setEventEndTime(Long eventEndTime) {
    this.eventEndTime = eventEndTime;
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

  public void setMeetingKey(String meetingKey) { this.meetingKey = meetingKey; }

  public String getMeetingKey() { return meetingKey; }

}

