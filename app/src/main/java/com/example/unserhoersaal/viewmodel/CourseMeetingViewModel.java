package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.StateLiveData;

/** ViewModel for CourseMeetingFragment.
 * Provides statuslivedata meeting for loading threads in this meeting.*/
public class CourseMeetingViewModel extends ViewModel {

  private final StateLiveData<MeetingsModel> meeting = new StateLiveData<>();

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  /** Sets a meeting to this ViewModel to load threads according to this meetingId.
   *
   * @param meeting a meetingmodel to set it as the current meeting to load threads. */
  public void setMeeting(MeetingsModel meeting) {
    this.meeting.postUpdate(meeting);
  }

  /** Determine the name of the tabs. Returns the name for the currently selected viewpager tab.
   *
   * @param position an int value determining the next position in the viewpager adapter.
   * @return returns the name for the tab.
   * */
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
