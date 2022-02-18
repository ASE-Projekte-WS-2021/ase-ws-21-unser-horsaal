package com.example.unserhoersaal.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileRepository {

  private static final String TAG = "ProfileRepo";

  private static ProfileRepository instance;

  private UserModel userModel;

  private MutableLiveData<UserModel> user = new MutableLiveData<>();

  public ProfileRepository() {
    this.loadUser();
  }

  public static ProfileRepository getInstance() {
    if (instance == null) {
      instance = new ProfileRepository();
    }
    return instance;
  }

  public MutableLiveData<UserModel> getUser() {
    return this.user;
  }


  public void loadUser() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String id = auth.getCurrentUser().getUid();

    Query query = reference.child(Config.CHILD_USER).child(id);
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        userModel = snapshot.getValue(UserModel.class);
        user.postValue(userModel);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: " + error.getMessage());
      }
    });
  }
}
