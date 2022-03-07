package com.example.unserhoersaal.model;

import com.example.unserhoersaal.enums.LikeStatus;

/** Model for a thread. */
public class ThreadModel {

  private static final String TAG = "ThreadModel";

  public boolean answered;
  public Long creationTime;
  public String creatorId;
  public int likes;
  public String text;
  public String title;
  public String key;
  public String creatorName;
  public int answersCount;
  public String photoUrl;
  public LikeStatus likeStatus;

  public ThreadModel() {}

  public int getAnswersCount() {
    return answersCount;
  }

  public void setAnswersCount(int answersCount) {
    this.answersCount = answersCount;
  }

  public boolean getAnswered() {
    return this.answered;
  }

  public void setAnswered(boolean answered) {
    this.answered = answered;
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

  public void setText(String textThread) {
    this.text = textThread;
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

  public LikeStatus getLikeStatus() {
    return this.likeStatus;
  }

  public void setLikeStatus(LikeStatus likeStatus) {
    this.likeStatus = likeStatus;
  }
}
