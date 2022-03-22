package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;


import com.example.unserhoersaal.enums.FilterEnum;
import com.example.unserhoersaal.enums.SortEnum;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.CourseMeetingRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.example.unserhoersaal.utils.ArrayListUtil;

import java.nio.charset.MalformedInputException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** ViewModel for the CourseMeetingFragment. */
public class CourseMeetingViewModel extends ViewModel {

  private static final String TAG = "CourseMeetingViewModel";

  private CourseMeetingRepository courseMeetingRepository;
  private ArrayListUtil arrayListUtil = new ArrayListUtil();

  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<List<ThreadModel>> threads;
  private StateLiveData<ThreadModel> threadModelMutableLiveData;
  public StateLiveData<ThreadModel> threadModelInputState = new StateLiveData<>();

  //private MutableLiveData<SortEnum> sortEnum = new MutableLiveData<>();
  private StateLiveData<SortEnum> sortEnum = new StateLiveData<>();
  private StateLiveData<FilterEnum> filterEnum = new StateLiveData<>();

  /** Initialise the ViewModel. */
  public void init() {
    if (this.threads != null) {
      return;
    }

    this.courseMeetingRepository = CourseMeetingRepository.getInstance();
    this.arrayListUtil = new ArrayListUtil();
    this.meeting = this.courseMeetingRepository.getMeeting();
    this.threadModelMutableLiveData =
            this.courseMeetingRepository.getThreadModelMutableLiveData();

    if (this.meeting.getValue() != null) {
      this.threads = this.courseMeetingRepository.getThreads();
    }
    this.threadModelInputState.postCreate(new ThreadModel());
  }

  public StateLiveData<List<ThreadModel>> getThreads() {
    return this.threads;
  }

  /** sort the threads list.
   *  First parameter is the treads list to sort.
   *  The second parameter is a sort option (String).
   *  Sort options: "newest", "likes", "answers", "page number asc" (ascending) and
   *  "page number desc" (descending)
   */
  public void sortThreads(List<ThreadModel> threadsModelList, String sortOption) {
    this.arrayListUtil.sortThreadList(threadsModelList, sortOption);
  }

  /** filter the threads list.
   *  First parameter is the treads list to filter.
   *  The second parameter is a filter option (String).
   *  filter options: "answered", "not answered", "course provider", "own", "tag subject matter",
   *  "tag organisation", "tag error", "tag examination", "tag other" and "reset" (show all)
   */
  public void filterThreads(List<ThreadModel> threadsModelList, String filterOption) {
    MeetingsModel actualMeeting = Validation.checkStateLiveData(this.meeting, TAG);
    List<ThreadModel> fullThreadsModelList = Validation.checkStateLiveData(this.threads, TAG);
    String userId = courseMeetingRepository.getUserId();
    this.arrayListUtil.filterThreadList(threadsModelList, fullThreadsModelList, filterOption,
            actualMeeting, userId);
  }


  public StateLiveData<MeetingsModel> getMeeting() { return this.meeting;}

  public void setSortEnum(SortEnum sortEnum) {
    //this.sortEnum.postValue(sortEnum);
    this.sortEnum.postCreate(sortEnum);
    //this.sortEnum.postUpdate(sortEnum);
    Log.d(TAG, "setSortEnum: " + sortEnum);
  }

  public void setFilterEnum(FilterEnum filterEnum) {
    //this.sortEnum.postValue(sortEnum);
    this.filterEnum.postCreate(filterEnum);
    //this.sortEnum.postUpdate(sortEnum);
    Log.d(TAG, "setFilterEnum: " + filterEnum);
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
