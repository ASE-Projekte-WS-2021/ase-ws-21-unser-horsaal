package com.example.unserhoersaal.viewmodel;

import android.os.Message;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.enums.LikeStatus;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.CurrentCourseRepository;
import com.example.unserhoersaal.utils.ArrayListUtil;
import com.example.unserhoersaal.utils.PreventDoubleClick;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.List;

/** This class is the ViewModel for the joined course. */
public class CurrentCourseViewModel extends ViewModel {

  private static final String TAG = "CurrentCourseViewModel";

  private CurrentCourseRepository currentCourseRepository;
  private StateLiveData<List<MessageModel>> messages;
  private StateLiveData<String> threadId = new StateLiveData<>();
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<ThreadModel> thread = new StateLiveData<>();
  private ArrayListUtil arrayListUtil = new ArrayListUtil();
  public StateLiveData<String> userId;
  public StateLiveData<MessageModel> messageModelInputState = new StateLiveData<>();

  /** This method initializes the database access. */
  public void init() {
    if (this.messages != null) {
      return;
    }
    this.currentCourseRepository = CurrentCourseRepository.getInstance();
    this.threadId = this.currentCourseRepository.getThreadId();
    this.meeting = this.currentCourseRepository.getMeeting();
    this.thread = this.currentCourseRepository.getThread();
    this.userId = this.currentCourseRepository.getUserId();
    this.messageModelInputState.postCreate(new MessageModel());

    // Only load the messages if the courseId is set. Thus, the shared fragments, that do not need
    // the messages and only set the courseId can init the CurrentCourseViewModel
    if (this.threadId.getValue() != null) {
      Log.d(TAG, "threadId: " + this.threadId.getValue().getData());
      this.messages = this.currentCourseRepository.getMessages();
    }
  }

  public StateLiveData<List<MessageModel>> getMessages() {
    return this.messages;
  }

  /** Sort the messages list by likes. */
  public void sortAnswersByLikes(List<MessageModel> messageModelList) {
    this.arrayListUtil.sortAnswersByLikes(messageModelList);
  }

  public StateLiveData<String> getThreadId() {
    return this.threadId;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public StateLiveData<ThreadModel> getThread() {
    return  this.thread;
  }

  public StateLiveData<String> getUserId() {
    return this.userId;
  }

  /** Send a new message in a thread. */
  public void sendMessage() {
    //TODO: removed loading because there is no place for it
    MessageModel messageModel = Validation.checkStateLiveData(this.messageModelInputState, TAG);
    if (messageModel == null) {
      Log.e(TAG, "messageModel is null.");
      this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (messageModel.getText() == null) {
      Log.d(TAG, "title is null.");
      this.messages.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
      return;
    } else if (!Validation.stringHasPattern(messageModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, "title has wrong pattern.");
      this.messages.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
      return;
    }

    messageModel.setCreationTime(System.currentTimeMillis());
    if (!messageModel.getText().equals("")) {
      this.messageModelInputState.postCreate(new MessageModel());
      this.currentCourseRepository.sendMessage(messageModel);
    }
  }

  public void setThreadId(String threadId) {
    this.currentCourseRepository.setThreadId(threadId);
  }

  public void setThread(ThreadModel threadModel) {
    this.thread.postCreate(threadModel);
    this.currentCourseRepository.setThread(threadModel);
  }

  public void setMeeting(MeetingsModel meeting) {
    this.currentCourseRepository.setMeetingId(meeting);
  }

  public void setUserId() {
    this.currentCourseRepository.setUserId();
  }

  /** JavaDoc for this method. */
  public void like(MessageModel message) {
    if(PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    String messageId = message.getKey();
    LikeStatus likeStatus = message.getLikeStatus();
    switch (likeStatus) {
      case LIKE:
        this.currentCourseRepository.handleLikeEvent(messageId, -1, LikeStatus.NEUTRAL);
        break;
      case DISLIKE:
        this.currentCourseRepository.handleLikeEvent(messageId, 2, LikeStatus.LIKE);
        break;
      case NEUTRAL:
        this.currentCourseRepository.handleLikeEvent(messageId, 1, LikeStatus.LIKE);
        break;
      default:
        break;
    }
  }

  /** JavaDoc for this method. */
  public void dislike(MessageModel message) {
    if(PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    String messageId = message.getKey();
    LikeStatus likeStatus = message.getLikeStatus();
    switch (likeStatus) {
      case LIKE:
        this.currentCourseRepository.handleLikeEvent(messageId, -2, LikeStatus.DISLIKE);
        break;
      case DISLIKE:
        this.currentCourseRepository.handleLikeEvent(messageId, 1, LikeStatus.NEUTRAL);
        break;
      case NEUTRAL:
        this.currentCourseRepository.handleLikeEvent(messageId, -1, LikeStatus.DISLIKE);
        break;
      default:
        break;
    }
  }

  //TODO do this in meetingsViewModel
  /** JavaDoc for this method. */
  public void likeThread(ThreadModel threadModel) {
    if(PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    String threadId  = threadModel.getKey();
    LikeStatus likeStatus = threadModel.getLikeStatus();
    switch (likeStatus) {
      case LIKE:
        this.currentCourseRepository.handleLikeEventThread(threadId, -1, LikeStatus.NEUTRAL);
        break;
      case DISLIKE:
        this.currentCourseRepository.handleLikeEventThread(threadId, 2, LikeStatus.LIKE);
        break;
      case NEUTRAL:
        this.currentCourseRepository.handleLikeEventThread(threadId, 1, LikeStatus.LIKE);
        break;
      default:
        break;
    }
  }

  /** JavaDoc for this method. */
  public void dislikeThread(ThreadModel threadModel) {
    if(PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    String threadId = threadModel.getKey();
    LikeStatus likeStatus = threadModel.getLikeStatus();
    switch (likeStatus) {
      case LIKE:
        this.currentCourseRepository.handleLikeEventThread(threadId, -2, LikeStatus.DISLIKE);
        break;
      case DISLIKE:
        this.currentCourseRepository.handleLikeEventThread(threadId, 1, LikeStatus.NEUTRAL);
        break;
      case NEUTRAL:
        this.currentCourseRepository.handleLikeEventThread(threadId, -1, LikeStatus.DISLIKE);
        break;
      default:
        break;
    }
  }

  public void solved(String messageId) {
    this.currentCourseRepository.solved(messageId);
  }

  public void deleteThreadText(ThreadModel threadModel) {
    currentCourseRepository.deleteThreadText(threadModel);
  }

  public void deleteAnswerText(MessageModel messageModel) {
    currentCourseRepository.deleteAnswerText(messageModel);
  }



}