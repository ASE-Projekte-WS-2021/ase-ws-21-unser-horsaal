package com.example.unserhoersaal.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Repository for the CourseHistoryViewModel. */
public class CourseHistoryRepository {

  private static final String TAG = "CourseHistoryRepo";

  private static CourseHistoryRepository instance;

  private ArrayList<MeetingsModel> meetingsModelList = new ArrayList<>();
  private MutableLiveData<List<MeetingsModel>> meetings = new MutableLiveData<>();
  private MutableLiveData<CourseModel> course = new MutableLiveData<>();
  private MutableLiveData<MeetingsModel> meetingsModelMutableLiveData = new MutableLiveData<>();
  private MutableLiveData<String> userId = new MutableLiveData<>();
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
  public MutableLiveData<List<MeetingsModel>> getMeetings() {
    /*if (this.meetingsModelList.size() == 0) {
      this.loadMeetings();
    }
    */

    this.meetings.setValue(this.meetingsModelList);
    return this.meetings;
  }

  public MutableLiveData<CourseModel> getCourse() {
    return this.course;
  }

  public MutableLiveData<String> getUserId() {
    return this.userId;
  }

  public MutableLiveData<MeetingsModel> getMeetingsModelMutableLiveData() {
    return this.meetingsModelMutableLiveData;
  }

  /** Setts the Id of the course. */
  public void setCourse(CourseModel courseModel) {
    String courseId = courseModel.getKey();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.course.getValue() != null) {
      reference.child(Config.CHILD_MEETINGS).child(this.course.getValue().getKey())
              .removeEventListener(this.listener);
    }
    reference.child(Config.CHILD_MEETINGS).child(courseId).addValueEventListener(this.listener);
    this.course.postValue(courseModel);
  }

  public void setUserId() {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    this.userId.postValue(uid);
  }

  /** Loads all meetings of the course. */
  //Query veraltert
  public void loadMeetings() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.child(Config.CHILD_COURSES).child(course.getValue().getKey())
            .child(Config.CHILD_MEETINGS);
    query.addValueEventListener(this.listener);
  }

  /** Creates a new meeting in the course. */
  public void createMeeting(MeetingsModel meetingsModel) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid();
    //TODO: eventTimeInput -> eventTime umschreiben; startTimeInput -> startTime umschreiben; bzw auf datapicker warten
    meetingsModel.setCreatorId(uid);
    meetingsModel.setCreationTime(System.currentTimeMillis());
    String meetingId = reference.getRoot().push().getKey();
    reference.child(Config.CHILD_MEETINGS).child(this.course.getValue().getKey()).child(meetingId)
            .setValue(meetingsModel)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                meetingsModel.setKey(meetingId);
                meetingsModelMutableLiveData.postValue(meetingsModel);
              }
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
          model.setKey(snapshot.getKey());
          meetingsModelList.add(model);
        }
        meetings.postValue(meetingsModelList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    };
  }
}
