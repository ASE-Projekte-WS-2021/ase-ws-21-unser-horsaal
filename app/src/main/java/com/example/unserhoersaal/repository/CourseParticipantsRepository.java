package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * For Loading all Participants registered in a Course. Displayed in Course Description.
 */
public class CourseParticipantsRepository {

  private static final String TAG = "CourseParticipantsRepo";

  private static CourseParticipantsRepository instance;
  private final DatabaseReference databaseReference;
  private final StateLiveData<String> courseId = new StateLiveData<>();
  private final List<UserModel> userList = new ArrayList<>();
  private final StateLiveData<List<UserModel>> users = new StateLiveData<>();
  private final HashSet<String> allUsers = new HashSet<>();

  public CourseParticipantsRepository() {
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.users.postCreate(new ArrayList<>());
  }

  /**
   * Returns the instance of this singleton class.
   *
   * @return Instance of the CourseParticipantsRepository
   */
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

  /**
   * Loads all users of a course.
   */
  private void loadUsers() {
    String courseKey = Validation.checkStateLiveData(this.courseId, TAG);
    if (courseKey == null) {
      return;
    }

    this.databaseReference
            .child(Config.CHILD_COURSES_USER)
            .child(courseKey)
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateAllUsers(dataSnapshot);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  getAuthor(snapshot.getKey());
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, Config.LISTENER_FAILED_TO_RESOLVE);
                users.postError(new Error(Config.LISTENER_FAILED_TO_RESOLVE), ErrorTag.REPO);
              }
            });
  }

  /**
   * Update the list of signed in users.
   *
   * @param dataSnapshot Snapshot with all signed up users
   */
  private void updateAllUsers(DataSnapshot dataSnapshot) {
    HashSet<String> userIds = new HashSet<>();
    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
      userIds.add(snapshot.getKey());
    }
    allUsers.clear();
    allUsers.addAll(userIds);
  }

  /**
   * Set the courseId of a new course and starts the loading of the participants.
   *
   * @param courseId Id of the new course
   */
  public void setCourseId(String courseId) {
    if (courseId == null) {
      return;
    }
    if (this.courseId.getValue() == null
            || this.courseId.getValue().getData() == null
            || !this.courseId.getValue().getData().equals(courseId)) {
      this.courseId.postUpdate(courseId);
      userList.clear();
      this.loadUsers();
    }

  }

  /**
   * Load the picture and name of a course member.
   *
   * @param userid id of the course member
   */
  private void getAuthor(String  userid) {
    this.databaseReference.child(Config.CHILD_USER).child(userid)
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null) {
                  user.setKey(snapshot.getKey());
                  updateUserList(user, userList);
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                users.postError(new Error(Config.COURSES_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
  }

  /**
   * Update teh members when a user has changed. Joined or lef the course or edited its profile.
   *
   * @param userModel data of the changed member
   * @param userList all member
   */
  private void updateUserList(UserModel userModel, List<UserModel> userList) {
    for (int i = 0; i < userList.size(); i++) {
      UserModel model = userList.get(i);
      if (model.getKey().equals(userModel.getKey())) {
        if (this.allUsers.contains(userModel.getKey())) {
          //update course
          userList.set(i, userModel);
        } else {
          //remove course
          userList.remove(i);
        }
        this.users.postUpdate(userList);
        return;
      }
    }
    //add course
    if (this.allUsers.contains(userModel.getKey())) {
      userList.add(userModel);
      this.users.postUpdate(userList);
    }
  }

}
