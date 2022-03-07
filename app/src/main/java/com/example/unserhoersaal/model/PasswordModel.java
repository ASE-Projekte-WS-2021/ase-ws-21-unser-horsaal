package com.example.unserhoersaal.model;

public class PasswordModel {

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

  private String currentPassword;
  private String newPassword;

  public PasswordModel() {}
}
