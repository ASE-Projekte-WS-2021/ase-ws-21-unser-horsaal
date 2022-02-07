package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.repository.CoursesRepository;
import java.util.List;


/** This class is the viewmodel for the signed up courses. */
public class CoursesViewModel extends ViewModel {

  private static final String TAG = "CoursesViewModel";

  private CoursesRepository coursesRepository;
  private MutableLiveData<List<UserCourse>> userCourses;

  /** Initializes the database access. */
  public void init() {
    if (userCourses != null) {
      return;
    }
    coursesRepository = CoursesRepository.getInstance();
    userCourses = coursesRepository.getUserCourses();
  }

  public LiveData<List<UserCourse>> getUserCourses() {
    return userCourses;
  }

}