package com.example.unserhoersaal.utils;

import android.view.View;
import android.view.ViewGroup;
import com.example.unserhoersaal.Config;

/** Utilities for the poll. */
public class PollUtil {

  /** Calculate the proportion of the whole votes for one option. */
  public static String calculatePercentage(View view, int optionVotes, int pollVotes) {
    if (pollVotes == 0) {
      return pollVotes + Config.PERCENTAGE_SING;
    }
    int percentage = (int) Math.round(((double) optionVotes / (double) pollVotes)
            * Config.FACTOR_PROPORTION_TO_PERCENTAGE);
    ViewGroup.LayoutParams params = view.getLayoutParams();
    if (percentage == 0) {
      params.width = Config.POLL_BAR_MIN_LENGTH;
    } else {
      params.width = Config.POLL_BAR_LENGTH_FACTOR * percentage;
    }
    view.setLayoutParams(params);
    return Math.round(percentage) + Config.PERCENTAGE_SING;
  }
}
