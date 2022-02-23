package com.example.unserhoersaal.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.ThreadModel;
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

/** Repository for the CourseMeetingViewModel. */
public class CourseMeetingRepository {

  private static final String TAG = "CourseMeetingRepository";

  private static CourseMeetingRepository instance;

  private ArrayList<ThreadModel> threadModelList = new ArrayList<>();
  private MutableLiveData<List<ThreadModel>> threads = new MutableLiveData<>();
  private MutableLiveData<String> meetingId = new MutableLiveData<>();
  private MutableLiveData<ThreadModel> threadModelMutableLiveData = new MutableLiveData<>();
  private ValueEventListener listener;

  public CourseMeetingRepository() {
    initListener();
  }

  /** Generate an instance of the class. */
  public static CourseMeetingRepository getInstance() {
    if (instance == null) {
      instance = new CourseMeetingRepository();
    }
    return instance;
  }

  /** Give back all threads of the Meeting. */
  public MutableLiveData<List<ThreadModel>> getThreads() {
    /*if (this.threadModelList.size() == 0) {
      loadThreads();
    }*/

    this.threads.setValue(this.threadModelList);
    return this.threads;
  }

  public MutableLiveData<String> getMeetingId() {
    return this.meetingId;
  }

  public MutableLiveData<ThreadModel> getThreadModelMutableLiveData() {
    return this.threadModelMutableLiveData;
  }

  /** Set the id of the current meeting. */
  public void setMeetingId(String meetingId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.meetingId.getValue() != null) {
      reference.child(Config.CHILD_MEETINGS).child(this.meetingId.getValue())
              .child(Config.CHILD_THREADS).removeEventListener(this.listener);
    }
    reference.child(Config.CHILD_MEETINGS).child(meetingId)
            .child(Config.CHILD_THREADS).addValueEventListener(this.listener);
    this.meetingId.postValue(meetingId);
  }

  /** Loads all threads of the current meeting from the database. */
  public void loadThreads() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.child(Config.CHILD_MEETINGS).child(this.meetingId.getValue())
            .child(Config.CHILD_THREADS);
    query.addValueEventListener(this.listener);
  }

  /** Creates a new threat in the meeting. */
  public void createThread(ThreadModel threadModel) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid();

    threadModel.setCreatorId(uid);
    threadModel.setCreationTime(System.currentTimeMillis());
    String threadId = reference.getRoot().push().getKey();
    reference.child(Config.CHILD_THREADS).child(threadId).setValue(threadModel);
    threadModel.setKey(threadId);
    this.addThreadToMeeting(meetingId.getValue(), threadId);
    threadModelMutableLiveData.postValue(threadModel);
  }

  /** Adds a new thread to the current meeting in the database. */
  public void addThreadToMeeting(String meeting, String thread) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_MEETINGS).child(meeting).child(Config.CHILD_THREADS).child(thread)
            .setValue(Boolean.TRUE);
  }

  /** Initialise the listener for the database access. */
  public void initListener() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        HashSet<String> threadIds = new HashSet<>();
        threadModelList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          threadIds.add(snapshot.getKey());
        }
        for (String key : threadIds) {
          reference.child(Config.CHILD_THREADS).child(key)
                  .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      ThreadModel model = snapshot.getValue(ThreadModel.class);
                      model.setKey(snapshot.getKey());
                      threadModelList.add(model);
                      threads.postValue(threadModelList);
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
