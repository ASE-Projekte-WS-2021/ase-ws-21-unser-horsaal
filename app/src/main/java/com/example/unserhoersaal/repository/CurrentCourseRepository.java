package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.enums.LikeStatus;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
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

/** Class communicates with Firebase for getting current course data. **/
public class CurrentCourseRepository {

  private static final String TAG = "CurrentCourseRepo";

  private static CurrentCourseRepository instance;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseReference;
  private ArrayList<MessageModel> messagesList = new ArrayList<>();
  private StateLiveData<List<MessageModel>> messages = new StateLiveData<>();
  private StateLiveData<String> threadId = new StateLiveData<>();
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<ThreadModel> thread = new StateLiveData<>();
  private StateLiveData<String> userId = new StateLiveData<>();
  private ValueEventListener messageListener;
  private ValueEventListener threadListener;

  /** TODO. */
  public CurrentCourseRepository() {
    this.initListener();
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
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
  public StateLiveData<List<MessageModel>> getMessages() {
    this.messages.postCreate(this.messagesList);
    return this.messages;
  }

  public StateLiveData<String> getThreadId() {
    return this.threadId;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public StateLiveData<ThreadModel> getThread() {
    return this.thread;
  }

  public StateLiveData<String> getUserId() {
    return this.userId;
  }

  /**
   * Loading all messages from the database.
   */
  public void loadMessages() {
    String threadKey = Validation.checkStateLiveData(this.threadId, TAG);
    if (threadKey == null) {
      Log.e(TAG, "threadKey is null.");
      return;
    }

    Query query = this.databaseReference.child(Config.CHILD_THREADS).child(threadKey)
            .child(Config.CHILD_MESSAGES);
    query.addValueEventListener(this.messageListener);
  }

  /**
   * This method saves a message in the data base.
   */
  public void sendMessage(MessageModel message) {
    String threadKey = Validation.checkStateLiveData(this.threadId, TAG);
    if (threadKey == null) {
      Log.e(TAG, "threadKey is null.");
      return;
    }

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }
    String uid = this.firebaseAuth.getCurrentUser().getUid();

    message.setCreatorId(uid);
    String messageId = this.databaseReference.getRoot().push().getKey();

    if (messageId == null) {
      Log.e(TAG, "messageid is null");
      this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    this.databaseReference.child(Config.CHILD_MESSAGES)
            .child(threadKey)
            .child(messageId)
            .setValue(message)
            .addOnSuccessListener(unused -> {
              updateAnswerCount();
              message.setKey(messageId);
            }).addOnFailureListener(e -> {
              Log.e(TAG, "Nachricht konnte nicht versent werden: " + e.getMessage());
            });

  }

  /** TODO. */
  public void updateAnswerCount() {
    String threadKey = Validation.checkStateLiveData(this.threadId, TAG);
    if (threadKey == null) {
      Log.e(TAG, "threadKey is null.");
      this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      Log.e(TAG, "threadKey is null.");
      this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    this.databaseReference
            .child(Config.CHILD_THREADS)
            .child(meetingObj.getKey())
            .child(threadKey)
            .child(Config.CHILD_ANSWER_COUNT)
            .setValue(ServerValue.increment(1));
  }

  public void setThread(ThreadModel threadModel) {
    this.thread.postCreate(threadModel);
  }

  /** TODO. */
  public void setUserId() {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.userId.postCreate(uid);
  }

  public void setMeetingId(MeetingsModel meeting) {
    this.meeting.postCreate(meeting);
  }

  /**
   * Sets the id of the new entered thread.
   */
  public void setThreadId(String threadId) {
    String threadKey = Validation.checkStateLiveData(this.threadId, TAG);
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      Log.e(TAG, "meetingObj is null.");
      return;
    }

    if (threadKey != null) {
      this.databaseReference.child(Config.CHILD_MESSAGES).child(threadKey)
              .removeEventListener(this.messageListener);

      this.databaseReference.child(Config.CHILD_THREADS).child(meetingObj.getKey())
              .child(threadKey).removeEventListener(this.threadListener);
    }

    this.databaseReference.child(Config.CHILD_MESSAGES).child(threadId)
            .addValueEventListener(this.messageListener);
    this.databaseReference.child(Config.CHILD_THREADS).child(meetingObj.getKey()).child(threadId)
            .addValueEventListener(this.threadListener);
    this.threadId.postCreate(threadId);
  }

  /** TODO. */
  public Task<DataSnapshot> getLikeStatusMessage(String id) {
    String userKey = Validation.checkStateLiveData(this.userId, TAG);
    if (userKey == null) {
      Log.e(TAG, "userKey is null.");
      return null;
    }

    return this.databaseReference.child(Config.CHILD_USER_LIKE).child(userKey).child(id).get();
  }

  /** TODO. */
  public void getLikeStatus(List<MessageModel> mesList) {
    List<Task<DataSnapshot>> likeList = new ArrayList<>();
    for (MessageModel message : mesList) {
      likeList.add(getLikeStatusMessage(message.getKey()));
    }
    //TODO optimize if else also in other classes
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
      messages.postUpdate(messagesList);
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
      thread.postUpdate(threadModel);
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
    return this.databaseReference.child(Config.CHILD_USER).child(authorId).get();
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

          if (model == null) {
            Log.e(TAG, "model is null");
            messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
            return;
          }

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

        if (threadModel == null) {
          Log.e(TAG, "threadModel is null");
          return;
        }

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
        Log.e(TAG, error.getMessage());
      }
    };
  }

  /** TODO. */
  public void handleLikeEvent(String messageId, int deltaCount, LikeStatus status) {
    String userKey = Validation.checkStateLiveData(this.userId, TAG);
    if (userKey == null) {
      Log.e(TAG, "userKey is null.");
      return;
    }

    String threadKey = Validation.checkStateLiveData(this.threadId, TAG);
    if (threadKey == null) {
      Log.e(TAG, "threadKey is null.");
      return;
    }

    if (status == LikeStatus.NEUTRAL) {
      this.databaseReference.child(Config.CHILD_USER_LIKE).child(userKey).child(messageId)
              .removeValue();
      this.databaseReference.child(Config.CHILD_LIKE_USER).child(messageId).child(userKey)
              .removeValue();
    } else {
      this.databaseReference.child(Config.CHILD_USER_LIKE).child(userKey).child(messageId)
              .setValue(status);
      this.databaseReference.child(Config.CHILD_LIKE_USER).child(messageId).child(userKey)
              .setValue(status);
    }
    this.databaseReference.child(Config.CHILD_MESSAGES).child(threadKey).child(messageId)
            .child(Config.CHILD_LIKE).setValue(ServerValue.increment(deltaCount));
  }

  /** TODO. */
  public void handleLikeEventThread(String threadId, int deltaCount, LikeStatus status) {
    String userKey = Validation.checkStateLiveData(this.userId, TAG);
    if (userKey == null) {
      Log.e(TAG, "userKey is null.");
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      Log.e(TAG, "meetingObj is null.");
      return;
    }

    if (status == LikeStatus.NEUTRAL) {
      this.databaseReference.child(Config.CHILD_USER_LIKE).child(userKey).child(threadId)
              .removeValue();
      this.databaseReference.child(Config.CHILD_LIKE_USER).child(threadId).child(userKey)
              .removeValue();
    } else {
      this.databaseReference.child(Config.CHILD_USER_LIKE).child(userKey).child(threadId)
              .setValue(status);
      this.databaseReference.child(Config.CHILD_LIKE_USER).child(threadId).child(userKey)
              .setValue(status);
    }
    this.databaseReference.child(Config.CHILD_THREADS).child(meetingObj.getKey()).child(threadId)
            .child(Config.CHILD_LIKE).setValue(ServerValue.increment(deltaCount));
  }

  /** TODO. */
  public void solved(String messageId) {
    ThreadModel threadObj = Validation.checkStateLiveData(this.thread, TAG);
    if (threadObj == null) {
      Log.e(TAG, "threadObj is null.");
      return;
    }

    String userKey = Validation.checkStateLiveData(this.userId, TAG);
    if (userKey == null) {
      Log.e(TAG, "userKey is null.");
      return;
    }

    String threadKey = Validation.checkStateLiveData(this.threadId, TAG);
    if (threadKey == null) {
      Log.e(TAG, "threadKey is null.");
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      Log.e(TAG, "meetingObj is null.");
      return;
    }

    boolean threadAnswered = threadObj.getAnswered();

    if (userKey.equals(threadObj.getCreatorId())) {
      this.databaseReference.child(Config.CHILD_MESSAGES)
              .child(threadKey)
              .child(messageId)
              .child(Config.CHILD_TOP_ANSWER)
              .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                  boolean topAnswer = snapshot.getValue(Boolean.class);
                  if (topAnswer && threadAnswered) {
                    //Thread is answered and the message is marked as answer
                    databaseReference.child(Config.CHILD_MESSAGES)
                            .child(threadKey)
                            .child(messageId)
                            .child(Config.CHILD_TOP_ANSWER)
                            .setValue(Boolean.FALSE);
                    databaseReference.child(Config.CHILD_THREADS)
                            .child(meetingObj.getKey())
                            .child(threadKey)
                            .child(Config.CHILD_ANSWERED)
                            .setValue(Boolean.FALSE);
                  } else if (!topAnswer && threadAnswered) {
                    //Thread is answered and the message is not marked as answer
                    Log.d(TAG, "onDataChange: " + "an message is already marked");
                    //TODO user feedback

                  } else if (!topAnswer) {
                    //Thread is not  answered and the message is not marked as answer
                    databaseReference.child(Config.CHILD_MESSAGES)
                            .child(threadKey)
                            .child(messageId)
                            .child(Config.CHILD_TOP_ANSWER)
                            .setValue(Boolean.TRUE);
                    databaseReference.child(Config.CHILD_THREADS)
                            .child(meetingObj.getKey())
                            .child(threadKey)
                            .child(Config.CHILD_ANSWERED)
                            .setValue(Boolean.TRUE);
                  }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                  Log.e(TAG, "solved failed: " + error.getMessage());
                }
              });
    }
  }

  /** TODO. */
  public void deleteThreadText(ThreadModel threadModel) {

    Log.d("Hier", "msg: Key of thread: " + threadModel.key);
    DatabaseReference databaseRefDelThread =
            this.databaseReference.child(Config.CHILD_THREADS)
            .child(meeting.getValue().getData().getKey())
            .child(threadId.getValue().getData());

    databaseRefDelThread
            .child("isTextDeleted")
            .setValue(true);

    databaseRefDelThread
            .child("text")
            .setValue("");

  }

  /** TODO. */
  public void deleteAnswerText(MessageModel messageModel) {

    Log.d("Hier", "msg: Key of thread: " + messageModel.getKey());
    DatabaseReference databaseRefDelThread =
            this.databaseReference.child(Config.CHILD_MESSAGES)
                    .child(threadId.getValue().getData())
                    .child(messageModel.getKey());

    databaseRefDelThread
            .child("text")
            .setValue("");

    databaseRefDelThread
            .child("isTextDeleted")
            .setValue(true);

  }



}
