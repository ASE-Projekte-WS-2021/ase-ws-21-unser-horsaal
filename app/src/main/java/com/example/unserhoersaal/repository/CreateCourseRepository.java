package com.example.unserhoersaal.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserCourse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**Class creates a course and saves it in Firebase.**/
public class CreateCourseRepository {

  private static final String TAG = "CourseCreationRepo";

  private static CreateCourseRepository instance;
  private MutableLiveData<CourseModel> courseModelMutableLiveData = new MutableLiveData<>();

  /** Generates an Instance of CreateCourseRepository. */
  public static CreateCourseRepository getInstance() {
    if (instance == null) {
      instance = new CreateCourseRepository();
    }
    return instance;
  }

  /**Method gets an instance of Firebase.**/
  public CreateCourseRepository() {
  }

  public MutableLiveData<CourseModel> getUserCourse() {
    return this.courseModelMutableLiveData;
  }

  /**Method creates an course.**/
  public void createNewCourse(CourseModel courseModel) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid();

    //todo generate codeMapping
    courseModel.setCreatorId(uid);
    courseModel.setCreationTime(System.currentTimeMillis());
    String courseId = databaseReference.getRoot().push().getKey();
    courseModel.addUser(uid);
    databaseReference.child(Config.CHILD_COURSES).child(courseId).setValue(courseModel);
    courseModel.setKey(courseId);
    this.addUserToCourse(courseId, uid);
    this.courseModelMutableLiveData.postValue(courseModel);
  }

  private void addUserToCourse(String course, String user) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Log.d(TAG, "addUserToCourse: "+user);
    reference.child(Config.CHILD_USER)
            .child(user).child(Config.CHILD_COURSES).child(course).setValue(Boolean.TRUE);
  }
}
