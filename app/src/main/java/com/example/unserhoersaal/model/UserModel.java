package com.example.unserhoersaal.model;

public class UserModel {
  private String displayName;
  private String email;
  private String photoURL;
  private String institution;

  public String getDisplayName() {
    return displayName;
  }

  public String getEmail() {
    return email;
  }

  public String getPhotoURL() {
    return photoURL;
  }

  public String getInstitution() {
    return institution;
  }

  public void setDisplayName(String userName) {
    this.displayName = userName;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPhotoURL(String photoURL) {
    this.photoURL = photoURL;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

}