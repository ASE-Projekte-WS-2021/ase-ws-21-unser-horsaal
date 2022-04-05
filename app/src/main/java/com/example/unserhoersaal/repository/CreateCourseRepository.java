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

/**
 * Class creates a course and saves it in Firebase.
 */
public class CreateCourseRepository {

  private static final String TAG = "CourseCreationRepo";

  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private static CreateCourseRepository instance;
  private final StateLiveData<CourseModel> courseModelMutableLiveData = new StateLiveData<>();

  /**
   * Generates an Instance of CreateCourseRepository according to the Singleton pattern.
   *
   * @return Instance of the CreateCourseRepository
   */
  public static CreateCourseRepository getInstance() {
    if (instance == null) {
      instance = new CreateCourseRepository();
    }
    return instance;
  }

  /**
   * Method gets an instance of Firebase.
   */
  public CreateCourseRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
  }

  public StateLiveData<CourseModel> getUserCourse() {
    return this.courseModelMutableLiveData;
  }

  /**
   * Method creates a new course.
   *
   * @param courseModel data of the new course
   */
  public void createNewCourse(CourseModel courseModel) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.courseModelMutableLiveData.postError(
              new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();

    courseModel.setCreatorId(uid);
    courseModel.setCreationTime(System.currentTimeMillis());
    String courseId = this.databaseReference.getRoot().push().getKey();

    if (courseId == null) {
      Log.e(TAG, "courseid is null");
      this.courseModelMutableLiveData.postError(
              new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    this.databaseReference.child(Config.CHILD_COURSES).child(courseId).setValue(courseModel)
            .addOnSuccessListener(unused -> {
              courseModel.setKey(courseId);
              addUserToCourse(courseModel, uid);
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, "Kurs konnte nicht erstellt werden: " + e.getMessage());
              courseModelMutableLiveData.postError(
                      new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
            });
  }


  /**
   * Method edits th current course.
   *
   * @param courseModel model with the changed course data
   */
  public void editCourse(CourseModel courseModel) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.courseModelMutableLiveData.postError(
              new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    String courseId = courseModel.getKey();


    if (courseId == null) {
      Log.e(TAG, "courseid is null");
      this.courseModelMutableLiveData.postError(
              new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    DatabaseReference courseDbRef =
            this.databaseReference.child(Config.CHILD_COURSES).child(courseId);


    courseDbRef.child(Config.CHILD_DESCRIPTION).setValue(courseModel.getDescription())
            .addOnSuccessListener(unused -> {
              courseModel.setKey(courseId);
              courseModelMutableLiveData.postUpdate(courseModel);
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, "Kurs konnte nicht bearbeited werden: " + e.getMessage());
              courseModelMutableLiveData.postError(
                      new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
            });

    courseDbRef.child(Config.CHILD_INSTITUTION).setValue(courseModel.getInstitution())
            .addOnSuccessListener(unused -> {
              courseModel.setKey(courseId);
              courseModelMutableLiveData.postUpdate(courseModel);

            })
            .addOnFailureListener(e -> {
              Log.e(TAG, "Kurs konnte nicht bearbeited werden: " + e.getMessage());
              courseModelMutableLiveData.postError(
                      new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
            });
    courseDbRef.child(Config.CHILD_TITLE).setValue(courseModel.getTitle())
            .addOnSuccessListener(unused -> {
              courseModel.setKey(courseId);
              courseModelMutableLiveData.postUpdate(courseModel);

            })
            .addOnFailureListener(e -> {
              Log.e(TAG, "Kurs konnte nicht bearbeited werden: " + e.getMessage());
              courseModelMutableLiveData.postError(
                      new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
            });
  }


  /**
   * Adds a user to a course.
   *
   * @param course course the user is added
   * @param user id of the added user
   */

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
                              Log.e(TAG, "User konnte dem Kurs nicht hinzugefügt werden: "
                                      + e.getMessage());
                              courseModelMutableLiveData.postError(
                                      new Error(Config.COURSES_COURSE_CREATION_FAILURE),
                                      ErrorTag.REPO);
                            }));
  }

  /**
   * Saves the mapping of a course in the database.
   *
   * @param course data of the course including the mapping code
   */
  private void addMapping(CourseModel course) {
    String mappingCode = course.getCodeMapping();

    this.databaseReference
            .child(Config.CHILD_CODE_MAPPING)
            .child(mappingCode)
            .setValue(course.getKey())
            .addOnSuccessListener(unused ->
                    courseModelMutableLiveData.postUpdate(course))
            .addOnFailureListener(e -> {
              Log.e(TAG, "Das Mapping konnte nicht erstellt werden: " + e.getMessage());
              courseModelMutableLiveData.postError(
                      new Error(Config.COURSES_COURSE_CREATION_FAILURE), ErrorTag.REPO);
            });
  }

}
