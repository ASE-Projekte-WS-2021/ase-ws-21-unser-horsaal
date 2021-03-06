package com.example.unserhoersaal.model;

/**
 * Model for an course.
 */
public class CourseModel {

  private String codeMapping;
  private Long creationTime;
  private String creatorId;
  private String description;
  private String institution;
  private String title;
  private String key;
  private String creatorName;
  private String photoUrl;
  private int meetingsCount;
  private int memberCount;

  public CourseModel() {
    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
  }

  /**
   * Constructor.
   */
  public CourseModel(String title, String description, String institution) {
    this.title = title;
    this.description = description;
    this.institution = institution;
  }

  /**
   * Getter.
   */
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

  public String getInstitution() {
    return this.institution;
  }

  public String getTitle() {
    return this.title;
  }

  public String getKey() {
    return this.key;
  }

  public String getCreatorName() {
    return this.creatorName;
  }

  public String getPhotoUrl() {
    return this.photoUrl;
  }

  public int getMeetingsCount() {
    return this.meetingsCount;
  }

  public int getMemberCount() {
    return this.memberCount;
  }

  /**
   * Setter.
   */
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

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

  public void setMeetingsCount(int meetingsCount) {
    this.meetingsCount = meetingsCount;
  }

  public void setMemberCount(int memberCount) {
    this.memberCount = memberCount;
  }
}