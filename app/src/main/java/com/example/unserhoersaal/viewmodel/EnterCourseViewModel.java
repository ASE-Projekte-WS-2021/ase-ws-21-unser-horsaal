package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.EnterCourseRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;

/**Class EnterCourseViewModel.**/
public class EnterCourseViewModel extends ViewModel {

  private static final String TAG = "EnterCourseViewModel";

  private EnterCourseRepository enterCourseRepository;
  private StateLiveData<CourseModel> foundCourseRepoState;
  private StateLiveData<CourseModel> enteredCourseRepoState;
  public StateLiveData<CourseModel> courseIdInputState = new StateLiveData<>();


  /** Initialize the EnterCourseViewModel. */
  public void init() {
    if (this.foundCourseRepoState != null) {
      return;
    }
    this.enterCourseRepository = EnterCourseRepository.getInstance();
    this.foundCourseRepoState = this.enterCourseRepository.getFoundCourseRepoState();
    this.enteredCourseRepoState = this.enterCourseRepository.getEnteredCourseRepoState();
    this.courseIdInputState.postCreate(new CourseModel());
  }

  public StateLiveData<CourseModel> getFoundCourseRepoState() {
    return this.foundCourseRepoState;
  }

  public StateLiveData<CourseModel> getEnteredCourseRepoState() {
    return this.enteredCourseRepoState;
  }

  public StateLiveData<CourseModel> getCourseIdInputState() {
    return this.courseIdInputState;
  }

  /** Reset the entered data after joining the course. */
  public void resetEnterCourseId() {
    this.courseIdInputState.postCreate(new CourseModel());
  }

  public void resetEnterCourse() {
    this.foundCourseRepoState.postCreate(null);
    this.enteredCourseRepoState.postCreate(null);
  }

  /** JavaDoc for this method. */
  public void checkCode() {
    this.foundCourseRepoState.postLoading();

    CourseModel courseModel = Validation.checkStateLiveData(this.courseIdInputState, TAG);
    if (courseModel == null) {
      Log.e(TAG, Config.ENTER_COURSE_NO_COURSE_MODEL);
      this.foundCourseRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (courseModel.getCodeMapping() == null) {
      this.foundCourseRepoState.postError(
              new Error(Config.DATABINDING_CODEMAPPING_NULL), ErrorTag.VM);
      Log.d(TAG, Config.ENTER_COURSE_NO_CODE_MAPPING);
      return;
    } else if (!Validation.stringHasPattern(
            courseModel.getCodeMapping(), Config.REGEX_PATTERN_CODE_MAPPING)) {
      this.foundCourseRepoState.postError(
              new Error(Config.DATABINDING_CODEMAPPING_WRONG_PATTERN), ErrorTag.VM);
      Log.d(TAG, Config.ENTER_COURSE_NO_CODE_MAPPING);
      return;
    }

    String codeMapping = courseModel.getCodeMapping();
    codeMapping = codeMapping.toUpperCase();
    codeMapping = codeMapping.replace(" ", "");
    codeMapping = codeMapping.replace("-", "");

    this.courseIdInputState.postCreate(new CourseModel());
    this.enterCourseRepository.checkCode(codeMapping);
  }

  /** JavaDoc for this method. */
  public void enterCourse() {
    CourseModel courseModel = Validation.checkStateLiveData(this.foundCourseRepoState, TAG);
    if (courseModel == null) {
      Log.e(TAG, Config.ENTER_COURSE_NO_COURSE_MODEL);
      return;
    }

    if (courseModel.getCodeMapping() == null) {
      this.foundCourseRepoState.postError(
              new Error(Config.DATABINDING_CODEMAPPING_NULL), ErrorTag.VM);
      Log.d(TAG, Config.ENTER_COURSE_NO_CODE_MAPPING);
      return;
    } else if (!Validation.stringHasPattern(
            courseModel.getCodeMapping(), Config.REGEX_PATTERN_CODE_MAPPING)) {
      this.foundCourseRepoState.postError(
              new Error(Config.DATABINDING_CODEMAPPING_WRONG_PATTERN), ErrorTag.VM);
      Log.d(TAG, Config.ENTER_COURSE_WRONG_CODE_MAPPING_PATTERN);
      return;
    }

    this.foundCourseRepoState.postCreate(new CourseModel());
    this.enterCourseRepository.isUserInCourse(courseModel);
  }

}