package com.example.unserhoersaal.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.LiveChatMessageModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/** Repo for the live chat. */
public class LiveChatRepository {

  private static final String TAG = "LiveChatRepository";

  private static LiveChatRepository instance;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseReference;
  private ArrayList<LiveChatMessageModel> liveChatMessages = new ArrayList<>();
  private StateLiveData<List<LiveChatMessageModel>> sldLiveChatMessages = new StateLiveData<>();
  private StateLiveData<String> sldUserId = new StateLiveData<>();
  private ValueEventListener liveChatMessageListener;
  private MeetingsModel meetingsModel;



  public LiveChatRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.sldUserId.postCreate(new String());
    this.sldLiveChatMessages.postCreate(liveChatMessages);
    setSldUserId();
  }

  /** Gives back an instance of the LiveChatRepo. */
  public static LiveChatRepository getInstance() {
    if (instance == null) {
      instance = new LiveChatRepository();
    }
    return instance;
  }

  /**
   * Initialise the listener for the database access.
   */
  public void initListener() {
    Log.d("Hier", "in initlistener");
    this.liveChatMessageListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        liveChatMessages.clear();
        List<LiveChatMessageModel> messList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          LiveChatMessageModel model = snapshot.getValue(LiveChatMessageModel.class);

          model.setKey(snapshot.getKey());
          messList.add(model);
        }
        getAuthor(messList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    };
  }

  public void setMeetingAndListener(MeetingsModel meeting) {
    this.meetingsModel = meeting;
    Log.d("Hier", "in repo setmetali" + meeting.getKey());
    this.liveChatMessages.clear();
    this.sldLiveChatMessages.postCreate(liveChatMessages);
    String meetingKey = meeting.getKey();
    if (meetingKey != null) {
      this.databaseReference.child(Config.LIVE_CHAT_MESSAGES_CHILD).child(meetingKey)
              .removeEventListener(this.liveChatMessageListener);
    }

    this.databaseReference.child(Config.LIVE_CHAT_MESSAGES_CHILD).child(meetingKey)
            .addValueEventListener(this.liveChatMessageListener);
  }

  /**
   * This method saves a message in the data base.
   */
  public void sendMessage(LiveChatMessageModel liveChatMessageModel) {
    String meetingKey = meetingsModel.getKey();

    if (meetingKey == null) {
      Log.e(TAG, "threadKey is null.");
      return;
    }

    liveChatMessageModel.setCreatorId(this.sldUserId.getValue().getData());
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

  /** TODO. */
  public void setSldUserId() {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.sldUserId.postCreate(uid);
  }



  /** TODO. */
  public void getAuthor(List<LiveChatMessageModel> mesList) {
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
      liveChatMessages.addAll(mesList);
      sldLiveChatMessages.postUpdate(liveChatMessages);

    });
  }

  /** TODO. */
  public Task<DataSnapshot> getAuthorModel(String authorId) {
    return this.databaseReference.child(Config.CHILD_USER).child(authorId).get();
  }

  public StateLiveData<String> getSldUserId() {
    return this.sldUserId;
  }

  public MeetingsModel getMeeting() {
    return this.meetingsModel;
  }

  public StateLiveData<List<LiveChatMessageModel>> getSldLiveChatMessages() { return sldLiveChatMessages; }

}