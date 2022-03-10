package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CoursesRepository;
import java.util.List;

/** This class is the ViewModel for the signed up courses. */
public class CoursesViewModel extends ViewModel {

  private static final String TAG = "CoursesViewModel";

  private CoursesRepository coursesRepository;
  private MutableLiveData<List<CourseModel>> userCourses;

  /** Initializes the database access. */
  public void init() {
    if (this.userCourses != null) {
      return;
    }
    this.coursesRepository = CoursesRepository.getInstance();
    this.userCourses = this.coursesRepository.getUserCourses();
  }

  public LiveData<List<CourseModel>> getUserCourses() {
    return this.userCourses;
  }

  /** Determine the name of the tabs. */
  public String getTabTitle(int position) {
    switch (position) {
      case Config.TAB_TODAY:
        return Config.TAB_TODAY_NAME;
      case Config.TAB_ALL:
        return Config.TAB_ALL_NAME;
      case Config.TAB_OWNED:
        return Config.TAB_OWNED_NAME;
      default:
        return null;
    }
  }

}