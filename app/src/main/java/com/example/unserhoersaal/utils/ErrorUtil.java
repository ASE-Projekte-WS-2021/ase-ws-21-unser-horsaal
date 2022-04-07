package com.example.unserhoersaal.utils;

import android.widget.TextView;
import androidx.databinding.BindingAdapter;

/**
 * Class to display Error Messages
 */
public class ErrorUtil {

  @BindingAdapter("app:setError")
  public static void setErrorText(TextView view, String errorMessage) {
    view.setError(errorMessage);
  }
}
