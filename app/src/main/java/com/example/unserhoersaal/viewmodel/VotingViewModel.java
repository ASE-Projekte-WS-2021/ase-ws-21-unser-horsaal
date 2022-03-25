package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.repository.VotingRepository;
import com.example.unserhoersaal.utils.StateLiveData;

/** ViewModel for the VotingFragment. */
public class VotingViewModel extends ViewModel {

  private static final String TAG = "VotingViewModel";

  private VotingRepository votingRepository;
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();

  /** Initialize the ViewModel. */
  public void init() {
    //TODO if ... != null

    this.votingRepository = VotingRepository.getInstance();
    this.meeting = this.votingRepository.getMeeting();

  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setMeeting(MeetingsModel meeting) {
    this.votingRepository.setMeeting(meeting);
  }
}
