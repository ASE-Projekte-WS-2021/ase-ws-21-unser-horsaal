package com.example.unserhoersaal.repository;

import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.StateLiveData;

/** Repo for the voting. */
public class VotingRepository {

  private static final String TAG = "VotingRepository";

  private static VotingRepository instance;
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();

  public VotingRepository() {
    this.meeting.postCreate(new MeetingsModel());
  }

  /** Give back an instance of the class. */
  public static VotingRepository getInstance() {
    if (instance == null) {
      instance = new VotingRepository();
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
