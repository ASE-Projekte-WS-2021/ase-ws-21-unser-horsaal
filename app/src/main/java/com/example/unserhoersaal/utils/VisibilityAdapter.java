package com.example.unserhoersaal.utils;

import android.view.View;
import androidx.databinding.BindingAdapter;

public class VisibilityAdapter {

  @BindingAdapter("openMeetingCreationLayout")
  public static void openMeetingCreationLayout(View floatButton, View creationLayout) {
    floatButton.setVisibility(View.GONE);
    creationLayout.setVisibility(View.VISIBLE);
  }

  @BindingAdapter("closeMeetingCreationLayout")
  public static void closeMeetingCreationLayout(View floatButton, View creationLayout) {
    floatButton.setVisibility(View.VISIBLE);
    creationLayout.setVisibility(View.GONE);
  }
}
