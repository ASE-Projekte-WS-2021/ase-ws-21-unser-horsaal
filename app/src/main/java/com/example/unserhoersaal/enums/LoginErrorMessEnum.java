package com.example.unserhoersaal.enums;

public enum LoginErrorMessEnum {
  EMAIL_EMPTY("Bitte geben Sie eine Email ein"),
  EMAIL_WRONG_PATTERN("Email wurde falsch eingegeben"),
  PASSWORD_EMPTY("Bitte geben Sie ein Passwort ein");

  private String errorMessage;
  private LoginErrorMessEnum(String errorMessage){
    this.errorMessage = errorMessage;
  }
  
  public String getErrorMessage(){
    return this.errorMessage;
  }
}
