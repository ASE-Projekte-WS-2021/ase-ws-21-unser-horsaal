package com.example.unserhoersaal.model;

public class PollModel {
  private static final String TAG = "PollModel";

  private String key;
  private String text;
  private Long creationTime;
  private String creatorId;
  private int votesCount;
  private String creatorName;
  private String photoUrl;

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
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

  public int getVotesCount() {
    return this.votesCount;
  }

  public void setVotesCount(int votesCount) {
    this.votesCount = votesCount;
  }

  public String getCreatorName() {
    return this.creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public String getPhotoUrl() {
    return this.photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }
}
