package com.example.unserhoersaal.repository;



import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.views.CreateCourseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/**Class creates a course and saves it in Firebase.**/

public class CreateCourseRepository {

  private static final String TAG = "CourseCreationRepo";

  //private FirebaseDatabase firebaseDatabase;
  //private DatabaseReference databaseReference;
  //private String courseId;
  //private FirebaseAuth firebaseAuth;


  private static CreateCourseRepository instance;
  private MutableLiveData<UserCourse> userCourse = new MutableLiveData<>();

  public static CreateCourseRepository getInstance(){
    if (instance == null) {
      instance = new CreateCourseRepository();
    }
    return instance;
  }

  /**Method gets an instance of Firebase.**/

  public CreateCourseRepository() {
    //this.firebaseDatabase = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app");
    //this.databaseReference = firebaseDatabase.getReference();
    //this.firebaseAuth = FirebaseAuth.getInstance();
  }

  public MutableLiveData<UserCourse> getUserCourse(){
    return userCourse;
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
    databaseReference.child("Courses").child(courseId).setValue(courseModel);
    userCourse.postValue(new UserCourse(courseId, courseName));
  }

  /**Gives back courseId.**/

  /*public String getCourseId() {
    return courseId;
  }*/

  /**Method sets courseId.**/

  /*public void setCourseId(String courseId) {
    this.courseId = courseId;
  }*/

}
