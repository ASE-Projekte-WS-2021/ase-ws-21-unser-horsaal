package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
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

  //TODO Meeting id needed instead of threadId
  public void updateAnswerCount() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_THREADS).child(this.meetingId.getValue())
            .child(this.threadId.getValue()).child(Config.CHILD_ANSWER_COUNT)
            .setValue(ServerValue.increment(1));
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
        messagesList.clear();
        messagesList.addAll(mesList);
        messages.postValue(messagesList);
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

  public void like(String messageId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_MESSAGES).child(this.threadId.getValue()).child(messageId)
            .child(Config.CHILD_LIKE).setValue(ServerValue.increment(1));
  }

  public void dislike(String messageId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_MESSAGES).child(this.threadId.getValue()).child(messageId)
            .child(Config.CHILD_LIKE).setValue(ServerValue.increment(-1));
  }

  public void solved(String messageId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_MESSAGES).child(this.threadId.getValue()).child(messageId)
            .child(Config.CHILD_TOP_ANSWER).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        boolean topAnswer = snapshot.getValue(Boolean.class) ? Boolean.FALSE : Boolean.TRUE;
        reference.child(Config.CHILD_MESSAGES).child(threadId.getValue()).child(messageId)
                .child(Config.CHILD_TOP_ANSWER).setValue(topAnswer);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }
}
