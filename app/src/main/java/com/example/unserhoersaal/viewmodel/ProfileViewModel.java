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
  public MutableLiveData<UserModel> dataBindingProfileInput;
  public MutableLiveData<String> dataBindingOldPasswordInput;
  public MutableLiveData<String> dataBindingNewPasswordInput;
  public MutableLiveData<Boolean> profileChanged;

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

    this.dataBindingProfileInput = new MutableLiveData<>(new UserModel());
    this.dataBindingOldPasswordInput = new MutableLiveData<>();
    this.dataBindingNewPasswordInput = new MutableLiveData<>();

    this.profileChanged = this.profileRepository.getProfileChanged();
  }

  /** Give back the user data. */
  public LiveData<FirebaseUser> getUserLiveData() {
    return this.userLiveData;
  }

  public LiveData<UserModel> getProfileLiveData() {
    return this.profileLiveData;
  }

  public LiveData<Boolean> getProfileChanged() {
    return this.profileChanged;
  }

  public void logout() {
    this.authAppRepository.logOut();
  }

  public void deleteAccount() {
    this.authAppRepository.deleteAccount();
  }

  public void changeDisplayName() {
    if (this.dataBindingProfileInput.getValue() == null) return;
    String displayName = this.dataBindingProfileInput.getValue().getDisplayName();
    //TODO: check if displayName matches our policy

    this.profileRepository.changeDisplayName(displayName);
  }

  public void changeInstitution() {
    if (this.dataBindingProfileInput.getValue() == null) return;
    String institution = this.dataBindingProfileInput.getValue().getInstitution();
    //TODO: check if institution matches our policy

    this.profileRepository.changeInstitution(institution);
  }

  public void changePassword() {
    if (this.dataBindingOldPasswordInput.getValue() == null || this.dataBindingNewPasswordInput.getValue() == null) return;
    String oldPassword = this.dataBindingOldPasswordInput.getValue();
    String newPassword = this.dataBindingNewPasswordInput.getValue();
    //TODO: check if they match our policy; comparing password is handled by firebase

    this.authAppRepository.changePassword(oldPassword, newPassword);
  }

  public void resetProfileInput() {
    this.dataBindingProfileInput.setValue(new UserModel());
    this.profileChanged.setValue(Boolean.FALSE);
  }
}
