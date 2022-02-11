package com.example.unserhoersaal.repository;


import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
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

  private DatabaseReference databaseReference;
  private FirebaseAuth firebaseAuth;
  private MutableLiveData<String> courseId = new MutableLiveData<>();

  private static EnterCourseRepository instance;

  /** Generates an Instance of the EnterCourseRepository. */
  public static EnterCourseRepository getInstance() {
    if (instance == null) {
      instance = new EnterCourseRepository();
    }
    return instance;
  }


  //private String courseId;

  /** Constructor. */
  public EnterCourseRepository() {
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.firebaseAuth = FirebaseAuth.getInstance();
  }

  /** Gives back the current course id. */
  public MutableLiveData<String> getCourseId() {
    return courseId;
  }

  /** Checks if the course exists. */
  public void checkCode(String id) {
    Query query = databaseReference.child("Courses").child(id);
    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          String courseName = dataSnapshot.child("courseName").getValue(String.class);
          isUserInCourse(id, courseName);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }

  /** Checks if user is in course. */
  public void isUserInCourse(String id, String courseName) {
    ValueEventListener eventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()) {
          saveCourseInUser(id, courseName);
          courseId.postValue(id);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.d("Error", databaseError.getMessage());
      }
    };
    this.databaseReference.child("User").child(firebaseAuth.getCurrentUser()
            .getUid()).child(id).addListenerForSingleValueEvent(eventListener);
  }

  /** Saves a entered course for a user. */
  public void saveCourseInUser(String courseId, String courseName) {

    if (firebaseAuth.getCurrentUser() != null) {
      this.databaseReference.child("User").child(firebaseAuth.getCurrentUser()
              .getUid()).child(courseId).setValue(courseName);
    }
  }

}
