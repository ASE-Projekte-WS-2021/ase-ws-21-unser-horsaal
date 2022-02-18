package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.repository.CreateCourseRepository;

import java.util.Random;

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
    String codeMapping = this.getCodeMapping();
    courseModel.setCodeMapping(codeMapping);
    this.createCourseRepository.createNewCourse(courseModel);
  }

  //https://www.codegrepper.com/code-examples/java/how+to+generate+random+letters+in+java
  private String getCodeMapping() {
    String chars = Config.CHARS;
    Random random = new Random();
    StringBuilder sb = new StringBuilder(Config.CODE_LENGTH);
    for (int i = 0; i < Config.CODE_LENGTH; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }
}
