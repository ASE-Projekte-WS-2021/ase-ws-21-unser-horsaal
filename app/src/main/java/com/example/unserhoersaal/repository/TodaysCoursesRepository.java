package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

/**
 * Repo to load all courses with a meeting today.
 */
public class TodaysCoursesRepository {

  private static final String TAG = "TodaysCoursesRepository";

  private static TodaysCoursesRepository instance;
  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private final ArrayList<CourseModel> todaysCoursesList = new ArrayList<>();
  private final StateLiveData<List<CourseModel>> courses = new StateLiveData<>();
  private String userId;
  private final HashSet<String> joinedCourses = new HashSet<>();
  private final HashSet<String> todaysCourses = new HashSet<>();

  public TodaysCoursesRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
  }

  /**
   * Get an instance of the repo.
   *
   * @return Instance of the TodaysCoursesRepository
   */
  public static TodaysCoursesRepository getInstance() {
    if (instance == null) {
      instance = new TodaysCoursesRepository();
    }
    return instance;
  }

  /**
   * Loads the new userId if a new user has logged in.
   */
  public void setUserId() {
    String uid;
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.courses.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
      return;
    }
    uid = this.firebaseAuth.getCurrentUser().getUid();
    if (this.userId == null || !this.userId.equals(uid)) {
      this.todaysCoursesList.clear();
      this.userId = uid;
      this.loadTodaysCourses();
    }
  }

  public StateLiveData<List<CourseModel>> getTodaysCourses() {
    this.courses.postCreate(todaysCoursesList);
    return this.courses;
  }

  /**
   * Load all courses with a meeting today.
   */
  private void loadTodaysCourses() {
    this.courses.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }

    String id = this.firebaseAuth.getCurrentUser().getUid();

    Query query = this.databaseReference.child(Config.CHILD_USER_COURSES).child(id);
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        updateJoinedCourses(dataSnapshot);
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          checkMeetingToday(snapshot.getKey());
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, error.getMessage());
        courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    });
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

  private String getDate() {
    Calendar calendar = Calendar.getInstance();
    return Config.DATE_FORMAT.format(calendar.getTimeInMillis());
  }

  /**
   * Check if the meetings of a course are today.
   *
   * @param courseId courseId of the course to check
   */
  private void checkMeetingToday(String courseId) {
    this.databaseReference.child(Config.CHILD_MEETINGS)
            .child(courseId)
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String date = getDate();
                todaysCourses.remove(courseId);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  MeetingsModel model = snapshot.getValue(MeetingsModel.class);
                  if (model == null) {
                    continue;
                  }
                  String eventTime = Config.DATE_FORMAT.format(model.getEventTime());
                  if (date.equals(eventTime)) {
                    todaysCourses.add(courseId);

                    break;
                  }
                }
                loadCourse(courseId);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
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
                updateCourseList(courseModel, todaysCoursesList);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
  }

  /**
   * Update today's courses when a course has changed. Add it, if the user joined a course.
   * Remove the course if the user left it or update the course data if they changed.
   *
   * @param courseModel data of the changed course
   * @param courseList all courses
   */
  private void updateCourseList(CourseModel courseModel, List<CourseModel> courseList) {
    for (int i = 0; i < courseList.size(); i++) {
      CourseModel model = courseList.get(i);
      if (model.getKey().equals(courseModel.getKey())) {
        if (this.joinedCourses.contains(courseModel.getKey())
                && this.todaysCourses.contains(courseModel.getKey())) {
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
    if (this.joinedCourses.contains(courseModel.getKey())
            && this.todaysCourses.contains(courseModel.getKey())) {
      courseList.add(courseModel);
      this.courses.postUpdate(courseList);
    }
  }

}
