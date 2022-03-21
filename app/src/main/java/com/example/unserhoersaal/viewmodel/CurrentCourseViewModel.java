package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.enums.LikeStatus;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.repository.CurrentCourseRepository;
import java.util.Collections;
import java.util.Comparator;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.List;

/** This class is the ViewModel for the joined course. */
public class CurrentCourseViewModel extends ViewModel {

  private static final String TAG = "CurrentCourseViewModel";

  private CurrentCourseRepository currentCourseRepository;
  private StateLiveData<List<MessageModel>> allMessagesRepoState;
  private StateLiveData<String> currentThreadIdRepoState = new StateLiveData<>();
  private StateLiveData<MeetingsModel> currentMeetingRepoState = new StateLiveData<>();
  private StateLiveData<ThreadModel> currentThreadRepoState = new StateLiveData<>();
  public StateLiveData<String> currentUserIdRepoState;
  public StateLiveData<MessageModel> messageModelInputState = new StateLiveData<>();

  /** This method initializes the database access. */
  public void init() {
    if (this.allMessagesRepoState != null) {
      return;
    }
    this.currentCourseRepository = CurrentCourseRepository.getInstance();
    this.currentThreadIdRepoState = this.currentCourseRepository.getCurrentThreadIdRepoState();
    this.currentMeetingRepoState = this.currentCourseRepository.getCurrentMeetingRepoState();
    this.currentThreadRepoState = this.currentCourseRepository.getCurrentThreadRepoState();
    this.currentCourseRepository.setUserId();
    this.currentUserIdRepoState = this.currentCourseRepository.getCurrentUserIdRepoState();
    this.messageModelInputState.postCreate(new MessageModel());

    // Only load the messages if the courseId is set. Thus, the shared fragments, that do not need
    // the messages and only set the courseId can init the CurrentCourseViewModel
    if (this.currentThreadRepoState.getValue() != null) {
      Log.d(TAG, "threadId: " + this.currentThreadRepoState.getValue().getData());
      this.allMessagesRepoState = this.currentCourseRepository.getAllMessagesRepoState();
    }
  }

  public StateLiveData<List<MessageModel>> getAllMessagesRepoState() {
    return this.allMessagesRepoState;
  }

  /** Sort the messages list by likes. */
  public void sortAnswersByLikes(List<MessageModel> messageModelList) {
    Collections.sort(messageModelList, new Comparator<MessageModel>() {
      @Override
      public int compare(MessageModel messageModel, MessageModel t1) {
        return t1.getLikes() - messageModel.getLikes();
      }
    });
  }

  public StateLiveData<String> getCurrentThreadIdRepoState() {
    return this.currentThreadIdRepoState;
  }

  public StateLiveData<MeetingsModel> getCurrentMeetingRepoState() {
    return this.currentMeetingRepoState;
  }

  public StateLiveData<ThreadModel> getCurrentThreadRepoState() {
    return  this.currentThreadRepoState;
  }

  /** Send a new message in a thread. */
  public void sendMessage() {
    //TODO: removed loading because there is no place for it
    MessageModel messageModel = Validation.checkStateLiveData(this.messageModelInputState, TAG);
    if (messageModel == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_MESSAGE_MODEL);
      this.allMessagesRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (messageModel.getText() == null) {
      Log.d(TAG, Config.CURRENT_COURSE_NO_TITLE);
      this.allMessagesRepoState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
      return;
    } else if (!Validation.stringHasPattern(messageModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, Config.CURRENT_COURSE_WORNG_TITLE_PATTERN);
      this.allMessagesRepoState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
      return;
    }

    messageModel.setCreationTime(System.currentTimeMillis());

    this.messageModelInputState.postCreate(new MessageModel());
    this.currentCourseRepository.sendMessage(messageModel);
  }

  public void setCurrentThreadRepoState(String currentThreadRepoState) {
    this.currentCourseRepository.setCurrentThreadIdRepoState(currentThreadRepoState);
  }

  public void setCurrentMeetingRepoState(MeetingsModel currentMeetingRepoState) {
    this.currentCourseRepository.setMeetingId(currentMeetingRepoState);
  }

  /** JavaDoc for this method. */
  public void like(MessageModel message) {
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

}