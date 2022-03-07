package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.CourseMeetingRepository;
import java.util.List;

/** ViewModel for the CourseMeetingFragment. */
public class CourseMeetingViewModel extends ViewModel {

  private static final String TAG = "CourseMeetingViewModel";

  private CourseMeetingRepository courseMeetingRepository;

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
    if (this.threadModelInput.getValue() == null) return;

    //TODO: error -> view
    ThreadModel threadModel = this.threadModelInput.getValue();
    if (threadModel.getTitle() == null) return;
    if (threadModel.getText() == null) return;

    this.courseMeetingRepository.createThread(threadModel);
  }
}
