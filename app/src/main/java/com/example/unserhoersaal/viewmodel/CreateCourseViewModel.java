package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CreateCourseRepository;
import java.util.Random;

/**Class transfers data from CreateCourseRepository to CreateCourseFragment and viceversa.**/
public class CreateCourseViewModel extends ViewModel {

  private static final String TAG = "CreateCourseViewModel";

  private CreateCourseRepository createCourseRepository;
  private MutableLiveData<CourseModel> courseModel;
  public MutableLiveData<CourseModel> courseModelInput;

  /** Initialization of the CreatCourseViewModel. */
  public void init() {
    if (this.courseModel != null) {
      return;
    }
    this.createCourseRepository = CreateCourseRepository.getInstance();
    this.courseModel = this.createCourseRepository.getUserCourse();
    this.courseModelInput = new MutableLiveData<>(new CourseModel());
  }

  public LiveData<CourseModel> getCourseModel() {
    return this.courseModel;
  }

  public void resetCourseModelInput() {
    this.courseModelInput.setValue(new CourseModel());
    this.courseModel.setValue(null);
  }

  /** Create a new course. */
  public void createCourse() {
    //TODO: status data to view
    if (this.courseModelInput.getValue() == null) return;

    CourseModel courseModel = this.courseModelInput.getValue();
    String codeMapping = this.getCodeMapping();
    courseModel.setCodeMapping(codeMapping);

    //TODO: status data to view
    if (courseModel.getTitle() == null) return;
    if (courseModel.getDescription() == null) return;
    if (courseModel.getInstitution() == null) return;

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
