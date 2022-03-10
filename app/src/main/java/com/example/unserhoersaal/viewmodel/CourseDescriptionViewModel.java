package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CourseDescriptionRepository;
import com.example.unserhoersaal.utils.StateLiveData;

/** ViewModel for the CourseDescriptionFragment. */
public class CourseDescriptionViewModel extends ViewModel {

  private static final String TAG = "courseDescriptionViewModel";
  private CourseDescriptionRepository courseDescriptionRepository;
  private StateLiveData<String> courseId = new StateLiveData<>();
  private StateLiveData<CourseModel> courseModelStateLiveData;

  /** Initialize the ViewModel. */
  public void init() {
    if (this.courseModelStateLiveData != null) {
      return;
    }

    this.courseDescriptionRepository = CourseDescriptionRepository.getInstance();
    this.courseId = this.courseDescriptionRepository.getCourseId();
    this.courseModelStateLiveData = this.courseDescriptionRepository.getCourseModel();
  }

  public StateLiveData<String> getCourseId() {
    return this.courseId;
  }

  public StateLiveData<CourseModel> getCourseModel() {
    return this.courseModelStateLiveData;
  }

  public void setCourseId(String courseId) {
    this.courseDescriptionRepository.setCourseId(courseId);
  }

  public void unregisterFromCourse() {
    //TODO: assert != null
    String id = courseId.getValue().getData();
    this.courseDescriptionRepository.unregisterFromCourse(id);
  }

}
