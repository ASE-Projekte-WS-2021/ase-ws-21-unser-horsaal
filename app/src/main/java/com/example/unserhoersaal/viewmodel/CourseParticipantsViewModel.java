package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.CourseParticipantsRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import java.util.List;

/** JavaDoc for this ViewModel. */
public class CourseParticipantsViewModel extends ViewModel {

  private static final String TAG = "courseParticipantsViewModel";

  private CourseParticipantsRepository courseParticipantsRepository;
  private StateLiveData<String> currentCourseIdRepoState = new StateLiveData<>();
  private StateLiveData<List<UserModel>> allUsersRepoState;

  /** Description for init method. */
  public void init() {
    if (this.allUsersRepoState != null) {
      return;
    }

    this.courseParticipantsRepository = CourseParticipantsRepository.getInstance();
    this.currentCourseIdRepoState = this.courseParticipantsRepository.getCurrentCourseIdRepoState();
    this.allUsersRepoState = this.courseParticipantsRepository.getAllUsersRepoState();
  }

  public StateLiveData<String> getCurrentCourseIdRepoState() {
    return this.currentCourseIdRepoState;
  }

  public StateLiveData<List<UserModel>> getAllUsersRepoState() {
    return this.allUsersRepoState;
  }

  public void setCurrentCourseIdRepoState(String currentCourseIdRepoState) {
    this.courseParticipantsRepository.setCurrentCourseIdRepoState(currentCourseIdRepoState);
  }

}
