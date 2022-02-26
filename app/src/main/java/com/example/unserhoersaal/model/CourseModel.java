package com.example.unserhoersaal.model;

import java.util.HashMap;

/** Class description. */
public class CourseModel {

  private static final String TAG = "CourseModel";

  private String codeMapping;
  private Long creationTime;
  private String creatorId;
  private String description;
  private String groupIcon;
  private String institution;
  private String title;

  private String key;

  public CourseModel() {
    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
  }

  /** Constructor. */
  public CourseModel(String title, String description, String institution) {
    this.title = title;
    this.description = description;
    this.institution = institution;
  }

  /** Getter. */
  public String getCodeMapping() {
    return this.codeMapping;
  }

  public Long getCreationTime() {
    return this.creationTime;
  }

  public String getCreatorId() {
    return this.creatorId;
  }

  public String getDescription() {
    return this.description;
  }

  public String getGroupIcon() {
    return this.groupIcon;
  }

  public String getInstitution() {
    return this.institution;
  }

  public String getTitle() {
    return this.title;
  }

  public String getKey() {
    return this.key;
  }

  /** Setter. */
  public void setCodeMapping(String codeMapping) {
    this.codeMapping = codeMapping;
  }

  public void setCreationTime(Long creationTime) {
    this.creationTime = creationTime;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setGroupIcon(String groupIcon) {
    this.groupIcon = groupIcon;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setKey(String key) {
    this.key = key;
  }

}