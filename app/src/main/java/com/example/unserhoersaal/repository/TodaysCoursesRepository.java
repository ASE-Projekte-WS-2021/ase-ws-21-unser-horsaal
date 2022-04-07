package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
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
      return;
    }
    uid = this.firebaseAuth.getCurrentUser().getUid();
    if (this.userId == null || !this.userId.equals(uid)) {
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
        checkMeetingToday(dataSnapshot);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

  private String getDate() {
    Calendar calendar = Calendar.getInstance();
    return Config.DATE_FORMAT.format(calendar.getTimeInMillis());
  }

  /**
   * Check if the meetings of the courses are today.
   *
   * @param dataSnapshot snapshot with the id of all courses of the user
   */
  private void checkMeetingToday(DataSnapshot dataSnapshot) {
    List<String> courseIdList = new ArrayList<>();
    String date = getDate();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
      if (snapshot.getKey() != null) {
        reference.child(Config.CHILD_MEETINGS)
                .child(snapshot.getKey())
                .addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                      if (date.equals(snapshot1.getValue(MeetingsModel.class).getMeetingDate())) {
                        courseIdList.add(snapshot.getKey());
                        break;
                      }
                    }
                    findCourses(courseIdList);
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: " + error.getMessage());
                  }
                });
      }
    }

  }

  /**
   * Load all courses by id, which are today.
   *
   * @param courseIdList list with all courseIds, which are today
   */
  private void findCourses(List<String> courseIdList) {
    ArrayList<Task<DataSnapshot>> taskList = new ArrayList<>();
    ArrayList<CourseModel> authorList = new ArrayList<>();
    for (String courseId : courseIdList) {
      taskList.add(getCourseTask(courseId));
    }
    Tasks.whenAll(taskList).addOnSuccessListener(unused -> {
      for (Task<DataSnapshot> task : taskList) {
        CourseModel model = task.getResult().getValue(CourseModel.class);
        model.setKey(task.getResult().getKey());
        authorList.add(model);
      }
      getAuthor(authorList);
    });
  }

  private Task<DataSnapshot> getCourseTask(String courseId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_COURSES).child(courseId).get();
  }

  /** Load the data of the course creators.
   *
   * @param authorList list with all courses, which are today
   */
  private void getAuthor(List<CourseModel> authorList) {
    List<Task<DataSnapshot>> authorData = new ArrayList<>();
    for (CourseModel course : authorList) {
      authorData.add(getAuthorData(course.getCreatorId()));
    }
    Tasks.whenAll(authorData).addOnSuccessListener(unused -> {
      for (int i = 0; i < authorList.size(); i++) {
        UserModel author = authorData.get(i).getResult().getValue(UserModel.class);
        if (author == null) {
          authorList.get(i).setCreatorName(Config.UNKNOWN_USER);
        } else {
          authorList.get(i).setCreatorName(author.getDisplayName());
          authorList.get(i).setPhotoUrl(author.getPhotoUrl());
        }
      }
      todaysCoursesList.clear();
      todaysCoursesList.addAll(authorList);
      courses.postUpdate(todaysCoursesList);
    });
  }

  private Task<DataSnapshot> getAuthorData(String authorId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER).child(authorId).get();
  }

}
