package com.example.unserhoersaal.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Repository for the CourseDescriptionViewModel. */
public class CourseDescriptionRepository {

  private static final String TAG = "CourseDescriptionRepository";

  private static CourseDescriptionRepository instance;

  private MutableLiveData<String> courseId = new MutableLiveData<>();
  private MutableLiveData<CourseModel> courseModel = new MutableLiveData<>();

  private ValueEventListener listener;

  public CourseDescriptionRepository() {
    initListener();
  }

  /** Generates an instance of CourseDescriptionRepository. */
  public static CourseDescriptionRepository getInstance() {
    if (instance == null) {
      instance = new CourseDescriptionRepository();
    }
    return instance;
  }

  public MutableLiveData<String> getCourseId() {
    return this.courseId;
  }

  public MutableLiveData<CourseModel> getCourseModel() {
    return this.courseModel;
  }

  /** Sets the current course. */
  public void setCourseId(String courseId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.courseId.getValue() != null) {
      reference.child(Config.CHILD_COURSES).child(this.courseId.getValue())
              .removeEventListener(this.listener);
    }
    reference.child(Config.CHILD_COURSES).child(courseId).addValueEventListener(this.listener);
    this.courseModel.postValue(new CourseModel());
    this.courseId.postValue(courseId);
  }

  /** Initializes the listener for the database access. */
  public void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        CourseModel model = snapshot.getValue(CourseModel.class);
        model.setKey(snapshot.getKey());
        getAuthorName(model);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    };
  }

  private void getAuthorName(CourseModel course) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Task<DataSnapshot> task = reference.child(Config.CHILD_USER).child(course.getCreatorId())
            .child(Config.CHILD_USER_NAME).get();

    task.addOnSuccessListener(dataSnapshot -> {
      if (dataSnapshot.exists()) {
        course.setCreatorName(dataSnapshot.getValue(String.class));
      } else {
        course.setCreatorName(Config.UNKNOWN_USER);
      }
      courseModel.postValue(course);
    });
  }

  /** unregisters a user from a course in real time database. */
  public void unregisterFromCourse(String id) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    reference.child(Config.CHILD_USER_COURSES)
            .child(uid).child(id)
            .removeValue()
            .addOnSuccessListener(unused ->
                    reference.child(Config.CHILD_COURSES_USER)
                            .child(id)
                            .child(uid)
                            .removeValue()
                            .addOnSuccessListener(unused1 -> courseModel.postValue(null)));
  }

}
