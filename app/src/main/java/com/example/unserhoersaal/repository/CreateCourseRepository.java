package com.example.unserhoersaal.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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

    courseModel.setCreatorId(uid);
    courseModel.setCreationTime(System.currentTimeMillis());
    String courseId = databaseReference.getRoot().push().getKey();
    databaseReference.child(Config.CHILD_COURSES).child(courseId).setValue(courseModel)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                courseModel.setKey(courseId);
                addUserToCourse(courseModel, uid);
              }
            });
  }

  private void addUserToCourse(CourseModel course, String user) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_USER_COURSES).child(user).child(course.getKey())
            .setValue(Boolean.TRUE)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                reference.child(Config.CHILD_COURSES_USER).child(course.getKey()).child(user)
                        .setValue(Boolean.TRUE)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                            addMapping(course);
                          }
                        });
              }
            });
  }

  private void addMapping(CourseModel course) {
    String mappingCode = course.getCodeMapping();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_CODE_MAPPING).child(mappingCode).setValue(course.getKey())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                courseModelMutableLiveData.postValue(course);
              }
            });
  }
}
