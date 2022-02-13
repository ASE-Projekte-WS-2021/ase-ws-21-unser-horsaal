package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.repository.CreateCourseRepository;

/**Class transfers data from CreateCourseRepository to CreateCourseFragment and viceversa.**/
public class CreateCourseViewModel extends ViewModel {

  private static final String TAG = "CreateCourseViewModel";

  private CreateCourseRepository createCourseRepository;

  private MutableLiveData<UserCourse> userCourse;

  /** Initialization of the CreatCourseViewModel. */
  public void init() {
    if (this.userCourse != null) {
      return;
    }
    this.createCourseRepository = CreateCourseRepository.getInstance();
    this.userCourse = this.createCourseRepository.getUserCourse();
  }

  public LiveData<UserCourse> getUserCourse() {
    return this.userCourse;
  }

  public void createCourse(String courseName, String courseDescription) {
    this.createCourseRepository.createNewCourse(courseName, courseDescription);
  }
}
