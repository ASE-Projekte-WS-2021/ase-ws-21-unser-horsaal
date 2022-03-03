package com.example.unserhoersaal.utils;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;
import java.util.Date;

public class Format {
  @BindingAdapter("getFormatedDate")
  public static String getFormatedDate(View view, Long time) {
    return Config.OLD_FORMAT.format(new Date(time));
  }

  @BindingAdapter("getFormatedDateTime")
  public static String getFormatedDateTime(View view, Long time) {
    return Config.DATE_TIME_FORMAT.format(new Date(time));
  }

  @BindingAdapter("setColor")
  public static void setColor(ImageView view, boolean answered) {
    if (answered) {
      view.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
    } else {
      view.clearColorFilter();
    }
  }
}
