package com.example.unserhoersaal.utils;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.enums.LikeStatus;
import com.example.unserhoersaal.model.CalendarModel;
import com.google.android.material.card.MaterialCardView;
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

  /** time formater. */
  @BindingAdapter("getFormatedTime")
  public static String getFormatedTime(View view, Long time) {
    if (time == null) {
      return "";
    }
    return Config.RECENT_FORMAT.format(new Date(time));
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

  /** sets border for thread card and colours it green when thread is solved. */
  @BindingAdapter("setBorder")
  public static void setBorder(MaterialCardView view, boolean answered) {
    if (answered) {
      view.setStrokeWidth(Config.CARD_STROKE_WIDTH);
      view.setStrokeColor(Color.GREEN);
    } else {
      view.setStrokeWidth(Config.NO_STROKE);
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

  /** Gets the calendarModel and Formats it to Date. */
  @BindingAdapter("app:formatStartDate")
  public static void formatStartDate(TextView view, CalendarModel calendarModel) {
    if (calendarModel.getYearInput() != -1
            && calendarModel.getMonthInput() != -1
            && calendarModel.getDayOfMonthInput() != -1) {
      int year = calendarModel.getYearInput();
      int month = calendarModel.getMonthInput();
      int day = calendarModel.getDayOfMonthInput();

      String dayAsString = day < 10 ? "0" + day : String.valueOf(day);
      month++;
      String monthAsString = month < 10 ? "0" + month : String.valueOf(month);

      String time = dayAsString + "." + monthAsString + "." + year;
      view.setText(time);
    } else {
      view.setText(R.string.current_date_placeholder);
    }
  }

  /** Format the calenderModel to startTime. */
  @BindingAdapter("app:formatStartTime")
  public static void formatStartTime(TextView view, CalendarModel calendarModel) {
    if (calendarModel.getHourInput() != -1
            && calendarModel.getMinuteInput() != -1) {
      int hour = calendarModel.getHourInput();
      int minute = calendarModel.getMinuteInput();

      String hourAsString = hour < 10 ? "0" + hour : String.valueOf(hour);
      String minuteAsString = minute < 10 ? "0" + minute : String.valueOf(minute);

      String time = hourAsString + ":" + minuteAsString;
      view.setText(time);
    } else {
      view.setText(R.string.current_time_placeholder);
    }
  }
}
