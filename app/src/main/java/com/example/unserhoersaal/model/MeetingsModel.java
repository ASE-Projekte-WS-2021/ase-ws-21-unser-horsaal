package com.example.unserhoersaal.model;

/** Model for a meeting. */
public class MeetingsModel {

  private static final String TAG = "MeetingsModel";

  private int yearInput;
  private int monthInput;
  private int dayOfMonthInput;
  private int hourInput;
  private int minuteInput;
  private int hourEndInput;
  private int minuteEndInput;
  private Long creationTime;
  private String creatorId;
  private String description;
  private Long eventTime;
  private Long eventEndTime;
  private String title;
  private String key;
  private String meetingDate;

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

  public int getHourEndInput() {
    return this.hourEndInput;
  }

  public void setHourEndInput(int hourEndInput) {
    this.hourEndInput = hourEndInput;
  }

  public int getMinuteEndInput() {
    return this.hourEndInput;
  }

  public void setMinuteEndInput(int minuteEndInput) {
    this.minuteEndInput = minuteEndInput;
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

  public String getMeetingDate() {
    return this.meetingDate;
  }

  public void setMeetingDate(String meetingDate) {
    this.meetingDate = meetingDate;
  }

}

