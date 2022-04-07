package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.CourseParticipantsRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import java.util.List;

/** Used in CourseParticipantsFragment. Connects to userlist in a set course.
 * provides statuslive data with course pariticipants. */
public class CourseParticipantsViewModel extends ViewModel {

  private CourseParticipantsRepository courseParticipantsRepository;
  private StateLiveData<String> courseId = new StateLiveData<>();
  private StateLiveData<List<UserModel>> userList;

  /** Description for init method. */
  public void init() {
    if (this.userList != null) {
      return;
    }

    this.courseParticipantsRepository = CourseParticipantsRepository.getInstance();
    this.courseId = this.courseParticipantsRepository.getCourseId();
    this.userList = this.courseParticipantsRepository.getUsers();
  }

  public StateLiveData<String> getCourseId() {
    return this.courseId;
  }

  public StateLiveData<List<UserModel>> getUserList() {
    return this.userList;
  }

  public void setCourseId(String courseId) {
    this.courseParticipantsRepository.setCourseId(courseId);
  }

}
