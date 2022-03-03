package com.example.unserhoersaal.repository;

import android.util.Log;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.LikeStatus;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.google.android.gms.tasks.OnSuccessListener;
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

  private ArrayList<MessageModel> messagesList = new ArrayList<MessageModel>();
  private MutableLiveData<List<MessageModel>> messages = new MutableLiveData<>();
  private MutableLiveData<String> threadId = new MutableLiveData<>();
  private MutableLiveData<String> meetingId = new MutableLiveData<>();
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

  public MutableLiveData<String> getMeetingId() {
    return this.meetingId;
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
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                updateAnswerCount();
                message.setKey(messageId);
              }
            });

  }

  public void updateAnswerCount() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_THREADS).child(this.meetingId.getValue())
            .child(this.threadId.getValue()).child(Config.CHILD_ANSWER_COUNT)
            .setValue(ServerValue.increment(1));
  }

  public void setUserId() {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    this.userId.postValue(uid);
  }

  public void setMeetingId(String meetingId) {
    this.meetingId.postValue(meetingId);
  }

  /**
   * Sets the id of the new entered thread.
   */
  public void setThreadId(String threadId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.threadId.getValue() != null) {
      reference.child(Config.CHILD_MESSAGES).child(this.threadId.getValue())
              .removeEventListener(this.messageListener);
      reference.child(Config.CHILD_THREADS).child(this.meetingId.getValue())
              .child(this.threadId.getValue()).removeEventListener(this.threadListener);
    }
    reference.child(Config.CHILD_MESSAGES).child(threadId)
            .addValueEventListener(this.messageListener);
    reference.child(Config.CHILD_THREADS).child(this.meetingId.getValue()).child(threadId)
            .addValueEventListener(this.threadListener);
    this.threadId.postValue(threadId);
  }

  public Task<DataSnapshot> getLikeStatusMessage(String messageId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER_LIKE).child(userId.getValue()).child(messageId).get();
  }

  public void getLikeStatus(List<MessageModel> mesList) {
    List<Task<DataSnapshot>> likeList = new ArrayList<>();
    for (MessageModel message : mesList) {
      likeList.add(getLikeStatusMessage(message.getKey()));
    }
    Tasks.whenAll(likeList).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void unused) {
        for (int i = 0; i < likeList.size(); i++) {
          if (!likeList.get(i).getResult().exists()) {
            mesList.get(i).setLikeStatus(LikeStatus.NEUTRAL);
          } else if (likeList.get(i).getResult().getValue(LikeStatus.class) == LikeStatus.LIKE){
            mesList.get(i).setLikeStatus(LikeStatus.LIKE);
          } else if (likeList.get(i).getResult().getValue(LikeStatus.class) == LikeStatus.DISLIKE){
            mesList.get(i).setLikeStatus(LikeStatus.DISLIKE);
          }
        }
        messagesList.clear();
        messagesList.addAll(mesList);
        messages.postValue(messagesList);
      }
    });
  }

  public void getAuthor(List<MessageModel> mesList) {
    List<Task<DataSnapshot>> authorNames = new ArrayList<>();
    for (MessageModel message : mesList) {
      authorNames.add(getAuthorName(message.getCreatorId()));
    }
    Tasks.whenAll(authorNames).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void unused) {
        for (int i = 0; i < authorNames.size(); i++) {
          mesList.get(i).setCreatorName(authorNames.get(i).getResult().getValue(String.class));
        }
        getLikeStatus(mesList);
      }
    });
  }

  public Task<DataSnapshot> getAuthorName(String authorId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER).child(authorId).child(Config.CHILD_USER_NAME).get();
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
        Task<DataSnapshot> task = getAuthorName(threadModel.creatorId);
        task.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
          @Override
          public void onSuccess(DataSnapshot dataSnapshot) {
            threadModel.setCreatorName(task.getResult().getValue(String.class));
            thread.postValue(threadModel);
          }
        });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    };
  }

  //TODO auslagern in extra methoden
  public void like(MessageModel message) {
    String messageId = message.getKey();
    LikeStatus likeStatus = message.getLikeStatus();
    switch(likeStatus){
      case LIKE:
        handleLikeEvent(messageId,-1,LikeStatus.NEUTRAL);
        break;
      case DISLIKE:
        handleLikeEvent(messageId,2,LikeStatus.LIKE);
        break;
      case NEUTRAL:
        handleLikeEvent(messageId,1,LikeStatus.LIKE);
        break;
      default:
        break;
    }

  }

  public void dislike(MessageModel message) {
    String messageId = message.getKey();
    LikeStatus likeStatus = message.getLikeStatus();
    switch(likeStatus) {
      case LIKE:
        handleLikeEvent(messageId,-2,LikeStatus.DISLIKE);
        break;
      case DISLIKE:
        handleLikeEvent(messageId,1,LikeStatus.NEUTRAL);
        break;
      case NEUTRAL:
        handleLikeEvent(messageId,-1,LikeStatus.DISLIKE);
        break;
      default:
        break;
    }
  }

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

  public void solved(String messageId) {
    boolean threadAnswered = this.thread.getValue().getAnswered();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (userId.getValue().equals(thread.getValue().getCreatorId())) {
      reference.child(Config.CHILD_MESSAGES).child(this.threadId.getValue()).child(messageId)
              .child(Config.CHILD_TOP_ANSWER).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          boolean topAnswer = snapshot.getValue(Boolean.class);
          if (topAnswer && threadAnswered) {
            //Thread is answered and the message is marked as answer
            reference.child(Config.CHILD_MESSAGES).child(threadId.getValue()).child(messageId)
                    .child(Config.CHILD_TOP_ANSWER).setValue(Boolean.FALSE);
            reference.child(Config.CHILD_THREADS).child(meetingId.getValue())
                    .child(threadId.getValue()).child(Config.CHILD_ANSWERED).setValue(Boolean.FALSE);
          } else if (!topAnswer && threadAnswered) {
            //Thread is answered and the message is not marked as answer
            Log.d(TAG, "onDataChange: " + "an message is already marked");
            //TODO user feedback

          } else if (!topAnswer && !threadAnswered) {
            //Thread is not  answered and the message is not marked as answer
            reference.child(Config.CHILD_MESSAGES).child(threadId.getValue()).child(messageId)
                    .child(Config.CHILD_TOP_ANSWER).setValue(Boolean.TRUE);
            reference.child(Config.CHILD_THREADS).child(meetingId.getValue())
                    .child(threadId.getValue()).child(Config.CHILD_ANSWERED).setValue(Boolean.TRUE);
          }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    }
  }
}
