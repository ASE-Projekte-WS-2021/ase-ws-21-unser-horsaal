package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.StateLiveData;
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
import java.util.List;

/** This class manages the database access for the overview of the courses of a user. */
public class CoursesRepository {

  private static final String TAG = "CoursesRepo";

  private static CoursesRepository instance;
  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private final ArrayList<CourseModel> userCoursesList = new ArrayList<>(); //TODO: replace or rename
  private final StateLiveData<List<CourseModel>> allCoursesRepoState = new StateLiveData<>();

  public CoursesRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
  }

  /** This method generates the Instance of the CourseRepository. */
  public static CoursesRepository getInstance() {
    if (instance == null) {
      instance = new CoursesRepository();
    }
    return instance;
  }

  /** This method provides all courses a user has signed up for. */
  public StateLiveData<List<CourseModel>> getAllCoursesRepoState() {
    if (this.userCoursesList.size() == 0) {
      this.loadUserCourses();
    }

    this.allCoursesRepoState.postCreate(this.userCoursesList);
    return this.allCoursesRepoState;
  }

  /** This method loads all courses in which the user is signed in. */
  public void loadUserCourses() {
    this.allCoursesRepoState.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.allCoursesRepoState.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
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
              allCoursesRepoState.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
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
        allCoursesRepoState.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    });
  }

  public Task<DataSnapshot> getCourseTask(String courseId) {
    return this.databaseReference.child(Config.CHILD_COURSES).child(courseId).get();
  }

  /** TODO. */
  public void getAuthor(List<CourseModel> authorList) {
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
      userCoursesList.clear();
      userCoursesList.addAll(authorList);

      allCoursesRepoState.postUpdate(userCoursesList);
    });
  }

  public Task<DataSnapshot> getAuthorData(String authorId) {
    return this.databaseReference.child(Config.CHILD_USER).child(authorId).get();
  }

}