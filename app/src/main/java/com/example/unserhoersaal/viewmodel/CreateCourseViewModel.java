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
      Log.d(TAG, "CreateCourseVM>createCourse courseModel is null.");
      return;
    }

    if (courseModel.getTitle() == null) {
      Log.d(TAG, "CreateCourseVM>createCourse: title is null.");
      this.courseModelInputState.postError(new Error(Config.VM_TITLE_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.titleHasPattern(courseModel.getTitle())) {
      Log.d(TAG, "CreateCourseVM>createCourse: title has wrong pattern.");
      this.courseModelInputState.postError(new Error(Config.VM_TITLE_WRONG_PATTERN), ErrorTag.VM);
      return;
    }
    if (courseModel.getDescription() == null) {
      Log.d(TAG, "CreateCourseVM>createCourse: description is null.");
      this.courseModelInputState.postError(new Error(Config.VM_TEXT_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.textHasPattern(courseModel.getDescription())) {
      Log.d(TAG, "CreateCourseVM>createCourse: description has wrong pattern.");
      this.courseModelInputState.postError(new Error(Config.VM_TEXT_WRONG_PATTERN), ErrorTag.VM);
      return;
    }
    if (courseModel.getInstitution() == null) {
      Log.d(TAG, "CreateCourseVM>createCourse: institution is null.");
      this.courseModelInputState.postError(new Error(Config.VM_INSTITUTION_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.institutionHasPattern(courseModel.getInstitution())) {
      Log.d(TAG, "CreateCourseVM>createCourse: institution has wrong pattern.");
      this.courseModelInputState.postError(new Error(Config.VM_INSTITUTION_WRONG_PATTERN), ErrorTag.VM);
      return;
    }

    courseModel.setCodeMapping(this.getCodeMapping());

    this.courseModelInputState.postComplete();
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
