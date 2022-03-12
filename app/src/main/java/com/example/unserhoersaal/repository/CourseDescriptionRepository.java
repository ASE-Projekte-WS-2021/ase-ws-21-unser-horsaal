package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Repository for the CourseDescriptionViewModel. */
public class CourseDescriptionRepository {

  private static final String TAG = "CourseDescriptionRepository";

  private static CourseDescriptionRepository instance;
  private StateLiveData<String> courseId = new StateLiveData<>();
  private StateLiveData<CourseModel> courseModel = new StateLiveData<>();
  private ValueEventListener listener;

  public CourseDescriptionRepository() {
    this.initListener();
  }

  /** Generates an instance of CourseDescriptionRepository. */
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

  /** Sets the current course. */
  public void setCourseId(String newCourseId) {
    String oldKey = Validation.checkStateLiveData(this.courseId, TAG);
    if (oldKey == null) {
      Log.e(TAG, "key is null.");
      return;
    }

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_COURSES)
            .child(oldKey)
            .removeEventListener(this.listener);

    reference.child(Config.CHILD_COURSES).child(newCourseId).addValueEventListener(this.listener);
    this.courseModel.postValue(new StateData<>(new CourseModel()));
    this.courseId.postValue(new StateData<>(newCourseId));
  }

  /** Initializes the listener for the database access. */
  public void initListener() {
    this.listener = new ValueEventListener() {
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
        Log.e(TAG, "setcourseid task failed.");
        courseModel.postError(
                new Error(Config.COURSE_DESCRIPTION_SETCOURSEID_FAILED), ErrorTag.REPO);
      }
    };
  }

  private void getAuthorName(CourseModel course) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Task<DataSnapshot> task = reference
            .child(Config.CHILD_USER)
            .child(course.getCreatorId())
            .child(Config.CHILD_USER_NAME).get();
    task.addOnSuccessListener(dataSnapshot -> {
      course.setCreatorName(dataSnapshot.getValue(String.class));
      this.courseModel.postSuccess(course);
    }).addOnFailureListener(e -> {
      Log.e(TAG, e.getMessage());
      this.courseModel.postError(
              new Error(Config.COURSE_DESCRIPTION_COULD_NOT_LOAD_USER), ErrorTag.REPO);
    });
  }

  /** unregisters a user from a course in real time database. */
  public void unregisterFromCourse(String id) {
    this.courseModel.postLoading();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.courseModel.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
      return;
    }

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    reference.child(Config.CHILD_USER_COURSES)
            .child(uid).child(id)
            .removeValue()
            .addOnSuccessListener(unused ->
                    reference.child(Config.CHILD_COURSES_USER)
                            .child(id)
                            .child(uid)
                            .removeValue()
                            .addOnSuccessListener(unused1 -> courseModel.postSuccess(null))
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

}
