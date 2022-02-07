package com.example.unserhoersaal.repository;


import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**Class communicates with Firebase for getting current course data.**/

public class CurrentCourseRepository {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference databaseReference;
    private final MutableLiveData<ArrayList> messages = new MutableLiveData<ArrayList>();
    private ArrayList<CoursesRepository.Message> messagesList = new ArrayList<CoursesRepository.Message>();
    private String courseId;

  /**Class constructor.**/

    public CurrentCourseRepository() {
        this.firebaseDB = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app");
        this.databaseReference = firebaseDB.getReference();
    }

    public void sendMessage(String messageText) {
        Long time = System.currentTimeMillis();
        CoursesRepository.Message message = new CoursesRepository.Message(messageText, time);
        databaseReference.child("Courses").child(courseId).child("Messages").push().setValue(message);
    }

    public MutableLiveData<ArrayList> getMessages() {
        return messages;
    }

    public void setCourseId(String courseId){
        this.courseId = courseId;
    }

    public void setupListeners() {
        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    messagesList.add(messageSnapshot.getValue(CoursesRepository.Message.class));
                }
                messages.setValue(messagesList);
            }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.w("loadPost:onCancelled", databaseError.toException());
      }
    };
    databaseReference.child("Courses").child(courseId)
            .child("Messages").addValueEventListener(messageListener);
  }
}
