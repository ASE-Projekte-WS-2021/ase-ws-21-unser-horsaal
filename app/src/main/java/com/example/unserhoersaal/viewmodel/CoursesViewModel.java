package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CoursesRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import java.util.List;

/** This class is the ViewModel for the signed up courses. */
public class CoursesViewModel extends ViewModel {

  private static final String TAG = "CoursesViewModel";

  private CoursesRepository coursesRepository;
  private StateLiveData<List<CourseModel>> allCoursesRepoState;

  /** Initializes the database access. */
  public void init() {
    if (this.allCoursesRepoState != null) {
      return;
    }
    this.coursesRepository = CoursesRepository.getInstance();
    this.allCoursesRepoState = this.coursesRepository.getAllCoursesRepoState();
  }

  public StateLiveData<List<CourseModel>> getAllCoursesRepoState() {
    return this.allCoursesRepoState;
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

  public void loadUserCourses() {
    this.coursesRepository.loadUserCourses();
  }
}