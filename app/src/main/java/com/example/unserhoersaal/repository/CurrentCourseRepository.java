package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.LikeStatus;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.model.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**Class communicates with Firebase for getting current course data.**/
public class CurrentCourseRepository {

  private static final String TAG = "CurrentCourseRepo";

  private static CurrentCourseRepository instance;

  private ArrayList<MessageModel> messagesList = new ArrayList<>();
  private MutableLiveData<List<MessageModel>> messages = new MutableLiveData<>();
  private MutableLiveData<String> threadId = new MutableLiveData<>();
  private MutableLiveData<MeetingsModel> meeting = new MutableLiveData<>();
  private MutableLiveData<ThreadModel> thread = new MutableLiveData<>();
  private MutableLiveData<String> userId = new MutableLiveData<>();
  private ValueEventListener messageListener;
  private ValueEventListener threadListener;

  public CurrentCourseRepository() {
    initListener();
  }

  /**
   * Generates a unique instance of CurrentCourseRepository.
   */
  public static CurrentCourseRepository getInstance() {
    if (instance == null) {
      instance = new CurrentCourseRepository();
    }
    return instance;
  }

  /**
   * This method provides all messages of a course.
   */
  public MutableLiveData<List<MessageModel>> getMessages() {
    /*if (this.messagesList.size() == 0) {
      this.loadMessages();
    }*/

    this.messages.setValue(this.messagesList);
    return this.messages;
  }

  public MutableLiveData<String> getThreadId() {
    return this.threadId;
  }

  public MutableLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public MutableLiveData<ThreadModel> getThread() {
    return this.thread;
  }

  public MutableLiveData<String> getUserId() {
    return this.userId;
  }

  /**
   * Loading all messages from the database.
   */
  public void loadMessages() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.child(Config.CHILD_THREADS).child(this.threadId.getValue())
            .child(Config.CHILD_MESSAGES);
    query.addValueEventListener(this.messageListener);
  }

  /**
   * This method saves a message in the data base.
   */
  public void sendMessage(MessageModel message) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid();

    message.setCreatorId(uid);
    message.setCreationTime(System.currentTimeMillis());
    String messageId = reference.getRoot().push().getKey();
    reference.child(Config.CHILD_MESSAGES).child(this.threadId.getValue()).child(messageId)
            .setValue(message)
            .addOnSuccessListener(unused -> {
              updateAnswerCount();
              message.setKey(messageId);
            });

  }

  /** TODO. */
  public void updateAnswerCount() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_THREADS).child(this.meeting.getValue().getKey())
            .child(this.threadId.getValue()).child(Config.CHILD_ANSWER_COUNT)
            .setValue(ServerValue.increment(1));
  }

  public void setUserId() {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    this.userId.postValue(uid);
  }

  public void setMeetingId(MeetingsModel meeting) {
    this.meeting.postValue(meeting);
  }

  /**
   * Sets the id of the new entered thread.
   */
  public void setThreadId(String threadId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.threadId.getValue() != null) {
      reference.child(Config.CHILD_MESSAGES).child(this.threadId.getValue())
              .removeEventListener(this.messageListener);
      reference.child(Config.CHILD_THREADS).child(this.meeting.getValue().getKey())
              .child(this.threadId.getValue()).removeEventListener(this.threadListener);
    }
    reference.child(Config.CHILD_MESSAGES).child(threadId)
            .addValueEventListener(this.messageListener);
    reference.child(Config.CHILD_THREADS).child(this.meeting.getValue().getKey()).child(threadId)
            .addValueEventListener(this.threadListener);
    this.threadId.postValue(threadId);
  }

  public Task<DataSnapshot> getLikeStatusMessage(String id) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER_LIKE).child(userId.getValue()).child(id).get();
  }

  /** TODO. */
  public void getLikeStatus(List<MessageModel> mesList) {
    List<Task<DataSnapshot>> likeList = new ArrayList<>();
    for (MessageModel message : mesList) {
      likeList.add(getLikeStatusMessage(message.getKey()));
    }
    Tasks.whenAll(likeList).addOnSuccessListener(unused -> {
      for (int i = 0; i < likeList.size(); i++) {
        if (!likeList.get(i).getResult().exists()) {
          mesList.get(i).setLikeStatus(LikeStatus.NEUTRAL);
        } else if (likeList.get(i).getResult().getValue(LikeStatus.class) == LikeStatus.LIKE) {
          mesList.get(i).setLikeStatus(LikeStatus.LIKE);
        } else if (likeList.get(i).getResult().getValue(LikeStatus.class) == LikeStatus.DISLIKE) {
          mesList.get(i).setLikeStatus(LikeStatus.DISLIKE);
        }
      }
      messagesList.clear();
      messagesList.addAll(mesList);
      messages.postValue(messagesList);
    });
  }

  /** TODO. */
  public void getLikeStatusThread(ThreadModel threadModel) {
    Task<DataSnapshot> task = getLikeStatusMessage(threadModel.getKey());
    task.addOnSuccessListener(dataSnapshot -> {
      if (!task.getResult().exists()) {
        threadModel.setLikeStatus(LikeStatus.NEUTRAL);
      } else if (task.getResult().getValue(LikeStatus.class) == LikeStatus.LIKE) {
        threadModel.setLikeStatus(LikeStatus.LIKE);
      } else if (task.getResult().getValue(LikeStatus.class) == LikeStatus.DISLIKE) {
        threadModel.setLikeStatus(LikeStatus.DISLIKE);
      }
      thread.postValue(threadModel);
    });

  }

  /** TODO. */
  public void getAuthor(List<MessageModel> mesList) {
    List<Task<DataSnapshot>> authorModels = new ArrayList<>();
    for (MessageModel message : mesList) {
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
      getLikeStatus(mesList);
    });
  }

  /** TODO. */
  public Task<DataSnapshot> getAuthorModel(String authorId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER).child(authorId).get();
  }

  /**
   * Initialise the listener for the database access.
   */
  public void initListener() {
    this.messageListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        List<MessageModel> mesList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          MessageModel model = snapshot.getValue(MessageModel.class);
          model.setKey(snapshot.getKey());
          mesList.add(model);
        }
        getAuthor(mesList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    };
    this.threadListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        ThreadModel threadModel = snapshot.getValue(ThreadModel.class);
        threadModel.setKey(snapshot.getKey());
        Task<DataSnapshot> task = getAuthorModel(threadModel.creatorId);
        task.addOnSuccessListener(dataSnapshot -> {
          UserModel model = task.getResult().getValue(UserModel.class);
          if (model == null) {
            threadModel.setCreatorName(Config.UNKNOWN_USER);
          } else {
            threadModel.setCreatorName(model.getDisplayName());
            threadModel.setPhotoUrl(model.getPhotoUrl());
          }
          getLikeStatusThread(threadModel);
        });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    };
  }

  /** TODO. */
  public void handleLikeEvent(String messageId, int deltaCount, LikeStatus status) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (status == LikeStatus.NEUTRAL) {
      reference.child(Config.CHILD_USER_LIKE).child(userId.getValue()).child(messageId)
              .removeValue();
      reference.child(Config.CHILD_LIKE_USER).child(messageId).child(userId.getValue())
              .removeValue();
    } else {
      reference.child(Config.CHILD_USER_LIKE).child(userId.getValue()).child(messageId)
              .setValue(status);
      reference.child(Config.CHILD_LIKE_USER).child(messageId).child(userId.getValue())
              .setValue(status);
    }
    reference.child(Config.CHILD_MESSAGES).child(this.threadId.getValue()).child(messageId)
            .child(Config.CHILD_LIKE).setValue(ServerValue.increment(deltaCount));
  }

  /** TODO. */
  public void handleLikeEventThread(String threadId, int deltaCount, LikeStatus status) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (status == LikeStatus.NEUTRAL) {
      reference.child(Config.CHILD_USER_LIKE).child(userId.getValue()).child(threadId)
              .removeValue();
      reference.child(Config.CHILD_LIKE_USER).child(threadId).child(userId.getValue())
              .removeValue();
    } else {
      reference.child(Config.CHILD_USER_LIKE).child(userId.getValue()).child(threadId)
              .setValue(status);
      reference.child(Config.CHILD_LIKE_USER).child(threadId).child(userId.getValue())
              .setValue(status);
    }
    reference.child(Config.CHILD_THREADS).child(this.meeting.getValue().getKey()).child(threadId)
            .child(Config.CHILD_LIKE).setValue(ServerValue.increment(deltaCount));
  }

  /** TODO. */
  public void solved(String messageId) {
    boolean threadAnswered = this.thread.getValue().getAnswered();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (userId.getValue().equals(thread.getValue().getCreatorId())) {
      reference.child(Config.CHILD_MESSAGES)
              .child(this.threadId.getValue())
              .child(messageId)
              .child(Config.CHILD_TOP_ANSWER)
              .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                  boolean topAnswer = snapshot.getValue(Boolean.class);
                  if (topAnswer && threadAnswered) {
                    //Thread is answered and the message is marked as answer
                    reference.child(Config.CHILD_MESSAGES)
                            .child(threadId.getValue())
                            .child(messageId)
                            .child(Config.CHILD_TOP_ANSWER)
                            .setValue(Boolean.FALSE);
                    reference.child(Config.CHILD_THREADS)
                            .child(meeting.getValue().getKey())
                            .child(threadId.getValue())
                            .child(Config.CHILD_ANSWERED)
                            .setValue(Boolean.FALSE);
                  } else if (!topAnswer && threadAnswered) {
                    //Thread is answered and the message is not marked as answer
                    Log.d(TAG, "onDataChange: " + "an message is already marked");
                    //TODO user feedback

                  } else if (!topAnswer && !threadAnswered) {
                    //Thread is not  answered and the message is not marked as answer
                    reference.child(Config.CHILD_MESSAGES)
                            .child(threadId.getValue())
                            .child(messageId)
                            .child(Config.CHILD_TOP_ANSWER)
                            .setValue(Boolean.TRUE);
                    reference.child(Config.CHILD_THREADS)
                            .child(meeting.getValue().getKey())
                            .child(threadId.getValue())
                            .child(Config.CHILD_ANSWERED)
                            .setValue(Boolean.TRUE);
                  }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
              });
    }
  }

}
