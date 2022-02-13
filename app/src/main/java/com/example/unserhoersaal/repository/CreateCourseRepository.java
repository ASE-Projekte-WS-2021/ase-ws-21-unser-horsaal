package com.example.unserhoersaal.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserCourse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**Class creates a course and saves it in Firebase.**/
public class CreateCourseRepository {

  private static final String TAG = "CourseCreationRepo";

  private static CreateCourseRepository instance;
  private MutableLiveData<UserCourse> userCourse = new MutableLiveData<>();

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

  public MutableLiveData<UserCourse> getUserCourse() {
    return this.userCourse;
  }

  /**Method creates an course.**/
  public void createNewCourse(String courseName, String courseDescription) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String courseCreatedById = firebaseAuth.getCurrentUser().getUid();
    String courseCreatedBy = firebaseAuth.getCurrentUser().getEmail();
    String courseCreatedAt = String.valueOf(System.currentTimeMillis());
    String courseId = databaseReference.getRoot().push().getKey();
    CourseModel courseModel = new CourseModel(courseName, courseId, courseDescription,
            courseCreatedById, courseCreatedBy, courseCreatedAt);
    databaseReference.child(Config.CHILD_COURSES).child(courseId).setValue(courseModel);
    this.userCourse.postValue(new UserCourse(courseId, courseName));
  }
}
