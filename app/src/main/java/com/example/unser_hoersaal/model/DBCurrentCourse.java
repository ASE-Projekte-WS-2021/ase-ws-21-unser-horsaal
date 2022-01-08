package com.example.unser_hoersaal.model;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBCurrentCourse {
    private Application application;
    private FirebaseDatabase firebaseDB;
    private DatabaseReference databaseReference;

    public DBCurrentCourse(Application application) {
        this.application = application;
        this.firebaseDB = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app/");
        this.databaseReference = firebaseDB.getReference();
    }

    public void sendMessage() {
    }

}
