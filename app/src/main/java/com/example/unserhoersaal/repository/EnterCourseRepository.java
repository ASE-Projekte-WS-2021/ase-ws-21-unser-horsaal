package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.android.gms.tasks.Task;
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

  private StateLiveData<CourseModel> courseModel = new StateLiveData<>();
  private StateLiveData<CourseModel> enteredCourse = new StateLiveData<>();

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

  public StateLiveData<CourseModel> getCourse() {
    return this.courseModel;
  }

  /** Gives back the current course id. */
  public StateLiveData<CourseModel> getEnteredCourse() {
    return this.enteredCourse;
  }

  /** Checks if the course exists. */
  public void checkCode(String code) {
    Query query = this.databaseReference.child(Config.CHILD_CODE_MAPPING).child(code);
    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          loadCourse((String) dataSnapshot.getValue());
        } else {
          courseModel.postValue(new StateData<>(new CourseModel()));
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

  /** Loads a course by its id from the data base. */
  public void loadCourse(String courseId) {
    this.courseModel.postLoading();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.child(Config.CHILD_COURSES).child(courseId);
    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        CourseModel course = snapshot.getValue(CourseModel.class);

        if (course == null) {
          Log.e(TAG, Config.COURSES_FAILED_TO_LOAD);
          courseModel.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
          return;
        }

        course.setKey(snapshot.getKey());
        getAuthorName(course);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, "Loading Course failed: " + error.getMessage());
        courseModel.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    });
  }

  /** TODO. */
  public void getAuthorName(CourseModel course) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Task<DataSnapshot> task = reference.child(Config.CHILD_USER).child(course.getCreatorId())
            .child(Config.CHILD_USER_NAME).get();

    task.addOnSuccessListener(dataSnapshot -> {
      if (dataSnapshot.exists()) {
        course.setCreatorName(dataSnapshot.getValue(String.class));
      } else {
        course.setCreatorName(Config.UNKNOWN_USER);
      }
      courseModel.postUpdate(course);
    }).addOnFailureListener(e -> {
      Log.e(TAG, "Loading Course failed: " + e.getMessage());
      courseModel.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
    });
  }

  /** Checks if user is in course. */
  public void isUserInCourse(CourseModel course) {
    this.enteredCourse.postLoading();

    ValueEventListener eventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()) {
          saveCourseInUser(course);
        } else {
          //if user has already entered the course just open it
          enteredCourse.postUpdate(course);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
        enteredCourse.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      }
    };

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.enteredCourse.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    this.databaseReference.child(Config.CHILD_USER_COURSES)
            .child(this.firebaseAuth.getCurrentUser().getUid()).child(course.getKey())
            .addListenerForSingleValueEvent(eventListener);
  }

  /** Saves a entered course for a user. */
  public void saveCourseInUser(CourseModel course) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.enteredCourse.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.databaseReference
            .child(Config.CHILD_USER_COURSES)
            .child(uid)
            .child(course.getKey())
            .setValue(Boolean.TRUE)
            .addOnSuccessListener(unused ->
                    databaseReference
                            .child(Config.CHILD_COURSES_USER)
                            .child(course.getKey())
                            .child(uid)
                    .setValue(Boolean.TRUE)
                    .addOnSuccessListener(unused1 -> enteredCourse.postUpdate(course)))
                    .addOnFailureListener(e -> {
                      Log.e(TAG, e.getMessage());
                      enteredCourse.postError(
                              new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
                    })
            .addOnFailureListener(e -> {
              Log.e(TAG, e.getMessage());
              enteredCourse.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
            });
  }

}
