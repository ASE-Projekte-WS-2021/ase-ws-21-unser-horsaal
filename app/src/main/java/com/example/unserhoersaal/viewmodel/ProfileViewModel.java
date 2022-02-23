package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.repository.ProfileRepository;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseUser;

/** Class Description. */
public class ProfileViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private ProfileRepository profileRepository;
  private MutableLiveData<FirebaseUser> userLiveData;
  private MutableLiveData<UserModel> profileLiveData;
  public MutableLiveData<String> oldPassword;
  public MutableLiveData<String> newPassword;
  public MutableLiveData<Boolean> changing;

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
    this.oldPassword = new MutableLiveData<>();
    this.newPassword = new MutableLiveData<>();
    this.changing = new MutableLiveData<>();
    this.changing.setValue(true);
  }

  /** Give back the user data. */
  public LiveData<FirebaseUser> getUserLiveData() {
    return this.userLiveData;
  }

  public LiveData<UserModel> getProfileLiveData() {
    return this.profileLiveData;
  }

  public void saveNewProfileData() {
    UserModel userModel = this.profileLiveData.getValue();
    if (userModel == null) return;

    this.profileRepository.changeProfileData(userModel);

    if (Validation.emptyPassword(newPassword.getValue()) || Validation.passwordHasPattern(newPassword.getValue())) {
      String password = Validation.emptyPassword(newPassword.getValue()) ? newPassword.getValue() : null;
      this.profileRepository.changeAuthData(userModel, password);
    }
    else {
      //TODO: propagate error message to databinding
    }

  }

  public void logout() {
    this.authAppRepository.logOut();
  }

  public void deleteAccount() {
    this.authAppRepository.deleteAccount();
  }

  public void toggle() {
    if (this.changing == null || this.changing.getValue() == null) return;
    boolean b = this.changing.getValue();
    this.changing.setValue(!b);
  }
  
}
