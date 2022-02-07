package com.example.unserhoersaal.repository;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**Class communicates with Firebase for getting current course data.**/

public class CurrentCourseRepository {

  private static final String TAG = "CurrentCourseRepo";

  private static CurrentCourseRepository instance;
  private ArrayList<Message> messagesList = new ArrayList<Message>();
  private MutableLiveData<List<Message>> messages = new MutableLiveData<>();
  private String courseId;

  public static CurrentCourseRepository getInstance(){
    if(instance == null){
      instance = new CurrentCourseRepository();
    }
    return instance;
  }

  public MutableLiveData<List<Message>> getMessages(){
    if(messagesList.size() == 0) {
      loadMessages();
    }

    messages.setValue(messagesList);
    return messages;
  }

  public void loadMessages(){
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    Query query = reference.child("Courses").child(courseId).child("Messages");
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.d("Message", "onDataChange: ");
        messagesList.clear();
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
          messagesList.add(snapshot.getValue(Message.class));
        }
        messages.postValue(messagesList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }

  public void sendMessage(String messageText) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Long time = System.currentTimeMillis();
    Message message = new Message(messageText, time);
    databaseReference.child("Courses").child(courseId).child("Messages").push().setValue(message);
  }

  public void setCourseId(String courseId){
    this.courseId = courseId;
  }
}
