package com.example.unserhoersaal.model;

import com.example.unserhoersaal.enums.CheckedOptionEnum;

/** Model for a poll. */
public class PollModel {
  private static final String TAG = "PollModel";

  private String key;
  private String text;
  private Long creationTime;
  private String creatorId;
  private int votesCount;
  private String creatorName;
  private String photoUrl;
  private String optionsText1;
  private String optionsText2;
  private String optionsText3;
  private String optionsText4;
  private int optionsCount1;
  private int optionsCount2;
  private int optionsCount3;
  private int optionsCount4;
  private CheckedOptionEnum checkedOption;

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

  public String getOptionsText1() {
    return this.optionsText1;
  }

  public void setOptionsText1(String optionsText1) {
    this.optionsText1 = optionsText1;
  }

  public String getOptionsText2() {
    return this.optionsText2;
  }

  public void setOptionsText2(String optionsText2) {
    this.optionsText2 = optionsText2;
  }

  public String getOptionsText3() {
    return this.optionsText3;
  }

  public void setOptionsText3(String optionsText3) {
    this.optionsText3 = optionsText3;
  }

  public String getOptionsText4() {
    return this.optionsText4;
  }

  public void setOptionsText4(String optionsText4) {
    this.optionsText4 = optionsText4;
  }

  public int getOptionsCount1() {
    return this.optionsCount1;
  }

  public void setOptionsCount1(int optionsCount1) {
    this.optionsCount1 = optionsCount1;
  }

  public int getOptionsCount2() {
    return this.optionsCount2;
  }

  public void setOptionsCount2(int optionsCount2) {
    this.optionsCount2 = optionsCount2;
  }

  public int getOptionsCount3() {
    return this.optionsCount3;
  }

  public void setOptionsCount3(int optionsCount3) {
    this.optionsCount3 = optionsCount3;
  }

  public int getOptionsCount4() {
    return this.optionsCount4;
  }

  public void setOptionsCount4(int optionsCount4) {
    this.optionsCount4 = optionsCount4;
  }

  public CheckedOptionEnum getCheckedOption() {
    return this.checkedOption;
  }

  public void setCheckedOption(CheckedOptionEnum checkedOption) {
    this.checkedOption = checkedOption;
  }
}
