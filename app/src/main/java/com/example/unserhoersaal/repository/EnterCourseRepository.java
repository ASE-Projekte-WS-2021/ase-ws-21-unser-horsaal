package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/** This class is the database access needed to enter a new course. */
public class EnterCourseRepository {

  private static final String TAG = "EnterCourseRepo";

  private static EnterCourseRepository instance;

  private DatabaseReference databaseReference;
  private FirebaseAuth firebaseAuth;

  private MutableLiveData<String> courseId = new MutableLiveData<>();

  /** Generates an Instance of the EnterCourseRepository. */
  public static EnterCourseRepository getInstance() {
    if (instance == null) {
      instance = new EnterCourseRepository();
    }
    return instance;
  }

  /** Constructor. */
  public EnterCourseRepository() {
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.firebaseAuth = FirebaseAuth.getInstance();
  }

  /** Gives back the current course id. */
  public MutableLiveData<String> getCourseId() {
    return this.courseId;
  }

  /** Checks if the course exists. */
  public void checkCode(String code) {
    Query query = this.databaseReference.child(Config.CHILD_CODE_MAPPING).child(code);
    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          isUserInCourse((String) dataSnapshot.getValue());
        }
        //TODO: Wrong Code
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

  /** Checks if user is in course. */
  public void isUserInCourse(String id) {
    ValueEventListener eventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()) {
          saveCourseInUser(id);
        }
        //if user has already entered the course just open it
        courseId.postValue(id);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
      }
    };

    this.databaseReference.child(Config.CHILD_USER)
            .child(this.firebaseAuth.getCurrentUser().getUid())
            .child(Config.CHILD_COURSES)
            .child(id).addListenerForSingleValueEvent(eventListener);
  }

  /** Saves a entered course for a user. */
  public void saveCourseInUser(String courseId) {
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.databaseReference.child(Config.CHILD_USER).child(uid).child(Config.CHILD_COURSES)
            .child(courseId).setValue(Boolean.TRUE);
    this.databaseReference.child(Config.CHILD_COURSES).child(courseId).child(Config.CHILD_USER)
            .child(uid).setValue(Boolean.TRUE);

  }

}
