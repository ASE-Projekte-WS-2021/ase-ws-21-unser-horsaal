package com.example.unserhoersaal.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
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

  //TODO get whole CourseModel from ViewModel
  /** Sets the current course. */
  public void setCourseId(String courseId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.courseId.getValue() != null) {
      reference.child(Config.CHILD_COURSES).child(this.courseId.getValue())
              .removeEventListener(this.listener);
    }
    reference.child(Config.CHILD_COURSES).child(courseId).addValueEventListener(this.listener);
    this.courseId.postValue(courseId);
  }

  /** Initializes the listener for the database access. */
  public void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        CourseModel model = snapshot.getValue(CourseModel.class);
        courseModel.postValue(model);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    };
  }
}
