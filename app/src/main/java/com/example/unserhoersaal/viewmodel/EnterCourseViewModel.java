package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.EnterCourseRepository;
import com.example.unserhoersaal.utils.PreventDoubleClick;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;

/**Class EnterCourseViewModel.**/
public class EnterCourseViewModel extends ViewModel {

  private static final String TAG = "EnterCourseViewModel";

  private EnterCourseRepository enterCourseRepository;
  private StateLiveData<CourseModel> courseModelStateLiveData;
  private StateLiveData<CourseModel> enteredCourse;
  public StateLiveData<CourseModel> courseIdInputState;


  /** Initialize the EnterCourseViewModel. */
  public void init() {
    if (this.courseModelStateLiveData != null) {
      return;
    }
    this.enterCourseRepository = EnterCourseRepository.getInstance();
    this.courseModelStateLiveData = this.enterCourseRepository.getCourse();
    this.enteredCourse = this.enterCourseRepository.getEnteredCourse();
    this.courseIdInputState = new StateLiveData<>();
    this.courseIdInputState.postCreate(new CourseModel());
  }

  public StateLiveData<CourseModel> getCourse() {
    return this.courseModelStateLiveData;
  }

  public StateLiveData<CourseModel> getEnteredCourse() {
    return this.enteredCourse;
  }

  public StateLiveData<CourseModel> getCourseIdInputState() {
    return this.courseIdInputState;
  }

  /** Reset the entered data after joining the course. */
  public void resetEnterCourseId() {
    this.courseIdInputState.postCreate(new CourseModel());
  }

  public void resetEnterCourse() {
    this.courseModelStateLiveData.postCreate(null);
    this.enteredCourse.postCreate(null);
  }

  public void setLiveDataComplete() {
    this.courseModelStateLiveData.postComplete();
    this.enteredCourse.postComplete();
  }

  /** checks if the entered codemapping matches our policy and passes it on to the repo.
   * also removes whitespace and hyphen which are just for readability. */
  public void checkCode() {
    this.courseIdInputState.postLoading();

    CourseModel courseModel = Validation.checkStateLiveData(this.courseIdInputState, TAG);
    if (courseModel == null) {
      this.courseModelStateLiveData.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (Validation.emptyString(courseModel.getCodeMapping())) {
      this.courseIdInputState.postError(
              new Error(Config.DATABINDING_CODEMAPPING_NULL), ErrorTag.CODEMAPPING);
    } else if (!Validation.stringHasPattern(
            courseModel.getCodeMapping(), Config.REGEX_PATTERN_CODE_MAPPING)) {
      this.courseIdInputState.postError(
              new Error(Config.DATABINDING_CODEMAPPING_WRONG_PATTERN), ErrorTag.CODEMAPPING);
    } else {
      String codeMapping = courseModel.getCodeMapping();
      codeMapping = codeMapping.toUpperCase();
      codeMapping = codeMapping.replace(" ", "");
      codeMapping = codeMapping.replace("-", "");

      this.courseIdInputState.postComplete();
      this.enterCourseRepository.checkCode(codeMapping);
    }
  }

  /** Checks user input before assigning user to the course. */
  public void enterCourse() {
    if(PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.enteredCourse.postLoading();

    CourseModel courseModel = Validation.checkStateLiveData(this.courseModelStateLiveData, TAG);
    if (courseModel == null) {
      this.enteredCourse.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (Validation.emptyString(courseModel.getCodeMapping())) {
      this.enteredCourse.postError(
              new Error(Config.DATABINDING_CODEMAPPING_NULL), ErrorTag.VM);
    } else if (!Validation.stringHasPattern(
            courseModel.getCodeMapping(), Config.REGEX_PATTERN_CODE_MAPPING)) {
      this.enteredCourse.postError(
              new Error(Config.DATABINDING_CODEMAPPING_WRONG_PATTERN), ErrorTag.VM);
    } else {
      this.enteredCourse.postComplete();
      this.enterCourseRepository.isUserInCourse(courseModel);
    }
  }

}