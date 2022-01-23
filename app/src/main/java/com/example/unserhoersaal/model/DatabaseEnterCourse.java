package com.example.unserhoersaal.model;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseEnterCourse {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference databaseReference;
    private final MutableLiveData<ArrayList> messages = new MutableLiveData<ArrayList>();
    private ArrayList<DatabaseCourses.Message> messagesList = new ArrayList<DatabaseCourses.Message>();
    private FirebaseAuth firebaseAuth;
    private final MutableLiveData<ThreeState> courseIdIsCorrect = new MutableLiveData<ThreeState>();
    public enum ThreeState {
        TRUE,
        FALSE,
        TRALSE
    };
    //private String courseId;


    public DatabaseEnterCourse() {
        this.firebaseDB = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app");
        this.databaseReference = firebaseDB.getReference();
        this.firebaseAuth = FirebaseAuth.getInstance();
        courseIdIsCorrect.setValue(ThreeState.TRALSE);
    }

    public MutableLiveData<ThreeState> saveUserCourses(String courseId) {
        this.courseIdIsCorrect.setValue(ThreeState.TRALSE);
        this.databaseReference.child("Courses").child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String courseName = dataSnapshot.child("courseName").getValue(String.class);
                    courseIdIsCorrect.setValue(ThreeState.TRUE);
                    isUserInCourse(courseId, courseName);
                }else{
                    courseIdIsCorrect.setValue(ThreeState.FALSE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return courseIdIsCorrect;
    }

    public void isUserInCourse(String courseId, String courseName) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    saveCourseInUser(courseId, courseName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        };
        this.databaseReference.child("User").child(firebaseAuth.getCurrentUser()
                .getUid()).child(courseId).addListenerForSingleValueEvent(eventListener);
    }

    public void saveCourseInUser(String courseId, String courseName){

        if (firebaseAuth.getCurrentUser() != null){
            this.databaseReference.child("User").child(firebaseAuth.getCurrentUser()
                    .getUid()).child(courseId).setValue(courseName);
        }
    }

}
