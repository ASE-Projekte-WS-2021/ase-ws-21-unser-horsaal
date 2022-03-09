package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
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
    this.profileRepository = ProfileRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserStateLiveData();
    this.profileLiveData = this.profileRepository.getUser();

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


  public void resetProfileInput() {
    this.userInputState.setValue(new StateData<>(new UserModel()));
  }

  /** JavaDoc for this method. */
  public void resetPasswordInput() {
    this.passwordInputState.setValue(new StateData<>(new PasswordModel()));
  }

  public void logout() {
    this.authAppRepository.logOut();
  }

  public void deleteAccount() {
    this.authAppRepository.deleteAccount();
  }

  /** JavaDoc for this method. */
  public void changeDisplayName() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    if (userModel == null) {
      Log.d(TAG, "ProfileViewModel>changeDisplayName userModel is null.");
      return;
    }

    String displayName = userModel.getDisplayName();
    //TODO: check if displayName matches our policy

    this.profileRepository.changeDisplayName(displayName);
  }

  /** JavaDoc for this method. */
  public void changeInstitution() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    if (userModel == null) {
      Log.d(TAG, "ProfileViewModel>changeInstitution userModel is null.");
      return;
    }

    String institution = userModel.getInstitution();
    //TODO: check if institution matches our policy -> pattern

    this.userInputState.postComplete();
    this.profileRepository.changeInstitution(institution);
  }

  /** JavaDoc for this method. */
  public void changePassword() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    PasswordModel passwordModel = Validation.checkStateLiveData(this.passwordInputState, TAG);
    if (userModel == null || passwordModel == null) {
      Log.d(TAG, "ProfileViewModel>changePassword userModel or passwordModel is null.");
      return;
    }

    String oldPassword = passwordModel.getCurrentPassword();
    String newPassword = passwordModel.getNewPassword();

    if (!Validation.passwordHasPattern(newPassword)) {
      Log.d(TAG, "ProfileViewModel>changePassword: password doesn't match pattern");
      this.userInputState.postError(new Error(Config.PASSWORD_RESET_FAILED), ErrorTag.REPO);
    } else {
      this.userInputState.postComplete();
      this.profileRepository.changePassword(oldPassword, newPassword);
    }
  }

}
