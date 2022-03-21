package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.UserModel;
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
  private final DatabaseReference databaseReference;
  private final StateLiveData<String> currentCourseIdRepoState = new StateLiveData<>();
  private final StateLiveData<List<UserModel>> allUsersRepoState = new StateLiveData<>();

  private ValueEventListener listener;

  /** JavaDoc. */
  public CourseParticipantsRepository() {
    this.initListener();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.allUsersRepoState.postCreate(new ArrayList<>());
  }

  /** Returns the instance of this singleton class. */
  public static CourseParticipantsRepository getInstance() {
    if (instance == null) {
      instance = new CourseParticipantsRepository();
    }
    return instance;
  }

  public StateLiveData<String> getCurrentCourseIdRepoState() {
    return this.currentCourseIdRepoState;
  }

  public StateLiveData<List<UserModel>> getAllUsersRepoState() {
    return this.allUsersRepoState;
  }

  /** TODO. */
  public void setCurrentCourseIdRepoState(String currentCourseIdRepoState) {
    if (this.currentCourseIdRepoState.getValue() != null) {
      this.databaseReference
              .child(Config.CHILD_COURSES_USER)
              .child(currentCourseIdRepoState)
              .removeEventListener(this.listener);
    }

    this.databaseReference
            .child(Config.CHILD_COURSES_USER)
            .child(currentCourseIdRepoState)
            .addValueEventListener(this.listener);

    this.currentCourseIdRepoState.postCreate(currentCourseIdRepoState);
  }

  private Task<DataSnapshot> getUserTask(String uid) {
    return this.databaseReference.child(Config.CHILD_USER).child(uid).get();
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

            if (model == null) {
              Log.e(TAG, Config.LISTENER_FAILED_TO_RESOLVE);
              allUsersRepoState.postError(new Error(Config.LISTENER_FAILED_TO_RESOLVE), ErrorTag.REPO);
              return;
            }
            model.setKey(task.getResult().getKey());
            userList.add(model);
          }
          allUsersRepoState.postUpdate(userList);
        });

      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, Config.LISTENER_FAILED_TO_RESOLVE);
        allUsersRepoState.postError(new Error(Config.LISTENER_FAILED_TO_RESOLVE), ErrorTag.REPO);
      }
    };
  }

}
