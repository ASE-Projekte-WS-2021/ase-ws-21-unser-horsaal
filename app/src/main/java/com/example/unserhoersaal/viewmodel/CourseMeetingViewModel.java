package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.StateLiveData;

/** ViewModel for the CourseMeetingFragment. */
public class CourseMeetingViewModel extends ViewModel {

  private static final String TAG = "CourseMeetingViewModel";


  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();

  /** Initialise the ViewModel. */
  public void init() {
    //TODO if ... != null
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setMeeting(MeetingsModel meeting) {
    this.meeting.postUpdate(meeting);
  }

  /** Determine the name of the tabs. */
  public String getTabTitle(int position) {
    switch (position) {
      case Config.TAB_LIVE_CHAT:
        return Config.TAB_LIVE_CHAT_NAME;
      case Config.TAB_POLL:
        return Config.TAB_POLL_NAME;
      case Config.TAB_QUESTIONS:
        return Config.TAB_QUESTIONS_NAME;
      default:
        return null;
    }
  }
}
