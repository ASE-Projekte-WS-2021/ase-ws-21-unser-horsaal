package com.example.unserhoersaal.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserCourse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CoursesRepository {

    private static CoursesRepository instance;
    private ArrayList<UserCourse> userCoursesList = new ArrayList<UserCourse>();
    private MutableLiveData<List<UserCourse>> courses = new MutableLiveData<>();

    public static CoursesRepository getInstance(){
        if(instance == null){
            instance = new CoursesRepository();
        }
        return instance;
    }

    public MutableLiveData<List<UserCourse>> getUserCourses(){
        if(userCoursesList.size() == 0) {
            loadUserCourses();
        }

        courses.setValue(userCoursesList);
        return courses;
    }

    public void loadUserCourses(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        Query query = reference.child("User").child(auth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCoursesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userCoursesList.add(new UserCourse(snapshot.getKey(), snapshot.getValue(String.class)));
                }
                courses.postValue(userCoursesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}