package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.PasswordModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.utils.PreventDoubleClick;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseUser;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** checks input for registration. */
public class RegistrationViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private StateLiveData<FirebaseUser> userLiveData;
  public StateLiveData<UserModel> userInputState;
  public StateLiveData<PasswordModel> passwordInputState;

  /**
   * Initialize the RegisterViewModel.
   */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserStateLiveData();

    this.userInputState = new StateLiveData<>();
    this.passwordInputState = new StateLiveData<>();
    this.setDefaultInputState();
  }

  /** Give back all user data. */
  public StateLiveData<FirebaseUser> getUserStateLiveData() {
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

  public void setDefaultInputState() {
    this.userInputState.postCreate(new UserModel());
    this.passwordInputState.postCreate(new PasswordModel());
  }

  public void setLiveDataComplete() {
    this.userLiveData.postComplete();
  }

  /** checks registration parameter before using firebase registration API. */
  public void register() {
    if(PreventDoubleClick.checkIfDoubleClick()) {
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

    String userName = userModel.getDisplayName();
    String email = userModel.getEmail();
    String password = passwordModel.getCurrentPassword();

    if (this.registerCheckInput(userName, email, password)) {
      this.userInputState.postComplete();
      this.passwordInputState.postComplete();
      this.authAppRepository.register(userName, email, password);
    }
  }

  private boolean registerCheckInput(String userName, String email, String password) {
    if (Validation.emptyString(userName)) {
      this.userInputState.postError(new Error(Config.AUTH_USERNAME_EMPTY), ErrorTag.USERNAME);
      return false;
    } else if (!Validation.stringHasPattern(userName, Config.REGEX_PATTERN_USERNAME)) {
      this.userInputState.postError(
              new Error(Config.AUTH_USERNAME_WRONG_PATTERN), ErrorTag.USERNAME);
      return false;
    } else if (Validation.emptyString(email)) {
      this.userInputState.postError(new Error(Config.AUTH_EMAIL_EMPTY), ErrorTag.EMAIL);
      return false;
    } else if (!Validation.emailHasPattern(email)) {
      this.userInputState.postError(
              new Error(Config.AUTH_EMAIL_WRONG_PATTERN_REGISTRATION), ErrorTag.EMAIL);
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

}
