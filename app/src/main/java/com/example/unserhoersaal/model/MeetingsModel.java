package com.example.unserhoersaal.model;

/** Model for a meeting. */
public class MeetingsModel {

  private int yearInput;
  private int monthInput;
  private int dayOfMonthInput;
  private int hourInput;
  private int minuteInput;
  private Long creationTime;
  private String creatorId;
  private String description;
  private Long eventTime;
  private String title;
  private String key;

  public MeetingsModel() {
    //needed for Firebase
  }

  public int getYearInput() {
    return yearInput;
  }

  public void setYearInput(int yearInput) {
    this.yearInput = yearInput;
  }

  public int getMonthInput() {
    return monthInput;
  }

  public void setMonthInput(int monthInput) {
    this.monthInput = monthInput;
  }

  public int getDayOfMonthInput() {
    return dayOfMonthInput;
  }

  public void setDayOfMonthInput(int dayOfMonthInput) {
    this.dayOfMonthInput = dayOfMonthInput;
  }

  public int getHourInput() {
    return hourInput;
  }

  public void setHourInput(int hourInput) {
    this.hourInput = hourInput;
  }

  public int getMinuteInput() {
    return minuteInput;
  }

  public void setMinuteInput(int minuteInput) {
    this.minuteInput = minuteInput;
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

