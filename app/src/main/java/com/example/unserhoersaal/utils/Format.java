package com.example.unserhoersaal.utils;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.LikeStatus;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Binding Aadapter Class that formats unix timestamp (milliseconds) in long datatype
 * and colours text. */
public class Format {

  /** time formater. */
  @BindingAdapter("getFormatedDate")
  public static String getFormatedDate(View view, Long time) {
    return Config.OLD_FORMAT.format(new Date(time));
  }

  /** datetime formater. */
  @BindingAdapter("getFormatedDateTime")
  public static String getFormatedDateTime(View view, Long time) {
    if (time == null) {
      return "";
    }
    return Config.DATE_TIME_FORMAT.format(new Date(time));
  }

  /** datetime formater that formats to date after 24h passed. */
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

  /** sets the view to current time. */
  @BindingAdapter("currentFormatedTime")
  public static void currentFormatedTime(TextView textView, SimpleDateFormat format) {
    textView.setText(format.format(new Date().getTime()));
  }

  /** colours the like button depending on if the user has interacted with it. */
  @BindingAdapter("setColor")
  public static void setColor(ImageView view, boolean answered) {
    if (answered) {
      view.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
    } else {
      view.clearColorFilter();
    }
  }

  /** colours the like button depending on if the user has interacted with it. */
  @BindingAdapter("setLikeStatus")
  public static void setLikeStatus(ImageView button, LikeStatus likeStatus) {
    if (likeStatus == LikeStatus.LIKE) {
      button.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
    } else {
      button.clearColorFilter();
    }
  }

  /** colours the like button depending on if the user has interacted with it. */
  @BindingAdapter("setDisLikeStatus")
  public static void setDisLikeStatus(ImageView button, LikeStatus likeStatus) {
    if (likeStatus == LikeStatus.DISLIKE) {
      button.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
    } else {
      button.clearColorFilter();
    }
  }

  /** Format the enter key for a course in a better readable string. */
  @BindingAdapter("formatEnterKey")
  public static String formatEnterKey(TextView view, String text) {
    if (text == null) {
      return "";
    }
    return new StringBuilder(text)
            .insert(Config.READABILITY_ITEM_POSITION_2, Config.CODE_MAPPING_READABILITY_ITEM)
            .insert(Config.READABILITY_ITEM_POSITION_1, Config.CODE_MAPPING_READABILITY_ITEM)
            .toString();
  }
}
