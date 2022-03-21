package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CreateCourseRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.Random;

/**Class transfers data from CreateCourseRepository to CreateCourseFragment and viceversa.**/
public class CreateCourseViewModel extends ViewModel {

  private static final String TAG = "CreateCourseViewModel";

  private CreateCourseRepository createCourseRepository;
  private StateLiveData<CourseModel> currentCourseRepoState;
  public StateLiveData<CourseModel> courseModelInputState = new StateLiveData<>();

  /** Initialization of the CreateCourseViewModel. */
  public void init() {
    if (this.currentCourseRepoState != null) {
      return;
    }
    this.createCourseRepository = CreateCourseRepository.getInstance();
    this.currentCourseRepoState = this.createCourseRepository.getCurrentCourseRepoState();
    this.courseModelInputState.postCreate(new CourseModel());
  }

  public StateLiveData<CourseModel> getCurrentCourseRepoState() {
    return this.currentCourseRepoState;
  }

  public void resetCourseModelInput() {
    this.courseModelInputState.postCreate(new CourseModel());
    this.currentCourseRepoState.postCreate(null);
  }

  /** Create a new course. */
  public void createCourse() {
    this.currentCourseRepoState.postLoading();

    CourseModel courseModel = Validation.checkStateLiveData(this.courseModelInputState, TAG);
    if (courseModel == null) {
      Log.e(TAG, Config.CREATE_COURSE_NO_COURES_MODEL);
      this.currentCourseRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (this.validateTitle(courseModel.getTitle())
            && this.validateDescription(courseModel.getDescription())
            && this.validateInstitution(courseModel.getInstitution())) {
      return;
    }

    courseModel.setCodeMapping(this.getCodeMapping());

    this.courseModelInputState.postCreate(new CourseModel());
    this.createCourseRepository.createNewCourse(courseModel);
  }

  private boolean validateTitle(String title) {
    if (title == null) {
      Log.d(TAG, Config.CREATE_COURSE_NO_TITLE);
      this.currentCourseRepoState.postError(new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.TITLE);
      return true;
    } else if (!Validation.stringHasPattern(title, Config.REGEX_PATTERN_TITLE)) {
      Log.d(TAG, Config.CREATE_COURSE_WRONG_TITLE_PATTERN);
      this.currentCourseRepoState.postError(new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.TITLE);
      return true;
    }
    return false;
  }
  private boolean validateDescription(String description) {
    if (description == null) {
      Log.d(TAG, Config.CREATE_COURSE_NO_DESCRIPTION);
      this.currentCourseRepoState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.DESCRIPTION);
      return true;
    } else if (!Validation.stringHasPattern(
            description, Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, Config.CREATE_COURSE_WRONG_DESCRIPTION_PATTERN);
      this.currentCourseRepoState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.DESCRIPTION);
      return true;
    }
    return false;
  }
  private boolean validateInstitution(String institution) {
    if (institution == null) {
      Log.d(TAG, Config.CREATE_COURSE_NO_INSTITUTION);
      this.currentCourseRepoState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.INSTITUTION);
      return true;
    } else if (!Validation.stringHasPattern(
            institution, Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, Config.CREATE_COURSE_WRONG_INSTITUTION_PATTERN);
      this.currentCourseRepoState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.INSTITUTION);
      return true;
    }
    return false;
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
