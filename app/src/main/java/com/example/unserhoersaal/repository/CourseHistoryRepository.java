package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/** Repository for the CourseHistoryViewModel. */
public class CourseHistoryRepository {

  private static final String TAG = "CourseHistoryRepo";

  private static CourseHistoryRepository instance;
  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private final ArrayList<MeetingsModel> meetingsModelList = new ArrayList<>(); //TODO: replace or rename
  private final StateLiveData<List<MeetingsModel>> allMeetingsRepoState = new StateLiveData<>();
  private final StateLiveData<CourseModel> courseRepoState = new StateLiveData<>();
  private final StateLiveData<MeetingsModel> currentMeetingRepoState = new StateLiveData<>();
  private final StateLiveData<String> currentUserIdRepoState = new StateLiveData<>();
  private ValueEventListener listener;

  /** JavaDoc. */
  public CourseHistoryRepository() {
    this.initListener();
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.courseRepoState.postCreate(new CourseModel());
    this.currentMeetingRepoState.postCreate(new MeetingsModel());
  }

  /** Generate an instance of the class. */
  public static CourseHistoryRepository getInstance() {
    if (instance == null) {
      instance = new CourseHistoryRepository();
    }
    return instance;
  }

  /** This method gives back all meetings of the course. */
  public StateLiveData<List<MeetingsModel>> getAllMeetingsRepoState() {
    this.allMeetingsRepoState.postCreate(this.meetingsModelList);
    return this.allMeetingsRepoState;
  }

  public StateLiveData<CourseModel> getCourseRepoState() {
    return this.courseRepoState;
  }

  public StateLiveData<String> getCurrentUserIdRepoState() {
    return this.currentUserIdRepoState;
  }

  public StateLiveData<MeetingsModel> getCurrentMeetingRepoState() {
    return this.currentMeetingRepoState;
  }

  /** Setts the Id of the course. */
  public void setCourseRepoState(CourseModel courseModel) {
    String courseId = courseModel.getKey();

    CourseModel courseObj = Validation.checkStateLiveData(this.courseRepoState, TAG);
    if (courseObj ==  null) {
      Log.e(TAG, Config.HISTORY_NO_COURSE_MODEL);
      this.courseRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.REPO);
      return;
    }

    if (courseObj.getKey() != null) {
      this.databaseReference
              .child(Config.CHILD_MEETINGS)
              .child(courseObj.getKey())
              .removeEventListener(this.listener);
    }

    this.databaseReference
            .child(Config.CHILD_MEETINGS)
            .child(courseId)
            .addValueEventListener(this.listener);

    this.courseRepoState.postCreate(courseModel);
  }

  /** JavaDoc. */
  public void setUserId() {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.currentUserIdRepoState.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.currentUserIdRepoState.postCreate(uid);
  }

  /** Loads all meetings of the course. */
  //Query veraltert
  public void loadMeetings() {
    CourseModel courseObj = Validation.checkStateLiveData(this.courseRepoState, TAG);
    if (courseObj == null) {
      Log.e(TAG, "courseObj is null.");
      return;
    }

    Query query = this.databaseReference
            .child(Config.CHILD_COURSES)
            .child(courseObj.getKey())
            .child(Config.CHILD_MEETINGS);
    query.addValueEventListener(this.listener);
  }

  /** Creates a new meeting in the course. */
  public void createMeeting(MeetingsModel meetingsModel) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.currentMeetingRepoState.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    CourseModel courseObj = Validation.checkStateLiveData(this.courseRepoState, TAG);
    if (courseObj == null) {
      Log.e(TAG, Config.HISTORY_NO_USER_MODEL);
      this.currentMeetingRepoState.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    meetingsModel.setCreatorId(uid);
    meetingsModel.setCreationTime(System.currentTimeMillis());

    String meetingId = this.databaseReference.getRoot().push().getKey();

    if (meetingId == null) {
      Log.e(TAG, Config.HISTORY_NO_MEETING_ID);
      this.currentMeetingRepoState.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    this.databaseReference
            .child(Config.CHILD_MEETINGS)
            .child(courseObj.getKey())
            .child(meetingId)
            .setValue(meetingsModel)
            .addOnSuccessListener(unused -> {
              meetingsModel.setKey(meetingId);
              this.databaseReference.child(Config.CHILD_COURSES)
                      .child(courseObj.getKey())
                      .child(Config.CHILD_MEETINGS_COUNT)
                      .setValue(ServerValue.increment(1))
                      .addOnSuccessListener(unused1 -> {
                        meetingsModel.setKey(meetingId);
                        currentMeetingRepoState.postUpdate(meetingsModel);
                      }).addOnFailureListener(e -> {
                        Log.e(TAG, e.getMessage());
                        currentMeetingRepoState.postError(
                                new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
            });
            }).addOnFailureListener(e -> {
              Log.e(TAG, e.getMessage());
              currentMeetingRepoState.postError(
                      new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
            });
  }

  /** Initialises the database listener. */
  public void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        meetingsModelList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          MeetingsModel model = snapshot.getValue(MeetingsModel.class);

          if (model == null) {
            Log.e(TAG, Config.COURSE_HISTORY_MEETING_CREATION_FAILURE);
            allMeetingsRepoState.postError(
                    new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
            return;
          }

          model.setKey(snapshot.getKey());
          meetingsModelList.add(model);
        }
        allMeetingsRepoState.postUpdate(meetingsModelList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, Config.HISTORY_LISTENER_FAILURE);
        allMeetingsRepoState.postError(
                new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      }
    };
  }

}
