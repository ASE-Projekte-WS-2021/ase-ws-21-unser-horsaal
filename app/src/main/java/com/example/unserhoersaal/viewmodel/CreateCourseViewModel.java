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
    if (userCourse != null) {
      return;
    }
    createCourseRepository = CreateCourseRepository.getInstance();
    userCourse = createCourseRepository.getUserCourse();
  }

  public void createCourse(String courseName, String courseDescription) {
    createCourseRepository.createNewCourse(courseName, courseDescription);
  }

  public LiveData<UserCourse> getUserCourse() {
    return userCourse;
  }

}
