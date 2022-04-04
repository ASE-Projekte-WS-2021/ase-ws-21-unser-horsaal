package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 *  Repository for the CourseDescriptionViewModel.
 */
public class CourseDescriptionRepository {

  private static final String TAG = "CourseDescriptionRepo";

  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private static CourseDescriptionRepository instance;
  private String creatorId;
  private final StateLiveData<String> courseId = new StateLiveData<>();
  private final StateLiveData<CourseModel> courseModel = new StateLiveData<>();


  /**
   * Constructor.
   */
  public CourseDescriptionRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.courseModel.postCreate(new CourseModel());
    this.courseId.postCreate(null);
  }

  /**
   * Generates an instance of CourseDescriptionRepository.
   *
   * @return Instance of CourseDescriptionRepository
   */
  public static CourseDescriptionRepository getInstance() {
    if (instance == null) {
      instance = new CourseDescriptionRepository();
    }
    return instance;
  }

  public StateLiveData<String> getCourseId() {
    return this.courseId;
  }

  public StateLiveData<CourseModel> getCourseModel() {
    return this.courseModel;
  }

  /**
   * Loads the description of a course.
   */
  public void loadDescription() {
    this.courseModel.postLoading();


    Query query = this.databaseReference.child(Config.CHILD_COURSES)
            .child(this.courseId.getValue().getData());
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        CourseModel model = snapshot.getValue(CourseModel.class);

        if (model == null) {
          Log.e(TAG, "model is null");
          courseModel.postError(
                  new Error(Config.COURSE_DESCRIPTION_SETCOURSEID_FAILED), ErrorTag.REPO);
          return;
        }

        model.setKey(snapshot.getKey());
        getAuthorName(model);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, "setCourseId task failed.");
        courseModel.postError(
                new Error(Config.COURSE_DESCRIPTION_SETCOURSEID_FAILED), ErrorTag.REPO);
      }
    });
  }

  /**
   * Sets the id of a new course the user wants to read the description.
   *
   * @param newCourseId Id of the course
   */
  public void setCourseId(String newCourseId) {
    if (newCourseId == null) {
      return;
    }
    if (this.courseId.getValue() == null
            || this.courseId.getValue().getData() == null
            || !this.courseId.getValue().getData().equals(newCourseId)) {
      this.courseId.postUpdate(newCourseId);
      this.loadDescription();
    }
  }

  private void getAuthorName(CourseModel course) {
    Task<DataSnapshot> task = this.databaseReference
            .child(Config.CHILD_USER)
            .child(course.getCreatorId())
            .child(Config.CHILD_USER_NAME)
            .get();

    task.addOnSuccessListener(dataSnapshot -> {
      if (dataSnapshot.exists()) {
        course.setCreatorName(dataSnapshot.getValue(String.class));
      } else {
        course.setCreatorName(Config.UNKNOWN_USER);
      }
      this.courseModel.postUpdate(course);
    }).addOnFailureListener(e -> {
      Log.e(TAG, e.getMessage());
      this.courseModel.postError(
              new Error(Config.COURSE_DESCRIPTION_COULD_NOT_LOAD_USER), ErrorTag.REPO);
    });
  }

  /**
   * Unregisters a user from a course in real time database.
   *
   * @param id id of the course
   */
  public void unregisterFromCourse(String id) {
    this.courseModel.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.courseModel.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.databaseReference
            .child(Config.CHILD_USER_COURSES)
            .child(uid).child(id)
            .removeValue()
            .addOnSuccessListener(unused ->
                    this.databaseReference
                            .child(Config.CHILD_COURSES_USER)
                            .child(id)
                            .child(uid)
                            .removeValue()
                            .addOnSuccessListener(unused1 -> courseModel.postUpdate(null))
                            .addOnFailureListener(e -> {
                              Log.e(TAG, "Could not unregister User from course");
                              this.courseModel.postError(
                                      new Error(Config.COURSE_DESCRIPTION_UNREGISTER_COURSE_FAILED),
                                      ErrorTag.REPO);
                            })
            ).addOnFailureListener(e -> {
              Log.e(TAG, "Could not unregister User from course");
              this.courseModel.postError(
                      new Error(Config.COURSE_DESCRIPTION_UNREGISTER_COURSE_FAILED), ErrorTag.REPO);
            });
  }

  public String getUid() {
    return firebaseAuth.getUid();
  }

  public String getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }
}
