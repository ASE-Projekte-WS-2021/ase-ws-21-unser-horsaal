package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.Message;
import com.example.unserhoersaal.repository.CurrentCourseRepository;
import java.util.List;

/** This class is the ViewModel for the joined course. */
public class CurrentCourseViewModel extends ViewModel {

  private static final String TAG = "CurrentCourseViewModel";

  private CurrentCourseRepository currentCourseRepository;
  private MutableLiveData<List<Message>> messages;
  private String courseid;

  /** This method initializes the database access. */
  public void init() {
    if (messages != null) {
      return;
    }
    currentCourseRepository = CurrentCourseRepository.getInstance();
    currentCourseRepository.setCourseId(this.courseid);
    messages = currentCourseRepository.getMessages();
  }

  public void sendMessage(String messageText) {
    currentCourseRepository.sendMessage(messageText);
  }

  public LiveData<List<Message>> getMessages() {
    return messages;
  }

  public void setupCurrentCourseViewModel(String courseId) {
    this.courseid = courseId;
  }

}