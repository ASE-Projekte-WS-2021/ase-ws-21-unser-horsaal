package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.CheckedOptionEnum;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.PollModel;
import com.example.unserhoersaal.repository.PollRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.Comparator;
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
    PollModel pollModel = Validation.checkStateLiveData(this.pollModelInputState, TAG);
    if (pollModel == null) {
      Log.e(TAG, Config.POLL_MODEL_NULL);
      this.pollModel.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }
    this.pollModel.postLoading();


    if (yesNoPoll) {
      pollModel.setOptionsText1(Config.OPTION_YES);
      pollModel.setOptionsText2(Config.OPTION_NO);
      pollModel.setOptionsText3(null);
      pollModel.setOptionsText4(null);
    } else {
      //TODO validate 1,2 everything
      if (pollModel.getText() == null) {
        Log.e(TAG, Config.DATABINDING_TEXT_NULL);
        this.pollModel.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
        return;
      } else if (!Validation.stringHasPattern(pollModel.getText(), Config.REGEX_PATTERN_TEXT)) {
        Log.e(TAG, Config.DATABINDING_TEXT_WRONG_PATTERN);
        this.pollModel.postError(new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
        return;
      }
      if (pollModel.getOptionsText3() != null && pollModel.getOptionsText3().equals("")) {
        pollModel.setOptionsText3(null);
      }
      if (pollModel.getOptionsText4() != null && pollModel.getOptionsText4().equals("")) {
        pollModel.setOptionsText4(null);
      }
    }
    this.pollModelInputState.postCreate(new PollModel());
    this.pollRepository.createNewPoll(pollModel);
  }

  public StateLiveData<List<PollModel>> getPolls() {
    return this.polls;
  }

  /** Handle the selection of an option from an user.*/
  public void vote(CheckedOptionEnum checkedOption, PollModel pollModel) {
    String pollId = pollModel.getKey();
    CheckedOptionEnum oldOption = pollModel.getCheckedOption();
    if (oldOption == checkedOption) {
      this.pollRepository.removeVote(getOptionPath(checkedOption), pollId);
    } else {
      if (oldOption != null && oldOption != CheckedOptionEnum.NONE) {
        this.pollRepository.removeVote(getOptionPath(oldOption), pollId);
      }
      this.pollRepository.vote(checkedOption, getOptionPath(checkedOption), pollId);
    }
  }

  private String getOptionPath(CheckedOptionEnum checkedOption) {
    switch (checkedOption) {
      case OPTION1:
        return "optionsCount1";
      case OPTION2:
        return "optionsCount2";
      case OPTION3:
        return "optionsCount3";
      case OPTION4:
        return "optionsCount4";
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
