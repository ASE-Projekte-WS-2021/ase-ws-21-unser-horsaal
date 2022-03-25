package com.example.unserhoersaal.repository;

import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.StateLiveData;

/** Repo for the live chat. */
public class LiveChatRepository {

  private static final String TAG = "LiveChatRepository";

  private static LiveChatRepository instance;
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();

  public LiveChatRepository() {
    this.meeting.postCreate(new MeetingsModel());
  }

  /** Gives back an instance of the LiveChatRepo. */
  public static LiveChatRepository getInstance() {
    if (instance == null) {
      instance = new LiveChatRepository();
    }
    return instance;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setMeeting(MeetingsModel meeting) {
    this.meeting.postUpdate(meeting);
  }
}
