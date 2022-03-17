package com.example.unserhoersaal.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.LikeStatus;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.model.UserModel;
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

  private ArrayList<ThreadModel> threadModelList = new ArrayList<>();
  private MutableLiveData<List<ThreadModel>> threads = new MutableLiveData<>();
  private MutableLiveData<MeetingsModel> meeting = new MutableLiveData<>();
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

  public MutableLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public MutableLiveData<ThreadModel> getThreadModelMutableLiveData() {
    return this.threadModelMutableLiveData;
  }

  /** Set the id of the current meeting. */
  public void setMeeting(MeetingsModel meeting) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    if (this.meeting.getValue() != null) {
      reference.child(Config.CHILD_THREADS).child(this.meeting.getValue().getKey())
              .removeEventListener(this.listener);
    }
    reference.child(Config.CHILD_THREADS)
            .child(meeting.getKey())
            .addValueEventListener(this.listener);
    this.meeting.postValue(meeting);
  }

  /** Loads all threads of the current meeting from the database. */
  //Query is not updated
  public void loadThreads() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.child(Config.CHILD_MEETINGS).child(this.meeting.getValue().getKey())
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
    reference.child(Config.CHILD_THREADS).child(this.meeting.getValue().getKey()).child(threadId)
            .setValue(threadModel)
            .addOnSuccessListener(unused -> {
              threadModel.setKey(threadId);
              threadModelMutableLiveData.postValue(threadModel);
            });
  }

  /** get the author for each thread in the provided list. */
  public void getAuthor(List<ThreadModel> threadList) {
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

  public Task<DataSnapshot> getAuthorData(String authorId) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    return reference.child(Config.CHILD_USER).child(authorId).get();
  }

  private void getLikeStatus(List<ThreadModel> threadList) {
    List<Task<DataSnapshot>> likeList = new ArrayList<>();
    for (ThreadModel thread : threadList) {
      likeList.add(getLikeStatusThread(thread.getKey()));
    }
    Tasks.whenAll(likeList).addOnSuccessListener(unused -> {
      for (int i = 0; i < likeList.size(); i++) {
        if(!likeList.get(i).getResult().exists()) {
          threadList.get(i).setLikeStatus(LikeStatus.NEUTRAL);
        } else if (likeList.get(i).getResult().getValue(LikeStatus.class) == LikeStatus.LIKE) {
          threadList.get(i).setLikeStatus(LikeStatus.LIKE);
        } else if (likeList.get(i).getResult().getValue(LikeStatus.class) == LikeStatus.DISLIKE) {
          threadList.get(i).setLikeStatus(LikeStatus.DISLIKE);
        }
      }
      threadModelList.clear();
      threadModelList.addAll(threadList);
      threads.postValue(threadModelList);
    });
  }

  private Task<DataSnapshot> getLikeStatusThread(String id) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    return reference.child(Config.CHILD_USER_LIKE).child(uid).child(id).get();
  }

  /** Initialise the listener for the database access. */
  public void initListener() {
    listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        List<ThreadModel> threadList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          ThreadModel model = snapshot.getValue(ThreadModel.class);
          model.setKey(snapshot.getKey());
          threadList.add(model);
        }
        getAuthor(threadList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    };
  }

}
