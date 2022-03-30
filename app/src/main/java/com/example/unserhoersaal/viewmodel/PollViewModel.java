package com.example.unserhoersaal.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.PollModel;
import com.example.unserhoersaal.repository.PollRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;

import java.util.List;

/** ViewModel for the PollFragment. */
public class PollViewModel extends ViewModel {

  private static final String TAG = "PollViewModel";

  private PollRepository pollRepository;
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<List<PollModel>> polls;
  private StateLiveData<PollModel> pollModel = new StateLiveData<>();
  public StateLiveData<PollModel> pollModelInputState = new StateLiveData<>();

  /** Initialize the ViewModel. */
  public void init() {
    //TODO really needed?
    if (this.polls != null) {
      return;
    }

    this.pollRepository = PollRepository.getInstance();
    this.meeting = this.pollRepository.getMeeting();
    if (this.meeting.getValue() != null) {
      this.polls = this.pollRepository.getPolls();
    }
    this.pollModel = this.pollRepository.getPollModelStateLiveData();
    this.pollModelInputState.postCreate(new PollModel());
  }

  public StateLiveData<PollModel> getPollModel() {
    return this.pollModel;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setMeeting(MeetingsModel meeting) {
    this.pollRepository.setMeeting(meeting);
  }

  public void resetPollData() {
    this.pollModelInputState.postCreate(new PollModel());
    this.pollModel.postCreate(new PollModel());
  }
  
  /** Create a new poll. */
  public void createPoll() {
    this.pollModel.postLoading();

    PollModel pollModel = Validation.checkStateLiveData(this.pollModelInputState, TAG);
    if (pollModel == null) {
      Log.e(TAG, "pollModel is null.");
      this.pollModel.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (pollModel.getText() == null) {
      Log.e(TAG, "text is null.");
      this.pollModel.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
      return;
    } else if (!Validation.stringHasPattern(pollModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      Log.e(TAG, "text has wrong pattern.");
      this.pollModel.postError(new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
      return;
    }
    this.pollModelInputState.postCreate(new PollModel());
    this.pollRepository.createNewPoll(pollModel);
  }

  public void loadPolls() {
    this.polls.postLoading();
    this.pollRepository.loadPolls();
  }

  public StateLiveData<List<PollModel>> getPolls() {
    return this.polls;
  }
}
