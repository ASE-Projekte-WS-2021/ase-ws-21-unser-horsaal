package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.enums.LikeStatus;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.android.gms.tasks.Task;
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

/**
 * Repository for the CourseMeetingViewModel.
 */
public class QuestionRepository {

  private static final String TAG = "QuestionRepository";

  private static QuestionRepository instance;
  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private final ArrayList<ThreadModel> threadModelList = new ArrayList<>();
  private final StateLiveData<List<ThreadModel>> threads = new StateLiveData<>();
  private final StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private final StateLiveData<ThreadModel> threadModelMutableLiveData = new StateLiveData<>();
  private final HashSet<String> threadSet = new HashSet<>();
  private ValueEventListener listener;

  /**
   * Constructor.
   */
  public QuestionRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.meeting.postCreate(new MeetingsModel());
    initListener();
  }

  /**
   * Initialize the listener for the current threads.
   */
  private void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        updateThreadSet(dataSnapshot);
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          ThreadModel model = snapshot.getValue(ThreadModel.class);

          if (model == null) {
            continue;
          }

          model.setKey(snapshot.getKey());
          getAuthor(model);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, Config.THREADS_FAILED_TO_LOAD);
        meeting.postError(new Error(Config.THREADS_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    };
  }

  /**
   * Generate an instance of the class.
   *
   * @return Instance of the QuestionRepository
   */
  public static QuestionRepository getInstance() {
    if (instance == null) {
      instance = new QuestionRepository();
    }
    return instance;
  }

  public StateLiveData<List<ThreadModel>> getThreads() {
    this.threads.postCreate(this.threadModelList);
    return this.threads;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public StateLiveData<ThreadModel> getThreadModelMutableLiveData() {
    return this.threadModelMutableLiveData;
  }

  /**
   * Set the id of the current meeting.
   *
   * @param meeting data of the new meeting
   */
  public void setMeeting(MeetingsModel meeting) {
    if (meeting == null || meeting.getKey() == null) {
      return;
    }
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      this.meeting.postUpdate(meeting);
      this.threadModelList.clear();
      this.loadThreads();
      return;
    }
    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      this.meeting.postUpdate(meeting);
      this.threadModelList.clear();
      this.loadThreads();
    } else if (!meetingKey.equals(meeting.getKey())) {
      this.meeting.postUpdate(meeting);
      this.databaseReference.child(Config.CHILD_POLL).child(meetingKey)
              .removeEventListener(this.listener);
      this.threadModelList.clear();
      this.loadThreads();
    }
  }

  /**
   * Loads all threads of the current meeting from the database.
   */
  private void loadThreads() {
    this.meeting.postLoading();

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      this.meeting.postError(new Error(Config.THREADS_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }

    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      this.meeting.postError(new Error(Config.THREADS_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }

    Query query = this.databaseReference
            .child(Config.CHILD_THREADS)
            .child(meetingKey);
    query.addValueEventListener(this.listener);
  }

  /**
   * Update the list of all threads in the meeting.
   *
   * @param dataSnapshot snapshot with meetings
   */
  private void updateThreadSet(DataSnapshot dataSnapshot) {
    HashSet<String> threadIds = new HashSet<>();
    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
      threadIds.add(snapshot.getKey());
    }
    this.threadSet.clear();
    this.threadSet.addAll(threadIds);
  }

  /**
   * Creates a new threat in the meeting.
   *
   * @param threadModel data of the created thread
   */
  public void createThread(ThreadModel threadModel) {
    this.threadModelMutableLiveData.postLoading();

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
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
              threadModelMutableLiveData.postUpdate(threadModel);
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, Config.COURSE_MEETING_THREAD_CREATION_FAILURE);
              threadModelMutableLiveData.postError(
                      new Error(Config.COURSE_MEETING_THREAD_CREATION_FAILURE), ErrorTag.REPO);
            });
  }

  /**
   * Get the author for each thread in the provided list.
   *
   * @param threadModel list with all threads of a meeting
   */
  private void getAuthor(ThreadModel threadModel) {
    this.databaseReference.child(Config.CHILD_USER).child(threadModel.getCreatorId())
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel author = snapshot.getValue(UserModel.class);
                if (author == null) {
                  threadModel.setCreatorName(Config.UNKNOWN_USER);
                } else {
                  threadModel.setCreatorName(author.getDisplayName());
                  threadModel.setPhotoUrl(author.getPhotoUrl());
                }
                setLikeStatus(threadModel, threadModelList);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                threads.postError(new Error(Config.THREADS_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
  }

  /**
   * Update all threads if a thread has changed.
   *
   * @param threadModel data of the change thread
   * @param threadList all threads
   */
  private void setLikeStatus(ThreadModel threadModel, List<ThreadModel> threadList) {
    Task<DataSnapshot> task = this.getLikeStatusThread(threadModel.getKey());
    if (task == null) {
      threadModel.setLikeStatus(LikeStatus.NEUTRAL);
      updateThreadList(threadModel, threadList);
      return;
    }
    task.addOnSuccessListener(runnable -> {
      LikeStatus status = runnable.getValue(LikeStatus.class);
      if (status == null) {
        status = LikeStatus.NEUTRAL;
      }
      threadModel.setLikeStatus(status);
      updateThreadList(threadModel, threadList);
    });
  }

  /**
   * Update threads if a thread has changed.
   *
   * @param threadModel data of the changed thread
   * @param threadList all threads of the meeting
   */
  private void updateThreadList(ThreadModel threadModel, List<ThreadModel> threadList) {
    for (int i = 0; i < threadList.size(); i++) {
      ThreadModel model = threadList.get(i);
      if (model.getKey().equals(threadModel.getKey())){
        if (this.threadSet.contains(threadModel.getKey())) {
          //update
          threadList.set(i, threadModel);
        } else {
          //remove
          threadList.remove(i);
        }
        this.threads.postUpdate(threadModelList);
        return;
      }
    }
    //add
    if (this.threadSet.contains(threadModel.getKey())) {
      threadList.add(threadModel);
      this.threads.postUpdate(threadModelList);
    }
  }

  private Task<DataSnapshot> getLikeStatusThread(String id) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      return null;
    }
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    return this.databaseReference.child(Config.CHILD_USER_LIKE).child(uid).child(id).get();
  }

}
