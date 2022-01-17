package com.example.unserhoersaal.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/** Utility class for keyboard-events*/
public class KeyboardUtil {

  /** method for hiding soft-keyboard
   * source: https://stackoverflow.com/questions/26911469/hide-keyboard-when-navigating-from-
   * a-fragment-to-another */
  public static void hideKeyboard(Activity activity) {
    if (activity != null){
      InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
              Context.INPUT_METHOD_SERVICE);

      View currentFocusedView = activity.getCurrentFocus();
      if (currentFocusedView != null) {
        inputMethodManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }
  }
}
