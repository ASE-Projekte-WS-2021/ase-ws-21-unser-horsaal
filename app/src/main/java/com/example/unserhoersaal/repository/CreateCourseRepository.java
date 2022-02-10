package com.example.unserhoersaal.repository;



import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.views.CreateCourseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/**Class creates a course and saves it in Firebase.**/

public class CreateCourseRepository {

  private static final String TAG = "CourseCreationRepo";

  private FirebaseDatabase firebaseDatabase;
  private DatabaseReference databaseReference;
  private String courseId;
  private FirebaseAuth firebaseAuth;
  private MutableLiveData<UserCourse> userCourse = new MutableLiveData<>();

  /**Method gets an instance of Firebase.**/

  public CreateCourseRepository() {
    this.firebaseDatabase = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app");
    this.databaseReference = firebaseDatabase.getReference();
    this.firebaseAuth = FirebaseAuth.getInstance();
  }

  public MutableLiveData<UserCourse> getUserCourse(){
    return userCourse;
  }

  /**Method creates an course.**/

  public void createNewCourse(String courseName, String courseDescription) {
    String courseCreatedById = firebaseAuth.getCurrentUser().getUid();
    String courseCreatedBy = firebaseAuth.getCurrentUser().getEmail();
    //TODO: replace by user name or leave out and use dummy names
    String courseCreatedAt = String.valueOf(System.currentTimeMillis());
    courseId = this.databaseReference.getRoot().push().getKey();
    CourseModel courseModel = new CourseModel(courseName, courseId, courseDescription,
            courseCreatedById, courseCreatedBy, courseCreatedAt);
    this.databaseReference.child("Courses").child(courseId).setValue(courseModel);
    userCourse.postValue(new UserCourse(courseId, courseName));
  }

  /**Gives back courseId.**/

  public String getCourseId() {
    return courseId;
  }

  /**Method sets courseId.**/

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

}
