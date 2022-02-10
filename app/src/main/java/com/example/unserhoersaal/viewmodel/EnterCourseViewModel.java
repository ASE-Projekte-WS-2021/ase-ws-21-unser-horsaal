package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.repository.EnterCourseRepository;
import com.example.unserhoersaal.views.EnterCourseFragment;

/**Class EnterCourseViewModel.**/

public class EnterCourseViewModel extends ViewModel {

  private static final String TAG = "EnterCourseViewModel";

  private EnterCourseRepository enterCourseRepository;
  private MutableLiveData<String> courseId;

  public EnterCourseViewModel() {
    enterCourseRepository = new EnterCourseRepository();
    courseId = enterCourseRepository.getCourseId();
  }

  public void checkCode(String id){
    enterCourseRepository.checkCode(id);
  }

  public LiveData<String> getCourseId() {
    return courseId;
  }

  public void addUserToCourse(String id, String title) {
    enterCourseRepository.saveCourseInUser(id, title);
  }

  /*public LiveData<Config.ThreeState> saveUserCourses(String courseId) {
    return enterCourseRepository.saveUserCourses(courseId);
  }*/

}