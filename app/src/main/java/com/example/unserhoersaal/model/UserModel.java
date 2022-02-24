package com.example.unserhoersaal.model;

import java.util.HashMap;

/** Model for an user of the app. */
public class UserModel {

  private HashMap<String, Boolean> courses = new HashMap<>();
  private String displayName;
  private String email;
  private String institution;
  private String photoUrl;

  public UserModel() {
    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
  }

  /** Getter. */
  public HashMap<String, Boolean> getCourses() {
    return this.courses;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public String getEmail() {
    return this.email;
  }

  public String getInstitution() {
    return this.institution;
  }

  public String getPhotoUrl() {
    return this.photoUrl;
  }

  /** Setter. */
  public void setCourses(HashMap<String, Boolean> courses) {
    this.courses = courses;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }
  
}
