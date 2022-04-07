package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.AllCoursesRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import java.util.List;

/** ViewModel for AllCoursesFragment. Provides statuslivedata courses for tab "Alle".*/
public class AllCoursesViewModel extends ViewModel {

  private AllCoursesRepository allCoursesRepository;
  private StateLiveData<List<CourseModel>> allCourses;

  /** Initialises AllCoursesFragment. Connects to statuslivedata courses from AllCoursesRepository
   * to make them accessible in AllCoursesFragment and corresponding databinding.*/
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
