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
  private StateLiveData<MeetingsModel> meeting;
  private StateLiveData<List<LiveChatMessageModel>> sldLiveChatMessages;
  private StateLiveData<String> sldUserId;
  private StateLiveData<LiveChatMessageModel> sldMessageModelInputState = new StateLiveData<>();

  /** Initialize the ViewModel. */
  public void init() {
    if (this.sldLiveChatMessages != null) {
      return;
    }
    this.liveChatRepository = LiveChatRepository.getInstance();
    this.meeting = this.liveChatRepository.getMeeting();
    this.sldLiveChatMessages = liveChatRepository.getSldLiveChatMessages();
    //TODO set other way
    this.sldUserId = liveChatRepository.getSldUserId();

    this.sldMessageModelInputState.postCreate(new LiveChatMessageModel());
    //this.sldLiveChatMessages = liveChatRepository.getSldLiveChatMessages();
  }

  /** Send a new message in a thread. */
  public void sendMessage() {
    this.sldMessageModelInputState.postLoading();

    LiveChatMessageModel liveChatMessageModel = Validation.checkStateLiveData(this.sldMessageModelInputState, TAG);
    if (liveChatMessageModel.getText() == null ) {
      this.sldMessageModelInputState.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.TEXT);
      return;
    } else if (!Validation.stringHasPattern(liveChatMessageModel.getText(), Config.REGEX_PATTERN_TEXT)) {
      this.sldMessageModelInputState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.TEXT);
      return;
    }

    liveChatMessageModel.setCreationTime(System.currentTimeMillis());

    if (!liveChatMessageModel.getText().equals("")) {
      this.liveChatRepository.sendMessage(liveChatMessageModel);
    }
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setMeeting(MeetingsModel meeting) {
    this.liveChatRepository.setMeeting(meeting);
  }

  public StateLiveData<List<LiveChatMessageModel>> getSldLiveChatMessages() { return sldLiveChatMessages; }

  public StateLiveData<LiveChatMessageModel> getSldMessageModelInputState() { return sldMessageModelInputState; }

  public StateLiveData<String> getSldUserId() { return sldUserId; }

}
