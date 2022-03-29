package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.StateLiveData;

/** ViewModel for the CourseMeetingFragment. */
public class CourseMeetingViewModel extends ViewModel {

  private static final String TAG = "CourseMeetingViewModel";


  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
/*filter
  private StateLiveData<List<ThreadModel>> threads;
  private StateLiveData<ThreadModel> threadModelMutableLiveData;
  private StateLiveData<List<ThreadModel>> outFilteredThreads = new StateLiveData<>();
  public StateLiveData<ThreadModel> threadModelInputState = new StateLiveData<>();
  private final StateLiveData<SortEnum> sortEnum = new StateLiveData<>();
  private final StateLiveData<FilterEnum> filterEnum = new StateLiveData<>();
  private ArrayListUtil arrayListUtil;

  /** Initialise the ViewModel. */
  /*public void init() {
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
    this.arrayListUtil = new ArrayListUtil();
  }

  public StateLiveData<List<ThreadModel>> getThreads() {
    return this.threads;
  }

  /** sort the threads list. */
  /*public void sortThreads(List<ThreadModel> threadsModelList) {
    SortEnum sortEnum = Validation.checkStateLiveData(this.sortEnum, TAG);
    if (sortEnum == null) {
      return;
    }
    this.arrayListUtil.sortThreadList(threadsModelList, sortEnum);
  }

  /** filter threads list. */
  /*public void filterThreads(List<ThreadModel> threadsModelList) {
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
            filterEnum, actualMeeting, userId);
  }

  public StateLiveData<MeetingsModel> getMeeting() { return this.meeting;}

  /** Initialise the ViewModel. */
  public void init() {

  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setMeeting(MeetingsModel meeting) {
    this.meeting.postUpdate(meeting);
  }

  /** Determine the name of the tabs. */
  public String getTabTitle(int position) {
    switch (position) {
      case Config.TAB_LIVE_CHAT:
        return Config.TAB_LIVE_CHAT_NAME;
      case Config.TAB_POLL:
        return Config.TAB_POLL_NAME;
      case Config.TAB_QUESTIONS:
        return Config.TAB_QUESTIONS_NAME;
      default:
        return null;
    }
  }
}
