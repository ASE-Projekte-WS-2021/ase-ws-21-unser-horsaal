package com.example.unserhoersaal.model;

public class PollOptionModel {

  private static final String TAG = "PollOptionModel";

  private String key;
  private int votesCount;
  private String text;

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public int getVotesCount() {
    return this.votesCount;
  }

  public void setVotesCount(int votesCount) {
    this.votesCount = votesCount;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
