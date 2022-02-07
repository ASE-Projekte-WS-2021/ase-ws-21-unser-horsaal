package com.example.unserhoersaal.repository;


import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
  private final MutableLiveData<ThreeState> courseIdIsCorrect = new MutableLiveData<ThreeState>();

  /** Corectness state of the course id. */
  public enum ThreeState {
    TRUE,
    FALSE,
    TRALSE
  }
  //private String courseId;

  /** Constructor. */
  public EnterCourseRepository() {
    this.firebaseDb = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app");
    this.databaseReference = firebaseDb.getReference();
    this.firebaseAuth = FirebaseAuth.getInstance();
    courseIdIsCorrect.setValue(ThreeState.TRALSE);
  }

  /** Saving the user courses. */
  public MutableLiveData<ThreeState> saveUserCourses(String courseId) {
    this.courseIdIsCorrect.setValue(ThreeState.TRALSE);
    this.databaseReference.child("Courses").child(courseId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  String courseName = dataSnapshot.child("courseName").getValue(String.class);
                  courseIdIsCorrect.setValue(ThreeState.TRUE);
                  isUserInCourse(courseId, courseName);
                } else {
                  courseIdIsCorrect.setValue(ThreeState.FALSE);
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
            });
    return courseIdIsCorrect;
  }

  /** Checks if user is in course. */
  public void isUserInCourse(String courseId, String courseName) {
    ValueEventListener eventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()) {
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

  /** Saves a entered course for a user. */
  public void saveCourseInUser(String courseId, String courseName) {

    if (firebaseAuth.getCurrentUser() != null) {
      this.databaseReference.child("User").child(firebaseAuth.getCurrentUser()
              .getUid()).child(courseId).setValue(courseName);
    }
  }

}
