package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
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

  public void loadCourses() {
    this.coursesRepository.loadUserCourses();
  }

}