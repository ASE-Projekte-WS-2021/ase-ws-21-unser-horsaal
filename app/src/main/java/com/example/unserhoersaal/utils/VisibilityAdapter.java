package com.example.unserhoersaal.utils;

import android.view.View;
import androidx.databinding.BindingAdapter;

public class VisibilityAdapter {

  @BindingAdapter("openCreationLayout")
  public static void openCreationLayout(View floatButton, View creationLayout) {
    floatButton.setVisibility(View.GONE);
    creationLayout.setVisibility(View.VISIBLE);
  }

  @BindingAdapter("closeCreationLayout")
  public static void closeCreationLayout(View floatButton, View creationLayout) {
    floatButton.setVisibility(View.VISIBLE);
    creationLayout.setVisibility(View.GONE);
  }

  @BindingAdapter("closeConfirmationDialog")
  public static void closeConfirmationDialog(View view, int a) {
    DeepLinkMode deepLinkMode = DeepLinkMode.getInstance();
    deepLinkMode.setDeepLinkMode(DeepLinkEnum.DEFAULT);
    view.setVisibility(View.GONE);
  }

  @BindingAdapter("toggleFloatingButtons")
  public static void toggleFloatingButtons(View floatButton1, View floatButton2) {
    if (floatButton1.getVisibility() == View.VISIBLE) {
      floatButton1.setVisibility(View.GONE);
      floatButton2.setVisibility(View.GONE);
    }
    else {
      floatButton1.setVisibility(View.VISIBLE);
      floatButton2.setVisibility(View.VISIBLE);
    }
  }

}
