package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.EnterCourseRepository;

/**Class EnterCourseViewModel.**/
public class EnterCourseViewModel extends ViewModel {

  private static final String TAG = "EnterCourseViewModel";

  private EnterCourseRepository enterCourseRepository;

  private MutableLiveData<CourseModel> courseModel;
  private MutableLiveData<String> courseId;

  /** Initialize the EnterCourseViewModel. */
  public void init() {
    if (this.courseModel != null) {
      return;
    }
    this.enterCourseRepository = EnterCourseRepository.getInstance();
    this.courseModel = this.enterCourseRepository.getCourse();
    this.courseId = this.enterCourseRepository.getCourseId();
  }

  public LiveData<CourseModel> getCourse() {
    return this.courseModel;
  }

  public LiveData<String> getCourseId() {
    return this.courseId;
  }

  public void checkCode(String code) {
    this.enterCourseRepository.checkCode(code);
  }

  public void enterCourse() {
    this.enterCourseRepository.isUserInCourse(courseModel.getValue().getKey());
  }

}