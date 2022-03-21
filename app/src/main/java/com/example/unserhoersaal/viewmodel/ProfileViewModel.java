package com.example.unserhoersaal.viewmodel;

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
  private StateLiveData<FirebaseUser> firebaseUserRepoState;
  private StateLiveData<UserModel> currentUserRepoState;
  public StateLiveData<UserModel> userInputState = new StateLiveData<>();
  public StateLiveData<PasswordModel> passwordInputState = new StateLiveData<>();
  public StateLiveData<Boolean> profileChangedRepoState;

  /** Initialize the LoginRegisterViewModel. */
  public void init() {
    if (this.firebaseUserRepoState != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.profileRepository = ProfileRepository.getInstance();
    this.firebaseUserRepoState = this.authAppRepository.getFirebaseUserRepoState();
    this.currentUserRepoState = this.profileRepository.getCurrentUserRepoState();

    this.userInputState.postCreate(new UserModel());
    this.passwordInputState.postCreate(new PasswordModel());
    this.profileChangedRepoState = this.profileRepository.getProfileChangedRepoState();
  }

  /** Give back the user data. */
  public StateLiveData<FirebaseUser> getFirebaseUserRepoState() {
    return this.firebaseUserRepoState;
  }

  public StateLiveData<UserModel> getCurrentUserRepoState() {
    return this.currentUserRepoState;
  }

  public StateLiveData<Boolean> getProfileChangedRepoState() {
    return this.profileChangedRepoState;
  }

  public void resetProfileInput() {
    this.userInputState.postCreate(new UserModel());
  }

  /** JavaDoc for this method. */
  public void resetPasswordInput() {
    this.passwordInputState.postCreate(new PasswordModel());
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
      Log.e(TAG, Config.PROFILE_NO_USER_MODEL);
      return;
    }

    String displayName = userModel.getDisplayName();

    if (Validation.emptyString(displayName)) {
      Log.d(TAG, Config.PROFILE_NO_NAME);
      this.profileChangedRepoState.postError(new Error(Config.AUTH_USERNAME_EMPTY), ErrorTag.USERNAME);
      return;
    } else if (!Validation.stringHasPattern(displayName, Config.REGEX_PATTERN_USERNAME)) {
      Log.d(TAG, Config.PROFILE_WRONG_NAME_PATTERN);
      this.profileChangedRepoState.postError(
              new Error(Config.AUTH_USERNAME_WRONG_PATTERN), ErrorTag.USERNAME);
      return;
    }

    this.userInputState.postCreate(new UserModel());
    this.profileRepository.changeDisplayName(displayName);
  }

  /** JavaDoc for this method. */
  public void changeInstitution() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    if (userModel == null) {
      Log.e(TAG, Config.PROFILE_NO_USER_MODEL);
      return;
    }

    String institution = userModel.getInstitution();

    if (Validation.emptyString(institution)) {
      Log.d(TAG, Config.PROFILE_NO_INSTITUTION);
      this.profileChangedRepoState.postError(new Error(Config.AUTH_INSTITUTION_EMPTY), ErrorTag.INSTITUTION);
      return;
    } else if (!Validation.stringHasPattern(institution, Config.REGEX_PATTERN_INSTITUTION)) {
      Log.d(TAG, Config.PROFILE_WRONG_INSTITUTION_PATTERN);
      this.profileChangedRepoState.postError(
              new Error(Config.AUTH_INSTITUTION_WRONG_PATTERN), ErrorTag.INSTITUTION);
      return;
    }

    this.userInputState.postCreate(new UserModel());
    this.profileRepository.changeInstitution(institution);
  }

  /** JavaDoc for this method. */
  public void changePassword() {
    PasswordModel passwordModel = Validation.checkStateLiveData(this.passwordInputState, TAG);
    if (passwordModel == null) {
      Log.e(TAG, Config.PROFILE_PASSWORD_MODEL_NULL);
      return;
    }

    String oldPassword = passwordModel.getCurrentPassword();
    String newPassword = passwordModel.getNewPassword();

    if (Validation.emptyString(oldPassword)) {
      Log.d(TAG, Config.PROFILE_NO_OLD_PASSWORD);
      this.profileChangedRepoState.postError(
              new Error(Config.AUTH_PASSWORD_EMPTY), ErrorTag.CURRENT_PASSWORD);
      return;
    } else if (!Validation.stringHasPattern(oldPassword, Config.REGEX_PATTERN_PASSWORD)) {
      Log.d(TAG, Config.PROFILE_WRONG_OLD_PASSWORD_PATTERN);
      this.profileChangedRepoState.postError(
              new Error(Config.AUTH_PASSWORD_WRONG_PATTERN), ErrorTag.CURRENT_PASSWORD);
      return;
    }

    if (Validation.emptyString(newPassword)) {
      Log.d(TAG, Config.PROFILE_NO_NEW_PASSWORD);
      this.profileChangedRepoState.postError(new Error(Config.AUTH_PASSWORD_EMPTY), ErrorTag.NEW_PASSWORD);
      return;
    } else if (!Validation.stringHasPattern(oldPassword, Config.REGEX_PATTERN_PASSWORD)) {
      Log.d(TAG, Config.PROFILE_WRONG_NEW_PASSWORD_PATTERN);
      this.profileChangedRepoState.postError(
              new Error(Config.AUTH_PASSWORD_WRONG_PATTERN), ErrorTag.NEW_PASSWORD);
      return;
    }

    this.passwordInputState.postCreate(new PasswordModel());
    this.profileRepository.changePassword(oldPassword, newPassword);
  }

}
