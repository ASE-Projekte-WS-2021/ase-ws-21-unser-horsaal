package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.CheckedOptionEnum;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.PollModel;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Repo for the polls.
 */
public class PollRepository {

  private static final String TAG = "PollRepository";

  private final FirebaseAuth firebaseAuth;
  private final DatabaseReference databaseReference;
  private static PollRepository instance;
  private final StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private final StateLiveData<PollModel> pollModelStateLiveData = new StateLiveData<>();

  private final ArrayList<PollModel> pollList = new ArrayList<>();
  private final StateLiveData<List<PollModel>> polls = new StateLiveData<>();

  /**
   * Constructor.
   */
  public PollRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.meeting.postCreate(new MeetingsModel());
  }

  /**
   * Give back an instance of the class.
   *
   * @return Instance of the PollRepository
   * */
  public static PollRepository getInstance() {
    if (instance == null) {
      instance = new PollRepository();
    }
    return instance;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  /**
   * Set the new meeting and load the polls of the meeting.
   *
   * @param meeting data of the meeting
   */
  public void setMeeting(MeetingsModel meeting) {
    if (meeting == null || meeting.getKey() == null) {
      return;
    }
    if (this.meeting.getValue() == null
            || this.meeting.getValue().getData() == null
            || this.meeting.getValue().getData().getKey() == null
            || !this.meeting.getValue().getData().getKey().equals(meeting.getKey())) {
      this.meeting.postUpdate(meeting);
      this.loadPolls();
    }
  }

  public StateLiveData<PollModel> getPollModelStateLiveData() {
    return this.pollModelStateLiveData;
  }

  /**
   * Save the data of a new poll in the database.
   *
   * @param pollModel data of the new poll
   */
  public void createNewPoll(PollModel pollModel) {
    this.pollModelStateLiveData.postLoading();
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      Log.e(TAG, Config.MEETING_OBJECT_NULL);
      this.pollModelStateLiveData.postError(
              new Error(Config.POLL_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.pollModelStateLiveData.postError(
              new Error(Config.POLL_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getCurrentUser().getUid();

    pollModel.setCreatorId(uid);
    pollModel.setCreationTime(System.currentTimeMillis());
    String pollId = this.databaseReference.getRoot().push().getKey();

    if (pollId == null) {
      Log.e(TAG, Config.POLL_ID_NULL);
      this.pollModelStateLiveData.postError(
              new Error(Config.POLL_CREATION_FAILURE), ErrorTag.REPO);
      return;
    }

    this.databaseReference.child(Config.CHILD_POLL)
            .child(meetingObj.getKey())
            .child(pollId)
            .setValue(pollModel)
            .addOnSuccessListener(unused -> {
              pollModel.setKey(pollId);
              pollModelStateLiveData.postUpdate(pollModel);
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, Config.POLL_CREATION_FAILURE);
              pollModelStateLiveData.postError(
                      new Error(Config.POLL_CREATION_FAILURE), ErrorTag.REPO);
            });
  }

  public StateLiveData<List<PollModel>> getPolls() {
    this.polls.postUpdate(pollList);
    return this.polls;
  }

  /**
   * Load all polls for the current meeting.
   */
  private void loadPolls() {
    this.polls.postLoading();

    Query query = databaseReference
            .child(Config.CHILD_POLL)
            .child(this.meeting.getValue().getData().getKey());
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        List<PollModel> pollModelList = new ArrayList<>();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
          PollModel model = dataSnapshot.getValue(PollModel.class);
          if (model == null) {
            Log.e(TAG, Config.POLLS_FAILED_TO_LOAD);
            meeting.postError(new Error(Config.POLLS_FAILED_TO_LOAD), ErrorTag.REPO);
            return;
          }

          model.setKey(dataSnapshot.getKey());
          pollModelList.add(model);
        }
        getAuthor(pollModelList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

  /**
   * Get the creator data of all polls of a meeting.
   *
   * @param pollModelList list of all polls of a meeting
   */
  private void getAuthor(List<PollModel> pollModelList) {
    List<Task<DataSnapshot>> authors = new ArrayList<>();
    for (PollModel poll : pollModelList) {
      authors.add(getAuthorData(poll.getCreatorId()));
    }
    Tasks.whenAll(authors).addOnSuccessListener(unused -> {
      for (int i = 0; i < pollModelList.size(); i++) {
        UserModel author = authors.get(i).getResult().getValue(UserModel.class);
        if (author == null) {
          pollModelList.get(i).setCreatorName(Config.UNKNOWN_USER);
        } else {
          pollModelList.get(i).setCreatorName(author.getDisplayName());
          pollModelList.get(i).setPhotoUrl(author.getPhotoUrl());
        }
      }
      getPollStatus(pollModelList);
    });
  }

  private Task<DataSnapshot> getAuthorData(String authorId) {
    return this.databaseReference.child(Config.CHILD_USER).child(authorId).get();
  }

  /**
   * Loads if the users has voted in a poll and which option.
   *
   * @param pollModelList list of all polls of a meeting
   */
  private void getPollStatus(List<PollModel> pollModelList) {
    List<Task<DataSnapshot>> taskList = new ArrayList<>();
    for (PollModel poll : pollModelList) {
      taskList.add(getPollOption(poll.getKey()));
    }
    Tasks.whenAll(taskList).addOnSuccessListener(unused -> {
      for (int i = 0; i < taskList.size(); i++) {
        if (!taskList.get(i).getResult().exists()) {
          pollModelList.get(i).setCheckedOption(CheckedOptionEnum.NONE);
        } else {
          pollModelList.get(i)
                  .setCheckedOption(taskList.get(i).getResult().getValue(CheckedOptionEnum.class));
        }
      }
      pollList.clear();
      pollList.addAll(pollModelList);
      polls.postUpdate(pollList);
    });
  }

  private Task<DataSnapshot> getPollOption(String id) {
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    return this.databaseReference.child(Config.CHILD_USER_POLL).child(uid).child(id).get();
  }

  //TODO ifs
  /**
   * Save a vote of the user in the database.
   *
   * @param checkedOptionEnum Indicator which option was chekced by the user
   * @param optionPath database path for the option
   * @param pollId id of the voted poll
   */
  public void vote(CheckedOptionEnum checkedOptionEnum, String optionPath, String pollId) {
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    //increase OptionCount
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meeting.getValue().getData().getKey())
            .child(pollId)
            .child(optionPath)
            .setValue(ServerValue.increment(1));
    //increase Count
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meeting.getValue().getData().getKey())
            .child(pollId)
            .child(Config.CHILD_VOTES_COUNT)
            .setValue(ServerValue.increment(1));
    //set userPoll
    this.databaseReference.child(Config.CHILD_USER_POLL)
            .child(uid)
            .child(pollId)
            .setValue(checkedOptionEnum);
    this.databaseReference.child(Config.CHILD_POLL_USER)
            .child(pollId)
            .child(uid)
            .setValue(checkedOptionEnum);
  }

  //TODO ifs
  /**
   * Change a user vote from one option to another.
   *
   * @param pollId id of the checked poll
   * @param checkedOption new option the user has chosen
   * @param checkedOptionPath database path for the new option
   * @param oldOptionPath database path for the old option
   */
  public void changeVote(CheckedOptionEnum checkedOption, String checkedOptionPath,
                         String oldOptionPath, String pollId) {
    String uid = this.firebaseAuth.getCurrentUser().getUid();

    //increase OptionCount
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meeting.getValue().getData().getKey())
            .child(pollId)
            .child(checkedOptionPath)
            .setValue(ServerValue.increment(1));
    //decrease OptionCount
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meeting.getValue().getData().getKey())
            .child(pollId)
            .child(oldOptionPath)
            .setValue(ServerValue.increment(-1));
    //set userPoll
    this.databaseReference.child(Config.CHILD_USER_POLL)
            .child(uid)
            .child(pollId)
            .setValue(checkedOption);
    this.databaseReference.child(Config.CHILD_POLL_USER)
            .child(pollId)
            .child(uid)
            .setValue(checkedOption);
  }

  //TODO ifs
  /**
   * Remove a vote from the user in the database.
   *
   * @param pollId id of the poll with the removed vote
   * @param optionPath database path for the removed option
   */
  public void removeVote(String optionPath, String pollId) {
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    //remove userPoll
    this.databaseReference.child(Config.CHILD_USER_POLL).child(uid).child(pollId).removeValue();
    this.databaseReference.child(Config.CHILD_POLL_USER).child(pollId).child(uid).removeValue();
    //decreaseOptionCount
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meeting.getValue().getData().getKey())
            .child(pollId)
            .child(optionPath)
            .setValue(ServerValue.increment(-1));
    //decrease count
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meeting.getValue().getData().getKey())
            .child(pollId)
            .child(Config.CHILD_VOTES_COUNT)
            .setValue(ServerValue.increment(-1));
  }
}
