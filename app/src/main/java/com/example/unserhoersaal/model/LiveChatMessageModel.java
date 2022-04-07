package com.example.unserhoersaal.model;

import com.example.unserhoersaal.enums.LikeStatus;

public class LiveChatMessageModel {
  private static final String TAG = "MessageModel";

  private Long creationTime;
  private String creatorId;
  private String text;
  private String key;
  private String creatorName;
  private String photoUrl;


  /**Default class constructor.**/
  public LiveChatMessageModel() {
    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
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

  public String getText() {
    return this.text;
  }

  public void setText(String textMessage) {
    this.text = textMessage;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getCreatorName() {
    return this.creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }
}
