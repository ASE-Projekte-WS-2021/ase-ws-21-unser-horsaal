package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.repository.LiveChatRepository;
import com.example.unserhoersaal.utils.StateLiveData;

/** ViewModel for the LiveChatFragment. */
public class LiveChatViewModel extends ViewModel {

  private static final String TAG = "LiveChatViewModel";

  private LiveChatRepository liveChatRepository;
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();

  /** Initialize the ViewModel. */
  public void init() {
    //TODO if ... != null

    this.liveChatRepository = LiveChatRepository.getInstance();
    this.meeting = this.liveChatRepository.getMeeting();
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setMeeting(MeetingsModel meeting) {
    this.liveChatRepository.setMeeting(meeting);
  }
}
