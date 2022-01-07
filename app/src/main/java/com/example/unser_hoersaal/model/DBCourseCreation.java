package com.example.unser_hoersaal.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DBCourseCreation {
    private Application application;
    private FirebaseDatabase firebaseDB;
    private DatabaseReference databaseReference;

    public DBCourseCreation(Application application) {
        this.application = application;
        this.firebaseDB = FirebaseDatabase.getInstance();
        this.databaseReference = firebaseDB.getReference();
    }

    public String createNewCourse(String courseName, String courseDescription, String courseCreatedById, String courseCreatedBy, String courseCreatedAt) {
        String courseId = this.databaseReference.getRoot().push().getKey();
        CourseModel courseModel = new CourseModel(courseName, courseId, courseDescription, courseCreatedById, courseCreatedBy, courseCreatedAt);

        this.databaseReference.child(courseId).setValue(courseModel);

        return courseId;
    }

}
