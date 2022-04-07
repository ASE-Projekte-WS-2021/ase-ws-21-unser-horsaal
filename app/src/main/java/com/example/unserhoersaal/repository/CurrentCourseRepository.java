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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Class communicates with Firebase for getting current course data. **/
public class CurrentCourseRepository {

  private static final String TAG = "CurrentCourseRepo";

  private static CurrentCourseRepository instance;
  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private final ArrayList<MessageModel> messagesList = new ArrayList<>();
  private final StateLiveData<List<MessageModel>> messages = new StateLiveData<>();
  private final StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private final StateLiveData<ThreadModel> thread = new StateLiveData<>();
  private final StateLiveData<String> userId = new StateLiveData<>();
  private final HashSet<String> messageSet = new HashSet<>();
  private ValueEventListener threadListener;
  private ValueEventListener messageListener;

  /**
   * Constructor. Gets the database instances.
   */
  public CurrentCourseRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    initListener();
  }

  /**
   * Initialize the listener for the current thread and messages.
   */
  private void initListener() {
    this.threadListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        ThreadModel threadModel = snapshot.getValue(ThreadModel.class);
        if (threadModel == null) {
          return;
        }
        threadModel.setKey(snapshot.getKey());
        getThreadAuthor(threadModel);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, error.getMessage());
      }
    };
    this.messageListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        updateMessageSet(dataSnapshot);
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          MessageModel model = snapshot.getValue(MessageModel.class);

          if (model == null) {
            continue;
          }

          model.setKey(snapshot.getKey());
          getAuthor(model);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      }
    };
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
    ThreadModel threadObj = Validation.checkStateLiveData(this.thread, TAG);
    if (threadObj == null) {
      messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }
    String threadKey = threadObj.getKey();
    if (threadKey == null) {
      messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    Query query = this.databaseReference.child(Config.CHILD_MESSAGES).child(threadKey);
    query.addValueEventListener(this.messageListener);
  }

  /**
   * Update the list of all messages in the thread.
   *
   * @param dataSnapshot snapshot with messages
   */
  private void updateMessageSet(DataSnapshot dataSnapshot) {
    HashSet<String> messageIds = new HashSet<>();
    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
      messageIds.add(snapshot.getKey());
    }
    this.messageSet.clear();
    this.messageSet.addAll(messageIds);
  }

  /**
   * Load the data of the current thread from the database.
   */
  private void loadThread() {
    ThreadModel threadObj = Validation.checkStateLiveData(this.thread, TAG);
    if (threadObj == null) {
      return;
    }
    String threadKey = threadObj.getKey();
    if (threadKey == null) {
      return;
    }
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      return;
    }
    this.databaseReference.child(Config.CHILD_THREADS).child(meetingObj.getKey()).child(threadKey)
            .addValueEventListener(this.threadListener);

  }

  /**
   * This method saves a message in the data base.
   *
   * @param message data of the message
   */
  public void sendMessage(MessageModel message) {
    ThreadModel threadObj = Validation.checkStateLiveData(this.thread, TAG);
    if (threadObj == null) {
      this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }
    String threadKey = threadObj.getKey();
    if (threadKey == null) {
      this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
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
            this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO));
  }

  /**
   * Increases the count of answers in a thread.
   */
  private void updateAnswerCount() {
    ThreadModel threadObj = Validation.checkStateLiveData(this.thread, TAG);
    if (threadObj == null) {
      return;
    }
    String threadKey = threadObj.getKey();
    if (threadKey == null) {
      this.messages.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
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
   * Sets the id of the new entered thread.
   */
  public void setThread(ThreadModel threadModel) {
    if (threadModel == null || threadModel.getKey() == null) {
      return;
    }
    ThreadModel threadObj = Validation.checkStateLiveData(this.thread, TAG);
    if (threadObj == null) {
      this.thread.postUpdate(threadModel);
      this.messagesList.clear();
      this.loadThread();
      this.loadMessages();
      return;
    }
    String threadKey = threadObj.getKey();
    if (threadKey == null) {
      this.thread.postUpdate(threadModel);
      this.messagesList.clear();
      this.loadThread();
      this.loadMessages();
    } else if (!threadKey.equals(threadModel.getKey())) {
      this.thread.postUpdate(threadModel);
      this.messagesList.clear();
      MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
      if (meetingObj == null) {
        return;
      }
      this.databaseReference.child(Config.CHILD_THREADS).child(meetingObj.getKey()).child(threadKey)
              .removeEventListener(this.threadListener);
      this.databaseReference.child(Config.CHILD_MESSAGES).child(threadKey)
              .removeEventListener(this.messageListener);
      this.loadThread();
      this.loadMessages();
    }
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

  public void setMeeting(MeetingsModel meeting) {
    this.meeting.postCreate(meeting);
  }

  /**
   * Loads the data of the author for a message.
   *
   * @param messageModel data of the message
   */
  private void getAuthor(MessageModel messageModel) {
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
                setLikeStatus(messageModel, messagesList);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                messages.postError(new Error(Config.MESSAGES_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
  }

  /**
   * Loads the data of the author for a message.
   *
   * @param threadModel data of the message
   */
  private void getThreadAuthor(ThreadModel threadModel) {
    this.databaseReference.child(Config.CHILD_USER).child(threadModel.getCreatorId())
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel author = snapshot.getValue(UserModel.class);
                if (author == null) {
                  threadModel.setCreatorName(Config.UNKNOWN_USER);
                } else {
                  threadModel.setCreatorName(author.getDisplayName());
                  threadModel.setPhotoUrl(author.getPhotoUrl());
                }
                setLikeStatusThread(threadModel);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                messages.postError(new Error(Config.THREADS_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
  }

  /**
   * Update the thread.
   *
   * @param threadModel data of the thread
   */
  private void setLikeStatusThread(ThreadModel threadModel) {
    Task<DataSnapshot> task = this.getLikeStatus(threadModel.getKey());
    if (task == null) {
      threadModel.setLikeStatus(LikeStatus.NEUTRAL);
      this.thread.postUpdate(threadModel);
    } else {
      task.addOnSuccessListener(runnable -> {
        LikeStatus status = runnable.getValue(LikeStatus.class);
        if (status == null) {
          status = LikeStatus.NEUTRAL;
        }
        threadModel.setLikeStatus(status);
        this.thread.postUpdate(threadModel);
      });
    }
  }

  /**
   * Update all messages if a message has changed.
   *
   * @param messageModel data of the change message
   * @param messageModelList all messages
   */
  private void setLikeStatus(MessageModel messageModel, List<MessageModel> messageModelList) {
    Task<DataSnapshot> task = this.getLikeStatus(messageModel.getKey());
    if (task == null) {
      messageModel.setLikeStatus(LikeStatus.NEUTRAL);
      updateMessageList(messageModel, messageModelList);
      return;
    }
    task.addOnSuccessListener(runnable -> {
      LikeStatus status = runnable.getValue(LikeStatus.class);
      if (status == null) {
        status = LikeStatus.NEUTRAL;
      }
      messageModel.setLikeStatus(status);
      updateMessageList(messageModel, messageModelList);
    });
  }

  private Task<DataSnapshot> getLikeStatus(String id) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      return null;
    }
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    return this.databaseReference.child(Config.CHILD_USER_LIKE).child(uid).child(id).get();
  }

  /**
   * Update threads if a thread has changed.
   *
   * @param messageModel data of the changed thread
   * @param messageModelList all threads of the meeting
   */
  private void updateMessageList(MessageModel messageModel, List<MessageModel> messageModelList) {
    for (int i = 0; i < messageModelList.size(); i++) {
      MessageModel model = messageModelList.get(i);
      if (model.getKey().equals(messageModel.getKey())) {
        if (this.messageSet.contains(messageModel.getKey())) {
          //update
          messageModelList.set(i, messageModel);
        } else {
          //remove
          messageModelList.remove(i);
        }
        this.messages.postUpdate(messagesList);
        return;
      }
    }
    //add
    if (this.messageSet.contains(messageModel.getKey())) {
      messageModelList.add(messageModel);
      this.messages.postUpdate(messagesList);
    }
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
      return;
    }

    ThreadModel threadObj = Validation.checkStateLiveData(this.thread, TAG);
    if (threadObj == null) {
      return;
    }

    String threadKey = threadObj.getKey();
    if (threadKey == null) {
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
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
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
      return;
    }

    String userKey = Validation.checkStateLiveData(this.userId, TAG);
    if (userKey == null) {
      return;
    }

    String threadKey = threadObj.getKey();
    if (threadKey == null) {
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
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
                  Boolean topAnswer = snapshot.getValue(Boolean.class);

                  if (Boolean.TRUE.equals(topAnswer) && threadAnswered) {
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
                  }  else if (Boolean.FALSE.equals(topAnswer)) {
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
                  thread.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
                }
              });
    }
  }

  /**
   * Deletes the message of the current thread.
   */
  public void deleteThreadText() {
    ThreadModel threadObj = Validation.checkStateLiveData(this.thread, TAG);
    if (threadObj == null) {
      return;
    }

    String threadKey = threadObj.getKey();
    if (threadKey == null) {
      return;
    }

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      return;
    }

    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      return;
    }

    DatabaseReference databaseRefDelThread =
            this.databaseReference.child(Config.CHILD_THREADS)
            .child(meetingKey)
            .child(threadKey);

    databaseRefDelThread
            .child(Config.CHILD_TEXT_DELETED)
            .setValue(Boolean.TRUE);

    databaseRefDelThread
            .child(Config.CHILD_TEXT)
            .setValue(Config.OPTION_EMPTY);

  }

  /**
   * Deletes the text of an answer for the current thread.
   *
   * @param messageModel data of the chosen method
   */
  public void deleteAnswerText(MessageModel messageModel) {
    ThreadModel threadObj = Validation.checkStateLiveData(this.thread, TAG);
    if (threadObj == null) {
      return;
    }

    String threadKey = threadObj.getKey();
    if (threadKey == null) {
      return;
    }

    DatabaseReference databaseRefDelThread =
            this.databaseReference.child(Config.CHILD_MESSAGES)
                    .child(threadKey)
                    .child(messageModel.getKey());

    databaseRefDelThread
            .child(Config.CHILD_TEXT)
            .setValue(Config.OPTION_EMPTY);

    databaseRefDelThread
            .child(Config.CHILD_TEXT_DELETED)
            .setValue(Boolean.TRUE);

  }

}
