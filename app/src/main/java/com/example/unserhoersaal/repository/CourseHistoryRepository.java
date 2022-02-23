package com.example.unserhoersaal.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.MeetingsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CourseHistoryRepository {

  private static final String TAG = "CourseHistoryRepo";

  private static CourseHistoryRepository instance;

  private ArrayList<MeetingsModel> meetingsModelList = new ArrayList<>();
  private MutableLiveData<List<MeetingsModel>> meetings = new MutableLiveData<>();
  private MutableLiveData<String> courseId = new MutableLiveData<>();
  private ValueEventListener listener;

  public CourseHistoryRepository() {
    initListener();
  }

  public static CourseHistoryRepository getInstance() {
    if (instance == null) {
      instance = new CourseHistoryRepository();
    }
    return instance;
  }

  public MutableLiveData<List<MeetingsModel>> getMeetings() {
    if (this.meetingsModelList.size() == 0) {
      this.loadMeetings();
    }

    this.meetings.setValue(this.meetingsModelList);
    return this.meetings;
  }

  public MutableLiveData<String> getCourseId() {
    return this.courseId;
  }

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

  public void loadMeetings() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.child(Config.CHILD_COURSES).child(courseId.getValue())
            .child(Config.CHILD_MEETINGS);
    query.addValueEventListener(this.listener);
  }

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
          reference.child(Config.CHILD_MEETINGS).child(key).addListenerForSingleValueEvent(
                  new ValueEventListener() {
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
