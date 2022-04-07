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

  public StateLiveData<List<PollModel>> getPolls() {
    return this.polls;
  }

  public void resetPollData() {
    this.pollModelInputState.postCreate(new PollModel());
    this.pollModel.postCreate(new PollModel());
  }

  public void setLiveDataComplete() {
    this.pollModelInputState.postComplete();
    this.pollModel.postComplete();
  }
  
  /** Create a new poll. */
  public void createPoll(boolean yesNoPoll) {
    if(PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    if(PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.pollModel.postLoading();
    this.pollModelInputState.postLoading();

    PollModel pollModel = Validation.checkStateLiveData(this.pollModelInputState, TAG);
    if (pollModel == null) {
      this.pollModel.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (Validation.emptyString(pollModel.getText())) {
      this.pollModelInputState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
    } else if (!Validation.stringHasPattern(pollModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      this.pollModelInputState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
    } else {
      if (yesNoPoll) {
        this.handleYesNoPoll(pollModel);
      } else {
        this.handleOptionsPoll(pollModel);
      }
    }

  }

  private void handleYesNoPoll(PollModel pollModel) {
    pollModel.setOptionsText1(Config.OPTION_YES);
    pollModel.setOptionsText2(Config.OPTION_NO);
    pollModel.setOptionsText3(null);
    pollModel.setOptionsText4(null);

    this.pollModelInputState.postComplete();

    this.pollRepository.createNewPoll(pollModel);
    this.pollModelInputState.postCreate(new PollModel());
    this.pollModel.postCreate(new PollModel());
  }

  private void handleOptionsPoll(PollModel pollModel) {
    if (Validation.emptyString(pollModel.getOptionsText1())) {
      this.pollModelInputState
              .postError(new Error(Config.DATABINDING_OPTION_NULL), ErrorTag.OPTION1);
    } else if (!Validation.stringHasPattern(pollModel.getOptionsText1(),
            Config.REGEX_PATTERN_OPTIONS)) {
      this.pollModelInputState
              .postError(new Error(Config.DATABINDING_OPTION_WRONG_PATTERN), ErrorTag.OPTION1);
    } else if (Validation.emptyString(pollModel.getOptionsText2())) {
      this.pollModelInputState.postError(
              new Error(Config.DATABINDING_OPTION_NULL), ErrorTag.OPTION2);
    } else if (!Validation.stringHasPattern(pollModel.getOptionsText2(),
            Config.REGEX_PATTERN_OPTIONS)) {
      this.pollModelInputState
              .postError(new Error(Config.DATABINDING_OPTION_WRONG_PATTERN), ErrorTag.OPTION2);
    } else {
      this.handleOptionalOptions(pollModel);
    }
  }

  private void handleOptionalOptions(PollModel pollModel) {
    if (Validation.emptyString(pollModel.getOptionsText3())) {
      pollModel.setOptionsText3(null);
    } else if (!Validation.stringHasPattern(pollModel.getOptionsText3(),
            Config.REGEX_PATTERN_OPTIONS)){
      this.pollModelInputState
              .postError(new Error(Config.DATABINDING_OPTION_WRONG_PATTERN), ErrorTag.OPTION3);
      return;
    }
    if (Validation.emptyString(pollModel.getOptionsText4())) {
      pollModel.setOptionsText4(null);
    } else if (!Validation.stringHasPattern(pollModel.getOptionsText3(),
            Config.REGEX_PATTERN_OPTIONS)) {
      this.pollModelInputState
              .postError(new Error(Config.DATABINDING_OPTION_WRONG_PATTERN), ErrorTag.OPTION2);
      return;
    }
    this.pollModelInputState.postComplete();
    this.pollRepository.createNewPoll(pollModel);
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
    pollModelList.sort((pollModel, t1) -> {
      if (pollModel.getCreationTime() == null || t1.getCreationTime() == null) {
        return 0;
      }
      return t1.getCreationTime().compareTo(pollModel.getCreationTime());
    });
  }
}
