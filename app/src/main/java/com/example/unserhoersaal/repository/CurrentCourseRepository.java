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
  private final ArrayList<MessageModel> messagesList = new ArrayList<>(); //TODO: replace or rename
  private final StateLiveData<List<MessageModel>> allMessagesRepoState = new StateLiveData<>();
  private final StateLiveData<String> currentThreadIdRepoState = new StateLiveData<>();
  private final StateLiveData<MeetingsModel> currentMeetingRepoState = new StateLiveData<>();
  private final StateLiveData<ThreadModel> currentThreadRepoState = new StateLiveData<>();
  private final StateLiveData<String> currentUserIdRepoState = new StateLiveData<>();
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
  public StateLiveData<List<MessageModel>> getAllMessagesRepoState() {
    this.allMessagesRepoState.postCreate(this.messagesList);
    return this.allMessagesRepoState;
  }

  public StateLiveData<String> getCurrentThreadIdRepoState() {
    return this.currentThreadIdRepoState;
  }

  public StateLiveData<MeetingsModel> getCurrentMeetingRepoState() {
    return this.currentMeetingRepoState;
  }

  public StateLiveData<ThreadModel> getCurrentThreadRepoState() {
    return this.currentThreadRepoState;
  }

  public StateLiveData<String> getCurrentUserIdRepoState() {
    return this.currentUserIdRepoState;
  }

  /**
   * Loading all messages from the database.
   */
  public void loadMessages() {
    String threadKey = Validation.checkStateLiveData(this.currentThreadIdRepoState, TAG);
    if (threadKey == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_THREAD_ID);
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
    String threadKey = Validation.checkStateLiveData(this.currentThreadIdRepoState, TAG);
    if (threadKey == null) {
      Log.e(TAG,  Config.CURRENT_COURSE_NO_THREAD_ID);
      return;
    }

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.allMessagesRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }
    String uid = this.firebaseAuth.getCurrentUser().getUid();

    message.setCreatorId(uid);
    String messageId = this.databaseReference.getRoot().push().getKey();

    if (messageId == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_MESSAGE_ID);
      this.allMessagesRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
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
              Log.e(TAG, Config.CURRENT_COURSE_SEND_MESSAGE_FAILED + e.getMessage());
            });

  }

  /** TODO. */
  public void updateAnswerCount() {
    String threadKey = Validation.checkStateLiveData(this.currentThreadIdRepoState, TAG);
    if (threadKey == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_THREAD_ID);
      this.allMessagesRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.currentMeetingRepoState, TAG);
    if (meetingObj == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_THREAD_ID);
      this.allMessagesRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    this.databaseReference
            .child(Config.CHILD_THREADS)
            .child(meetingObj.getKey())
            .child(threadKey)
            .child(Config.CHILD_ANSWER_COUNT)
            .setValue(ServerValue.increment(1));
  }

  /** TODO. */
  public void setUserId() {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.currentUserIdRepoState.postCreate(uid);
  }

  public void setMeetingId(MeetingsModel meeting) {
    this.currentMeetingRepoState.postCreate(meeting);
  }

  /**
   * Sets the id of the new entered thread.
   */
  public void setCurrentThreadIdRepoState(String currentThreadIdRepoState) {
    String threadKey = Validation.checkStateLiveData(this.currentThreadIdRepoState, TAG);
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.currentMeetingRepoState, TAG);
    if (meetingObj == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_MEETING_MODEL);
      return;
    }

    if (threadKey != null) {
      this.databaseReference.child(Config.CHILD_MESSAGES).child(threadKey)
              .removeEventListener(this.messageListener);

      this.databaseReference.child(Config.CHILD_THREADS).child(meetingObj.getKey())
              .child(threadKey).removeEventListener(this.threadListener);
    }

    this.databaseReference.child(Config.CHILD_MESSAGES).child(currentThreadIdRepoState)
            .addValueEventListener(this.messageListener);
    this.databaseReference.child(Config.CHILD_THREADS).child(meetingObj.getKey()).child(currentThreadIdRepoState)
            .addValueEventListener(this.threadListener);
    this.currentThreadIdRepoState.postCreate(currentThreadIdRepoState);
  }

  /** TODO. */
  public Task<DataSnapshot> getLikeStatusMessage(String id) {
    String userKey = Validation.checkStateLiveData(this.currentUserIdRepoState, TAG);
    if (userKey == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_USER_ID);
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
      allMessagesRepoState.postUpdate(messagesList);
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
      currentThreadRepoState.postUpdate(threadModel);
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
            Log.e(TAG, Config.CURRENT_COURSE_NO_MODEL);
            allMessagesRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
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
          Log.e(TAG, Config.CURRENT_COURSE_NO_THREAD_MODEL);
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
    String userKey = Validation.checkStateLiveData(this.currentUserIdRepoState, TAG);
    if (userKey == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_USER_ID);
      return;
    }

    String threadKey = Validation.checkStateLiveData(this.currentThreadIdRepoState, TAG);
    if (threadKey == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_THREAD_ID);
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
    String userKey = Validation.checkStateLiveData(this.currentUserIdRepoState, TAG);
    if (userKey == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_USER_ID);
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.currentMeetingRepoState, TAG);
    if (meetingObj == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_MEETING_MODEL);
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
    ThreadModel threadObj = Validation.checkStateLiveData(this.currentThreadRepoState, TAG);
    if (threadObj == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_THREAD_OBJECT);
      return;
    }

    String userKey = Validation.checkStateLiveData(this.currentUserIdRepoState, TAG);
    if (userKey == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_USER_ID);
      return;
    }

    String threadKey = Validation.checkStateLiveData(this.currentThreadIdRepoState, TAG);
    if (threadKey == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_THREAD_ID);
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.currentMeetingRepoState, TAG);
    if (meetingObj == null) {
      Log.e(TAG, Config.CURRENT_COURSE_NO_MEETING_MODEL);
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
                    Log.d(TAG, "onDataChange: " + Config.CURRENT_COURSE_MESSAGE_MARKED);
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

}
