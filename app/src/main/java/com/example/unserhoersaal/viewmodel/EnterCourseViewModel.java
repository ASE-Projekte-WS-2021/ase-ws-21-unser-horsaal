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

  public MutableLiveData<String> dataBindingCourseIdInput;
  private MutableLiveData<CourseModel> enteredCourse;

  /** Initialize the EnterCourseViewModel. */
  public void init() {
    if (this.courseModel != null) {
      return;
    }
    this.enterCourseRepository = EnterCourseRepository.getInstance();
    this.courseModel = this.enterCourseRepository.getCourse();
    this.dataBindingCourseIdInput = new MutableLiveData<>();
    this.enteredCourse = this.enterCourseRepository.getEnteredCourse();
  }

  public LiveData<CourseModel> getCourse() {
    return this.courseModel;
  }

  public LiveData<CourseModel> getEnteredCourse() {
    return this.enteredCourse;
  }

  /** Reset the entered data after joining the course. */
  public void resetEnterCourseId() {
    this.dataBindingCourseIdInput.setValue(null);
  }

  public void resetEnterCourse() {
    this.courseModel.setValue(null);
    this.enteredCourse.setValue(null);
  }

  /** JavaDoc for this method. */
  public void checkCode() {

    String id = dataBindingCourseIdInput.getValue();
    if (id != null) {
      id = id.toUpperCase();
      id = id.replace(" ", "");
      id = id.replace("-", "");
      this.enterCourseRepository.checkCode(id);
    }
  }

  /** JavaDoc for this method. */
  public void enterCourse() {
    //TODO: send status data back to view on error
    if (courseModel.getValue() == null) {
      return;
    }
    if (courseModel.getValue().getKey() == null) {
      return;
    }
    this.enterCourseRepository.isUserInCourse(courseModel.getValue());
  }

}