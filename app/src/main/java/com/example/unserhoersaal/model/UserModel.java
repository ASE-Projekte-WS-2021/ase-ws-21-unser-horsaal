package com.example.unserhoersaal.model;

import java.util.HashMap;

public class UserModel {

  private HashMap<String, Boolean> courses;
  private String displayName;
  private String email;
  private String institution;
  private String photoURL;

  public UserModel() {
    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
  }

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

  public String getPhotoURL() {
    return this.photoURL;
  }

  public void setCourses(HashMap<String, Boolean> courses) {
    this.courses = courses;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public void setPhotoURL(String photoURL) {
    this.photoURL = photoURL;
  }
}
