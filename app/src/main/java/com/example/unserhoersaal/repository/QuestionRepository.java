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

  /**
   * Constructor.
   */
  public QuestionRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.meeting.postCreate(new MeetingsModel());
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
    String meetingId = meeting.getKey();
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingId == null) {
      return;
    }
    if (meetingObj == null
            || meetingObj.getKey() == null
            || !meetingObj.getKey().equals(meetingId)) {
      this.meeting.postUpdate(meeting);
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

    Query query = this.databaseReference
            .child(Config.CHILD_THREADS)
            .child(meetingObj.getKey());
    query.addValueEventListener(new ValueEventListener() {
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
    });
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
   * @param threadList list with all threads of a meeting
   */
  private void getAuthor(List<ThreadModel> threadList) {
    this.threads.postLoading();

    List<Task<DataSnapshot>> authors = new ArrayList<>();
    for (ThreadModel thread : threadList) {
      authors.add(getAuthorData(thread.getCreatorId()));
    }
    Tasks.whenAll(authors).addOnSuccessListener(unused -> {
      for (int i = 0; i < threadList.size(); i++) {
        UserModel author = authors.get(i).getResult().getValue(UserModel.class);
        if (author == null) {
          threadList.get(i).setCreatorName(Config.UNKNOWN_USER);
        } else {
          threadList.get(i).setCreatorName(author.getDisplayName());
          threadList.get(i).setPhotoUrl(author.getPhotoUrl());
        }
      }
      getLikeStatus(threadList);
    });
  }

  private Task<DataSnapshot> getAuthorData(String authorId) {
    return this.databaseReference.child(Config.CHILD_USER).child(authorId).get();
  }

  /**
   * Load if a user has liked/disliked or not interacted with the thread.
   *
   * @param threadList list of all threads of the current meeting
   */
  private void getLikeStatus(List<ThreadModel> threadList) {
    List<Task<DataSnapshot>> likeList = new ArrayList<>();
    for (ThreadModel thread : threadList) {
      likeList.add(getLikeStatusThread(thread.getKey()));
    }
    Tasks.whenAll(likeList).addOnSuccessListener(unused -> {
      for (int i = 0; i < likeList.size(); i++) {
        if (!likeList.get(i).getResult().exists()) {
          threadList.get(i).setLikeStatus(LikeStatus.NEUTRAL);
        } else if (likeList.get(i).getResult().getValue(LikeStatus.class) == LikeStatus.LIKE) {
          threadList.get(i).setLikeStatus(LikeStatus.LIKE);
        } else if (likeList.get(i).getResult().getValue(LikeStatus.class) == LikeStatus.DISLIKE) {
          threadList.get(i).setLikeStatus(LikeStatus.DISLIKE);
        }
      }
      threadModelList.clear();
      threadModelList.addAll(threadList);
      threads.postUpdate(threadModelList);
    });
  }

  private Task<DataSnapshot> getLikeStatusThread(String id) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      return null;
    }
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    return this.databaseReference.child(Config.CHILD_USER_LIKE).child(uid).child(id).get();
  }

}
