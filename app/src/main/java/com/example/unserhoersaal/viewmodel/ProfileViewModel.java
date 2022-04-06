package com.example.unserhoersaal.viewmodel;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.PasswordModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.repository.ProfileRepository;
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
  public StateLiveData<UserModel> userInputState = new StateLiveData<>();
  public StateLiveData<PasswordModel> passwordInputState = new StateLiveData<>();
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

    this.userInputState.postCreate(new UserModel());
    this.passwordInputState.postCreate(new PasswordModel());
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

  /** reset statelivedata for user input. */
  public void resetProfileInput() {
    this.userInputState.postCreate(new UserModel());
  }

  /** reset statelivedata for password input. */
  public void resetPasswordInput() {
    this.passwordInputState.postCreate(new PasswordModel());
  }

  public void logout() {
    this.authAppRepository.logOut();
  }

  public void deleteAccount() {
    this.authAppRepository.deleteAccount();
  }

  /** checks user input before editing name with firebase API. */
  public void changeDisplayName() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    if (userModel == null) {
      Log.e(TAG, "userModel is null.");
      return;
    }

    String displayName = userModel.getDisplayName();

    if (Validation.emptyString(displayName)) {
      this.profileChanged.postError(new Error(Config.AUTH_USERNAME_EMPTY), ErrorTag.USERNAME);
    } else if (!Validation.stringHasPattern(displayName, Config.REGEX_PATTERN_USERNAME)) {
      this.profileChanged.postError(
              new Error(Config.AUTH_USERNAME_WRONG_PATTERN), ErrorTag.USERNAME);
    } else {
      this.profileRepository.changeDisplayName(displayName);
    }
  }

  /** checks user input before editing name with firebase API. */
  public void changeInstitution() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    if (userModel == null) {
      Log.e(TAG, "userModel is null.");
      return;
    }

    String institution = userModel.getInstitution();

    if (Validation.emptyString(institution)) {
      this.profileChanged.postError(new Error(Config.AUTH_INSTITUTION_EMPTY), ErrorTag.INSTITUTION);
    } else if (!Validation.stringHasPattern(institution, Config.REGEX_PATTERN_INSTITUTION)) {
      this.profileChanged.postError(
              new Error(Config.AUTH_INSTITUTION_WRONG_PATTERN), ErrorTag.INSTITUTION);
    } else {
      this.profileRepository.changeInstitution(institution);
    }
  }

  /** checks user input before editing name with firebase API. */
  public void changePassword() {
    PasswordModel passwordModel = Validation.checkStateLiveData(this.passwordInputState, TAG);
    if (passwordModel == null) {
      Log.e(TAG, "passwordModel is null.");
      return;
    }

    String oldPassword = passwordModel.getCurrentPassword();
    String newPassword = passwordModel.getNewPassword();

    if (Validation.emptyString(oldPassword)) {
      this.profileChanged.postError(
              new Error(Config.AUTH_PASSWORD_EMPTY), ErrorTag.CURRENT_PASSWORD);
    } else if (!Validation.stringHasPattern(oldPassword, Config.REGEX_PATTERN_PASSWORD)) {
      this.profileChanged.postError(
              new Error(Config.AUTH_PASSWORD_WRONG_PATTERN), ErrorTag.CURRENT_PASSWORD);
    } else if (Validation.emptyString(newPassword)) {
      this.profileChanged.postError(new Error(Config.AUTH_PASSWORD_EMPTY), ErrorTag.NEW_PASSWORD);
    } else if (!Validation.stringHasPattern(newPassword, Config.REGEX_PATTERN_PASSWORD)) {
      this.profileChanged.postError(
              new Error(Config.AUTH_PASSWORD_WRONG_PATTERN), ErrorTag.NEW_PASSWORD);
    } else {
      this.profileRepository.changePassword(oldPassword, newPassword);
    }
  }

  /** transfer uri to repo to change profile picture. */
  public void uploadImageToFireStore(Uri uri) {
    this.profileRepository.uploadImageToFirebase(uri);
  }

  public void setUserId() {
    this.profileRepository.setUserId();
  }

}
