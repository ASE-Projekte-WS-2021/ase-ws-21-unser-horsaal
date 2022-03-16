package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.CourseMeetingRepository;
import com.example.unserhoersaal.utils.ArrayListUtil;

import java.util.List;

/** ViewModel for the CourseMeetingFragment. */
public class CourseMeetingViewModel extends ViewModel {

  private static final String TAG = "CourseMeetingViewModel";

  private CourseMeetingRepository courseMeetingRepository;
  private ArrayListUtil arrayListUtil = new ArrayListUtil();

  private MutableLiveData<MeetingsModel> meeting = new MutableLiveData<>();
  private MutableLiveData<List<ThreadModel>> threads;
  private MutableLiveData<ThreadModel> threadModelMutableLiveData;
  public MutableLiveData<ThreadModel> threadModelInput;

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
    this.threadModelInput = new MutableLiveData<>(new ThreadModel());
  }

  public LiveData<List<ThreadModel>> getThreads() {
    return this.threads;
  }

  /** sort the threads list.
   *  First parameter is the treads list to sort.
   *  The second parameter is a sort option (String).
   *  Sort options: "newest", "likes" and "answers"
   */
  public void sortThreads(List<ThreadModel> threadsModelList, String sortOption) {
    this.arrayListUtil.sortThreadList(threadsModelList, sortOption);
  }

  /** filter the threads list.
   *  First parameter is the treads list to filter.
   *  The second parameter is a filter option (String).
   *  filter options: "answered" and "not answered"
   */
  public void filterThreads(List<ThreadModel> threadsModelList, String filterOption) {
    MeetingsModel actualMeeting = this.meeting.getValue();
    String userId = courseMeetingRepository.getUserId();
    this.arrayListUtil.filterThreadList(threadsModelList, filterOption, actualMeeting, userId);
  }

  public LiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public LiveData<ThreadModel> getThreadModel() {
    return this.threadModelMutableLiveData;
  }

  public void resetThreadModelInput() {
    this.threadModelInput.setValue(new ThreadModel());
    this.threadModelMutableLiveData.setValue(null);
  }

  public void setMeeting(MeetingsModel meeting) {
    this.courseMeetingRepository.setMeeting(meeting);
  }

  /** Create a new Thread. */
  public void createThread() {
    //TODO: error -> view
    if (this.threadModelInput.getValue() == null) {
      return;
    }

    //TODO: error -> view
    ThreadModel threadModel = this.threadModelInput.getValue();
    if (threadModel.getTitle() == null) {
      return;
    }
    if (threadModel.getText() == null) {
      return;
    }

    this.courseMeetingRepository.createThread(threadModel);
  }
}
