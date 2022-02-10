package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.repository.CreateCourseRepository;
import com.example.unserhoersaal.views.CreateCourseFragment;

/**Class transfers data from CreateCourseRepository to CreateCourseFragment and viceversa.**/

public class CreateCourseViewModel extends ViewModel {

  private static final String TAG = "CreateCourseViewModel";

  private CreateCourseRepository createCourseRepository;
  /*private MutableLiveData<String> courseId = new MutableLiveData<String>();*/
  private MutableLiveData<UserCourse> userCourse;

  public void init(){
    if (userCourse != null){
      return;
    }
    createCourseRepository = CreateCourseRepository.getInstance();
    userCourse = createCourseRepository.getUserCourse();
  }

  /*public CreateCourseViewModel() {
    createCourseRepository = new CreateCourseRepository();
    userCourse = createCourseRepository.getUserCourse();
  }*/

  public void createCourse(String courseName, String courseDescription) {
    createCourseRepository.createNewCourse(courseName, courseDescription);
  }

  public LiveData<UserCourse> getUserCourse() {
    return userCourse;
  }

  /*public String getCourseId() {
    return createCourseRepository.getCourseId();
  }*/

  /*public void setCourseId(String courseId) {
    createCourseRepository.setCourseId(courseId);
  }*/
}
