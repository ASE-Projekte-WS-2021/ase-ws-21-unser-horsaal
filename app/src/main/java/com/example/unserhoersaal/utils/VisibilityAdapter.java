package com.example.unserhoersaal.utils;

import android.view.View;
import androidx.databinding.BindingAdapter;

/** Show/Hide Floating Buttons. */
public class VisibilityAdapter {

  /** Opens Floating Buttons in Course Fragment Fragment. */
  @BindingAdapter("openCreationLayout")
  public static void openCreationLayout(View floatButton, View creationLayout) {
    floatButton.setVisibility(View.GONE);
    creationLayout.setVisibility(View.VISIBLE);
  }

  /** Closes Floating Buttons in Course Fragment Fragment. */
  @BindingAdapter("closeCreationLayout")
  public static void closeCreationLayout(View floatButton, View creationLayout) {
    floatButton.setVisibility(View.VISIBLE);
    creationLayout.setVisibility(View.GONE);
  }

  /** Toggles Floating Buttons in Meeting Fragment. */
  @BindingAdapter("toggleFloatingButtons")
  public static void toggleFloatingButtons(View floatButton1, View floatButton2) {
    if (floatButton1.getVisibility() == View.VISIBLE) {
      floatButton1.setVisibility(View.GONE);
      floatButton2.setVisibility(View.GONE);
    } else {
      floatButton1.setVisibility(View.VISIBLE);
      floatButton2.setVisibility(View.VISIBLE);
    }
  }

  /** Toggles Hashtag/PageNumber Editfield in Create Thread Fragment. */
  @BindingAdapter("toggleEditField")
  public static void toggleEditField(View editField, View arrow) {
    if(PreventDoubleClick.checkIfDoubleClick()) {
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
