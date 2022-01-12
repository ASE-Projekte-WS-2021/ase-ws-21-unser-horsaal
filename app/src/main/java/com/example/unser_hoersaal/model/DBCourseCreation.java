package com.example.unser_hoersaal.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DBCourseCreation {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference databaseReference;
    private String courseId;


    public DBCourseCreation() {
        this.firebaseDB = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app");
        this.databaseReference = firebaseDB.getReference();
    }

    public void createNewCourse(String courseName, String courseDescription, String courseCreatedById, String courseCreatedBy, String courseCreatedAt) {
        courseId = this.databaseReference.getRoot().push().getKey();
        CourseModel courseModel = new CourseModel(courseName, courseId, courseDescription, courseCreatedById, courseCreatedBy, courseCreatedAt);

        this.databaseReference.child("Courses").child(courseId).setValue(courseModel);
    }

    public String getCourseId(){
        return courseId;
    }

    public void setCourseId(String courseId){ this.courseId = courseId; }





}
