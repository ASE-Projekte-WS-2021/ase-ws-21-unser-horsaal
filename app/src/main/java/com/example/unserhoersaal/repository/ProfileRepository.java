package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/** Repository for the ProfileViewModel. */
public class ProfileRepository {

  private static final String TAG = "ProfileRepo";

  private static ProfileRepository instance;
  private StateLiveData<UserModel> user = new StateLiveData<>();
  private StateLiveData<Boolean> profileChanged = new StateLiveData<>();

  public ProfileRepository() {
    this.loadUser();
  }

  /** Generate an instance of the class. */
  public static ProfileRepository getInstance() {
    if (instance == null) {
      instance = new ProfileRepository();
    }
    return instance;
  }

  public StateLiveData<UserModel> getUser() {
    return this.user;
  }

  public StateLiveData<Boolean> getProfileChanged() {
    return this.profileChanged;
  }

  /** Loads an user from the database. */
  public void loadUser() {
    this.user.postLoading();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    //TODO: maybe make firebaseauth to an instance variable?
    //TODO: assert firebaseuser != null
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String id = auth.getCurrentUser().getUid();

    Query query = reference.child(Config.CHILD_USER).child(id);
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        UserModel userModel = snapshot.getValue(UserModel.class);
        //TODO: assert userModel != null
        userModel.setKey(snapshot.getKey());
        user.postSuccess(userModel);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
        user.postError(new Error(Config.PROFILE_FAILED_TO_LOAD_USER), ErrorTag.REPO);
      }
    });
  }

  /** TODO. */
  public void changeDisplayName(String displayName) {
    this.profileChanged.postLoading();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    //TODO: maybe make firebaseauth to an instance variable?
    //TODO: assert firebaseuser == null
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    reference.child(Config.CHILD_USER)
            .child(uid)
            .child(Config.CHILD_DISPLAY_NAME)
            .setValue(displayName)
            .addOnSuccessListener(unused ->
              profileChanged.postSuccess(Boolean.TRUE)
            )
            .addOnFailureListener(e -> {
              Log.e(TAG, e.getMessage());
              profileChanged.postError(
                      new Error(Config.PROFILE_FAILED_TO_CHANGE_DISPLAYNAME), ErrorTag.REPO);
            });
  }

  /** TODO. */
  public void changeInstitution(String institution) {
    this.profileChanged.postLoading();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    //TODO: assert if firebaseauth.getinstance.getcurrentuser returns null -> this.user.postError
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    reference.child(Config.CHILD_USER)
            .child(uid)
            .child(Config.CHILD_INSTITUTION)
            .setValue(institution)
            .addOnSuccessListener(unused -> profileChanged.postSuccess(Boolean.TRUE))
            .addOnFailureListener(e ->
                    profileChanged.postError(
                            new Error(Config.PROFILE_FAILED_TO_CHANGE_INSTITUTION), ErrorTag.REPO));
  }

  /** TODO. */
  public void changePassword(String oldPassword, String newPassword) {
    //TODO: loading for password -> firebaseuser?
    this.profileChanged.postLoading();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //TODO: check if user is not null
    String email = user.getEmail();
    AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

    user.reauthenticate(credential).addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
          if (task1.isSuccessful()) {
            profileChanged.postSuccess(Boolean.TRUE);
          } else {
            //todo password reset failed
            Log.d(TAG, "onComplete: " + "reset failed");
            profileChanged.postError(new Error(Config.PROFILE_FAILED_TO_CHANGE_PASSWORD), ErrorTag.REPO);
          }
        });
      } else {
        //todo old password wrong
        Log.d(TAG, "onComplete: " + "old password wrong");
        profileChanged.postError(new Error(Config.PROFILE_FAILED_TO_CHANGE_PASSWORD), ErrorTag.REPO);
      }
    });
  }

}
