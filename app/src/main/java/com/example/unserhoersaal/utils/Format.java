package com.example.unserhoersaal.utils;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.LikeStatus;

import java.util.Date;

public class Format {
  @BindingAdapter("getFormatedDate")
  public static String getFormatedDate(View view, Long time) {
    return Config.OLD_FORMAT.format(new Date(time));
  }

  @BindingAdapter("getFormatedDateTime")
  public static String getFormatedDateTime(View view, Long time) {
    if (time == null) return "";
    return Config.DATE_TIME_FORMAT.format(new Date(time));
  }

  @BindingAdapter("calculateDate")
  public static String calculateDate(TextView view, Long timeInMillis) {
    String date;
    if (System.currentTimeMillis() - timeInMillis < 1000 * 3600 * 24) {
      date = Config.RECENT_FORMAT.format(new Date(timeInMillis));
    } else {
      date = Config.OLD_FORMAT.format(new Date(timeInMillis));
    }

    return date;
  }

  @BindingAdapter("setColor")
  public static void setColor(ImageView view, boolean answered) {
    System.out.println(answered);
    if (answered) {
      view.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
    } else {
      view.clearColorFilter();
    }
  }

  @BindingAdapter("setLikeStatus")
  public static void setLikeStatus(ImageView button, LikeStatus likeStatus) {
    System.out.println(button);
    if (likeStatus == LikeStatus.LIKE) {
      button.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
    }
    else {
      button.clearColorFilter();
    }
  }

  @BindingAdapter("setDisLikeStatus")
  public static void setDisLikeStatus(ImageView button, LikeStatus likeStatus) {
    if (likeStatus == LikeStatus.DISLIKE) {
      button.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
    }
    else {
      button.clearColorFilter();
    }
  }
}
