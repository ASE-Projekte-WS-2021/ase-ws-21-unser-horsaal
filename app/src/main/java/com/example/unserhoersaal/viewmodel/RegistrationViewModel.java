package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.PasswordModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseUser;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** Class Description. */
public class RegistrationViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private StateLiveData<FirebaseUser> firebaseUserRepoState;
  public StateLiveData<UserModel> userInputState;
  public StateLiveData<PasswordModel> passwordInputState;

  /**
   * Initialize the LoginRegisterViewModel.
   */
  public void init() {
    if (this.firebaseUserRepoState != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.firebaseUserRepoState = this.authAppRepository.getFirebaseUserRepoState();

    this.userInputState = new StateLiveData<>();
    this.passwordInputState = new StateLiveData<>();
    this.setDefaultInputState();
  }

  /** Give back all user data. */
  public StateLiveData<FirebaseUser> getUserStateLiveData() {
    this.setDefaultInputState();

    return this.firebaseUserRepoState;
  }

  /** Returns UserInput to the Fragment to observe DataStatus changes. */
  public StateLiveData<UserModel> getUserInputState() {
    return this.userInputState;
  }

  /** Returns PasswordInput to the Fragment to observe DataStatus changes. */
  public StateLiveData<PasswordModel> getPasswordInputState() {
    return this.passwordInputState;
  }

  public void setDefaultInputState() {
    this.userInputState.postCreate(new UserModel());
    this.passwordInputState.postCreate(new PasswordModel());
  }

  /** JavaDoc for this method. */
  public void register() {
    this.firebaseUserRepoState.postLoading();

    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    PasswordModel passwordModel = Validation.checkStateLiveData(this.passwordInputState, TAG);
    if (userModel == null || passwordModel == null) {
      Log.e(TAG, Config.REGISTRATION_USER_MODEL_NULL);
      this.firebaseUserRepoState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    String userName = userModel.getDisplayName();
    String email = userModel.getEmail();
    String password = passwordModel.getCurrentPassword();

    if (this.validateDisplayName(userName)
            && this.validateEmail(email)
            && this.validatePassword(password)) {
      return;
    }

    this.userInputState.postCreate(new UserModel());
    //do not listen for this status because we would get two spinner loops
    this.passwordInputState.postCreate(new PasswordModel());
    this.authAppRepository.register(userName, email, password);
  }

  private boolean validateDisplayName(String displayName) {
    if (Validation.emptyString(displayName)) {
      Log.d(TAG, Config.REGISTRATION_USERNAME_NULL);
      this.firebaseUserRepoState.postError(new Error(Config.AUTH_USERNAME_EMPTY), ErrorTag.USERNAME);
      return true;
    } else if (!Validation.stringHasPattern(displayName, Config.REGEX_PATTERN_USERNAME)) {
      Log.d(TAG, Config.REGISTRATION_USERNAME_WRONG_PATTERN);
      this.firebaseUserRepoState.postError(
              new Error(Config.AUTH_USERNAME_WRONG_PATTERN), ErrorTag.USERNAME);
      return true;
    }
    return false;
  }

  private boolean validateEmail(String email) {
    if (Validation.emptyString(email)) {
      Log.d(TAG, Config.REGISTRATION_EMAIL_NULL);
      this.firebaseUserRepoState.postError(new Error(Config.AUTH_EMAIL_EMPTY), ErrorTag.EMAIL);
      return true;
    } else if (!Validation.emailHasPattern(email)) {
      Log.d(TAG, Config.REGISTRATION_EMAIL_WRONG_PATTERN);
      this.firebaseUserRepoState.postError(
              new Error(Config.AUTH_EMAIL_WRONG_PATTERN_REGISTRATION), ErrorTag.EMAIL);
      return true;
    }
    return false;
  }

  private boolean validatePassword(String password) {
    if (Validation.emptyString(password)) {
      Log.d(TAG, Config.REGISTRATION_PASSWORD_NULL);
      this.firebaseUserRepoState.postError(
              new Error(Config.AUTH_PASSWORD_EMPTY), ErrorTag.CURRENT_PASSWORD);
      return true;
    } else if (!Validation.stringHasPattern(password, Config.REGEX_PATTERN_PASSWORD)) {
      Log.d(TAG, Config.REGISTRATION_PASSWORD_WRONG_PATTERN);
      this.firebaseUserRepoState.postError(
              new Error(Config.AUTH_PASSWORD_WRONG_PATTERN), ErrorTag.CURRENT_PASSWORD);
      return true;
    }
    return false;
  }

}
