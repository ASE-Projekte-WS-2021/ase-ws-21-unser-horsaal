package com.example.unserhoersaal.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
      Log.e(TAG, "userModel is null.");
      return;
    }

    String displayName = userModel.getDisplayName();

    if (Validation.emptyString(displayName)) {
      Log.d(TAG, "displayName is null.");
      this.profileChanged.postError(new Error(Config.AUTH_USERNAME_EMPTY), ErrorTag.USERNAME);
      return;
    } else if (!Validation.stringHasPattern(displayName, Config.REGEX_PATTERN_USERNAME)) {
      Log.d(TAG, "displayName has wrong pattern.");
      this.profileChanged.postError(
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
      Log.e(TAG, "userModel is null.");
      return;
    }

    String institution = userModel.getInstitution();

    if (Validation.emptyString(institution)) {
      Log.d(TAG, "institution is null.");
      this.profileChanged.postError(new Error(Config.AUTH_INSTITUTION_EMPTY), ErrorTag.INSTITUTION);
      return;
    } else if (!Validation.stringHasPattern(institution, Config.REGEX_PATTERN_INSTITUTION)) {
      Log.d(TAG, "institution has wrong pattern.");
      this.profileChanged.postError(
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
      Log.e(TAG, "passwordModel is null.");
      return;
    }

    String oldPassword = passwordModel.getCurrentPassword();
    String newPassword = passwordModel.getNewPassword();

    if (Validation.emptyString(oldPassword)) {
      Log.d(TAG, "oldPassword is null.");
      this.profileChanged.postError(
              new Error(Config.AUTH_PASSWORD_EMPTY), ErrorTag.CURRENT_PASSWORD);
      return;
    } else if (!Validation.stringHasPattern(oldPassword, Config.REGEX_PATTERN_PASSWORD)) {
      Log.d(TAG, "oldPassword has wrong pattern.");
      this.profileChanged.postError(
              new Error(Config.AUTH_PASSWORD_WRONG_PATTERN), ErrorTag.CURRENT_PASSWORD);
      return;
    }

    if (Validation.emptyString(newPassword)) {
      Log.d(TAG, "newPassword is null.");
      this.profileChanged.postError(new Error(Config.AUTH_PASSWORD_EMPTY), ErrorTag.NEW_PASSWORD);
      return;
    } else if (!Validation.stringHasPattern(oldPassword, Config.REGEX_PATTERN_PASSWORD)) {
      Log.d(TAG, "newPassword has wrong pattern.");
      this.profileChanged.postError(
              new Error(Config.AUTH_PASSWORD_WRONG_PATTERN), ErrorTag.NEW_PASSWORD);
      return;
    }

    this.passwordInputState.postCreate(new PasswordModel());
    this.profileRepository.changePassword(oldPassword, newPassword);
  }

  /** JavaDoc for this method. */
  public void uploadImageToFireStore(Uri uri) {
    this.profileRepository.uploadImageToFirebase(uri);
  }

}
