package com.example.unserhoersaal.model;

/** model class for passwords for databinding and livedata. */
public class PasswordModel {

  private String currentPassword;
  private String newPassword;

  public PasswordModel() {}

  public String getCurrentPassword() {
    return currentPassword;
  }

  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
