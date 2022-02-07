package com.example.unserhoersaal.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.model.UserCourse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CoursesRepository {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference databaseReference;
    private final MutableLiveData<ArrayList<UserCourse>> userCourses = new MutableLiveData<ArrayList<UserCourse>>();
    private ArrayList<UserCourse> userCoursesList = new ArrayList<UserCourse>();
    private FirebaseAuth firebaseAuth;


    public CoursesRepository() {
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
}