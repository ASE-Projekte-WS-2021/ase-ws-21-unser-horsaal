package com.example.unserhoersaal.utils;

import android.util.Patterns;
import com.example.unserhoersaal.Config;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

  public static boolean emptyEmail(String email) {
    return email == null || email.equals("");
  }

  public static boolean emptyUserName(String userName) {
    return userName == null || userName.equals("");
  }

  public static boolean emptyPassword(String password) {
    return password == null || password.equals("");
  }

  public static boolean emailHasPattern(String email) {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches();
  }

  public static boolean userNameHasPattern(String userName) {
    Pattern pattern = Pattern.compile(Config.USERNAME_PATTERN);
    Matcher matcher = pattern.matcher(userName);
    return matcher.matches();
  }

  public static boolean passwordHasPattern(String password) {
    Pattern pattern = Pattern.compile(Config.PASSWORD_PATTERN);
    Matcher matcher = pattern.matcher(password);
    return matcher.matches();
  }

}