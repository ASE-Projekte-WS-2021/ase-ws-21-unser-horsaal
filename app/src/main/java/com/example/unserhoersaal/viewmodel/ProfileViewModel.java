package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.PasswordModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.repository.ProfileRepository;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseUser;

/** Class Description. */
public class ProfileViewModel extends ViewModel {

  private static final String TAG = "ProfileViewModel";

  private AuthAppRepository authAppRepository;
  private ProfileRepository profileRepository;
  private StateLiveData<FirebaseUser> userLiveData;
  private StateLiveData<UserModel> profileLiveData;
  public StateLiveData<UserModel> userInputState;
  public StateLiveData<PasswordModel> passwordInputState;
  public StateLiveData<Boolean> profileChanged;

  /** Initialize the LoginRegisterViewModel. */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserStateLiveData();

    //this live data loads profile data into the text views
    this.profileRepository = ProfileRepository.getInstance();
    this.profileLiveData = this.profileRepository.getUser();

    //this live data retrieves input from the user
    this.userInputState.setValue(new StateData<>(new UserModel()));
    this.passwordInputState.setValue(new StateData<>(new PasswordModel()));
    this.profileChanged = this.profileRepository.getProfileChanged();
  }

  /** Give back the user data. */
  public StateLiveData<FirebaseUser> getUserLiveData() {
    return this.userLiveData;
  }

  public StateLiveData<UserModel> getProfileLiveData() {
    return this.profileLiveData;
  }

  public StateLiveData<Boolean> getProfileChanged() {
    return this.profileChanged;
  }

  public void logout() {
    this.authAppRepository.logOut();
  }

  public void deleteAccount() {
    this.authAppRepository.deleteAccount();
  }

  /** JavaDoc for this method. */
  public void changeDisplayName() {
    if (this.userInputState.getValue() == null) {
      return;
    }
    String displayName = this.userInputState.getValue().getData().getDisplayName();
    //TODO: check if displayName matches our policy

    this.profileRepository.changeDisplayName(displayName);
  }

  /** JavaDoc for this method. */
  public void changeInstitution() {
    if (this.userInputState.getValue() == null) {
      return;
    }
    String institution = this.userInputState.getValue().getData().getInstitution();
    //TODO: check if institution matches our policy

    this.profileRepository.changeInstitution(institution);
  }

  /** JavaDoc for this method. */
  public void changePassword() {
    if (this.dataBindingOldPasswordInput.getValue() == null
            || this.dataBindingNewPasswordInput.getValue() == null) {
      return;
    }
    String oldPassword = this.dataBindingOldPasswordInput.getValue();
    String newPassword = this.dataBindingNewPasswordInput.getValue();
    //TODO: check if they match our policy; Feedback
    if (Validation.passwordHasPattern(oldPassword) && Validation.passwordHasPattern(newPassword)) {
      this.profileRepository.changePassword(oldPassword, newPassword);
    } else {
      //TODO: check if they match our policy; Feedback
      Log.d(TAG, "changePassword: " + "password doesn't match pattern");
    }
  }

  public void resetProfileInput() {
    this.userInputState.setValue(new StateData<>(new UserModel()));
  }

  /** JavaDoc for this method. */
  public void resetPasswordInput() {
    this.passwordInputState.setValue(new StateData<>(new PasswordModel()));
  }

}
