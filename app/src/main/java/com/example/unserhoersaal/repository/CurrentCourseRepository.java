package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.enums.LikeStatus;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/** Class communicates with Firebase for getting current course data. **/
public class CurrentCourseRepository {

  private static final String TAG = "CurrentCourseRepo";

  private static CurrentCourseRepository instance;
  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private final ArrayList<MessageModel> messagesList = new ArrayList<>();
  private final StateLiveData<List<MessageModel>> messages = new StateLiveData<>();
  private final StateLiveData<String> threadId = new StateLiveData<>();
  private final StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private final StateLiveData<ThreadModel> thread = new StateLiveData<>();
  private final StateLiveData<String> userId = new StateLiveData<>();

  /**
   * Constructor. Gets the database instances.
   */
  public CurrentCourseRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
  }

  /**
   * Generates a unique instance of CurrentCourseRepository.
   *
   * @return Instance of the CurrentCourseRepository
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
  private void loadMessages() {
    String threadKey = Validation.checkStateLiveData(this.threadId, TAG);
    if (threadKey == null) {
      Log.e(TAG, "threadKey is null.");
      return;
    }

    Query query = this.databaseReference.child(Config.CHILD_MESSAGES).child(threadKey);
    query.addValueEventListener(new ValueEventListener() {
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
    });
  }

  /**
   * Load the data of the current thread from the database.
   */
  private void loadThread() {
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
    this.databaseReference.child(Config.CHILD_THREADS).child(meetingObj.getKey()).child(threadKey)
            .addValueEventListener(new ValueEventListener() {
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
            });

  }

  /**
   * This method saves a message in the data base.
   *
   * @param message data of the message
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
      Log.e(TAG, "messageId is null");
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
            }).addOnFailureListener(e ->
              Log.e(TAG, "Nachricht konnte nicht versent werden: " + e.getMessage())
            );

  }

  /**
   * Increases the count of answers in a thread.
   */
  private void updateAnswerCount() {
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

  /**
   * Sets the id of the current user.
   */
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
    if (threadId == null) {
      return;
    }
    if (threadKey == null
            || !threadKey.equals(threadId)) {
      this.threadId.postUpdate(threadId);
      this.loadThread();
      this.loadMessages();
    }
  }

  /**
   * Loads the like status of a message.
   *
   * @param id id of the message
   */
  private Task<DataSnapshot> getLikeStatusMessage(String id) {
    String userKey = Validation.checkStateLiveData(this.userId, TAG);
    if (userKey == null) {
      Log.e(TAG, "userKey is null.");
      return null;
    }

    return this.databaseReference.child(Config.CHILD_USER_LIKE).child(userKey).child(id).get();
  }

  /** Manages the like status of all messages of a thread.
   *
   * @param mesList list of all messages of a thread
   */
  private void getLikeStatus(List<MessageModel> mesList) {
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
      messages.postUpdate(messagesList);
    });

  }

  /**
   * Manages the like status of a thread.
   *
   * @param threadModel data of the thread
   */
  private void getLikeStatusThread(ThreadModel threadModel) {
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

  /**
   * Loads the data of the author for all messages of a thread.
   *
   * @param mesList list of all messages of a thread
   */
  private void getAuthor(List<MessageModel> mesList) {
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

  /**
   * Load the author data from database.
   *
   * @param authorId id of the author, for which the data is loaded
   */
  private Task<DataSnapshot> getAuthorModel(String authorId) {
    return this.databaseReference.child(Config.CHILD_USER).child(authorId).get();
  }

  /**
   * React to a user liking/disliking a message.
   *
   * @param messageId Id of the liked/disliked message
   * @param deltaCount amount the like count of the message changes
   * @param status new like status of the message
   */
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

  /**
   * React to a user liking/disliking a thread.
   *
   * @param threadId Id of the liked/disliked thread
   * @param deltaCount amount the like count of the thread changes
   * @param status new like status of the thread
   */
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

  /**
   * Toggles a message between solved and unsolved.
   *
   * @param messageId id of the toggled message
   */
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

}
