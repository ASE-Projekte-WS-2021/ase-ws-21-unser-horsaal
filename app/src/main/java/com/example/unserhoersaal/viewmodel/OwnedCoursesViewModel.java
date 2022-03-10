package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.OwnedCoursesRepository;
import java.util.List;

/** ViewModel for the OwnedCoursesFragment. */
public class OwnedCoursesViewModel extends ViewModel {

  private static final String TAG = "OwnedCoursesViewModel";

  private OwnedCoursesRepository ownedCoursesRepository;
  private MutableLiveData<List<CourseModel>> ownedCourses;

  /** Initialize the ViewModel. */
  public void init() {
    if (this.ownedCourses != null) {
      return;
    }

    this.ownedCoursesRepository = OwnedCoursesRepository.getInstance();
    this.ownedCourses = this.ownedCoursesRepository.getOwnedCourses();
  }

  public LiveData<List<CourseModel>> getOwnedCourses() {
    return this.ownedCourses;
  }

}
