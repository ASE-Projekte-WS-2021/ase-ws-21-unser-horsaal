package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.PasswordModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.utils.PreventDoubleClick;
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

  public void setLiveDataComplete() {
    this.userLiveData.postComplete();
    this.emailSentLiveData.postComplete();
  }

  /** checks login parameter before using firebase login API. */
  public void login() {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.userInputState.postLoading();
    this.passwordInputState.postLoading();

    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    PasswordModel passwordModel = Validation.checkStateLiveData(this.passwordInputState, TAG);
    if (userModel == null || passwordModel == null) {
      this.userLiveData.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    String email = userModel.getEmail();
    String password = passwordModel.getCurrentPassword();

    if (this.loginCheckInput(email, password)) {
      this.userInputState.postComplete();
      this.passwordInputState.postComplete();
      this.authAppRepository.login(email, password);
    }
  }

  private boolean loginCheckInput(String email, String password) {
    if (Validation.emptyString(email)) {
      this.userInputState.postError(new Error(Config.AUTH_EMAIL_EMPTY), ErrorTag.EMAIL);
      return false;
    } else if (!Validation.emailHasPattern(email)) {
      this.userInputState.postError(
              new Error(Config.AUTH_EMAIL_WRONG_PATTERN_LOGIN), ErrorTag.EMAIL);
      return false;
    } else if (Validation.emptyString(password)) {
      this.passwordInputState.postError(
              new Error(Config.AUTH_PASSWORD_EMPTY), ErrorTag.CURRENT_PASSWORD);
      return false;
    } else if (!Validation.stringHasPattern(password, Config.REGEX_PATTERN_PASSWORD)) {
      this.passwordInputState.postError(
              new Error(Config.AUTH_PASSWORD_WRONG_PATTERN), ErrorTag.CURRENT_PASSWORD);
      return false;
    }
    return true;
  }

  /** Send reset password email.*/
  public void sendPasswordResetMail() {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.emailSentLiveData.postLoading();

    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    if (userModel == null) {
      this.emailSentLiveData.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    String email = userModel.getEmail();

    if (this.sendPasswordCheckInput(email)) {
      this.authAppRepository.sendPasswordResetMail(email);
    }
  }

  private boolean sendPasswordCheckInput(String email) {
    if (Validation.emptyString(email)) {
      this.emailSentLiveData.postError(new Error(Config.AUTH_EMAIL_EMPTY), ErrorTag.EMAIL);
      return false;
    } else if (!Validation.emailHasPattern(email)) {
      this.emailSentLiveData.postError(
              new Error(Config.AUTH_EMAIL_WRONG_PATTERN_LOGIN), ErrorTag.CURRENT_PASSWORD);
      return false;
    }
    return true;
  }

  /** Resend email verification email. Requires a logged in user! Cant send an email without
   * the user being logged in! */
  public void sendVerificationEmail() {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.emailSentLiveData.postLoading();
    this.authAppRepository.sendVerificationEmail();
  }

  public void isUserEmailVerified() {
    this.authAppRepository.isUserEmailVerified();
  }

  /** logout user. */
  public void logout() {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.authAppRepository.logOut();
  }

}
