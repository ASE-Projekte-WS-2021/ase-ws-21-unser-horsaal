package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.UserModel;
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

/** Repo to load all courses with a meeting today. */
public class TodaysCoursesRepository {

  private static final String TAG = "TodaysCoursesRepository";

  private static TodaysCoursesRepository instance;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseReference;
  private ArrayList<CourseModel> todaysCoursesList = new ArrayList<>();
  private MutableLiveData<List<CourseModel>> courses = new MutableLiveData<>();
  private String userId;

  public TodaysCoursesRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
  }

  /** Get an instance of the repo. */
  public static TodaysCoursesRepository getInstance() {
    if (instance == null) {
      instance = new TodaysCoursesRepository();
    }
    return instance;
  }

  public void setUserId() {
    String uid;
    if (this.firebaseAuth.getCurrentUser() == null ) {
      return;
    }
    uid = this.firebaseAuth.getCurrentUser().getUid();
    if (this.userId == null || !this.userId.equals(uid)) {
      this.userId = uid;
      this.loadTodaysCourses();
    }
  }

  /** Give back all courses with a meeting today. */
  public MutableLiveData<List<CourseModel>> getTodaysCourses() {
    this.courses.setValue(todaysCoursesList);
    return this.courses;
  }


  /** Load all courses with a meeting today. */
  public void loadTodaysCourses() {
    String id = this.firebaseAuth.getCurrentUser().getUid();
    this.todaysCoursesList.clear();
    this.courses.postValue(todaysCoursesList);

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

  public Task<DataSnapshot> getCourseTask(String courseId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_COURSES).child(courseId).get();
  }

  /** TODO. */
  public void getAuthor(List<CourseModel> authorList) {
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
      courses.postValue(todaysCoursesList);
    });
  }

  public Task<DataSnapshot> getAuthorData(String authorId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER).child(authorId).get();
  }

}
