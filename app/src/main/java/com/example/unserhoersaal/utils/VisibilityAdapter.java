package com.example.unserhoersaal.utils;

import android.view.View;
import androidx.databinding.BindingAdapter;

/** Show/Hide Floating Buttons. */
public class VisibilityAdapter {

  /** Toggles Hashtag/PageNumber Editfield in Create Thread Fragment. */
  @BindingAdapter("toggleEditField")
  public static void toggleEditField(View editField, View arrow) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    if (editField.getVisibility() == View.VISIBLE) {
      editField.setVisibility(View.GONE);
      arrow.animate().rotationBy(-90);
    } else {
      editField.setVisibility(View.VISIBLE);
      arrow.animate().rotationBy(90);
    }
  }
}
