package com.example.unserhoersaal.repository;

import android.app.Activity;
import android.provider.ContactsContract;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.compose.runtime.snapshots.Snapshot;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.google.android.gms.tasks.OnSuccessListener;
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

  private ArrayList<CourseModel> userCoursesList = new ArrayList<CourseModel>();
  private MutableLiveData<List<CourseModel>> courses = new MutableLiveData<>();

  /** This method generates the Instance of the CourseRepository. */
  public static CoursesRepository getInstance() {
    if (instance == null) {
      instance = new CoursesRepository();
    }
    return instance;
  }

  /** This method provides all courses a user has signed up for. */
  public MutableLiveData<List<CourseModel>> getUserCourses() {
    if (this.userCoursesList.size() == 0) {
      this.loadUserCourses();
    }

    this.courses.setValue(userCoursesList);
    return this.courses;
  }

  /** This method loads all courses in which the user is signed in. */
  public void loadUserCourses() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String id = auth.getCurrentUser().getUid();


    Query query = reference.child(Config.CHILD_USER_COURSES).child(id);
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        ArrayList<Task<DataSnapshot>> taskList = new ArrayList<>();
        ArrayList<CourseModel> authorList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          taskList.add(getCourseTask(snapshot.getKey()));
        }
        Tasks.whenAll(taskList).addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void unused) {
            for (Task<DataSnapshot> task : taskList) {
              CourseModel model = task.getResult().getValue(CourseModel.class);
              model.setKey(task.getResult().getKey());
              authorList.add(model);
            }
            getAuthor(authorList);
          }
        });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

  public Task<DataSnapshot> getCourseTask(String courseId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_COURSES).child(courseId).get();
  }

  public void getAuthor(List<CourseModel> authorList) {
    List<Task<DataSnapshot>> authorNames = new ArrayList<>();
    for (CourseModel course : authorList) {
      authorNames.add(getAuthorName(course.getCreatorId()));
    }
    Tasks.whenAll(authorNames).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void unused) {
        for (int i = 0; i < authorList.size(); i++) {
          CourseModel model = authorList.get(i);
          model.setCreatorName((String) authorNames.get(i).getResult().getValue());
          authorList.set(i, model);
        }
        userCoursesList.clear();
        userCoursesList.addAll(authorList);
        courses.postValue(userCoursesList);
      }
    });
  }

  public Task<DataSnapshot> getAuthorName(String authorId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER).child(authorId).child(Config.CHILD_USER_NAME).get();
  }
}