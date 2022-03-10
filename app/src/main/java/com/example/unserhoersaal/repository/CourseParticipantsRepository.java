package com.example.unserhoersaal.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/** For Loading all Participants registered in a Course. Displayed in Course Description. */
public class CourseParticipantsRepository {

  private static final String TAG = "CourseParticipantsRepo";

  private static CourseParticipantsRepository instance;

  private StateLiveData<String> courseId = new StateLiveData<>();
  private StateLiveData<List<UserModel>> users = new StateLiveData<>();

  private ValueEventListener listener;

  public CourseParticipantsRepository() {
    initListener();
  }

  /** Returns the instance of this singleton class. */
  public static CourseParticipantsRepository getInstance() {
    if (instance == null) {
      instance = new CourseParticipantsRepository();
    }
    return instance;
  }

  public StateLiveData<String> getCourseId() {
    return this.courseId;
  }

  public StateLiveData<List<UserModel>> getUsers() {
    return this.users;
  }

  /** TODO. */
  public void setCourseId(String courseId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.courseId.getValue() != null) {
      reference.child(Config.CHILD_COURSES_USER).child(courseId).removeEventListener(this.listener);
    }
    reference.child(Config.CHILD_COURSES_USER).child(courseId).addValueEventListener(this.listener);
    this.courseId.postValue(new StateData<>(courseId));
  }

  private Task<DataSnapshot> getUserTask(String uid) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER).child(uid).get();
  }

  /** TODO. */
  public void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        ArrayList<Task<DataSnapshot>> taskList = new ArrayList<>();
        ArrayList<UserModel> userList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          taskList.add(getUserTask(snapshot.getKey()));
        }
        Tasks.whenAll(taskList).addOnSuccessListener(unused -> {
          for (Task<DataSnapshot> task : taskList) {
            UserModel model = task.getResult().getValue(UserModel.class);
            //TODO: assert setkey != null
            model.setKey(task.getResult().getKey());
            userList.add(model);
          }
          users.postValue(new StateData<>(userList));
        });

      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, Config.COURSE_PARTICIPANTS_LISTENER_FAILURE);
        //TODO: can we set an error on statelivedata to inform the user about errors?
      }
    };
  }

}
