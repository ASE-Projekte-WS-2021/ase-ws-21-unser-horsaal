package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.LiveChatMessageModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.repository.LiveChatRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.List;

/** ViewModel for the LiveChatFragment. */
public class LiveChatViewModel extends ViewModel {

  private static final String TAG = "LiveChatViewModel";

  private LiveChatRepository liveChatRepository;
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<List<LiveChatMessageModel>> liveChatMessages;
  private StateLiveData<String> userId;
  public StateLiveData<LiveChatMessageModel> sldMessageModelInputState = new StateLiveData<>();


  /** Initialize the ViewModel. */
  public void init() {
    if (this.liveChatMessages != null) {
      return;
    }

    this.liveChatRepository = LiveChatRepository.getInstance();
    this.meeting = this.liveChatRepository.getMeeting();
    this.liveChatMessages = this.liveChatRepository.getLiveChatMessages();
    this.userId = this.liveChatRepository.getUserId();

    this.sldMessageModelInputState.postCreate(new LiveChatMessageModel());
  }

  /** Send a new message in a thread. */
  public void sendMessage() {
    this.sldMessageModelInputState.postLoading();

    LiveChatMessageModel liveChatMessageModel = Validation
            .checkStateLiveData(this.sldMessageModelInputState, TAG);
    if (liveChatMessageModel == null) {
      this.liveChatMessages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (Validation.emptyString(liveChatMessageModel.getText())) {
      this.sldMessageModelInputState
              .postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
    } else if (!Validation
            .stringHasPattern(liveChatMessageModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      this.sldMessageModelInputState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
    } else {
      liveChatMessageModel.setCreationTime(System.currentTimeMillis());
      this.liveChatRepository.sendMessage(liveChatMessageModel);
    }
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setMeeting(MeetingsModel meeting) {
    this.liveChatRepository.setMeeting(meeting);
  }

  public StateLiveData<List<LiveChatMessageModel>> getLiveChatMessages() {
    return this.liveChatMessages;
  }

  public void setUserId() {
    this.liveChatRepository.setUserId();
  }

  public StateLiveData<String> getUserId() {
    return this.userId;
  }

  public void setDefaultInputState() {
    this.sldMessageModelInputState.postCreate(new LiveChatMessageModel());
  }

  public void setLiveDataComplete() {
    this.sldMessageModelInputState.postComplete();
    this.meeting.postComplete();
  }

}