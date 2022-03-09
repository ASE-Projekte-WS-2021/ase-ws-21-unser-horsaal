package com.example.unserhoersaal.utils;

import android.util.Log;
import android.util.Patterns;
import com.example.unserhoersaal.Config;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Class for String validation. */
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

  /** Check if the username fits the pattern. */
  public static boolean userNameHasPattern(String userName) {
    Pattern pattern = Pattern.compile(Config.USERNAME_PATTERN);
    Matcher matcher = pattern.matcher(userName);
    return matcher.matches();
  }

  /** Check if the password fits the pattern. */
  public static boolean passwordHasPattern(String password) {
    Pattern pattern = Pattern.compile(Config.PASSWORD_PATTERN);
    Matcher matcher = pattern.matcher(password);
    return matcher.matches();
  }

  /** When access StateLiveData in ViewModels, check if the StateData and/or Model exists. Also
   * returns the blank data. */
  public static <T> T checkStateLiveData(StateLiveData<T> data, String debugTag)  {
    if (data.getValue() == null) {
      Log.e(debugTag, Config.STATE_LIVE_DATA_NULL);
      return null;
    } else if (data.getValue().getData() == null) {
      Log.e(debugTag, Config.STATE_LIVE_DATA_NULL);
      return null;
    } else {
      return data.getValue().getData();
    }
  }

}