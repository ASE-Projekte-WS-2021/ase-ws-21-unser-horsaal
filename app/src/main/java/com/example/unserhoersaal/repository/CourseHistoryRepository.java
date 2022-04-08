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
import java.util.HashSet;
import java.util.List;

/**
 * Repository for the CourseHistoryViewModel.
 */
public class CourseHistoryRepository {

  private static final String TAG = "CourseHistoryRepo";

  private static CourseHistoryRepository instance;
  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private final ArrayList<MeetingsModel> meetingsModelList = new ArrayList<>();
  private final StateLiveData<List<MeetingsModel>> meetings = new StateLiveData<>();
  private final StateLiveData<CourseModel> course = new StateLiveData<>();
  private final StateLiveData<MeetingsModel> meetingsModelMutableLiveData = new StateLiveData<>();
  private final StateLiveData<String> userId = new StateLiveData<>();
  private final HashSet<String> meetingSet = new HashSet<>();
  private ValueEventListener listener;

  /**
   * Constructor. Get the firebase instances
   */
  public CourseHistoryRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.course.postCreate(new CourseModel());
    this.meetingsModelMutableLiveData.postCreate(new MeetingsModel());
    this.initListener();
  }

  /**
   * Initialize the ValueEventListener.
   */
  private void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        updateMeetingSet(dataSnapshot);
        meetingsModelList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          MeetingsModel model = snapshot.getValue(MeetingsModel.class);

          if (model == null) {
            continue;
          }

          model.setKey(snapshot.getKey());
          meetingsModelList.add(model);
          updateMeetingList(meetingsModelList, model);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        meetings.postError(
                new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      }
    };
  }

  /**
   * Updates the list of all meetings after a change.
   *
   * @param dataSnapshot snapshot with all meetings of a course
   */
  private void updateMeetingSet(DataSnapshot dataSnapshot) {
    HashSet<String> meetingIds = new HashSet<>();
    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
      meetingIds.add(snapshot.getKey());
    }
    meetingSet.clear();
    meetingSet.addAll(meetingIds);
  }

  /**
   * Posts the new courses for the view.
   *
   * @param meetingsModels list of meetings
   */
  private void updateMeetingList(List<MeetingsModel> meetingsModels, MeetingsModel meetingsModel) {
    for (int i = 0; i < meetingsModels.size(); i++) {
      MeetingsModel m = meetingsModels.get(i);
      if (m.getKey().equals(meetingsModel.getKey())) {
        if (meetingSet.contains(meetingsModel.getKey())) {
          meetingsModels.set(i, meetingsModel);
        } else {
          meetingsModels.remove(i);
        }
        this.meetings.postUpdate(meetingsModels);
        return;
      }
    }
    if (this.meetingSet.contains(meetingsModel.getKey())) {
      meetingsModels.add(meetingsModel);
      this.meetings.postUpdate(meetingsModels);
    }
  }

  /**
   * Generate an instance of the class.
   *
   * @return Instance if the CourseHistoryRepository
   */
  public static CourseHistoryRepository getInstance() {
    if (instance == null) {
      instance = new CourseHistoryRepository();
    }
    return instance;
  }

  public StateLiveData<List<MeetingsModel>> getMeetings() {
    this.meetings.postCreate(this.meetingsModelList);
    return this.meetings;
  }

  public StateLiveData<CourseModel> getCourse() {
    return this.course;
  }

  public StateLiveData<String> getUserId() {
    return this.userId;
  }

  public StateLiveData<MeetingsModel> getMeetingsModelMutableLiveData() {
    return this.meetingsModelMutableLiveData;
  }

  /**
   * Setts the Id of the course and load the data if the course changed.
   *
   * @param courseModel model of the new course
   */
  public void setCourse(CourseModel courseModel) {
    String courseId = courseModel.getKey();
    CourseModel courseObj = Validation.checkStateLiveData(this.course, TAG);
    if (courseId == null) {
      return;
    }
    if (courseObj == null
            || courseObj.getKey() == null) {
      this.course.postUpdate(courseModel);
      this.meetingsModelList.clear();
      this.loadMeetings();
    } else if (!courseObj.getKey().equals(courseId)) {
      this.course.postUpdate(courseModel);
      this.meetingsModelList.clear();
      this.loadMeetings();
      this.databaseReference
              .child(Config.CHILD_MEETINGS)
              .child(courseObj.getKey()).removeEventListener(this.listener);
    }
  }

  /**
   * Set the id of the user that is currently logged in.
   */
  public void setUserId() {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.userId.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.userId.postCreate(uid);
  }

  /**
   * Loads all meetings of the course.
   */
  public void loadMeetings() {
    CourseModel courseObj = Validation.checkStateLiveData(this.course, TAG);
    if (courseObj == null) {
      return;
    }

    Query query = this.databaseReference
            .child(Config.CHILD_MEETINGS)
            .child(courseObj.getKey());
    query.addValueEventListener(this.listener);
  }

  /**
   * Creates a new meeting in the course.
   *
   * @param meetingsModel data of the new meeting
   */
  public void createMeeting(MeetingsModel meetingsModel) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    CourseModel courseObj = Validation.checkStateLiveData(this.course, TAG);
    if (courseObj == null) {
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    meetingsModel.setCreatorId(uid);
    meetingsModel.setCreationTime(System.currentTimeMillis());

    String meetingId = this.databaseReference.getRoot().push().getKey();

    if (meetingId == null) {
      this.meetingsModelMutableLiveData.postError(
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
                        meetingsModelMutableLiveData.postUpdate(meetingsModel);
                      }).addOnFailureListener(e -> {
                        Log.e(TAG, e.getMessage());
                        meetingsModelMutableLiveData.postError(
                                new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE),
                                ErrorTag.REPO);
                      });
            }).addOnFailureListener(e -> {
              Log.e(TAG, e.getMessage());
              meetingsModelMutableLiveData.postError(
                      new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
            });
  }

  /**
   * Edit existing Meeting in the course.
   *
   * @param meetingsModel new meeting data
   */
  public void editMeeting(MeetingsModel meetingsModel) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    CourseModel courseObj = Validation.checkStateLiveData(this.course, TAG);
    if (courseObj == null) {
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    String meetingId = meetingsModel.getKey();

    if (meetingId == null) {
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    //reset meeting key thus it is not saved twice in the database
    meetingsModel.setKey(null);
    this.databaseReference
            .child(Config.CHILD_MEETINGS)
            .child(courseObj.getKey())
            .child(meetingId)
            .setValue(meetingsModel)
            .addOnSuccessListener(unused -> meetingsModelMutableLiveData.postUpdate(meetingsModel))
            .addOnFailureListener(e -> {
              Log.e(TAG, e.getMessage());
              meetingsModelMutableLiveData.postError(
                      new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
            });
  }

  /**
   * Gets the user id of the current firebase user.
   *
   * @return id of the current user
   */
  public String getUid() {
    if (this.firebaseAuth.getCurrentUser() == null) {
      return null;
    }
    return firebaseAuth.getCurrentUser().getUid();
  }
}
