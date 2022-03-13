package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/** Repository for the CourseMeetingViewModel. */
public class CourseMeetingRepository {

  private static final String TAG = "CourseMeetingRepository";

  private static CourseMeetingRepository instance;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseReference;
  private ArrayList<ThreadModel> threadModelList = new ArrayList<>();
  private StateLiveData<List<ThreadModel>> threads = new StateLiveData<>();
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<ThreadModel> threadModelMutableLiveData = new StateLiveData<>();
  private ValueEventListener listener;

  public CourseMeetingRepository() {
    this.initListener();
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
  }

  /** Generate an instance of the class. */
  public static CourseMeetingRepository getInstance() {
    if (instance == null) {
      instance = new CourseMeetingRepository();
    }
    return instance;
  }

  /** Give back all threads of the Meeting. */
  public StateLiveData<List<ThreadModel>> getThreads() {
    this.threads.postValue(new StateData<>(this.threadModelList));
    return this.threads;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public StateLiveData<ThreadModel> getThreadModelMutableLiveData() {
    return this.threadModelMutableLiveData;
  }

  /** Set the id of the current meeting. */
  public void setMeeting(MeetingsModel meeting) {
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      Log.e(TAG, "meetingObj is null.");
      return;
    }

    this.databaseReference
            .child(Config.CHILD_THREADS)
            .child(meetingObj.getKey())
            .removeEventListener(this.listener);

    this.databaseReference
            .child(Config.CHILD_THREADS)
            .child(meeting.getKey())
            .addValueEventListener(this.listener);

    this.meeting.postSuccess(meeting);
  }

  /** Loads all threads of the current meeting from the database. */
  //Query is not updated
  public void loadThreads() {
    this.meeting.postLoading();

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      Log.e(TAG, "meetingObj is null.");
      this.meeting.postError(new Error(Config.THREADS_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }

    Query query = this.databaseReference
            .child(Config.CHILD_MEETINGS)
            .child(meetingObj.getKey())
            .child(Config.CHILD_THREADS);
    query.addValueEventListener(this.listener);
  }

  /** Creates a new threat in the meeting. */
  public void createThread(ThreadModel threadModel) {
    this.threadModelMutableLiveData.postLoading();

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      Log.e(TAG, "meetingObj is null.");
      this.threadModelMutableLiveData.postError(
              new Error(Config.COURSE_MEETING_THREAD_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.threadModelMutableLiveData.postError(
              new Error(Config.COURSE_MEETING_THREAD_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }


    String uid = this.firebaseAuth.getCurrentUser().getUid();

    threadModel.setCreatorId(uid);
    threadModel.setCreationTime(System.currentTimeMillis());

    String threadId = this.databaseReference.getRoot().push().getKey();

    if (threadId == null) {
      Log.e(TAG, Config.COURSE_MEETING_THREAD_CREATION_FAILURE);
      this.threadModelMutableLiveData.postError(
              new Error(Config.COURSE_MEETING_THREAD_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    this.databaseReference.child(Config.CHILD_THREADS)
            .child(meetingObj.getKey())
            .child(threadId)
            .setValue(threadModel)
            .addOnSuccessListener(unused -> {
              threadModel.setKey(threadId);
              threadModelMutableLiveData.postSuccess(threadModel);
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, Config.COURSE_MEETING_THREAD_CREATION_FAILURE);
              threadModelMutableLiveData.postError(
                      new Error(Config.COURSE_MEETING_THREAD_CREATION_FAILURE), ErrorTag.REPO);
            });
  }

  /** get the author for each thread in the provided list. */
  public void getAuthor(List<ThreadModel> threadList) {
    this.threads.postLoading();

    List<Task<DataSnapshot>> authorNames = new ArrayList<>();
    for (ThreadModel thread : threadList) {
      authorNames.add(getAuthorName(thread.getCreatorId()));
    }
    Tasks.whenAll(authorNames).addOnSuccessListener(unused -> {
      for (int i = 0; i < threadList.size(); i++) {
        threadList.get(i).setCreatorName(authorNames.get(i).getResult().getValue(String.class));
      }
      threadModelList.clear();
      threadModelList.addAll(threadList);
      threads.postSuccess(threadModelList);
    });
  }

  public Task<DataSnapshot> getAuthorName(String authorId) {
    return this.databaseReference
            .child(Config.CHILD_USER)
            .child(authorId)
            .child(Config.CHILD_USER_NAME)
            .get();
  }

  /** Initialise the listener for the database access. */
  public void initListener() {
    listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        List<ThreadModel> threadList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          ThreadModel model = snapshot.getValue(ThreadModel.class);

          if (model == null) {
            Log.e(TAG, Config.THREADS_FAILED_TO_LOAD);
            meeting.postError(new Error(Config.THREADS_FAILED_TO_LOAD), ErrorTag.REPO);
            return;
          }

          model.setKey(snapshot.getKey());
          threadList.add(model);
        }
        getAuthor(threadList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, Config.THREADS_FAILED_TO_LOAD);
        meeting.postError(new Error(Config.THREADS_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    };
  }

}
