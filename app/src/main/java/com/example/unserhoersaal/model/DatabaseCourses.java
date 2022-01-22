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
import java.util.HashMap;


public class DatabaseCourses {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference databaseReference;
    private final MutableLiveData<HashMap> userCourses = new MutableLiveData<HashMap>();
    private HashMap<String, String> coursesHashMap = new HashMap<String, String>();
    private ArrayList<String> courseIdList = new ArrayList<String>();
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
                courseIdList.clear();
                for (DataSnapshot userCourse : dataSnapshot.getChildren()) {
                    coursesHashMap.put(userCourse.getKey(), userCourse.getValue(String.class));
                }
                userCourses.setValue(coursesHashMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.child("User").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(userCoursesListener);
    }

    public MutableLiveData<HashMap> getUserCourses() {
        return userCourses;
    }
}