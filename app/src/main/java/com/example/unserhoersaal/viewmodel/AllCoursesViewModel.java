package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.AllCoursesRepository;
import com.example.unserhoersaal.utils.StateLiveData;

import java.util.List;

public class AllCoursesViewModel extends ViewModel {

  private AllCoursesRepository allCoursesRepository;
  private StateLiveData<List<CourseModel>> allCourses;

  private static final String TAG = "AllCoursesViewModel";
  public void init() {
    if (this.allCourses != null) {
      return;
    }
    this.allCoursesRepository = AllCoursesRepository.getInstance();
    this.allCourses = this.allCoursesRepository.getAllCourses();
  }

  public StateLiveData<List<CourseModel>> getAllCourses() {
    return this.allCourses;
  }

  public void setUserId() {
    this.allCoursesRepository.setUserId();
  }
}
