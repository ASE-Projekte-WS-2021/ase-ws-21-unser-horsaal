package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.CourseMeetingRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** ViewModel for the CourseMeetingFragment. */
public class CourseMeetingViewModel extends ViewModel {

  private static final String TAG = "CourseMeetingViewModel";

  private CourseMeetingRepository courseMeetingRepository;
  private StateLiveData<MeetingsModel> currentMeetingRepoState;
  private StateLiveData<List<ThreadModel>> allThreadsRepoState;
  private StateLiveData<ThreadModel> currentThreadRepoState;
  public StateLiveData<ThreadModel> threadModelInputState = new StateLiveData<>();

  /** Initialise the ViewModel. */
  public void init() {
    if (this.allThreadsRepoState != null) {
      return;
    }

    this.courseMeetingRepository = CourseMeetingRepository.getInstance();
    this.currentMeetingRepoState = this.courseMeetingRepository.getCurrentMeetingRepoState();
    this.currentThreadRepoState =
            this.courseMeetingRepository.getCurrentThreadRepoState();

    if (this.currentMeetingRepoState.getValue() != null) {
      this.allThreadsRepoState = this.courseMeetingRepository.getAllThreadsRepoState();
    }
    this.threadModelInputState.postCreate(new ThreadModel());
  }

  public StateLiveData<List<ThreadModel>> getAllThreadsRepoState() {
    return this.allThreadsRepoState;
  }

  public StateLiveData<MeetingsModel> getCurrentMeetingRepoState() {
    return this.currentMeetingRepoState;
  }

  public StateLiveData<ThreadModel> getCurrentThreadRepoState() {
    return this.currentThreadRepoState;
  }

  public StateLiveData<ThreadModel> getThreadModelInputState() {
    return this.threadModelInputState;
  }

  /** Sort the threads list by likes. */
  public void sortThreadsByLikes(List<ThreadModel> threadsModelList) {
    Collections.sort(threadsModelList, new Comparator<ThreadModel>() {
      @Override
      public int compare(ThreadModel threadModel, ThreadModel t1) {
        return t1.getLikes() - threadModel.getLikes();
      }
    });
  }

  public void resetThreadModelInput() {
    this.threadModelInputState.postCreate(new ThreadModel());
    this.currentThreadRepoState.postCreate(null);
  }

  public void setCurrentMeetingRepoState(MeetingsModel currentMeetingRepoState) {
    this.courseMeetingRepository.setCurrentMeetingRepoState(currentMeetingRepoState);
  }

  /** Create a new Thread. */
  public void createThread() {
    ThreadModel threadModel = Validation.checkStateLiveData(this.threadModelInputState, TAG);
    if (threadModel == null) {
      Log.e(TAG, Config.MEETING_NO_THREAD_MODEL);
      this.currentThreadRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }
    
    if (this.validateText(threadModel.text)) {
      return;
    }

    this.threadModelInputState.postCreate(new ThreadModel());
    this.courseMeetingRepository.createThread(threadModel);
  }

  private boolean validateText(String text) {
    if (text == null) {
      Log.d(TAG, Config.MEETING_NO_TEXT);
      this.currentThreadRepoState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
      return true;
    } else if (!Validation.stringHasPattern(text, Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, Config.MEETING_WRONG_TEXT_PATTERN);
      this.currentThreadRepoState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
      return true;
    }
    return false;
  }

}
