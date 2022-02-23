package com.example.unserhoersaal.repository;

import android.net.Uri;
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

/** Repository for the ProfileViewModel. */
public class ProfileRepository {

  private static final String TAG = "ProfileRepo";

  private static ProfileRepository instance;
  private UserModel userModel;
  private MutableLiveData<UserModel> user = new MutableLiveData<>();

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

  public MutableLiveData<UserModel> getUser() {
    return this.user;
  }


  /** Loads an user from the database. */
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

  public void changePhotoURL(Uri file) {
    //TODO: https://firebase.google.com/docs/storage/android/upload-files#upload_from_a_local_file
    //TODO: upload local file to firebase storage -> save URL to UserModel -> update UserModel -> update liveData -> BindingAdapter loads picture from storage (caching?)
  }

  public void changeProfileData(UserModel profileChanges) {
    //TODO: save updated profile data to database and inform user about success -> return to profile fragment
    //TODO: Things to change: displayName, institution, email
    //duplicate data saved to firebase auth!!!
  }

  public void changeAuthData(UserModel profileChanges, String password) {
    //TODO: change the password of logged in user; separated
    //TODO: Things to change: email, displayName, password
  }
}
