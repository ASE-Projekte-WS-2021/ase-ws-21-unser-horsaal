package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * This class is the database access needed to enter a new course.
 */
public class EnterCourseRepository {

  private static final String TAG = "EnterCourseRepo";

  private static EnterCourseRepository instance;

  private final DatabaseReference databaseReference;
  private final FirebaseAuth firebaseAuth;

  private final StateLiveData<CourseModel> courseModel = new StateLiveData<>();
  private final StateLiveData<CourseModel> enteredCourse = new StateLiveData<>();

  /**
   * Generates an Instance of the EnterCourseRepository.
   *
   * @return Instance of the EnterCourseRepository
   */
  public static EnterCourseRepository getInstance() {
    if (instance == null) {
      instance = new EnterCourseRepository();
    }
    return instance;
  }

  /**
   * Constructor.
   */
  public EnterCourseRepository() {
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.firebaseAuth = FirebaseAuth.getInstance();
  }

  public StateLiveData<CourseModel> getCourse() {
    return this.courseModel;
  }

  public StateLiveData<CourseModel> getEnteredCourse() {
    return this.enteredCourse;
  }

  /**
   * Checks if the course exists.
   *
   * @param code mapping code for the course
   */
  public void checkCode(String code) {
    Query query = this.databaseReference.child(Config.CHILD_CODE_MAPPING).child(code);
    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          loadCourse((String) dataSnapshot.getValue());
        } else {
          courseModel.postCreate(new CourseModel());
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        courseModel.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    });
  }

  /**
   * Loads a course by its id from the data base.
   *
   * @param courseId id of the course to load
   */
  private void loadCourse(String courseId) {
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
        getCreator(course);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        courseModel.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    });
  }

  /** Get the name of the creator of a course by the creator id.
   *
   * @param course data of the course with the creatorId
   */
  private void getCreator(CourseModel course) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Task<DataSnapshot> task = reference.child(Config.CHILD_USER).child(course.getCreatorId()).get();

    task.addOnSuccessListener(dataSnapshot -> {
      UserModel creator = dataSnapshot.getValue(UserModel.class);
      if (creator != null) {
        course.setCreatorName(creator.getDisplayName());
        course.setPhotoUrl(creator.getPhotoUrl());
      } else {
        course.setCreatorName(Config.UNKNOWN_USER);
      }
      courseModel.postUpdate(course);
    }).addOnFailureListener(e ->
            courseModel.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO));
  }

  /**
   * Checks if user is in course.
   *
   * @param course data of the course for the check
   */
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
      public void onCancelled(@NonNull DatabaseError databaseError) {
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

  /**
   * Saves a entered course for a user.
   *
   * @param course data of the course with the course id
   */
  private void saveCourseInUser(CourseModel course) {
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
            .addOnSuccessListener(unused -> this.databaseReference
                    .child(Config.CHILD_COURSES_USER)
                    .child(course.getKey())
                    .child(uid)
                    .setValue(Boolean.TRUE)
                    .addOnSuccessListener(unused1 -> this.databaseReference
                            .child(Config.CHILD_COURSES)
                            .child(course.getKey())
                            .child(Config.CHILD_MEMBER_COUNT)
                            .setValue(ServerValue.increment(1))
                            .addOnSuccessListener(unused2 -> enteredCourse.postUpdate(course))
                            .addOnFailureListener(e -> {
                              Log.e(TAG, e.getMessage());
                              enteredCourse.postError(
                                      new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
                            })).addOnFailureListener(e -> {
                              Log.e(TAG, e.getMessage());
                              enteredCourse.postError(
                                      new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
                            })).addOnFailureListener(e -> {
                              Log.e(TAG, e.getMessage());
                              enteredCourse.postError(new Error(Config.UNSPECIFIC_ERROR),
                                      ErrorTag.REPO);
                            });
  }

}
