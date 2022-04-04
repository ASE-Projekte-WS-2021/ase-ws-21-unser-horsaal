package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.viewmodel.OwnedCoursesViewModel;
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

/** Repo to get the owned courses. */
public class OwnedCoursesRepository {

  private static final String TAG = "OwnedCoursesRepo";

  private static OwnedCoursesRepository instance;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseReference;
  private ArrayList<CourseModel> ownedCoursesList = new ArrayList<>();
  private StateLiveData<List<CourseModel>> courses = new StateLiveData<>();
  private String userId;

  public OwnedCoursesRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
  }

  /** Generate an instance of the repo. */
  public static OwnedCoursesRepository getInstance() {
    if (instance == null) {
      instance = new OwnedCoursesRepository();
    }
    return instance;
  }

  /** JavaDoc. */
  public void setUserId() {
    String uid;
    if (this.firebaseAuth.getCurrentUser() == null) {
      return;
    }
    uid = this.firebaseAuth.getCurrentUser().getUid();
    if (this.userId == null || !this.userId.equals(uid)) {
      this.userId = uid;
      this.loadOwnedCourses();
    }
  }

  /** Give back the owned courses of the user. */
  public StateLiveData<List<CourseModel>> getOwnedCourses() {
    this.courses.postCreate(ownedCoursesList);
    return this.courses;
  }

  /** Load all owned courses. */
  public void loadOwnedCourses() {
    this.courses.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.courses.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
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
            if (model != null) {
              if (model.getCreatorId().equals(id)) {
                model.setKey(task.getResult().getKey());
                authorList.add(model);
              }
            }
          }
          getAuthor(authorList);
        });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, "onCancelled: " + error.getMessage());
      }
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
      ownedCoursesList.clear();
      ownedCoursesList.addAll(authorList);
      courses.postUpdate(ownedCoursesList);
    });
  }

  public Task<DataSnapshot> getAuthorData(String authorId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER).child(authorId).get();
  }

}
