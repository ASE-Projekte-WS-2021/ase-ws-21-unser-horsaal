package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.DatabaseCourseCreation;

/**Class transfers data from DatabaseCourseCreation to CreateCourseFragment and viceversa.**/

public class CreateCourseViewModel extends ViewModel {
  private DatabaseCourseCreation databaseCourseCreation;
  private final MutableLiveData<String> courseId = new MutableLiveData<String>();


  public CreateCourseViewModel() {
    databaseCourseCreation = new DatabaseCourseCreation();
  }

  public void createCourse(String courseName, String courseDescription) {
    databaseCourseCreation.createNewCourse(courseName, courseDescription);
  }

  public String getCourseId() {
    return databaseCourseCreation.getCourseId();
  }

  public void setCourseId(String courseId) {
    databaseCourseCreation.setCourseId(courseId);
  }
}
