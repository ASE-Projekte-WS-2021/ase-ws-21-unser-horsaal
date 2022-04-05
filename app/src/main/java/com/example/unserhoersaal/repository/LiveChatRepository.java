package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.LiveChatMessageModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
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

  /**
   * Constructor.
   */
  public LiveChatRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
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
    if (this.meeting.getValue() == null
            || this.meeting.getValue().getData() == null
            || this.meeting.getValue().getData().getKey() == null
            || !this.meeting.getValue().getData().getKey().equals(meeting.getKey())){
      this.meeting.postUpdate(meeting);
      this.loadLiveChat();
    }
  }

  /**
   * Load all chat messages for the meeting.
   */
  private void loadLiveChat() {
    this.liveChatMessages.postLoading();

    Query query = this.databaseReference
            .child(Config.LIVE_CHAT_MESSAGES_CHILD)
            .child(this.meeting.getValue().getData().getKey());
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        List<LiveChatMessageModel> messList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          LiveChatMessageModel model = snapshot.getValue(LiveChatMessageModel.class);
          //TODO poll to livechat
          if (model == null) {
            Log.e(TAG, Config.POLLS_FAILED_TO_LOAD);
            meeting.postError(new Error(Config.POLLS_FAILED_TO_LOAD), ErrorTag.REPO);
            return;
          }

          model.setKey(snapshot.getKey());
          messList.add(model);
        }
        getAuthor(messList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

  /**
   * This method saves a message in the data base.
   */
  public void sendMessage(LiveChatMessageModel liveChatMessageModel) {
    String meetingKey = meeting.getValue().getData().getKey();

    if (meetingKey == null) {
      Log.e(TAG, "threadKey is null.");
      return;
    }

    liveChatMessageModel.setCreatorId(this.userId.getValue().getData());
    String messageId = this.databaseReference.getRoot().push().getKey();

    this.databaseReference.child(Config.LIVE_CHAT_MESSAGES_CHILD)
            .child(meetingKey)
            .child(messageId)
            .setValue(liveChatMessageModel)
            .addOnSuccessListener(unused -> {
              liveChatMessageModel.setKey(messageId);
            }).addOnFailureListener(e -> {
      Log.e(TAG, "Nachricht konnte nicht versandt werden: " + e.getMessage());
    });
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
   * Get the creator data of all polls of a meeting.
   *
   * @param mesList List of all chat messages of a meeting
   */
  private void getAuthor(List<LiveChatMessageModel> mesList) {
    List<Task<DataSnapshot>> authorModels = new ArrayList<>();
    for (LiveChatMessageModel message : mesList) {
      authorModels.add(getAuthorModel(message.getCreatorId()));
    }
    Tasks.whenAll(authorModels).addOnSuccessListener(unused -> {
      for (int i = 0; i < authorModels.size(); i++) {
        UserModel model = authorModels.get(i).getResult().getValue(UserModel.class);
        if (model == null) {
          mesList.get(i).setCreatorName(Config.UNKNOWN_USER);
        } else {
          mesList.get(i).setCreatorName(model.getDisplayName());
          mesList.get(i).setPhotoUrl(model.getPhotoUrl());
        }
      }
      liveChatMessageList.clear();
      liveChatMessageList.addAll(mesList);
      liveChatMessages.postUpdate(liveChatMessageList);

    });
  }

  private Task<DataSnapshot> getAuthorModel(String authorId) {
    return this.databaseReference.child(Config.CHILD_USER).child(authorId).get();
  }

  public StateLiveData<List<LiveChatMessageModel>> getLiveChatMessages() {
    this.liveChatMessages.postUpdate(liveChatMessageList);
    return this.liveChatMessages;
  }

  public StateLiveData<String> getUserId() {
    return this.userId;
  }

}
