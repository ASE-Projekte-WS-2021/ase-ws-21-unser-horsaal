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
  private MutableLiveData<List<CourseModel>> todaysCourses;

  /** Initialize the ViewModel. */
  public void init() {
    if (this.todaysCourses != null) {
      return;
    }
    this.todaysCoursesRepository = TodaysCoursesRepository.getInstance();
    this.todaysCourses = this.todaysCoursesRepository.getTodaysCourses();
  }

  public LiveData<List<CourseModel>> getTodaysCourses() {
    return this.todaysCourses;
  }

  public void setUserId() {
    this.todaysCoursesRepository.setUserId();
  }
}
