package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
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

  private ArrayList<MeetingsModel> meetingsModelList = new ArrayList<>();
  private StateLiveData<List<MeetingsModel>> meetings = new StateLiveData<>();
  private StateLiveData<CourseModel> course = new StateLiveData<>();
  private StateLiveData<MeetingsModel> meetingsModelMutableLiveData = new StateLiveData<>();
  private StateLiveData<String> userId = new StateLiveData<>();
  private ValueEventListener listener;

  public CourseHistoryRepository() {
    initListener();
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
    /*if (this.meetingsModelList.size() == 0) {
      this.loadMeetings();
    }
    */

    this.meetings.setValue(new StateData<>(this.meetingsModelList));
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
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.course.getValue() != null) {
      //TODO: assert getdata != null
      reference.child(Config.CHILD_MEETINGS).child(this.course.getValue().getData().getKey())
              .removeEventListener(this.listener);
    }
    reference.child(Config.CHILD_MEETINGS).child(courseId).addValueEventListener(this.listener);
    this.course.postValue(new StateData<>(courseModel));
  }

  public void setUserId() {
    //TODO: assert getdata != null
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    this.userId.postValue(new StateData<>(uid));
  }

  /** Loads all meetings of the course. */
  //Query veraltert
  public void loadMeetings() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    //TODO: assert getdata != null
    Query query = reference.child(Config.CHILD_COURSES).child(course.getValue().getData().getKey())
            .child(Config.CHILD_MEETINGS);
    query.addValueEventListener(this.listener);
  }

  /** Creates a new meeting in the course. */
  public void createMeeting(MeetingsModel meetingsModel) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    //TODO: assert getdata != null
    String uid = firebaseAuth.getCurrentUser().getUid();
    //TODO: eventTimeInput -> eventTime umschreiben;
    // startTimeInput -> startTime umschreiben; bzw auf datapicker warten
    meetingsModel.setCreatorId(uid);
    meetingsModel.setCreationTime(System.currentTimeMillis());
    //TODO: assert getdata != null
    String meetingId = reference.getRoot().push().getKey();
    reference.child(Config.CHILD_MEETINGS)
            //TODO: assert getdata != null
            .child(this.course.getValue().getData().getKey()).child(meetingId)
            .setValue(meetingsModel)
            .addOnSuccessListener(unused -> {
              meetingsModel.setKey(meetingId);
              meetingsModelMutableLiveData.postSuccess(meetingsModel);
            }).addOnFailureListener(e -> {
              Log.e(TAG, "Meeting konnte nicht erstellt werden.");
              meetingsModelMutableLiveData.postError(
                      new Error(Config.COURSE_HISTORY_MEETING_NOT_CREATED), ErrorTag.REPO);
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
          //TODO: assert getdata != null
          model.setKey(snapshot.getKey());
          meetingsModelList.add(model);
        }
        //TODO: is this data ready to be released? else use postValue
        meetings.postSuccess(meetingsModelList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, "Course History Listener Failure");
        meetingsModelMutableLiveData.postError(
                new Error(Config.COURSE_HISTORY_LISTENER_FAILURE), ErrorTag.REPO);
      }
    };
  }

}
