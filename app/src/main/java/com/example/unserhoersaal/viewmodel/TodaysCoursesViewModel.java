package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.TodaysCoursesRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import java.util.List;

/** ViewModel for TodayCoursesFragment. */
public class TodaysCoursesViewModel extends ViewModel {

  private TodaysCoursesRepository todaysCoursesRepository;
  private StateLiveData<List<CourseModel>> todaysCourses;

  /** Initialize the ViewModel. */
  public void init() {
    if (this.todaysCourses != null) {
      return;
    }
    this.todaysCoursesRepository = TodaysCoursesRepository.getInstance();
    this.todaysCourses = this.todaysCoursesRepository.getTodaysCourses();
  }

  public StateLiveData<List<CourseModel>> getTodaysCourses() {
    return this.todaysCourses;
  }

  public void setUserId() {
    this.todaysCoursesRepository.setUserId();
  }
}
