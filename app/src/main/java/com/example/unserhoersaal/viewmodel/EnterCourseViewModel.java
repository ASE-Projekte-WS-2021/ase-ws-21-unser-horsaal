package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.repository.EnterCourseRepository;

/**Class EnterCourseViewModel.**/
public class EnterCourseViewModel extends ViewModel {

  private static final String TAG = "EnterCourseViewModel";

  private EnterCourseRepository enterCourseRepository;
  private MutableLiveData<String> courseId;
  public MutableLiveData<String> enteredCourseId;

  /** Initialize the EnterCourseViewModel. */
  public void init() {
    if (this.courseId != null) {
      return;
    }
    this.enterCourseRepository = EnterCourseRepository.getInstance();
    this.courseId = this.enterCourseRepository.getCourseId();
    this.enteredCourseId = new MutableLiveData<>();
  }

  public LiveData<String> getCourseId() {
    return this.courseId;
  }

  public void checkCode() {
    String id = enteredCourseId.getValue();
    this.enterCourseRepository.checkCode(id);
  }

  public void addUserToCourse(String id, String title) {
    this.enterCourseRepository.saveCourseInUser(id, title);
  }
}