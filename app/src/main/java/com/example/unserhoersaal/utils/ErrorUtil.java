package com.example.unserhoersaal.utils;

import android.widget.TextView;
import androidx.databinding.BindingAdapter;

public class ErrorUtil {

  @BindingAdapter("app:setError")
  public static void setErrorText(TextView view, String errorMessage) {
    view.setError(errorMessage);
  }
}
