package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.repository.CourseHistoryRepository;

import java.util.List;

public class CourseHistoryViewModel extends ViewModel {

  private static final String TAG = "CourseHistoryViewModel";

  private CourseHistoryRepository courseHistoryRepository;

  private MutableLiveData<String> courseId = new MutableLiveData<>();
  private MutableLiveData<List<MeetingsModel>> meetings;

  public void init(){
    if (this.meetings != null) {
      return;
    }

    this.courseHistoryRepository = CourseHistoryRepository.getInstance();
    this.courseId = this.courseHistoryRepository.getCourseId();

    if (this.courseId.getValue() != null) {
      this.meetings = this.courseHistoryRepository.getMeetings();
    }
  }

  public LiveData<List<MeetingsModel>> getMeetings() {
    return this.meetings;
  }

  public LiveData<String> getCourseId() {
    return this.courseId;
  }

  public void setCourseId(String courseId) {
    this.courseHistoryRepository.setCourseId(courseId);
  }

}
