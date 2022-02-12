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
  private MutableLiveData<String> courseId = new MutableLiveData<String>();

  /** This method initializes the database access. */
  public void init() {
    if (this.messages != null) {
      return;
    }
    this.currentCourseRepository = CurrentCourseRepository.getInstance();
    this.currentCourseRepository.setCourseId(this.courseId.getValue());
    this.messages = this.currentCourseRepository.getMessages();
  }

  public LiveData<List<Message>> getMessages() {
    return this.messages;
  }

  public LiveData<String> getCourseId() {
    return this.courseId;
  }

  public void sendMessage(String messageText) {
    this.currentCourseRepository.sendMessage(messageText);
  }

  public void setCourseId(String courseId) {
    this.courseId.setValue(courseId);
  }
}