package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.CourseMeetingRepository;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;

import java.util.List;

/** ViewModel for the CourseMeetingFragment. */
public class CourseMeetingViewModel extends ViewModel {

  private static final String TAG = "CourseMeetingViewModel";

  private CourseMeetingRepository courseMeetingRepository;

  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<List<ThreadModel>> threads;
  private StateLiveData<ThreadModel> threadModelMutableLiveData;
  public StateLiveData<ThreadModel> threadModelInputState = new StateLiveData<>();

  /** Initialise the ViewModel. */
  public void init() {
    if (this.threads != null) {
      return;
    }

    this.courseMeetingRepository = CourseMeetingRepository.getInstance();
    this.meeting = this.courseMeetingRepository.getMeeting();
    this.threadModelMutableLiveData =
            this.courseMeetingRepository.getThreadModelMutableLiveData();

    if (this.meeting.getValue() != null) {
      this.threads = this.courseMeetingRepository.getThreads();
    }
    this.threadModelInputState.postValue(new StateData<>(new ThreadModel()));
  }

  public StateLiveData<List<ThreadModel>> getThreads() {
    return this.threads;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public StateLiveData<ThreadModel> getThreadModel() {
    return this.threadModelMutableLiveData;
  }

  public void resetThreadModelInput() {
    this.threadModelInputState.postValue(new StateData<>(new ThreadModel()));
    this.threadModelMutableLiveData.postValue(new StateData<>(null));
  }

  public void setMeeting(MeetingsModel meeting) {
    this.courseMeetingRepository.setMeeting(meeting);
  }

  /** Create a new Thread. */
  public void createThread() {
    ThreadModel threadModel = Validation.checkStateLiveData(this.threadModelInputState, TAG);
    if (threadModel == null) {
      Log.e(TAG, "threadModel is null.");
      return;
    }

    if (threadModel.getTitle() == null) {
      Log.d(TAG, "title is null.");
      this.threadModelInputState.postError(new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.stringHasPattern(threadModel.getTitle(), Config.REGEX_PATTERN_TITLE)) {
      Log.d(TAG, "title wrong pattern.");
      this.threadModelInputState.postError(new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.VM);
      return;
    }
    if (threadModel.getText() == null) {
      Log.d(TAG, "text is null.");
      this.threadModelInputState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.stringHasPattern(threadModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, "text wrong pattern.");
      this.threadModelInputState.postError(new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.VM);
      return;
    }

    this.threadModelInputState.postComplete();
    this.courseMeetingRepository.createThread(threadModel);
  }
}
