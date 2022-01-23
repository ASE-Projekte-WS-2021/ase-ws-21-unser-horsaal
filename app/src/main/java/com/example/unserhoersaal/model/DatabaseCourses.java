package com.example.unserhoersaal.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DatabaseCourses {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference databaseReference;
    private final MutableLiveData<ArrayList<UserCourse>> userCourses = new MutableLiveData<ArrayList<UserCourse>>();
    private ArrayList<UserCourse> userCoursesList = new ArrayList<UserCourse>();
    private FirebaseAuth firebaseAuth;


    public DatabaseCourses() {
        this.firebaseDB = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app");
        this.databaseReference = firebaseDB.getReference();
        this.firebaseAuth = FirebaseAuth.getInstance();
        setupUserCoursesListener();
    }

    public void setupUserCoursesListener() {
        ValueEventListener userCoursesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userCoursesList.clear();
                for (DataSnapshot userCourse : dataSnapshot.getChildren()) {
                    userCoursesList.add(new UserCourse(userCourse.getKey(), userCourse.getValue(String.class)));
                }
                userCourses.setValue(userCoursesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.child("User").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(userCoursesListener);
    }

    public MutableLiveData<ArrayList<UserCourse>> getUserCourses() {
        return userCourses;
    }

    public static class Message {
        public String messageText;
        public Long time;


        public Message() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Message(String messageText, Long time) {
            this.messageText = messageText;
            this.time = time;

        }

        public Long getTime() {
            return time;
        }

        public String getMessageText() {
            return messageText;
        }

        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }

        public void setTime(Long time) {
            this.time = time;
        }
    }
}