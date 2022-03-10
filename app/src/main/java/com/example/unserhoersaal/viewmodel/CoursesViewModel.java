package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CoursesRepository;
import com.example.unserhoersaal.utils.StateLiveData;

import java.util.List;

/** This class is the ViewModel for the signed up courses. */
public class CoursesViewModel extends ViewModel {

  private static final String TAG = "CoursesViewModel";

  private CoursesRepository coursesRepository;
  private StateLiveData<List<CourseModel>> userCourses;

  /** Initializes the database access. */
  public void init() {
    if (this.userCourses != null) {
      return;
    }
    this.coursesRepository = CoursesRepository.getInstance();
    this.userCourses = this.coursesRepository.getUserCourses();
  }

  public StateLiveData<List<CourseModel>> getUserCourses() {
    return this.userCourses;
  }

}