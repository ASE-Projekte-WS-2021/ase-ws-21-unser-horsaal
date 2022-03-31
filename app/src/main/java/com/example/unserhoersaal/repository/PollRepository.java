package com.example.unserhoersaal.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.PollModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/** Repo for the voting. */
public class PollRepository {

  private static final String TAG = "PollRepository";

  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseReference;
  private static PollRepository instance;
  private StateLiveData<MeetingsModel> meeting = new StateLiveData<>();
  private StateLiveData<PollModel> pollModelStateLiveData = new StateLiveData<>();

  private ArrayList<PollModel> pollList = new ArrayList<>();
  private StateLiveData<List<PollModel>> polls = new StateLiveData<>();

  public PollRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.meeting.postCreate(new MeetingsModel());
  }

  /** Give back an instance of the class. */
  public static PollRepository getInstance() {
    if (instance == null) {
      instance = new PollRepository();
    }
    return instance;
  }

  public StateLiveData<MeetingsModel> getMeeting() {
    return this.meeting;
  }

  public void setMeeting(MeetingsModel meeting) {
    this.meeting.postUpdate(meeting);
  }

  public StateLiveData<PollModel> getPollModelStateLiveData() {
    return this.pollModelStateLiveData;
  }

  public void createNewPoll(PollModel pollModel) {
    MeetingsModel meetingObj = Validation.checkStateLiveData(this.meeting, TAG);
    if (meetingObj == null) {
      Log.e(TAG, "meetingObj is null.");
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
      Log.e(TAG, "pollid is null");
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
    if (this.pollList.size() == 0) {
      this.loadPolls();
    }

    this.polls.postUpdate(pollList);
    return this.polls;
  }

  public void loadPolls() {
    this.pollList.clear();
    this.polls.postLoading();

    //TODO better!!!
    if (this.meeting.getValue() == null) return;
    if (this.meeting.getValue().getData() == null) return;
    if (this.meeting.getValue().getData().getKey() == null) return;

    Query query = databaseReference
            .child(Config.CHILD_POLL)
            .child(this.meeting.getValue().getData().getKey());
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
          pollList.add(dataSnapshot.getValue(PollModel.class));

        }
        polls.postUpdate(pollList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }
}
