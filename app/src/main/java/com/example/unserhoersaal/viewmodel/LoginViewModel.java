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
public class LoginViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private StateLiveData<FirebaseUser> userLiveData;
  public StateLiveData<UserModel> userInputState;
  public StateLiveData<PasswordModel> passwordInputState;
  public StateLiveData<Boolean> emailSentLiveData;

  /**
   * Initialize the LoginRegisterViewModel.
   */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserStateLiveData();
    this.emailSentLiveData = this.authAppRepository.getEmailSentLiveData();
    this.userInputState = new StateLiveData<>();
    this.passwordInputState = new StateLiveData<>();
    this.setDefaultInputState();
  }

  /** Give Back the current user. */
  public StateLiveData<FirebaseUser> getUserLiveData() {
    this.setDefaultInputState();
    return this.userLiveData;
  }

  /** Returns UserInput to the Fragment to observe DataStatus changes. */
  public StateLiveData<UserModel> getUserInputState() {
    return this.userInputState;
  }

  /** Returns PasswordInput to the Fragment to observe DataStatus changes. */
  public StateLiveData<PasswordModel> getPasswordInputState() {
    return this.passwordInputState;
  }

  public StateLiveData<Boolean> getEmailSentLiveData() {
    return this.emailSentLiveData;
  }

  /** Sets the values in StateLiveData to their default values. These StateLiveData are connected
   * to multiple Databinding Fragments. (Registration, ResetPassword, Login)
   * Used when initialising this Fragment and when leaving the Fragment. */
  public void setDefaultInputState() {
    this.userInputState.postCreate(new UserModel());
    this.passwordInputState.postCreate(new PasswordModel());
  }

  /** JavaDoc for this method. */
  public void login() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    PasswordModel passwordModel = Validation.checkStateLiveData(this.passwordInputState, TAG);
    if (userModel == null || passwordModel == null) {
      Log.e(TAG, "userModel or passwordModel is null.");
      this.userInputState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    String email = userModel.getEmail();
    String password = passwordModel.getCurrentPassword();

    if (Validation.emptyString(email)) {
      Log.d(TAG, "email is null.");
      this.userInputState.postError(new Error(Config.AUTH_EMAIL_EMPTY), ErrorTag.EMAIL);
      return;
    } else if (!Validation.emailHasPattern(email)) {
      Log.d(TAG, "email has wrong pattern.");
      this.userInputState.postError(
              new Error(Config.AUTH_EMAIL_WRONG_PATTERN_LOGIN), ErrorTag.EMAIL);
      return;
    }
    if (Validation.emptyString(password)) {
      Log.d(TAG, "password is null.");
      this.passwordInputState.postError(
              new Error(Config.AUTH_PASSWORD_EMPTY), ErrorTag.CURRENT_PASSWORD);
      return;
    } else if (!Validation.stringHasPattern(password, Config.REGEX_PATTERN_PASSWORD)) {
      Log.d(TAG, "password has wrong pattern.");
      this.passwordInputState.postError(
              new Error(Config.AUTH_PASSWORD_WRONG_PATTERN), ErrorTag.CURRENT_PASSWORD);
      return;
    }

    this.setDefaultInputState();
    this.authAppRepository.login(email, password);
  }

  /** Send reset password email.*/
  public void sendPasswordResetMail() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    if (userModel == null) {
      Log.e(TAG, "LoginViewModel>sendPasswordResetMail userModel is null.");
      this.userInputState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    String email = userModel.getEmail();

    if (Validation.emptyString(email)) {
      Log.d(TAG, "email is null.");
      this.userInputState.postError(new Error(Config.AUTH_EMAIL_EMPTY), ErrorTag.EMAIL);
    } else if (!Validation.emailHasPattern(email)) {
      Log.d(TAG, "email has wrong pattern.");
      this.userInputState.postError(
              new Error(Config.AUTH_EMAIL_WRONG_PATTERN_LOGIN), ErrorTag.CURRENT_PASSWORD);
    } else {

      this.setDefaultInputState();
      this.authAppRepository.sendPasswordResetMail(email);
    }
  }

  /** Resend email verification email. Requires a logged in user! Cant send an email without
   * the user being logged in! */
  public void resendVerificationEmail() {
    this.authAppRepository.resendVerificationEmail();
  }

  /** Changes the password of the user. */
  public void resetPassword() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    if (userModel == null) {
      Log.e(TAG, "userModel is null.");
      this.userInputState.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    String email = userModel.getEmail();

    if (Validation.emptyString(email)) {
      Log.d(TAG, "email is null.");
      this.userInputState.postError(new Error(Config.AUTH_EMAIL_EMPTY), ErrorTag.EMAIL);
    } else if (!Validation.emailHasPattern(email)) {
      Log.d(TAG, "email has wrong pattern.");
      this.userInputState.postError(
              new Error(Config.AUTH_EMAIL_WRONG_PATTERN_LOGIN), ErrorTag.EMAIL);
    } else {

      this.setDefaultInputState();
      this.authAppRepository.resetPassword(email);
    }
  }

  public void reloadFirebaseUser() {
    this.authAppRepository.reloadFirebaseUser();
  }

  public void logout() {
    this.authAppRepository.logOut();
  }

}
