package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.enums.EmailVerificationEnum;
import com.example.unserhoersaal.enums.LogRegErrorMessEnum;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseUser;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** Class Description. */
public class RegistrationViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private MutableLiveData<FirebaseUser> userLiveData;

  public MutableLiveData<LogRegErrorMessEnum> errorMessageRegUserName;
  public MutableLiveData<LogRegErrorMessEnum> errorMessageRegEmail;
  public MutableLiveData<LogRegErrorMessEnum> errorMessageRegPassword;
  public MutableLiveData<LogRegErrorMessEnum> errorMessageRegProcess;
  public MutableLiveData<EmailVerificationEnum> verificationStatus;
  //public MutableLiveData<Boolean> emailVerificationRequest;

  public MutableLiveData<UserModel> dataBindingUserInput;
  public MutableLiveData<String> dataBindingPasswordInput;


  /**
   * Initialize the LoginRegisterViewModel.
   */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserLiveData();
    this.errorMessageRegUserName = new MutableLiveData<>();
    this.errorMessageRegEmail = new MutableLiveData<>();
    this.errorMessageRegPassword = new MutableLiveData<>();
    this.errorMessageRegProcess = new MutableLiveData<>();
    this.verificationStatus = new MutableLiveData<>();
    //this.emailVerificationRequest = new MutableLiveData<>();
    this.errorMessageRegUserName.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageRegEmail.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageRegPassword.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageRegProcess.setValue(LogRegErrorMessEnum.NONE);
    //this.emailVerificationRequest.setValue(false);
    this.verificationStatus.setValue(EmailVerificationEnum.NONE);

    this.dataBindingUserInput = new MutableLiveData<>();
    this.dataBindingPasswordInput = new MutableLiveData<>();
    this.resetDatabindingData();

  }

  /** Give back all user data. */
  public LiveData<FirebaseUser> getUserLiveData() {
    //remove user data from mutablelivedata after successful firebase interaction
    //TODO: is this best practice?
    this.resetDatabindingData();

    return this.userLiveData;
  }


  public void resetDatabindingData() {
    this.dataBindingUserInput.setValue(new UserModel());
    this.dataBindingPasswordInput.setValue("");
  }

  /** JavaDoc for this method. */
  public void resetErrorMessageLiveData() {
    this.errorMessageRegUserName.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageRegEmail.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageRegPassword.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageRegProcess.setValue(LogRegErrorMessEnum.NONE);
  }

  /* registration process
   *
   * check username, email and password input
   * show error message if a input is empty or the pattern is wrong
   * if not -> register new user -> if registration fails-> show error message
   *
   */

  /** JavaDoc for this method. */
  public void register() {
    if (this.dataBindingPasswordInput.getValue() == null) {
      return;
    }
    /* User input*/
    String userName = this.dataBindingUserInput.getValue().getDisplayName();
    String email = this.dataBindingUserInput.getValue().getEmail();
    String password = this.dataBindingPasswordInput.getValue();
    /* Check if username input is empty or has wrong pattern.*/
    if (Validation.emptyUserName(userName)) {
      this.errorMessageRegUserName.setValue(LogRegErrorMessEnum.USERNAME_EMPTY);
    } else if (!Validation.userNameHasPattern(userName)) {
      this.errorMessageRegUserName.setValue(LogRegErrorMessEnum.USERNAME_WRONG_PATTERN);
    } else {
      this.errorMessageRegUserName.setValue(LogRegErrorMessEnum.NONE);
    }
    /* Check if email input is empty or has wrong pattern.*/
    if (Validation.emptyEmail(email)) {
      this.errorMessageRegEmail.setValue(LogRegErrorMessEnum.EMAIL_EMPTY);
    } else if (!Validation.emailHasPattern(email)) {
      this.errorMessageRegEmail.setValue(LogRegErrorMessEnum.EMAIL_WRONG_PATTERN);
    } else {
      this.errorMessageRegEmail.setValue(LogRegErrorMessEnum.NONE);
    }
    /* Check if password input is empty or has wrong pattern.*/
    if (Validation.emptyPassword(password)) {
      this.errorMessageRegPassword.setValue(LogRegErrorMessEnum.PASSWORD_EMPTY);
    } else if (!Validation.passwordHasPattern(password)) {
      this.errorMessageRegPassword.setValue(LogRegErrorMessEnum.PASSWORD_WRONG_PATTERN);
    } else {
      this.errorMessageRegPassword.setValue(LogRegErrorMessEnum.NONE);
    }
    /* Register new user or throw error message if it fails.*/
    if (!Validation.emptyUserName(userName) && Validation.userNameHasPattern(userName)
            && !Validation.emptyEmail(email) && Validation.emailHasPattern(email)
            && !Validation.emptyPassword(password) && Validation.passwordHasPattern(password)) {
      this.authAppRepository.register(userName, email, password, errorMessageRegProcess,
              verificationStatus);
    }
  }

  /** Set email verification status on completed.*/
  public void setVerificationStatusOnNull() {
    verificationStatus.setValue(EmailVerificationEnum.NONE);
  }

  /** Resend email verification email.*/
  public void resendEmailVerification() {
    authAppRepository.resendEmailVerification();
  }

}
