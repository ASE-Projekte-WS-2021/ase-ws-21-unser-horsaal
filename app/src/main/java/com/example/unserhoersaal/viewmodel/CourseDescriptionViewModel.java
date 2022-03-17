package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CourseDescriptionRepository;

/** ViewModel for the CourseDescriptionFragment. */
public class CourseDescriptionViewModel extends ViewModel {

  private static final String TAG = "courseDescriptionViewModel";

  private CourseDescriptionRepository courseDescriptionRepository;

  private MutableLiveData<String> courseId = new MutableLiveData<>();
  private MutableLiveData<CourseModel> courseModel;

  /** Initialize the ViewModel. */
  public void init() {
    if (this.courseModel != null) {
      return;
    }

    this.courseDescriptionRepository = CourseDescriptionRepository.getInstance();
    this.courseId = this.courseDescriptionRepository.getCourseId();
    this.courseModel = this.courseDescriptionRepository.getCourseModel();
  }

  public LiveData<String> getCourseId() {
    return this.courseId;
  }

  public LiveData<CourseModel> getCourseModel() {
    return this.courseModel;
  }

  public void setCourseId(String courseId) {
    this.courseDescriptionRepository.setCourseId(courseId);
  }

  public void unregisterFromCourse() {
    String id = courseId.getValue();
    this.courseDescriptionRepository.unregisterFromCourse(id);
  }

  public void createQrCode(){
    
  }

}
