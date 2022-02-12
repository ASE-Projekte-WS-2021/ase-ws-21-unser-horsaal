package com.example.unserhoersaal.model;

/** Class represents the key data of a course.  */
public class UserCourse {

  private static final String TAG = "UserCourse";

  private String key;
  private String name;

  public UserCourse() {
    // Default constructor required for calls to DataSnapshot.getValue(User.class)
  }

  /** Constructor. */
  public UserCourse(String key, String name) {
    this.key = key;
    this.name = name;

  }

  /** Getter. */
  public String getKey() {
    return this.key;
  }

  public String getName() {
    return this.name;
  }

  /** Setter. */
  public void setKey(String key) {
    this.key = key;
  }

  public void setName(String name) {
    this.name = name;
  }
}
