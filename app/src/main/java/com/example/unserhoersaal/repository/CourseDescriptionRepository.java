package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
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
  private final StateLiveData<CourseModel> course = new StateLiveData<>();
  private ValueEventListener listener;


  /**
   * Constructor. Get Firebase instances and init the listener.
   */
  public CourseDescriptionRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.course.postCreate(new CourseModel());
    this.courseId.postCreate(null);
    initListener();
  }

  /**
   * Initialize the ValueEventListener.
   */
  private void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        CourseModel model = snapshot.getValue(CourseModel.class);

        if (model == null) {
          course.postError(
                  new Error(Config.COURSE_DESCRIPTION_SETCOURSEID_FAILED), ErrorTag.REPO);
          return;
        }

        model.setKey(snapshot.getKey());
        getAuthor(model);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        course.postError(
                new Error(Config.COURSE_DESCRIPTION_SETCOURSEID_FAILED), ErrorTag.REPO);
      }
    };
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

  public StateLiveData<CourseModel> getCourse() {
    return this.course;
  }

  /**
   * Loads the description of a course.
   */
  private void loadDescription() {
    this.course.postLoading();

    String courseKey = Validation.checkStateLiveData(this.courseId, TAG);
    if (courseKey == null) {
      course.postError(
              new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }

    Query query = this.databaseReference.child(Config.CHILD_COURSES)
            .child(courseKey);
    query.addValueEventListener(this.listener);
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
            || this.courseId.getValue().getData() == null) {
      this.courseId.postUpdate(newCourseId);
      this.loadDescription();
    } else if (!this.courseId.getValue().getData().equals(newCourseId)) {
      this.databaseReference.child(Config.CHILD_COURSES)
              .child(this.courseId.getValue().getData()).removeEventListener(this.listener);
      this.courseId.postUpdate(newCourseId);
      this.loadDescription();
    }
  }


  /**
  * Load the picture and name of the course creator.
  *
  * @param courseModel data of the course for loading the author
  */
  private void getAuthor(CourseModel courseModel) {
    this.databaseReference.child(Config.CHILD_USER).child(courseModel.getCreatorId())
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel author = snapshot.getValue(UserModel.class);
                if (author == null) {
                  courseModel.setCreatorName(Config.UNKNOWN_USER);
                } else {
                  courseModel.setCreatorName(author.getDisplayName());
                  courseModel.setPhotoUrl(author.getPhotoUrl());
                }
                String courseKey = Validation.checkStateLiveData(courseId, TAG);
                if (courseModel.getKey().equals(courseKey)) {
                  course.postCreate(courseModel);
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                course.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
  }

  /**
   * Unregisters a user from a course in real time database.
   *
   * @param id id of the course
   */
  public void unregisterFromCourse(String id) {
    this.course.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.course.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
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
                            .addOnSuccessListener(unused1 -> {
                              this.decreaseMemberCount(id);
                              course.postUpdate(null);
                            })
                            .addOnFailureListener(e -> this.course.postError(
                                    new Error(Config.COURSE_DESCRIPTION_UNREGISTER_COURSE_FAILED),
                                    ErrorTag.REPO))
            ).addOnFailureListener(e -> this.course.postError(
                    new Error(Config.COURSE_DESCRIPTION_UNREGISTER_COURSE_FAILED), ErrorTag.REPO));
  }

  /**
   * Decrease the number of course member after a user designed from a course.
   *
   * @param courseId id of the course that the user left
   */
  private void decreaseMemberCount(String courseId) {
    this.databaseReference.child(Config.CHILD_COURSES)
            .child(courseId)
            .child(Config.CHILD_MEMBER_COUNT)
            .setValue(ServerValue.increment(-1));
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
