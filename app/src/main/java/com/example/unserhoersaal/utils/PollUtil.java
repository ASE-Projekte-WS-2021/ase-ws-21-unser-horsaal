package com.example.unserhoersaal.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.databinding.BindingAdapter;

import com.example.unserhoersaal.model.PollModel;

public class PollUtil {

  private static final String TAG = "PollUtil";

  public static String calculatePercentage(View view, int optionVotes, int pollVotes) {
    if (pollVotes == 0) {
      return "0%";
    }
    int percentage = (int) Math.round(((double) optionVotes / (double) pollVotes)*100);
    ViewGroup.LayoutParams params = view.getLayoutParams();
    if (percentage == 0) {
      params.width = 1;
    } else {
      params.width = 5 * percentage;
    }
    view.setLayoutParams(params);
    return Math.round(percentage) + "%";
  }
}
