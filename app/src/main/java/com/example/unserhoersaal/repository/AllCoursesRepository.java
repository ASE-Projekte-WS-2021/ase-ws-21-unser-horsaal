package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class manages the database access for the overview of all courses of a user.
 */
public class AllCoursesRepository {

  private static final String TAG = "AllCoursesRepo";

  private static AllCoursesRepository instance;
  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private final ArrayList<CourseModel> allCoursesList = new ArrayList<>();
  private final StateLiveData<List<CourseModel>> courses = new StateLiveData<>();
  private String userId;
  private final HashSet<String> joinedCourses = new HashSet<>();
  private ValueEventListener listener;

  /**
   * Constructor. Get Firebase instances and init the listener.
   */
  public AllCoursesRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.initListener();
  }

  /**
   * Initialize the ValueEventListener.
   */
  private void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        updateJoinedCourses(dataSnapshot);
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          loadCourse(snapshot.getKey());
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, error.getMessage());
        courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    };
  }

  /**
   * This method generates the instance of the AllCoursesRepository.
   *
   * @return Instance of AllCoursesRepository
   */
  public static AllCoursesRepository getInstance() {
    if (instance == null) {
      instance = new AllCoursesRepository();
    }
    return instance;
  }

  /**
   * Set the userId if a new account is logged in and load all courses of the new user.
   */
  public void setUserId() {
    String uid;
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.courses.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
      return;
    }
    uid = this.firebaseAuth.getCurrentUser().getUid();
    if (this.userId == null ) {
      this.allCoursesList.clear();
      this.userId = uid;
      this.loadAllCourses();
    } else if (!this.userId.equals(uid)) {
      this.allCoursesList.clear();
      this.databaseReference.child(Config.CHILD_USER_COURSES).child(this.userId)
              .removeEventListener(this.listener);
      this.userId = uid;
      this.loadAllCourses();
    }
  }

  public StateLiveData<List<CourseModel>> getAllCourses() {
    this.courses.postCreate(this.allCoursesList);
    return this.courses;
  }

  /**
   * This method loads all courses in which the user is signed in
   * and updates the data if it changes.
   */
  private void loadAllCourses() {
    this.courses.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }

    String id = this.firebaseAuth.getCurrentUser().getUid();

    Query query = this.databaseReference.child(Config.CHILD_USER_COURSES).child(id);
    query.addValueEventListener(this.listener);
  }

  /**
   * Update the list of joined courses if the user enters or leaves a course.
   *
   * @param dataSnapshot Snapshot with all entered courses
   */
  private void updateJoinedCourses(DataSnapshot dataSnapshot) {
    HashSet<String> courseIds = new HashSet<>();
    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
      courseIds.add(snapshot.getKey());
    }
    joinedCourses.clear();
    joinedCourses.addAll(courseIds);
  }

  /**
   * Load the data of a course from the database.
   *
   * @param courseId id of the course
   */
  private void loadCourse(String courseId) {
    this.databaseReference.child(Config.CHILD_COURSES)
            .child(courseId)
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                CourseModel model = snapshot.getValue(CourseModel.class);
                if (model == null) {
                  return;
                }
                model.setKey(snapshot.getKey());
                getAuthor(model);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
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
                updateCourseList(courseModel, allCoursesList);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
  }

  /**
   * Update all courses when a course has changed. Add it, if the user joined a course.
   * Remove the course if the user left it or update the course data if they changed.
   *
   * @param courseModel data of the changed course
   * @param courseList all courses
   */
  private void updateCourseList(CourseModel courseModel, List<CourseModel> courseList) {
    for (int i = 0; i < courseList.size(); i++) {
      CourseModel model = courseList.get(i);
      if (model.getKey().equals(courseModel.getKey())) {
        if (this.joinedCourses.contains(courseModel.getKey())) {
          //update course
          courseList.set(i, courseModel);
        } else {
          //remove course
          courseList.remove(i);
        }
        this.courses.postUpdate(courseList);
        return;
      }
    }
    //add course
    if (this.joinedCourses.contains(courseModel.getKey())) {
      courseList.add(courseModel);
      this.courses.postUpdate(courseList);
    }
  }

}