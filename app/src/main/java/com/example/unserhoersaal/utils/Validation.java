package com.example.unserhoersaal.utils;

import android.util.Log;
import android.util.Patterns;
import com.example.unserhoersaal.Config;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Class for String validation. */
public class Validation {

  public static boolean emptyString(String string) {
    return string == null || string.equals("");
  }

  public static boolean emailHasPattern(String email) {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches();
  }

  public static boolean stringHasPattern(String text, String regex) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);
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