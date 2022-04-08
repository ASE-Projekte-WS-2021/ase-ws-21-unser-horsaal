package com.example.unserhoersaal.utils;

import android.os.SystemClock;

import com.example.unserhoersaal.Config;

/**
 * Static Class to avoid double clicks.
 */
public class PreventDoubleClick {

  private static long mLastClickTime;

  /**
   * Checks if the last time the Button got clicked is less then 1.5 sec.
   */
  public static Boolean checkIfDoubleClick() {
    if (SystemClock.elapsedRealtime() - mLastClickTime < Config.DOUBLE_CLICK_WAIT) {
      return true;
    }
    mLastClickTime = SystemClock.elapsedRealtime();
    return false;
  }
}
