package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.LiveChatMessageModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Repo for the live chat.
 */
public class LiveChatRepository {

  private static final String TAG = "LiveChatRepository";

  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private static LiveChatRepository instance;

  private final StateLiveData<MeetingsModel> meeting = new StateLiveData<>();

  private final ArrayList<LiveChatMessageModel> liveChatMessageList = new ArrayList<>();
  private final StateLiveData<List<LiveChatMessageModel>> liveChatMessages = new StateLiveData<>();

  private final StateLiveData<String> userId = new StateLiveData<>();
  private final HashSet<String> liveChatSet = new HashSet<>();
  private ValueEventListener listener;

  /**
   * Constructor. Initializes database instances and listener.
   */
  public LiveChatRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    initListener();
  }

  /**
   * Initializes the listener for the current LiveChat.
   */
  private void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        updateLiveChatSet(dataSnapshot);
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          LiveChatMessageModel model = snapshot.getValue(LiveChatMessageModel.class);

          if (model == null) {
            continue;
          }

          model.setKey(snapshot.getKey());
          getAuthor(model);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        meeting.postError(new Error(Config.LIVE_CHAT_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    };
  }

  /**
   * Gives back an instance of the LiveChatRepo.
   *
   * @return Instance of the LiveChatRepository
   */
  public static LiveChatRepository getInstance() {
    if (instance == null) {
      instance = new LiveChatRepository();
    }
    return instance;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  /**
   * Set the new meeting and load the chats of the meeting.
   *
   * @param meeting data of the meeting
   */
  public void setMeeting(MeetingsModel meeting) {

    if (meeting == null || meeting.getKey() == null) {
      return;
    }
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      this.meeting.postUpdate(meeting);
      this.liveChatMessageList.clear();
      this.loadLiveChat();
      return;
    }
    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      this.meeting.postUpdate(meeting);
      this.liveChatMessageList.clear();
      this.loadLiveChat();
    } else if (!meetingKey.equals(meeting.getKey())) {
      this.meeting.postUpdate(meeting);
      //reset listener if set on another meeting
      this.databaseReference
              .child(Config.LIVE_CHAT_MESSAGES_CHILD)
              .child(meetingKey)
              .removeEventListener(this.listener);
      this.liveChatMessageList.clear();
      this.loadLiveChat();
    }
  }

  /**
   * Load all chat messages for the meeting.
   */
  private void loadLiveChat() {
    this.liveChatMessages.postLoading();

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      meeting.postError(new Error(Config.LIVE_CHAT_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }
    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      meeting.postError(new Error(Config.LIVE_CHAT_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }

    Query query = this.databaseReference
            .child(Config.LIVE_CHAT_MESSAGES_CHILD)
            .child(meetingKey);
    query.addValueEventListener(this.listener);
  }

  private void updateLiveChatSet(DataSnapshot dataSnapshot) {
    HashSet<String> messageIds = new HashSet<>();
    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
      messageIds.add(snapshot.getKey());
    }
    liveChatSet.clear();
    liveChatSet.addAll(messageIds);
  }

  /**
   * This method saves a message in the data base.
   */
  public void sendMessage(LiveChatMessageModel liveChatMessageModel) {
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      return;
    }
    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      return;
    }
    String uid = Validation.checkStateLiveData(this.userId, TAG);

    liveChatMessageModel.setCreatorId(uid);
    String messageId = this.databaseReference.getRoot().push().getKey();
    if (messageId == null) {
      return;
    }

    this.databaseReference.child(Config.LIVE_CHAT_MESSAGES_CHILD)
            .child(meetingKey)
            .child(messageId)
            .setValue(liveChatMessageModel)
            .addOnSuccessListener(unused -> liveChatMessageModel.setKey(messageId))
            .addOnFailureListener(e -> Log.e(TAG,  e.getMessage()));
  }

  /**
   * This method sets the new userId after a user has logged in.
   */
  public void setUserId() {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.userId.postCreate(uid);
  }

  /**
   * Load the picture and name of the message creator
   *
   * @param messageModel data of the message for loading the author
   */
  private void getAuthor(LiveChatMessageModel messageModel) {
    this.databaseReference.child(Config.CHILD_USER).child(messageModel.getCreatorId())
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel author = snapshot.getValue(UserModel.class);
                if (author == null) {
                  messageModel.setCreatorName(Config.UNKNOWN_USER);
                } else {
                  messageModel.setCreatorName(author.getDisplayName());
                  messageModel.setPhotoUrl(author.getPhotoUrl());
                }
                updateMesseageList(messageModel, liveChatMessageList);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                liveChatMessages.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
  }

  /**
   * Update all messages if a message has changed. 
   *
   * @param messageModel data of the changed course
   * @param messageList all courses
   */
  private void updateMesseageList(LiveChatMessageModel messageModel,
                                List<LiveChatMessageModel> messageList) {
    for (int i = 0; i < messageList.size(); i++) {
      LiveChatMessageModel model = messageList.get(i);
      if (model.getKey().equals(messageModel.getKey())) {
        if (this.liveChatSet.contains(messageModel.getKey())) {
          //update course
          messageList.set(i, messageModel);
        } else {
          //remove course
          messageList.remove(i);
        }
        this.liveChatMessages.postUpdate(messageList);
        return;
      }
    }
    //add course
    if (this.liveChatSet.contains(messageModel.getKey())) {
      messageList.add(messageModel);
      this.liveChatMessages.postUpdate(messageList);
    }
  }

  public StateLiveData<List<LiveChatMessageModel>> getLiveChatMessages() {
    this.liveChatMessages.postUpdate(liveChatMessageList);
    return this.liveChatMessages;
  }

  public StateLiveData<String> getUserId() {
    return this.userId;
  }

}
