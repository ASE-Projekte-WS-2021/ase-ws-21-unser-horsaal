package com.example.unserhoersaal.utils;

import android.os.SystemClock;

public class PreventDoubleClick {

  private static long mLastClickTime;

  public static Boolean checkIfDoubleClick() {
    if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
      return true;
    }
    mLastClickTime = SystemClock.elapsedRealtime();
    return false;
  }
}
