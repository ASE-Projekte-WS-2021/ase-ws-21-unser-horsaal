package com.example.unserhoersaal.viewmodel;

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
    this.meeting = this.currentCourseRepository.getMeeting();
    this.thread = this.currentCourseRepository.getThread();
    this.userId = this.currentCourseRepository.getUserId();
    this.messageModelInputState.postCreate(new MessageModel());

    this.messages = this.currentCourseRepository.getMessages();
  }

  public StateLiveData<List<MessageModel>> getMessages() {
    return this.messages;
  }

  /** Sort the messages list by likes. */
  public void sortAnswersByLikes(List<MessageModel> messageModelList) {
    this.arrayListUtil.sortAnswersByLikes(messageModelList);
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
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    MessageModel messageModel = Validation.checkStateLiveData(this.messageModelInputState, TAG);
    if (messageModel == null) {
      this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (Validation.emptyString(messageModel.getText())) {
      this.messages.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
    } else if (!Validation.stringHasPattern(messageModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      this.messages.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
    } else {
      messageModel.setCreationTime(System.currentTimeMillis());
      this.messageModelInputState.postCreate(new MessageModel());
      this.currentCourseRepository.sendMessage(messageModel);
    }
  }

  public void setThread(ThreadModel threadModel) {
    this.currentCourseRepository.setThread(threadModel);
  }

  public void setMeeting(MeetingsModel meeting) {
    this.currentCourseRepository.setMeeting(meeting);
  }

  public void setUserId() {
    this.currentCourseRepository.setUserId();
  }

  /** Checks the likestatus of the message and passes it to the corresponding
   * repo method where the likecount is updated.
   *
   * @param message the message where the user clicked like */
  public void like(MessageModel message) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
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

  /** Checks the dislikestatus of the message and passes it to the corresponding
   * repo method where the dislikecount is updated.
   *
   * @param message the message where the user clicked dislike */
  public void dislike(MessageModel message) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
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

  /** Checks the likestatus of the thread and passes it to the corresponding
   * repo method where the likecount is updated.
   *
   * @param threadModel the message where the user clicked like */
  public void likeThread(ThreadModel threadModel) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
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

  /** Checks the dislikestatus of the thread and passes it to the corresponding
   * repo method where the dislikecount is updated.
   *
   * @param threadModel the message where the user clicked dislike */
  public void dislikeThread(ThreadModel threadModel) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
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

  /** Calls Repo Methode that marks the Answer as solved.
   *
   * @param messageId the Id of the Message that gets marked as solved */
  public void solved(String messageId) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.currentCourseRepository.solved(messageId);
  }

  public void deleteThreadText() {
    currentCourseRepository.deleteThreadText();
  }

  public void deleteAnswerText(MessageModel messageModel) {
    currentCourseRepository.deleteAnswerText(messageModel);
  }

}