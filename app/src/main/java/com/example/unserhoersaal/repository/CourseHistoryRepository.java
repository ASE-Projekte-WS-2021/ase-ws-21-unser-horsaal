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
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/** Repository for the CourseHistoryViewModel. */
public class CourseHistoryRepository {

  private static final String TAG = "CourseHistoryRepo";

  private static CourseHistoryRepository instance;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseReference;
  private ArrayList<MeetingsModel> meetingsModelList = new ArrayList<>();
  private StateLiveData<List<MeetingsModel>> meetings = new StateLiveData<>();
  private StateLiveData<CourseModel> course = new StateLiveData<>();
  private StateLiveData<MeetingsModel> meetingsModelMutableLiveData = new StateLiveData<>();
  private StateLiveData<String> userId = new StateLiveData<>();
  private ValueEventListener listener;

  /** JavaDoc. */
  public CourseHistoryRepository() {
    this.initListener();
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.course.postCreate(new CourseModel());
    this.meetingsModelMutableLiveData.postCreate(new MeetingsModel());
  }

  /** Generate an instance of the class. */
  public static CourseHistoryRepository getInstance() {
    if (instance == null) {
      instance = new CourseHistoryRepository();
    }
    return instance;
  }

  /** This method gives back all meetings of the course. */
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

  /** Setts the Id of the course. */
  public void setCourse(CourseModel courseModel) {
    String courseId = courseModel.getKey();

    CourseModel courseObj = Validation.checkStateLiveData(this.course, TAG);

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

    this.course.postCreate(courseModel);
  }

  /** JavaDoc. */
  public void setUserId() {
    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.userId.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.userId.postCreate(uid);
  }

  /** Loads all meetings of the course. */
  //Query veraltert
  public void loadMeetings() {
    CourseModel courseObj = Validation.checkStateLiveData(this.course, TAG);
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
    this.meetingsModelMutableLiveData.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    CourseModel courseObj = Validation.checkStateLiveData(this.course, TAG);
    if (courseObj == null) {
      Log.e(TAG, "userModel is null.");
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();
    //TODO: eventTimeInput -> eventTime umschreiben;
    // startTimeInput -> startTime umschreiben; bzw auf datapicker warten
    // (micha: wusste nicht was ich hier machen soll)
    meetingsModel.setCreatorId(uid);
    meetingsModel.setCreationTime(System.currentTimeMillis());

    String meetingId = this.databaseReference.getRoot().push().getKey();

    if (meetingId == null) {
      Log.e(TAG, "meeting id is null");
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
              meetingsModelMutableLiveData.postUpdate(meetingsModel);
            }).addOnFailureListener(e -> {
              Log.e(TAG, "Meeting konnte nicht erstellt werden.");
              meetingsModelMutableLiveData.postError(
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
            meetings.postError(
                    new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
            return;
          }

          model.setKey(snapshot.getKey());
          meetingsModelList.add(model);
        }
        meetings.postUpdate(meetingsModelList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, "Course History Listener Failure");
        meetings.postError(
                new Error(Config.COURSE_HISTORY_MEETING_CREATION_FAILURE), ErrorTag.REPO);
      }
    };
  }

}
