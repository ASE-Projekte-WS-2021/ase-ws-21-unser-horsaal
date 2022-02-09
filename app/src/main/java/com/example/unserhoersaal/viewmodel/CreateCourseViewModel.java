package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.repository.CreateCourseRepository;
import com.example.unserhoersaal.views.CreateCourseFragment;

/**Class transfers data from CreateCourseRepository to CreateCourseFragment and viceversa.**/

public class CreateCourseViewModel extends ViewModel {

  private static final String TAG = "CreateCourseViewModel";

  private CreateCourseRepository createCourseRepository;
  private final MutableLiveData<String> courseId = new MutableLiveData<String>();


  public CreateCourseViewModel() {
    createCourseRepository = new CreateCourseRepository();
  }

  public void createCourse(String courseName, String courseDescription, CreateCourseFragment listener) {
    createCourseRepository.createNewCourse(courseName, courseDescription, listener);
  }

  public String getCourseId() {
    return createCourseRepository.getCourseId();
  }

  public void setCourseId(String courseId) {
    createCourseRepository.setCourseId(courseId);
  }
}
