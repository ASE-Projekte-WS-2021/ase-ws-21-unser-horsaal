package com.example.unserhoersaal.model;

/**
 * Model for an user of the app.
 */
public class UserModel {

  private String displayName;
  private String email;
  private String institution;
  private String photoUrl;
  private String key;

  public UserModel() {
    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
  }

  /**
   * Getter.
   */
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

  public String getKey() {
    return this.key;
  }

  /**
   * Setter.
   */
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

  public void setKey(String key) {
    this.key = key;
  }
  
}
