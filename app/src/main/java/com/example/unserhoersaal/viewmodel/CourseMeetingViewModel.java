package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.CourseMeetingRepository;
import java.util.List;

/** ViewModel for the CourseMeetingFragment. */
public class CourseMeetingViewModel extends ViewModel {

  private static final String TAG = "CourseMeetingViewModel";

  private CourseMeetingRepository courseMeetingRepository;

  private MutableLiveData<String> meetingId = new MutableLiveData<>();
  private MutableLiveData<List<ThreadModel>> threads;
  private MutableLiveData<ThreadModel> threadModelMutableLiveData;

  /** Initialise the ViewModel. */
  public void init() {
    if (this.threads != null) {
      return;
    }

    this.courseMeetingRepository = CourseMeetingRepository.getInstance();
    this.meetingId = this.courseMeetingRepository.getMeetingId();
    this.threadModelMutableLiveData =
            this.courseMeetingRepository.getThreadModelMutableLiveData();

    if (this.meetingId.getValue() != null) {
      this.threads = this.courseMeetingRepository.getThreads();
    }
  }

  public LiveData<List<ThreadModel>> getThreads() {
    return this.threads;
  }

  public LiveData<String> getMeetingId() {
    return this.meetingId;
  }

  public LiveData<ThreadModel> getThreadModel() {
    return this.threadModelMutableLiveData;
  }

  public void setMeetingId(String meetingId) {
    this.courseMeetingRepository.setMeetingId(meetingId);
  }

  /** Create a new Thread. */
  public void createThread(String title, String text) {
    ThreadModel threadModel = new ThreadModel();
    threadModel.setTitle(title);
    threadModel.setText(text);
    this.courseMeetingRepository.createThread(threadModel);
  }
}
