package com.example.unserhoersaal.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.enums.LogRegErrorMessEnum;
import com.example.unserhoersaal.enums.EmailVerificationEnum;
import com.example.unserhoersaal.enums.ResetPasswordEnum;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.utils.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** Class Description. */
public class LoginViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private MutableLiveData<FirebaseUser> userLiveData;

  public MutableLiveData<LogRegErrorMessEnum> errorMessageLogEmail;
  public MutableLiveData<LogRegErrorMessEnum> errorMessageLogPassword;
  public MutableLiveData<LogRegErrorMessEnum> errorMessageLogProcess;
  public MutableLiveData<EmailVerificationEnum> verificationStatus;
  public MutableLiveData<ResetPasswordEnum> resetPasswordStatus;
  public MutableLiveData<ResetPasswordEnum> emailExistency;

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
    this.errorMessageLogEmail = new MutableLiveData<>();
    this.errorMessageLogPassword = new MutableLiveData<>();
    this.errorMessageLogProcess = new MutableLiveData<>();
    this.verificationStatus = new MutableLiveData<>();
    this.resetPasswordStatus = new MutableLiveData<>();
    this.emailExistency = new MutableLiveData<>();
    this.errorMessageLogEmail.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageLogPassword.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageLogProcess.setValue(LogRegErrorMessEnum.NONE);
    this.verificationStatus.setValue(EmailVerificationEnum.NONE);
    this.emailExistency = this.authAppRepository.getExistency();

    //Databinding containers
    this.dataBindingUserInput = new MutableLiveData<>();
    this.dataBindingPasswordInput = new MutableLiveData<>();
    this.resetDatabindingData();
  }

  /** Give Back the current user. */
  public LiveData<FirebaseUser> getUserLiveData() {
    //remove user data from mutablelivedata after successful firebase interaction
    //TODO: is this best practice?
    this.resetDatabindingData();

    return this.userLiveData;
  }

  private void resetDatabindingData() {
    this.dataBindingUserInput.setValue(new UserModel());
    this.dataBindingPasswordInput.setValue("");
  }

  public void resetErrorMessageLiveData() {
    this.errorMessageLogEmail.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageLogPassword.setValue(LogRegErrorMessEnum.NONE);
    this.errorMessageLogProcess.setValue(LogRegErrorMessEnum.NONE);
  }

  /** login process
   *
   * check email and password input
   * show error message if a input is empty or the pattern is wrong
   * if not -> log in -> if login process fails -> show error message
   *
   */
  public void login() {
    if (this.dataBindingUserInput.getValue() == null) {
      return;
    }

    String email = this.dataBindingUserInput.getValue().getEmail();
    String password = this.dataBindingPasswordInput.getValue();

  /** Check if email input is empty or has wrong pattern.*/
    if (Validation.emptyEmail(email)) {
      this.errorMessageLogEmail.setValue(LogRegErrorMessEnum.EMAIL_EMPTY);
    } else if (!Validation.emailHasPattern(email)) {
      this.errorMessageLogEmail.setValue(LogRegErrorMessEnum.EMAIL_WRONG_PATTERN);
    } else {
      this.errorMessageLogEmail.setValue(LogRegErrorMessEnum.NONE);
    }
  /** Check if password is empty or has wrong pattern.*/
    if (Validation.emptyPassword(password)) {
      this.errorMessageLogPassword.setValue(LogRegErrorMessEnum.PASSWORD_EMPTY);
    } else if (!Validation.passwordHasPattern(password)) {
      this.errorMessageLogPassword.setValue(LogRegErrorMessEnum.PASSWORD_WRONG_PATTERN);
    } else {
      this.errorMessageLogPassword.setValue(LogRegErrorMessEnum.NONE);
    }
  /** Log in or throw error message if login process fails.*/
    if (!Validation.emptyEmail(email) && Validation.emailHasPattern(email) &&
            !Validation.emptyPassword(password) && Validation.passwordHasPattern(password)) {
      this.authAppRepository.login(email, password, errorMessageLogProcess, verificationStatus);
    }
  }

  /** Send reset password email.*/
  public void sendPasswordResetMail() {
    String email = this.dataBindingUserInput.getValue().getEmail();
    this.authAppRepository.sendPasswordResetMail(email);

  }

  public void checkEmailExists() {
    String email = this.dataBindingUserInput.getValue().getEmail();
    if (email == null) {
      return;
    }
    if (Validation.emailHasPattern(email)){
      this.authAppRepository.emailExist(email);
    } else {
      this.emailExistency.setValue(ResetPasswordEnum.ERROR);

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

  public LiveData<ResetPasswordEnum> getEmailExistency() {
    return emailExistency;
  }

  public void resetPasswordReseter() {
    this.emailExistency.setValue(ResetPasswordEnum.DEFAULT);
    this.dataBindingUserInput.setValue(new UserModel());
  }

}
