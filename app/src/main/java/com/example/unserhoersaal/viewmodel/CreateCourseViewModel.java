package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.repository.CreateCourseRepository;

/**Class transfers data from CreateCourseRepository to CreateCourseFragment and viceversa.**/
public class CreateCourseViewModel extends ViewModel {

  private static final String TAG = "CreateCourseViewModel";

  private CreateCourseRepository createCourseRepository;

  private MutableLiveData<CourseModel> courseModel;

  /** Initialization of the CreatCourseViewModel. */
  public void init() {
    if (this.courseModel != null) {
      return;
    }
    this.createCourseRepository = CreateCourseRepository.getInstance();
    this.courseModel = this.createCourseRepository.getUserCourse();
  }

  public LiveData<CourseModel> getCourseModel() {
    return this.courseModel;
  }

  public void createCourse(CourseModel courseModel) {
    this.createCourseRepository.createNewCourse(courseModel);
  }
}
