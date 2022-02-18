package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.repository.ProfileRepository;
import com.google.firebase.auth.FirebaseUser;

/** Class Description. */
public class ProfileViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private ProfileRepository profileRepository;
  private MutableLiveData<FirebaseUser> userLiveData;
  private MutableLiveData<UserModel> profileLiveData;

  /** Initialize the LoginRegisterViewModel. */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserLiveData();

    //this live data loads profile data into the text views
    this.profileRepository = ProfileRepository.getInstance();
    this.profileLiveData = this.profileRepository.getUser();
  }

  /** Give back the user data. */
  public LiveData<FirebaseUser> getUserLiveData() {
    return this.userLiveData;
  }

  public LiveData<UserModel> getProfileLiveData() {
    return this.profileLiveData;
  }

  public void logout() {
    this.authAppRepository.logOut();
  }

  public void deleteAccount() {
    this.authAppRepository.deleteAccount();
  }
  
}
