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
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**Class communicates with Firebase for getting current course data.**/
public class CurrentCourseRepository {

  private static final String TAG = "CurrentCourseRepo";

  private static CurrentCourseRepository instance;

  private ArrayList<MessageModel> messagesList = new ArrayList<MessageModel>();
  private MutableLiveData<List<MessageModel>> messages = new MutableLiveData<>();
  private MutableLiveData<String> threadId = new MutableLiveData<>();
  private ValueEventListener listener;

  public CurrentCourseRepository() {
    initListener();
  }

  /** Generates a unique instance of CurrentCourseRepository. */
  public static CurrentCourseRepository getInstance() {
    if (instance == null) {
      instance = new CurrentCourseRepository();
    }
    return instance;
  }

  /** This method provides all messages of a course. */
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

  /** Loading all messages from the database. */
  public void loadMessages() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.child(Config.CHILD_THREADS).child(this.threadId.getValue())
            .child(Config.CHILD_MESSAGES);
    query.addValueEventListener(this.listener);
  }

  /** This method saves a message in the data base. */
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
                //todo needed?
                message.setKey(messageId);
              }
            });

  }

  /** Sets the id of the new entered thread. */
  public void setThreadId(String threadId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.threadId.getValue() != null) {
      reference.child(Config.CHILD_MESSAGES).child(this.threadId.getValue())
              .removeEventListener(this.listener);
    }
    reference.child(Config.CHILD_MESSAGES).child(threadId).addValueEventListener(this.listener);
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

  /** Initialise the listener for the database access. */
  public void initListener() {
    this.listener = new ValueEventListener() {
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
  }
}
