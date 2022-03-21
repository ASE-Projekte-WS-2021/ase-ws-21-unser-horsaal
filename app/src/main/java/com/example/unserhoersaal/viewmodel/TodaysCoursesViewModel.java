package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.TodaysCoursesRepository;
import java.util.List;

/** ViewModel for TodayCoursesFragment. */
public class TodaysCoursesViewModel extends ViewModel {

  private static final String TAG = "TodaysCoursesViewModel";

  private TodaysCoursesRepository todaysCoursesRepository;
  private MutableLiveData<List<CourseModel>> allCoursesRepoState;

  /** Initialize the ViewModel. */
  public void init() {
    if (this.allCoursesRepoState != null) {
      return;
    }
    this.todaysCoursesRepository = TodaysCoursesRepository.getInstance();
    this.allCoursesRepoState = this.todaysCoursesRepository.getAllCoursesRepoState();
  }

  public LiveData<List<CourseModel>> getAllCoursesRepoState() {
    return this.allCoursesRepoState;
  }

  public void loadTodaysCourses() {
    this.todaysCoursesRepository.loadTodaysCourses();
  }
}
