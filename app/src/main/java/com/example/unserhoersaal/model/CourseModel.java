package com.example.unserhoersaal.model;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.List;

/** Class description. */
@IgnoreExtraProperties
public class CourseModel {

  private static final String TAG = "CourseModel";

  private String courseName;
  private String courseId;
  private String courseDescription;
  private String courseCreatedById;
  private String courseCreatedBy;
  private String courseCreatedAt;
  private List<String> courseMessages;

  public CourseModel() {
    // Default constructor required for calls to DataSnapshot.getValue(User.class)
  }

  /**
   * Constructor for Course.
   */
  public CourseModel(String courseName, String courseId, String courseDescription,
                     String courseCreatedById, String courseCreatedBy, String courseCreatedAt) {
    this.courseName = courseName;
    this.courseId = courseId;
    this.courseDescription = courseDescription;
    this.courseCreatedById = courseCreatedById;
    this.courseCreatedBy = courseCreatedBy;
    this.courseCreatedAt = courseCreatedAt;
    //this.courseMessages = new ArrayList<>();
  }

  /** Getter. */
  public String getCourseName() {
    return this.courseName;
  }

  public String getCourseId() {
    return this.courseId;
  }

  public String getCourseDescription() {
    return this.courseDescription;
  }

  public String getCourseCreatedById() {
    return this.courseCreatedById;
  }

  public String getCourseCreatedBy() {
    return this.courseCreatedBy;
  }

  public String getCourseCreatedAt() {
    return this.courseCreatedAt;
  }

  public List<String> getCourseMessages() {
    return this.courseMessages;
  }

  /** Setter. */
  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public void setCourseDescription(String courseDescription) {
    this.courseDescription = courseDescription;
  }

  public void setCourseCreatedById(String courseCreatedById) {
    this.courseCreatedById = courseCreatedById;
  }

  public void setCourseCreatedBy(String courseCreatedBy) {
    this.courseCreatedBy = courseCreatedBy;
  }

  public void setCourseCreatedAt(String courseCreatedAt) {
    this.courseCreatedAt = courseCreatedAt;
  }

  public void setCourseMessages(List<String> courseMessages) {
    this.courseMessages = courseMessages;
  }
}