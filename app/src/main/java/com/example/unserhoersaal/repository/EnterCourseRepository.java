package com.example.unserhoersaal.repository;


import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.Message;
import com.example.unserhoersaal.views.EnterCourseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/** This class is the database access needed to enter a new course. */
public class EnterCourseRepository {

  private static final String TAG = "EnterCourseRepo";

  private FirebaseDatabase firebaseDb;
  private DatabaseReference databaseReference;
  private final MutableLiveData<ArrayList> messages = new MutableLiveData<ArrayList>();
  private ArrayList<Message> messagesList = new ArrayList<Message>();
  private FirebaseAuth firebaseAuth;
  private final MutableLiveData<Config.ThreeState> courseIdIsCorrect = new MutableLiveData<Config.ThreeState>();

  //private String courseId;

  /** Constructor. */
  public EnterCourseRepository() {
    this.firebaseDb = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app");
    this.databaseReference = firebaseDb.getReference();
    this.firebaseAuth = FirebaseAuth.getInstance();
    courseIdIsCorrect.setValue(Config.ThreeState.TRALSE);
  }

  public void checkCode(String id, EnterCourseFragment listener){
    Query query = databaseReference.child("Courses").child(id);
    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          String courseName = dataSnapshot.child("courseName").getValue(String.class);
          isUserInCourse(id, courseName, listener);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }

  /** Saving the user courses. */
  /*public MutableLiveData<Config.ThreeState> saveUserCourses(String courseId) {
    this.courseIdIsCorrect.setValue(Config.ThreeState.TRALSE);
    this.databaseReference.child("Courses").child(courseId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  String courseName = dataSnapshot.child("courseName").getValue(String.class);
                  courseIdIsCorrect.setValue(Config.ThreeState.TRUE);
                  isUserInCourse(courseId, courseName);
                } else {
                  courseIdIsCorrect.setValue(Config.ThreeState.FALSE);
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
            });
    return courseIdIsCorrect;
  }*/

  /** Checks if user is in course. */
  public void isUserInCourse(String courseId, String courseName, EnterCourseFragment listener) {
    ValueEventListener eventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()) {
          saveCourseInUser(courseId, courseName);
          listener.openNewCourse(courseId);
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

  /** Saves a entered course for a user. */
  public void saveCourseInUser(String courseId, String courseName) {

    if (firebaseAuth.getCurrentUser() != null) {
      this.databaseReference.child("User").child(firebaseAuth.getCurrentUser()
              .getUid()).child(courseId).setValue(courseName);
    }
  }

}
