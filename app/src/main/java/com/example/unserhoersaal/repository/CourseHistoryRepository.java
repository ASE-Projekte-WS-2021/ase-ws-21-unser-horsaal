package com.example.unserhoersaal.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.MeetingsModel;
import com.google.firebase.auth.FirebaseAuth;
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
  private MutableLiveData<String> courseId = new MutableLiveData<>();
  private MutableLiveData<MeetingsModel> meetingsModelMutableLiveData = new MutableLiveData<>();
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
    }*/

    this.meetings.setValue(this.meetingsModelList);
    return this.meetings;
  }

  public MutableLiveData<String> getCourseId() {
    return this.courseId;
  }

  public MutableLiveData<MeetingsModel> getMeetingsModelMutableLiveData() {
    return this.meetingsModelMutableLiveData;
  }

  /** Setts the Id of the course. */
  public void setCourseId(String courseId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.courseId.getValue() != null) {
      reference.child(Config.CHILD_COURSES).child(this.courseId.getValue())
              .child(Config.CHILD_MEETINGS).removeEventListener(this.listener);
    }
    reference.child(Config.CHILD_COURSES).child(courseId)
            .child(Config.CHILD_MEETINGS).addValueEventListener(this.listener);
    this.courseId.postValue(courseId);
  }

  /** Loads all meetings of the course. */
  public void loadMeetings() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.child(Config.CHILD_COURSES).child(courseId.getValue())
            .child(Config.CHILD_MEETINGS);
    query.addValueEventListener(this.listener);
  }

  /** Creats a new meeting in the course. */
  public void createMeeting(MeetingsModel meetingsModel) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid();

    meetingsModel.setCreatorId(uid);
    meetingsModel.setCreationTime(System.currentTimeMillis());
    String meetingId = reference.getRoot().push().getKey();
    reference.child(Config.CHILD_MEETINGS).child(meetingId).setValue(meetingsModel);
    meetingsModel.setKey(meetingId);
    this.addMeetingToCourse(courseId.getValue(), meetingId);
    //TODO make react to finish better
    meetingsModelMutableLiveData.postValue(meetingsModel);
  }

  /** Adds the meeting to the course in the database. */
  public void addMeetingToCourse(String course, String meeting) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_COURSES).child(course).child(Config.CHILD_MEETINGS).child(meeting)
            .setValue(Boolean.TRUE);
  }

  /** Initialises the database listener. */
  public void initListener() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        HashSet<String> meetingIds = new HashSet<>();
        meetingsModelList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          meetingIds.add(snapshot.getKey());
        }
        for (String key : meetingIds) {
          reference.child(Config.CHILD_MEETINGS).child(key)
                  .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      MeetingsModel model = snapshot.getValue(MeetingsModel.class);
                      model.setKey(snapshot.getKey());
                      meetingsModelList.add(model);
                      meetings.postValue(meetingsModelList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                  }
          );
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    };
  }
}
