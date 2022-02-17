package com.example.unserhoersaal.model;

public class UserModel {
  private String userName;
  private String email;
  private String password;
  private String photoURL;
  private String institution;

  public String getUserName() {
    return userName;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getPhotoURL() {
    return photoURL;
  }

  public String getInstitution() {
    return institution;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPhotoURL(String photoURL) {
    this.photoURL = photoURL;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

}