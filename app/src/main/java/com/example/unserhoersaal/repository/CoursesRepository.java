package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.UserCourse;
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

  private ArrayList<UserCourse> userCoursesList = new ArrayList<UserCourse>();
  private MutableLiveData<List<UserCourse>> courses = new MutableLiveData<>();

  /** This method generates the Instance of the CourseRepository. */
  public static CoursesRepository getInstance() {
    if (instance == null) {
      instance = new CoursesRepository();
    }
    return instance;
  }

  /** This method provides all courses a user has signed up for. */
  public MutableLiveData<List<UserCourse>> getUserCourses() {
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

    Query query = reference.child(Config.CHILD_USER).child(auth.getCurrentUser().getUid());
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        userCoursesList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          userCoursesList.add(new UserCourse(snapshot.getKey(), snapshot.getValue(String.class)));
        }
        courses.postValue(userCoursesList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }
}