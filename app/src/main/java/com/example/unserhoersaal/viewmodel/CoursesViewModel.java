package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.utils.StateLiveData;

/** This class is the ViewModel for the signed up courses. */
public class CoursesViewModel extends ViewModel {

  private StateLiveData<String> userId = new StateLiveData<>();

  public StateLiveData<String> getUserId() {
    return this.userId;
  }

  public void setUserId(String userId) {
    this.userId.postUpdate(userId);
  }

  /** Determine the name of the tabs. */
  public String getTabTitle(int position) {
    switch (position) {
      case Config.TAB_TODAY:
        return Config.TAB_TODAY_NAME;
      case Config.TAB_ALL:
        return Config.TAB_ALL_NAME;
      case Config.TAB_OWNED:
        return Config.TAB_OWNED_NAME;
      default:
        return null;
    }
  }


}