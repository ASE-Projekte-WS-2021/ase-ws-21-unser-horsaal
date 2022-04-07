package com.example.unserhoersaal.model;

/**
 * Model to save the input of the date picker.
 */
public class CalendarModel {

  private int yearInput = -1;
  private int monthInput = -1;
  private int dayOfMonthInput = -1;
  private int hourInput = -1;
  private int minuteInput = -1;
  private String hourDuration;
  private String minuteDuration;

  public CalendarModel() {
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

  public String getHourDuration() {
    return hourDuration;
  }

  public void setHourDuration(String hourDuration) {
    this.hourDuration = hourDuration;
  }

  public String getMinuteDuration() {
    return minuteDuration;
  }

  public void setMinuteDuration(String minuteDuration) {
    this.minuteDuration = minuteDuration;
  }

}
