package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.repository.CourseCreationRepository;

/**Class transfers data from CourseCreationRepository to CreateCourseFragment and viceversa.**/

public class CreateCourseViewModel extends ViewModel {

  private static final String TAG = "CreateCourseViewModel";

  private CourseCreationRepository courseCreationRepository;
  private final MutableLiveData<String> courseId = new MutableLiveData<String>();


  public CreateCourseViewModel() {
    courseCreationRepository = new CourseCreationRepository();
  }

  public void createCourse(String courseName, String courseDescription) {
    courseCreationRepository.createNewCourse(courseName, courseDescription);
  }

  public String getCourseId() {
    return courseCreationRepository.getCourseId();
  }

  public void setCourseId(String courseId) {
    courseCreationRepository.setCourseId(courseId);
  }
}
