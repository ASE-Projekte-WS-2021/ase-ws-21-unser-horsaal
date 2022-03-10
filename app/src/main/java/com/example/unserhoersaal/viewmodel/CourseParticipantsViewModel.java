package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.CourseParticipantsRepository;
import java.util.List;

/** JavaDoc for this ViewModel. */
public class CourseParticipantsViewModel extends ViewModel {

  private static final String TAG = "courseParticipantsViewModel";

  private CourseParticipantsRepository courseParticipantsRepository;

  private MutableLiveData<String> courseId = new MutableLiveData<>();
  private MutableLiveData<List<UserModel>> userList;

  /** Description for init method. */
  public void init() {
    if (this.userList != null) {
      return;
    }

    this.courseParticipantsRepository = CourseParticipantsRepository.getInstance();
    this.courseId = this.courseParticipantsRepository.getCourseId();
    this.userList = this.courseParticipantsRepository.getUsers();
  }

  public LiveData<String> getCourseId() {
    return this.courseId;
  }

  public LiveData<List<UserModel>> getUserList() {
    return this.userList;
  }

  public void setCourseId(String courseId) {
    this.courseParticipantsRepository.setCourseId(courseId);
  }

}
