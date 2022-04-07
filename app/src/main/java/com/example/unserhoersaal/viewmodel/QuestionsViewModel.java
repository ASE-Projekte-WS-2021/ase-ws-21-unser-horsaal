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
import com.example.unserhoersaal.repository.QuestionRepository;
import com.example.unserhoersaal.utils.ArrayListUtil;
import com.example.unserhoersaal.utils.PreventDoubleClick;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

/** ViewModel for the QuestionsFragment. */
public class QuestionsViewModel extends ViewModel {

  private static final String TAG = "QuestionsViewModel";

  private QuestionRepository questionRepository;
  private AuthAppRepository authAppRepository;
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<List<ThreadModel>> threads;
  private StateLiveData<ThreadModel> threadModelMutableLiveData;
  private StateLiveData<List<ThreadModel>> outFilteredThreads = new StateLiveData<>();
  public StateLiveData<ThreadModel> threadModelInputState = new StateLiveData<>();
  private final StateLiveData<SortEnum> sortEnum = new StateLiveData<>();
  private StateLiveData<FilterEnum> filterEnum = new StateLiveData<>();
  private ArrayList<FilterEnum> enumArray = new ArrayList<>();
  private ArrayListUtil arrayListUtil;

  /** Initialise the ViewModel. */
  public void init() {
    if (this.threads != null) {
      return;
    }

    this.questionRepository = QuestionRepository.getInstance();
    this.authAppRepository = AuthAppRepository.getInstance();
    this.meeting = this.questionRepository.getMeeting();
    this.threadModelMutableLiveData =
            this.questionRepository.getThreadModelMutableLiveData();

    if (this.meeting.getValue() != null) {
      this.threads = this.questionRepository.getThreads();
    }
    this.threadModelInputState.postCreate(new ThreadModel());

    this.sortEnum.postCreate(SortEnum.NEWEST);
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
    String userId = firebaseUser.getUid();
    this.arrayListUtil.filterThreadList(threadsModelList, outFilteredThreads,
            filterEnum, enumArray, actualMeeting, userId);
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setSortEnum(SortEnum sortEnum) {
    this.sortEnum.postUpdate(sortEnum);
  }

  /** JavaDoc. */
  public void setFilterEnum(FilterEnum filterEnum) {
    if (!enumArray.contains(filterEnum)) {
      this.enumArray.add(filterEnum);
      this.filterEnum.postUpdate(filterEnum);
    } else {
      this.enumArray.remove(filterEnum);
      this.filterEnum.postUpdate(FilterEnum.NONE);
    }
  }

  /** JavaDoc. */
  public void resetFilters() {
    this.enumArray.clear();
    this.filterEnum.postUpdate(FilterEnum.NONE);
    this.filterEnum.postUpdate(FilterEnum.RESET);
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

  public void setLiveDataComplete() {
    this.threadModelMutableLiveData.postComplete();
    this.threadModelInputState.postComplete();
  }

  public void setMeeting(MeetingsModel meeting) {
    this.questionRepository.setMeeting(meeting);
  }

  /** Create a new Thread. */
  public void createThread() {
    if(PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.threadModelInputState.postLoading();

    ThreadModel threadModel = Validation.checkStateLiveData(this.threadModelInputState, TAG);
    if (threadModel == null) {
      this.threadModelInputState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (Validation.emptyString(threadModel.getText())) {
      this.threadModelInputState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
    } else if (!Validation.stringHasPattern(threadModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      this.threadModelInputState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
    } else {
      this.threadModelInputState.postComplete();
      this.questionRepository.createThread(threadModel);
    }
  }
}
