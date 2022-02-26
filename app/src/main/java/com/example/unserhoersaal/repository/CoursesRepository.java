package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

  //TODO set up pipeline
  /** This method loads all courses in which the user is signed in. */
  public void loadUserCourses() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String id = auth.getCurrentUser().getUid();


    Query query = reference.child(Config.CHILD_USER_COURSES).child(id);
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        HashSet<String> courseIds = new HashSet<>();
        userCoursesList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          courseIds.add(snapshot.getKey());
        }
        for (String key : courseIds) {
          reference.child(Config.CHILD_COURSES).child(key)
                  .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      CourseModel model = snapshot.getValue(CourseModel.class);
                      model.setKey(snapshot.getKey());
                      userCoursesList.add(model);
                      //TODO: update after loading
                      courses.postValue(userCoursesList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                  });
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

}