package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.EnterCourseRepository;
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
  }

  public StateLiveData<CourseModel> getCourse() {
    return this.courseModelStateLiveData;
  }

  public StateLiveData<CourseModel> getEnteredCourse() {
    return this.enteredCourse;
  }

  /** Reset the entered data after joining the course. */
  public void resetEnterCourseId() {
    this.courseIdInputState.postValue(new StateData<>(new CourseModel()));
  }

  public void resetEnterCourse() {
    this.courseModelStateLiveData.postValue(new StateData<>(null));
    this.enteredCourse.postValue(new StateData<>(null));
  }

  /** JavaDoc for this method. */
  public void checkCode() {
    CourseModel courseModel = Validation.checkStateLiveData(this.courseIdInputState, TAG);
    if (courseModel == null) {
      Log.e(TAG, "courseModel is null.");
      return;
    }

    if (courseModel.getCodeMapping()== null) {
      this.courseIdInputState.postError(new Error(Config.DATABINDING_CODEMAPPING_NULL), ErrorTag.VM);
      Log.d(TAG, "codeMapping is null.");
      return;
    } else if (!Validation.stringHasPattern(courseModel.getCodeMapping(), Config.REGEX_PATTERN_CODE_MAPPING)) {
      this.courseIdInputState.postError(new Error(Config.DATABINDING_CODEMAPPING_WRONG_PATTERN), ErrorTag.VM);
      Log.d(TAG, "codeMapping has wrong pattern.");
      return;
    }

    String codeMapping = courseModel.getCodeMapping();
    codeMapping = codeMapping.toUpperCase();
    codeMapping = codeMapping.replace(" ", "");
    codeMapping = codeMapping.replace("-", "");

    this.courseIdInputState.postComplete();
    this.enterCourseRepository.checkCode(codeMapping);
  }

  /** JavaDoc for this method. */
  public void enterCourse() {
    CourseModel courseModel = Validation.checkStateLiveData(this.courseModelStateLiveData, TAG);
    if (courseModel == null) {
      Log.e(TAG, "courseModel is null.");
      return;
    }

    if (courseModel.getCodeMapping() == null) {
      this.courseModelStateLiveData.postError(new Error(Config.DATABINDING_CODEMAPPING_NULL), ErrorTag.VM);
      Log.d(TAG, "codeMapping is null.");
      return;
    }
    else if (!Validation.stringHasPattern(courseModel.getCodeMapping(), Config.REGEX_PATTERN_CODE_MAPPING)) {
      this.courseModelStateLiveData.postError(new Error(Config.DATABINDING_CODEMAPPING_WRONG_PATTERN), ErrorTag.VM);
      Log.d(TAG, "codeMapping has wrong pattern.");
      return;
    }

    this.courseModelStateLiveData.postComplete();
    this.enterCourseRepository.isUserInCourse(courseModel);
  }

}