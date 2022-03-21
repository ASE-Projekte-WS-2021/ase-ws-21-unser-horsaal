package com.example.unserhoersaal.repository;

import android.util.Log;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

/**Class creates a course and saves it in Firebase.**/
public class CreateCourseRepository {

  private static final String TAG = "CourseCreationRepo";
  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private static CreateCourseRepository instance;
  private final StateLiveData<CourseModel> currentCourseRepoState = new StateLiveData<>();

  /** Generates an Instance of CreateCourseRepository. */
  public static CreateCourseRepository getInstance() {
    if (instance == null) {
      instance = new CreateCourseRepository();
    }
    return instance;
  }

  /**Method gets an instance of Firebase.**/
  public CreateCourseRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
  }

  public StateLiveData<CourseModel> getCurrentCourseRepoState() {
    return this.currentCourseRepoState;
  }

  /**Method creates an course.**/
  public void createNewCourse(CourseModel courseModel) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.currentCourseRepoState.postError(
              new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();

    courseModel.setCreatorId(uid);
    courseModel.setCreationTime(System.currentTimeMillis());
    String courseId = this.databaseReference.getRoot().push().getKey();

    if (courseId == null) {
      Log.e(TAG, Config.CREATE_COURSE_NO_COURSE_ID);
      this.currentCourseRepoState.postError(
              new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    this.databaseReference.child(Config.CHILD_COURSES).child(courseId).setValue(courseModel)
            .addOnSuccessListener(unused -> {
              courseModel.setKey(courseId);
              addUserToCourse(courseModel, uid);
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, Config.CREATE_COURSE_CREATION_FAILED + e.getMessage());
              currentCourseRepoState.postError(
                      new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
            });
  }

  private void addUserToCourse(CourseModel course, String user) {
    this.databaseReference
            .child(Config.CHILD_USER_COURSES)
            .child(user)
            .child(course.getKey())
            .setValue(Boolean.TRUE)
            .addOnSuccessListener(unused ->
                    databaseReference.child(Config.CHILD_COURSES_USER)
                            .child(course.getKey())
                            .child(user)
                            .setValue(Boolean.TRUE)
                            .addOnSuccessListener(unused1 -> {
                               this.databaseReference.child(Config.CHILD_COURSES)
                                      .child(course.getKey())
                                      .child(Config.CHILD_MEMBER_COUNT)
                                      .setValue(ServerValue.increment(1))
                                      .addOnSuccessListener(unused2 -> addMapping(course))
                                      .addOnFailureListener(e -> {
                                        Log.e(TAG, e.getMessage());
                                      });
                            })
                            .addOnFailureListener(e -> {
                              Log.e(TAG, Config.CREAT_COURSE_ADDING_USER_FAILED
                                      + e.getMessage());
                              currentCourseRepoState.postError(
                                      new Error(Config.COURSES_COURSE_CREATION_FAILURE),
                                      ErrorTag.REPO);
                            }));
  }

  private void addMapping(CourseModel course) {
    String mappingCode = course.getCodeMapping();

    this.databaseReference
            .child(Config.CHILD_CODE_MAPPING)
            .child(mappingCode)
            .setValue(course.getKey())
            .addOnSuccessListener(unused ->
                    currentCourseRepoState.postUpdate(course))
            .addOnFailureListener(e -> {
              Log.e(TAG, Config.CREAT_COURSE_MAPPING_FAILED + e.getMessage());
              currentCourseRepoState.postError(
                      new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
            });
  }

}
