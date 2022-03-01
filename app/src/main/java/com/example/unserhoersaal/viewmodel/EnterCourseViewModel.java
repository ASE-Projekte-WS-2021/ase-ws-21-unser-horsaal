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
  private MutableLiveData<CourseModel> enteredCourse;
  public MutableLiveData<String> enteredCourseId;

  /** Initialize the EnterCourseViewModel. */
  public void init() {
    if (this.courseModel != null) {
      return;
    }
    this.enterCourseRepository = EnterCourseRepository.getInstance();
    this.courseModel = this.enterCourseRepository.getCourse();
    this.enteredCourse = this.enterCourseRepository.getEnteredCourse();
    this.enteredCourseId = new MutableLiveData<>();
  }

  public LiveData<CourseModel> getCourse() {
    return this.courseModel;
  }

  public LiveData<CourseModel> getEnteredCourse() {
    return this.enteredCourse;
  }

  /** Reset the entered data after joining the course. */
  public void resetEnterCourseId() {
    this.enteredCourseId.setValue(null);
    this.courseModel.setValue(null);
    this.enteredCourse.setValue(null);
  }

  public void checkCode() {
    String id = enteredCourseId.getValue();
    if (id != null) {
      id = id.toUpperCase();
      id = id.replace(" ", "");
      id = id.replace("-", "");
      this.enterCourseRepository.checkCode(id);
    }

  }

  public void enterCourse() {
    //TODO: send status data back to view on error
    if (courseModel.getValue() == null) return;
    if (courseModel.getValue().getKey() == null) return;
    this.enterCourseRepository.isUserInCourse(courseModel.getValue());
  }

}