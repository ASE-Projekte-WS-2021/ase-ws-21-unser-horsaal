package com.example.unserhoersaal.model;

import com.example.unserhoersaal.enums.LikeStatus;

/**
 * Class represent the message model.
 */
public class MessageModel {

  private Long creationTime;
  private String creatorId;
  private int likes;
  private String text;
  private boolean topAnswer;
  private String key;
  private String creatorName;
  private LikeStatus likeStatus;
  private String photoUrl;
  private Boolean isTextDeleted = false;

  /**
   * Default class constructor.
   */
  public MessageModel() {
    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
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

  public int getLikes() {
    return this.likes;
  }

  public void setLikes(int likes) {
    this.likes = likes;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String textMessage) {
    this.text = textMessage;
  }

  public boolean getTopAnswer() {
    return this.topAnswer;
  }

  public void setTopAnswer(boolean topAnswer) {
    this.topAnswer = topAnswer;
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

  public LikeStatus getLikeStatus() {
    return this.likeStatus;
  }

  public void setLikeStatus(LikeStatus likeStatus) {
    this.likeStatus = likeStatus;
  }

  public void setIsTextDeleted(Boolean isTextDeleted) {
    this.isTextDeleted = isTextDeleted;
  }

  public Boolean getIsTextDeleted() {
    return this.isTextDeleted;
  }

}
