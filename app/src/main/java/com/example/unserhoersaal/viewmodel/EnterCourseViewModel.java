package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.EnterCourseRepository;

/**Class EnterCourseViewModel.**/
public class EnterCourseViewModel extends ViewModel {

  private static final String TAG = "EnterCourseViewModel";

  private EnterCourseRepository enterCourseRepository;
  private MutableLiveData<CourseModel> courseModel;
  private MutableLiveData<String> courseId;
  public MutableLiveData<String> enteredCourseId;
  public MutableLiveData<Boolean> toggleConfirmation;

  /** Initialize the EnterCourseViewModel. */
  public void init() {
    if (this.courseModel != null) {
      return;
    }
    this.enterCourseRepository = EnterCourseRepository.getInstance();
    this.courseModel = this.enterCourseRepository.getCourse();
    this.courseId = this.enterCourseRepository.getCourseId();
    this.enteredCourseId = new MutableLiveData<>();
    this.toggleConfirmation = new MutableLiveData<>(false);
  }

  public LiveData<CourseModel> getCourse() {
    return this.courseModel;
  }

  public LiveData<String> getCourseId() {
    return this.courseId;
  }

  /** Reset the entered data after joining the course. */
  public void resetEnterCourseId() {
    this.enteredCourseId.setValue(null);
    this.courseModel.setValue(null);
    this.courseId.setValue(null);
    this.toggleConfirmation.setValue(false);
  }

  public void checkCode() {
    String id = enteredCourseId.getValue();
    if (id == null) return;
    this.enterCourseRepository.checkCode(id);
  }

  /** Join a course. */
  public void enterCourse() {
    if (courseModel.getValue() != null) {
      this.enterCourseRepository.isUserInCourse(courseModel.getValue().getKey());
    }
    //TODO: else error?
  }

  public void toggleConfirmationWindow() {
    if (this.toggleConfirmation.getValue() == null) return;
    this.toggleConfirmation.setValue(!this.toggleConfirmation.getValue());
  }

}