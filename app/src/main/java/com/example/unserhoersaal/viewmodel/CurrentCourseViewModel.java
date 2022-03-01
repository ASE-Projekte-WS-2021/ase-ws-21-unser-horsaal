package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.repository.CurrentCourseRepository;
import java.util.List;

/** This class is the ViewModel for the joined course. */
public class CurrentCourseViewModel extends ViewModel {

  private static final String TAG = "CurrentCourseViewModel";

  private CurrentCourseRepository currentCourseRepository;

  private MutableLiveData<List<MessageModel>> messages;
  private MutableLiveData<String> threadId = new MutableLiveData<>();

  /** This method initializes the database access. */
  public void init() {
    if (this.messages != null) {
      return;
    }
    this.currentCourseRepository = CurrentCourseRepository.getInstance();
    this.threadId = this.currentCourseRepository.getThreadId();

    // Only load the messages if the courseId is set. Thus, the shared fragments, that do not need
    // the messages and only set the courseId can init the CurrentCourseViewModel
    if (this.threadId.getValue() != null) {
      this.messages = this.currentCourseRepository.getMessages();
    }
  }

  public LiveData<List<MessageModel>> getMessages() {
    return this.messages;
  }

  public LiveData<String> getThreadId() {
    return this.threadId;
  }

  /** Send a new message in a thread. */
  public void sendMessage(String messageText) {
    MessageModel messageModel = new MessageModel();
    messageModel.setText(messageText);
    this.currentCourseRepository.sendMessage(messageModel);
  }

  public void setThreadId(String threadId) {
    this.currentCourseRepository.setThreadId(threadId);
  }

  public void like(String messageId) {
    this.currentCourseRepository.like(messageId);
  }

  public void dislike(String messageId) {
    this.currentCourseRepository.dislike(messageId);
  }

  public void solved(String messageId) {
    this.currentCourseRepository.solved(messageId);
  }
}