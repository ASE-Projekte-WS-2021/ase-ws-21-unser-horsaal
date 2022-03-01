package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.enums.LogRegErrorMessEnum;
import com.example.unserhoersaal.enums.LogRegToastEnum;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseUser;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** Class Description. */
public class LoginViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private MutableLiveData<FirebaseUser> userLiveData;
  public MutableLiveData<UserModel> user;
  public MutableLiveData<String> password;
  public MutableLiveData<LogRegErrorMessEnum> errorMessageLogEmail;
  public MutableLiveData<LogRegErrorMessEnum> errorMessageLogPassword;
  public MutableLiveData<LogRegErrorMessEnum> errorMessageLogProcess;
  public MutableLiveData<LogRegToastEnum> logToastMessages;

  /**
   * Initialize the LoginRegisterViewModel.
   */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserLiveData();
    this.errorMessageLogEmail = new MutableLiveData<>();
    this.errorMessageLogPassword = new MutableLiveData<>();
    this.errorMessageLogProcess = new MutableLiveData<>();
    this.logToastMessages = new MutableLiveData<>();
    this.errorMessageLogEmail.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageLogPassword.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageLogProcess.setValue(LogRegErrorMessEnum.NONE);
    this.logToastMessages.setValue(LogRegToastEnum.NONE);

    //Databinding containers
    this.user = new MutableLiveData<>();
    this.password = new MutableLiveData<>();
    this.resetDatabindingData();
  }

  public LiveData<FirebaseUser> getUserLiveData() {
    //remove user data from mutablelivedata after successful firebase interaction
    //TODO: is this best practice?
    this.resetDatabindingData();

    return this.userLiveData;
  }

  private void resetDatabindingData() {
    this.user.setValue(new UserModel());
    this.password.setValue("");
  }

  /** login process
   *
   * check email and password input
   * show error message if a input is empty or the pattern is wrong
   * if not -> log in -> if login process fails -> show error message
   *
   */
  public void login() {
    if (this.user.getValue() == null) return;
  // get email and password input
    String email = this.user.getValue().getEmail();
    String password = this.password.getValue();
  //check if email input is empty or has wrong pattern
    if (Validation.emptyEmail(email)) {
      this.errorMessageLogEmail.setValue(LogRegErrorMessEnum.EMAIL_EMPTY);
    } else if (!Validation.emailHasPattern(email)) {
      this.errorMessageLogEmail.setValue(LogRegErrorMessEnum.EMAIL_WRONG_PATTERN);
    } else {
      this.errorMessageLogEmail.setValue(LogRegErrorMessEnum.NONE);
    }
  // check if password is empty or has wrong pattern
    if (Validation.emptyPassword(password)) {
      this.errorMessageLogPassword.setValue(LogRegErrorMessEnum.PASSWORD_EMPTY);
    } else if (!Validation.passwordHasPattern(password)) {
      this.errorMessageLogPassword.setValue(LogRegErrorMessEnum.PASSWORD_WRONG_PATTERN);
    } else {
      this.errorMessageLogPassword.setValue(LogRegErrorMessEnum.NONE);
    }
  // log in or throw error message if login process fails
    if (!Validation.emptyEmail(email) && Validation.emailHasPattern(email) &&
            !Validation.emptyPassword(password) && Validation.passwordHasPattern(password)) {
      this.authAppRepository.login(email, password, errorMessageLogProcess, logToastMessages);
    }
  }
}
