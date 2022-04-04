package com.example.unserhoersaal.model;

import com.example.unserhoersaal.enums.LikeStatus;
import com.example.unserhoersaal.enums.TagEnum;
import java.util.ArrayList;
import java.util.List;

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
  public String pageNumber;
  public String hashtag;
  public List<TagEnum> tags = new ArrayList<>();
  private Boolean isTextDeleted = false;

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

  public String getPageNumber() {
    return this.pageNumber;
  }

  public void setPageNumber(String pageNumber) {
    this.pageNumber = pageNumber;
  }

  public String getHashtag() {
    return this.hashtag;
  }

  public void setHashtag(String hashtag) {
    this.hashtag = hashtag;
  }

  public List<TagEnum> getTags() {
    return this.tags;
  }

  public void setTags(List<TagEnum> tags) {
    this.tags = tags;
  }

  public void setIsTextDeleted(Boolean isTextDeleted) {
    this.isTextDeleted = isTextDeleted;
  }

  public Boolean getIsTextDeleted() {
    return this.isTextDeleted;
  }
}
