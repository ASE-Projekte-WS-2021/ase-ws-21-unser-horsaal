package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.MessageModel;
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

  /** Generates a unique instance of CurrentCourseRepository. */
  public static CurrentCourseRepository getInstance() {
    if (instance == null) {
      instance = new CurrentCourseRepository();
    }
    return instance;
  }

  /** This method provides all messages of a course. */
  public MutableLiveData<List<MessageModel>> getMessages() {
    if (this.messagesList.size() == 0) {
      this.loadMessages();
    }

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
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        HashSet<String> messageIds = new HashSet<>();
        messagesList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          messageIds.add(snapshot.getKey());
        }
        for (String key : messageIds) {
          reference.child(Config.CHILD_MESSAGES).child(key).addListenerForSingleValueEvent(
                  new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      MessageModel model = snapshot.getValue(MessageModel.class);
                      model.setKey(snapshot.getKey());
                      messagesList.add(model);
                      messages.postValue(messagesList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                  }
          );
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

  /** This method saves a message in the data base. */
  public void sendMessage(String messageText) {
    //todo correct path
    /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Long time = System.currentTimeMillis();
    MessageModel messageModel = new MessageModel(messageText, time);
    databaseReference.child(Config.CHILD_COURSES)
            .child(Objects.requireNonNull(this.threadId.getValue()))
            .child(Config.CHILD_MESSAGES).push().setValue(messageModel);

     */
  }

  public void setThreadId(String threadId) {
    this.threadId.postValue(threadId);
  }
}
