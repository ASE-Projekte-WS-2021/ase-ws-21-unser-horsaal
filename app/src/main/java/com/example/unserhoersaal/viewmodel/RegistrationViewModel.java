package com.example.unserhoersaal.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.EmailVerificationEnum;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.enums.LogRegErrorMessEnum;
import com.example.unserhoersaal.model.PasswordModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseUser;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** Class Description. */
public class RegistrationViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private StateLiveData<FirebaseUser> userLiveData;
  public StateLiveData<UserModel> userInputState;
  public StateLiveData<PasswordModel> passwordInputState;


  /**
   * Initialize the LoginRegisterViewModel.
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
    this.userInputState.setValue(new StateData<>(new UserModel()));
    this.passwordInputState.setValue(new StateData<>(new PasswordModel()));
  }

  /** JavaDoc for this method. */
  public void register() {
    UserModel userModel = Validation.checkStateLiveData(this.userInputState, TAG);
    PasswordModel passwordModel = Validation.checkStateLiveData(this.passwordInputState, TAG);
    if (userModel == null || passwordModel == null) {
      Log.d(TAG, "RegisterViewModel>register userModel or passwordModel is null.");
      return;
    }

    /* User input*/
    String userName = userModel.getDisplayName();
    String email = userModel.getEmail();
    String password = passwordModel.getCurrentPassword();

    /* Check if username input is empty or has wrong pattern.*/
    if (Validation.emptyUserName(userName)) {
      this.userInputState.postError(new Error(Config.REG_USERNAME_EMPTY), ErrorTag.USERNAME);
      return;
    } else if (!Validation.userNameHasPattern(userName)) {
      this.userInputState.postError(new Error(Config.REG_USERNAME_WRONG_PATTERN), ErrorTag.USERNAME);
      return;
    }
    /* Check if email input is empty or has wrong pattern.*/
    if (Validation.emptyEmail(email)) {
      this.userInputState.postError(new Error(Config.REG_EMAIL_EMPTY), ErrorTag.EMAIL);
      return;
    } else if (!Validation.emailHasPattern(email)) {
      this.userInputState.postError(new Error(Config.REG_EMAIL_PATTERN_WRONG), ErrorTag.EMAIL);
      return;
    }
    /* Check if password input is empty or has wrong pattern.*/
    if (Validation.emptyPassword(password)) {
      this.passwordInputState.postError(new Error(Config.REG_PASSWORD_EMPTY), ErrorTag.PASSWORD);
      return;
    } else if (!Validation.passwordHasPattern(password)) {
      this.passwordInputState.postError(new Error(Config.REG_PASSWORD_PATTERN_WRONG), ErrorTag.PASSWORD);
      return;
    }

    this.userInputState.postComplete();
    this.authAppRepository.register(userName, email, password);

  }

}
