package com.example.unserhoersaal.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
  private UserModel userModel;
  private MutableLiveData<UserModel> user = new MutableLiveData<>();
  private MutableLiveData<Boolean> profileChanged = new MutableLiveData<>();

  public ProfileRepository() {
    this.loadUser();
  }

  /**
   * Generate an instance of the class.
   */
  public static ProfileRepository getInstance() {
    if (instance == null) {
      instance = new ProfileRepository();
    }
    return instance;
  }

  public MutableLiveData<UserModel> getUser() {
    return this.user;
  }

  public MutableLiveData<Boolean> getProfileChanged() {
    return this.profileChanged;
  }

  /**
   * Loads an user from the database.
   */
  public void loadUser() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String id = auth.getCurrentUser().getUid();

    Query query = reference.child(Config.CHILD_USER).child(id);
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        userModel = snapshot.getValue(UserModel.class);
        userModel.setKey(snapshot.getKey());
        user.postValue(userModel);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }

  //TODO: @Julian -> see EditProfileNameFragment & ProfileViewModel
  public void changeDisplayName(String displayName) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    reference.child(Config.CHILD_USER).child(uid).child(Config.CHILD_DISPLAY_NAME)
            .setValue(displayName).addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                profileChanged.postValue(Boolean.TRUE);
              }
            });
  }

  //TODO: @Julian -> see EditProfileInstitutionFragment & ProfileViewModel
  public void changeInstitution(String institution) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    reference.child(Config.CHILD_USER).child(uid).child(Config.CHILD_INSTITUTION)
            .setValue(institution).addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                profileChanged.postValue(Boolean.TRUE);
              }
            });
  }

}
