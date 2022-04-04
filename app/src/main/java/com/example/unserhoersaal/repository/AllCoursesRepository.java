package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
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

  public AllCoursesRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
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
      return;
    }
    uid = this.firebaseAuth.getCurrentUser().getUid();
    if (this.userId == null || !this.userId.equals(uid)) {
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
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        ArrayList<Task<DataSnapshot>> taskList = new ArrayList<>();
        ArrayList<CourseModel> authorList = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          taskList.add(getCourseTask(snapshot.getKey()));
        }
        Tasks.whenAll(taskList).addOnSuccessListener(unused -> {
          for (Task<DataSnapshot> task : taskList) {
            CourseModel model = task.getResult().getValue(CourseModel.class);

            if (model == null) {
              courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
              return;
            }
            model.setKey(task.getResult().getKey());
            authorList.add(model);
          }
          getAuthor(authorList);
        });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, "onCancelled: " + error.getMessage());
        courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    });
  }

  private Task<DataSnapshot> getCourseTask(String courseId) {
    return this.databaseReference.child(Config.CHILD_COURSES).child(courseId).get();
  }

  /**
   * Load the picture and name of the course creator.
   *
   *  @param authorList List of all courses the user is signed in
   */
  private void getAuthor(List<CourseModel> authorList) {
    List<Task<DataSnapshot>> authorNames = new ArrayList<>();
    for (CourseModel course : authorList) {
      authorNames.add(getAuthorData(course.getCreatorId()));
    }
    Tasks.whenAll(authorNames).addOnSuccessListener(unused -> {
      for (int i = 0; i < authorList.size(); i++) {
        UserModel author = authorNames.get(i).getResult().getValue(UserModel.class);
        if (author == null) {
          authorList.get(i).setCreatorName(Config.UNKNOWN_USER);
        } else {
          authorList.get(i).setCreatorName(author.getDisplayName());
          authorList.get(i).setPhotoUrl(author.getPhotoUrl());
        }
      }
      allCoursesList.clear();
      allCoursesList.addAll(authorList);

      courses.postUpdate(allCoursesList);
    });
  }

  private Task<DataSnapshot> getAuthorData(String authorId) {
    return this.databaseReference.child(Config.CHILD_USER).child(authorId).get();
  }

}