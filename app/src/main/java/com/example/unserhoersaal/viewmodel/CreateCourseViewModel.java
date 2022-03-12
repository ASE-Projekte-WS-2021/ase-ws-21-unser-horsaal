package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CreateCourseRepository;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.Random;

/**Class transfers data from CreateCourseRepository to CreateCourseFragment and viceversa.**/
public class CreateCourseViewModel extends ViewModel {

  private static final String TAG = "CreateCourseViewModel";

  private CreateCourseRepository createCourseRepository;
  private StateLiveData<CourseModel> courseModel;
  public StateLiveData<CourseModel> courseModelInputState;

  /** Initialization of the CreateCourseViewModel. */
  public void init() {
    if (this.courseModel != null) {
      return;
    }
    this.createCourseRepository = CreateCourseRepository.getInstance();
    this.courseModel = this.createCourseRepository.getUserCourse();
    this.courseModelInputState.postValue(new StateData<>(new CourseModel()));
  }

  public StateLiveData<CourseModel> getCourseModel() {
    return this.courseModel;
  }

  public void resetCourseModelInput() {
    this.courseModelInputState.postValue(new StateData<>(new CourseModel()));
    this.courseModel.postValue(new StateData<>(null));
  }

  /** Create a new course. */
  public void createCourse() {
    CourseModel courseModel = Validation.checkStateLiveData(this.courseModelInputState, TAG);
    if (courseModel == null) {
      Log.e(TAG, "courseModel is null.");
      return;
    }

    if (courseModel.getTitle() == null) {
      Log.d(TAG, "title is null.");
      this.courseModelInputState.postError(new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.stringHasPattern(courseModel.getTitle(), Config.REGEX_PATTERN_TITLE)) {
      Log.d(TAG, "title has wrong pattern.");
      this.courseModelInputState.postError(new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.VM);
      return;
    }
    if (courseModel.getDescription() == null) {
      Log.d(TAG, "description is null.");
      this.courseModelInputState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.stringHasPattern(courseModel.getDescription(), Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, "description has wrong pattern.");
      this.courseModelInputState.postError(new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.VM);
      return;
    }
    if (courseModel.getInstitution() == null) {
      Log.d(TAG, "institution is null.");
      this.courseModelInputState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.stringHasPattern(courseModel.getInstitution(), Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, "institution has wrong pattern.");
      this.courseModelInputState.postError(new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.VM);
      return;
    }

    courseModel.setCodeMapping(this.getCodeMapping());

    this.courseModelInputState.postComplete();
    this.createCourseRepository.createNewCourse(courseModel);
  }

  //https://www.codegrepper.com/code-examples/java/how+to+generate+random+letters+in+java
  private String getCodeMapping() {
    String chars = Config.CODE_MAPPING_ALLOWED_CHARACTERS;
    Random random = new Random();
    StringBuilder sb = new StringBuilder(Config.CODE_MAPPING_LENGTH);
    for (int i = 0; i < Config.CODE_MAPPING_LENGTH; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }

}
