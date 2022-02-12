package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**Class communicates with Firebase for getting current course data.**/
public class CurrentCourseRepository {

  private static final String TAG = "CurrentCourseRepo";

  private static CurrentCourseRepository instance;

  private ArrayList<Message> messagesList = new ArrayList<Message>();
  private MutableLiveData<List<Message>> messages = new MutableLiveData<>();
  private MutableLiveData<String> courseId = new MutableLiveData<>();

  /** Generates a unique instance of CurrentCourseRepository. */
  public static CurrentCourseRepository getInstance() {
    if (instance == null) {
      instance = new CurrentCourseRepository();
    }
    return instance;
  }

  /** This method provides all messages of a course. */
  public MutableLiveData<List<Message>> getMessages() {
    if (this.messagesList.size() == 0) {
      this.loadMessages();
    }

    this.messages.setValue(this.messagesList);
    return this.messages;
  }

  public MutableLiveData<String> getCourseId() {
    return this.courseId;
  }

  /** Loading all messages from the database. */
  public void loadMessages() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.child(Config.CHILD_COURSES)
            .child(Objects.requireNonNull(this.courseId.getValue())).child(Config.CHILD_MESSAGES);
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        messagesList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          messagesList.add(snapshot.getValue(Message.class));
        }
        messages.postValue(messagesList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

  /** This method saves a message in the data base. */
  public void sendMessage(String messageText) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Long time = System.currentTimeMillis();
    Message message = new Message(messageText, time);
    databaseReference.child(Config.CHILD_COURSES)
            .child(Objects.requireNonNull(this.courseId.getValue()))
            .child(Config.CHILD_MESSAGES).push().setValue(message);
  }

  public void setCourseId(String courseId) {
    this.courseId.postValue(courseId);
  }
}
