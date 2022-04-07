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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
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
  private final HashSet<String> pollSet = new HashSet<>();
  private ValueEventListener listener;

  /**
   * Constructor.
   */
  public PollRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.meeting.postCreate(new MeetingsModel());
    initListener();
  }

  /**
   * Initializes the listener for the current polls.
   */
  private void initListener() {
    this.listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        updatePollSet(snapshot);
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
          PollModel model = dataSnapshot.getValue(PollModel.class);

          if (model == null) {
            continue;
          }

          model.setKey(dataSnapshot.getKey());
          getAuthor(model);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        polls.postError(new Error(Config.POLLS_FAILED_TO_LOAD), ErrorTag.REPO);
      }
    };
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
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      this.meeting.postUpdate(meeting);
      this.pollList.clear();
      this.loadPolls();
      return;
    }
    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      this.meeting.postUpdate(meeting);
      this.pollList.clear();
      this.loadPolls();
    } else if (!meetingKey.equals(meeting.getKey())) {
      this.meeting.postUpdate(meeting);
      this.databaseReference.child(Config.CHILD_POLL).child(meetingKey)
              .removeEventListener(this.listener);
      this.pollList.clear();
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

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      this.polls.postError(new Error(Config.POLLS_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }

    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      this.polls.postError(new Error(Config.POLLS_FAILED_TO_LOAD), ErrorTag.REPO);
      return;
    }

    Query query = this.databaseReference
            .child(Config.CHILD_POLL)
            .child(meetingKey);
    query.addValueEventListener(this.listener);
  }

  /**
   * Update the list of all polls in the meeting.
   *
   * @param dataSnapshot snapshot with polls
   */
  private void updatePollSet(DataSnapshot dataSnapshot) {
    HashSet<String> pollIds = new HashSet<>();
    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
      pollIds.add(snapshot.getKey());
    }
    this.pollSet.clear();
    this.pollSet.addAll(pollIds);
  }

  /**
   * Load the picture and name of the poll creator.
   *
   * @param pollModel data of the poll
   */
  private void getAuthor(PollModel pollModel) {
    this.databaseReference.child(Config.CHILD_USER).child(pollModel.getCreatorId())
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel author = snapshot.getValue(UserModel.class);
                if (author == null) {
                  pollModel.setCreatorName(Config.UNKNOWN_USER);
                } else {
                  pollModel.setCreatorName(author.getDisplayName());
                  pollModel.setPhotoUrl(author.getPhotoUrl());
                }
                setPollOption(pollModel, pollList);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
                polls.postError(new Error(Config.POLLS_FAILED_TO_LOAD), ErrorTag.REPO);
              }
            });
  }

  /**
   * Loads the checked option of a user.
   *
   * @param pollModel data of the changed poll
   * @param pollModelList all polls
   */
  private void setPollOption(PollModel pollModel, List<PollModel> pollModelList) {
    Task<DataSnapshot> task = this.getPollOption(pollModel.getKey());
    if (task == null) {
      pollModel.setCheckedOption(CheckedOptionEnum.NONE);
      updatePollList(pollModel, pollModelList);
      return;
    }
    task.addOnSuccessListener(runnable -> {
      CheckedOptionEnum option = runnable.getValue(CheckedOptionEnum.class);
      if (option == null) {
        option = CheckedOptionEnum.NONE;
      }
      pollModel.setCheckedOption(option);
      updatePollList(pollModel, pollModelList);
    });
  }

  /**
   * Update polls if a poll has changed.
   *
   * @param pollModel data of the changed poll
   * @param pollModelList all polls of the meeting
   */
  private void updatePollList(PollModel pollModel, List<PollModel> pollModelList) {
    for (int i = 0; i < pollModelList.size(); i++) {
      PollModel model = pollModelList.get(i);
      if (model.getKey().equals(pollModel.getKey())) {
        if (this.pollSet.contains(pollModel.getKey())) {
          //update
          pollModelList.set(i, pollModel);
        } else {
          //remove
          pollModelList.remove(i);
        }
        this.polls.postUpdate(pollList);
        return;
      }
    }
    //add
    if (this.pollSet.contains(pollModel.getKey())) {
      pollModelList.add(pollModel);
      this.polls.postUpdate(pollList);
    }
  }

  private Task<DataSnapshot> getPollOption(String id) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      return null;
    }
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    return this.databaseReference.child(Config.CHILD_USER_POLL).child(uid).child(id).get();
  }

  /**
   * Save a vote of the user in the database.
   *
   * @param checkedOptionEnum Indicator which option was chekced by the user
   * @param optionPath database path for the option
   * @param pollId id of the voted poll
   */
  public void vote(CheckedOptionEnum checkedOptionEnum, String optionPath, String pollId) {
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      return;
    }

    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      return;
    }

    if (this.firebaseAuth.getCurrentUser() == null) {
      return;
    }
    String uid = this.firebaseAuth.getCurrentUser().getUid();

    //increase OptionCount
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meetingKey)
            .child(pollId)
            .child(optionPath)
            .setValue(ServerValue.increment(1));
    //increase Count
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meetingKey)
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
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      return;
    }

    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      return;
    }

    if (this.firebaseAuth.getCurrentUser() == null) {
      return;
    }
    String uid = this.firebaseAuth.getCurrentUser().getUid();

    //increase OptionCount
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meetingKey)
            .child(pollId)
            .child(checkedOptionPath)
            .setValue(ServerValue.increment(1));
    //decrease OptionCount
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meetingKey)
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

  /**
   * Remove a vote from the user in the database.
   *
   * @param pollId id of the poll with the removed vote
   * @param optionPath database path for the removed option
   */
  public void removeVote(String optionPath, String pollId) {
    if (this.firebaseAuth.getCurrentUser() == null) {
      return;
    }
    String uid = this.firebaseAuth.getCurrentUser().getUid();

    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      return;
    }

    String meetingKey = meetingObj.getKey();
    if (meetingKey == null) {
      return;
    }
    //remove userPoll
    this.databaseReference.child(Config.CHILD_USER_POLL).child(uid).child(pollId).removeValue();
    this.databaseReference.child(Config.CHILD_POLL_USER).child(pollId).child(uid).removeValue();
    //decreaseOptionCount
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meetingKey)
            .child(pollId)
            .child(optionPath)
            .setValue(ServerValue.increment(-1));
    //decrease count
    this.databaseReference.child(Config.CHILD_POLL)
            .child(meetingKey)
            .child(pollId)
            .child(Config.CHILD_VOTES_COUNT)
            .setValue(ServerValue.increment(-1));
  }
}
