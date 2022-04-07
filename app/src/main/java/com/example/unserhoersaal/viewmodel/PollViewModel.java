package com.example.unserhoersaal.viewmodel;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.CheckedOptionEnum;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.PollModel;
import com.example.unserhoersaal.repository.PollRepository;
import com.example.unserhoersaal.utils.PreventDoubleClick;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
  public void createPoll(boolean yesNoPoll) {
    this.pollModel.postLoading();
    PollModel pollModel = Validation.checkStateLiveData(this.pollModelInputState, TAG);
    if (pollModel == null) {
      Log.e(TAG, Config.POLL_MODEL_NULL);
      this.pollModel.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }
    if (pollModel.getText() == null) {
      Log.e(TAG, Config.DATABINDING_TEXT_NULL);
      this.pollModel.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
      return;
    } else if (!Validation.stringHasPattern(pollModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      Log.e(TAG, Config.DATABINDING_TEXT_WRONG_PATTERN);
      this.pollModel.postError(new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
      return;
    }

    if (yesNoPoll) {
      pollModel.setOptionsText1(Config.OPTION_YES);
      pollModel.setOptionsText2(Config.OPTION_NO);
      pollModel.setOptionsText3(null);
      pollModel.setOptionsText4(null);
    } else {

      if (pollModel.getOptionsText1() == null || pollModel.getOptionsText2() == null) {
        Log.e(TAG, Config.DATABINDING_OPTION_NULL);
        this.pollModel.postError(new Error(Config.DATABINDING_OPTION_NULL), ErrorTag.OPTION);
        //TODO PATTERN FOR OPTION NEEDED?
      } else if (!Validation.stringHasPattern(pollModel.getOptionsText1(),
              Config.REGEX_PATTERN_TEXT)
              || !Validation.stringHasPattern(pollModel.getOptionsText2(),
              Config.REGEX_PATTERN_TEXT)) {
        Log.e(TAG, Config.DATABINDING_OPTION_WRONG_PATTERN);
        this.pollModel
                .postError(new Error(Config.DATABINDING_OPTION_WRONG_PATTERN), ErrorTag.OPTION);
      }
      if (pollModel.getOptionsText3()
              != null && pollModel.getOptionsText3().equals(Config.OPTION_EMPTY)) {
        pollModel.setOptionsText3(null);
      }
      if (pollModel.getOptionsText4()
              != null && pollModel.getOptionsText4().equals(Config.OPTION_EMPTY)) {
        pollModel.setOptionsText4(null);
      }
    }

    this.pollRepository.createNewPoll(pollModel);
    this.pollModelInputState.postCreate(new PollModel());
    this.pollModel.postCreate(new PollModel());
  }

  public StateLiveData<List<PollModel>> getPolls() {
    return this.polls;
  }

  /** Handle the selection of an option from an user.*/
  public void vote(CheckedOptionEnum checkedOption, PollModel pollModel) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.pollModel.postLoading();
    String pollId = pollModel.getKey();
    CheckedOptionEnum oldOption = pollModel.getCheckedOption();
    if (oldOption == checkedOption) {
      this.pollRepository.removeVote(getOptionPath(checkedOption), pollId);
    } else {
      if (oldOption != null && oldOption != CheckedOptionEnum.NONE) {
        this.pollRepository.changeVote(checkedOption, getOptionPath(checkedOption),
                getOptionPath(oldOption), pollId);
      } else {
        this.pollRepository.vote(checkedOption, getOptionPath(checkedOption), pollId);
      }
    }
    this.pollModel.postCreate(new PollModel());
  }

  private String getOptionPath(CheckedOptionEnum checkedOption) {
    switch (checkedOption) {
      case OPTION1:
        return Config.CHILD_OPTION_COUNT_1;
      case OPTION2:
        return Config.CHILD_OPTION_COUNT_2;
      case OPTION3:
        return Config.CHILD_OPTION_COUNT_3;
      case OPTION4:
        return Config.CHILD_OPTION_COUNT_4;
      default:
        return null;
    }
  }

  /** Sort the polls so that the newest is first. */
  public void sortNewestFirst(List<PollModel> pollModelList) {
    if (pollModelList == null) {
      return;
    }
    pollModelList.sort(new Comparator<PollModel>() {
      @Override
      public int compare(PollModel pollModel, PollModel t1) {
        if (pollModel.getCreationTime() == null || t1.getCreationTime() == null) {
          return 0;
        }
        return t1.getCreationTime().compareTo(pollModel.getCreationTime());
      }
    });
  }
}
