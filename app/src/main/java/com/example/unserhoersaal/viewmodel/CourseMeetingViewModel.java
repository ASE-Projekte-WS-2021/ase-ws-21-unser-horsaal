package com.example.unserhoersaal.viewmodel;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.CourseMeetingRepository;

import java.util.List;

public class CourseMeetingViewModel extends ViewModel {

  private static final String TAG = "CourseMeetingViewModel";

  private CourseMeetingRepository courseMeetingRepository;

  private MutableLiveData<String> meetingId = new MutableLiveData<>();
  private MutableLiveData<List<ThreadModel>> threads;

  public void init() {
    if (this.threads != null) {
      return;
    }

    this.courseMeetingRepository = CourseMeetingRepository.getInstance();
    this.meetingId = this.courseMeetingRepository.getMeetingId();

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

  public void setMeetingId(String meetingId) {
    this.courseMeetingRepository.setMeetingId(meetingId);
  }
}
