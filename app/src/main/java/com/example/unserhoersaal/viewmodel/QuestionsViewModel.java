package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.enums.FilterEnum;
import com.example.unserhoersaal.enums.SortEnum;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.repository.CourseMeetingRepository;
import com.example.unserhoersaal.utils.ArrayListUtil;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

/** ViewModel for the QuestionsFragment. */
public class QuestionsViewModel extends ViewModel {

  private static final String TAG = "QuestionsViewModel";

  private CourseMeetingRepository courseMeetingRepository;
  private AuthAppRepository authAppRepository;
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<List<ThreadModel>> threads;
  private StateLiveData<ThreadModel> threadModelMutableLiveData;
  public StateLiveData<ThreadModel> threadModelInputState = new StateLiveData<>();
  private final StateLiveData<SortEnum> sortEnum = new StateLiveData<>();
  private final StateLiveData<FilterEnum> filterEnum = new StateLiveData<>();
  private ArrayListUtil arrayListUtil;

  /** Initialise the ViewModel. */
  public void init() {
    if (this.threads != null) {
      return;
    }

    this.courseMeetingRepository = CourseMeetingRepository.getInstance();
    this.authAppRepository = AuthAppRepository.getInstance();
    this.meeting = this.courseMeetingRepository.getMeeting();
    this.threadModelMutableLiveData =
            this.courseMeetingRepository.getThreadModelMutableLiveData();

    if (this.meeting.getValue() != null) {
      this.threads = this.courseMeetingRepository.getThreads();
    }
    this.threadModelInputState.postCreate(new ThreadModel());

    this.sortEnum.postCreate(SortEnum.NEWEST);
    this.filterEnum.postCreate(FilterEnum.NONE);
    this.arrayListUtil = new ArrayListUtil();
  }

  public StateLiveData<List<ThreadModel>> getThreads() {
    return this.threads;
  }

  /** sort the threads list. */
  public void sortThreads(List<ThreadModel> threadsModelList) {
    SortEnum sortEnum = Validation.checkStateLiveData(this.sortEnum, TAG);
    if (sortEnum == null) {
      return;
    }
    this.arrayListUtil.sortThreadList(threadsModelList, sortEnum);
  }

  /** filter the threads list. */
  public void filterThreads(List<ThreadModel> threadsModelList) {
    FilterEnum filterEnum = Validation.checkStateLiveData(this.filterEnum, TAG);
    if (filterEnum == null) {
      return;
    }
    FirebaseUser firebaseUser = Validation.checkStateLiveData(
            this.authAppRepository.getUserStateLiveData(), TAG);
    if (firebaseUser == null) {
      return;
    }
    MeetingsModel actualMeeting = Validation.checkStateLiveData(this.meeting, TAG);
    //List<ThreadModel> fullThreadsModelList = Validation.checkStateLiveData(this.threads, TAG);
    List<ThreadModel> fullThreadsModelList = new ArrayList<>(this.courseMeetingRepository
            .getThreadModelList());
    //fullThreadsModelList.addAll(this.courseMeetingRepository.getThreadModelList());
    String userId = firebaseUser.getUid();
    this.arrayListUtil.filterThreadList(threadsModelList, fullThreadsModelList,
            filterEnum, actualMeeting, userId);
  }

  public List<ThreadModel> getFullList() {
    return this.courseMeetingRepository.getThreadModelList();
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setSortEnum(SortEnum sortEnum) {
    this.sortEnum.postUpdate(sortEnum);
  }

  public void setFilterEnum(FilterEnum filterEnum) {
    this.filterEnum.postUpdate(filterEnum);
  }

  public StateLiveData<SortEnum> getSortEnum() {
    return this.sortEnum;
  }

  public StateLiveData<FilterEnum> getFilterEnum() {
    return this.filterEnum;
  }

  public StateLiveData<ThreadModel> getThreadModel() {
    return this.threadModelMutableLiveData;
  }

  public StateLiveData<ThreadModel> getThreadModelInputState() {
    return this.threadModelInputState;
  }

  public void resetThreadModelInput() {
    this.threadModelInputState.postCreate(new ThreadModel());
    this.threadModelMutableLiveData.postCreate(null);
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

    if (threadModel.getText() == null) {
      Log.d(TAG, "text is null.");
      this.threadModelInputState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
      return;
    } else if (!Validation.stringHasPattern(threadModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, "text wrong pattern.");
      this.threadModelInputState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
      return;
    }

    this.threadModelInputState.postCreate(new ThreadModel());
    this.courseMeetingRepository.createThread(threadModel);
  }
}
