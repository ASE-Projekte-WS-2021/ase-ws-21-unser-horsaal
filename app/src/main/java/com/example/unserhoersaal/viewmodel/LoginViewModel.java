package com.example.unserhoersaal.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.enums.LoginErrorMessEnum;
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
  public MutableLiveData<LoginErrorMessEnum> errorMessage;


  /**
   * Initialize the LoginRegisterViewModel.
   */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserLiveData();
    this.errorMessage = new MutableLiveData<>();
    this.errorMessage.setValue(LoginErrorMessEnum.NONE);

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
   * if not -> log in -> if login info is wrong -> show error message
   */
  public void login() {
    if (this.user.getValue() == null) return;

    String email = this.user.getValue().getEmail();
    String password = this.password.getValue();

    // login-error-case 1: email and password inputs are empty
    if(Validation.emptyEmail(email) && Validation.emptyPassword(password)) {
      this.errorMessage.setValue(LoginErrorMessEnum.EMAIL_AND_PASSWORD_EMPTY);
    // login-error-case 2: email and password pattern are wrong
    } else if(!Validation.emptyEmail(email) && !Validation.emailHasPattern(email) && !Validation.emptyPassword(password) && !Validation.passwordHasPattern(password)) {
        this.errorMessage.setValue(LoginErrorMessEnum.EMAIL_AND_PASSWORD_PATTERN_WRONG);
    // login-error-case 3: email is empty and password pattern is wrong
    } else if(!Validation.passwordHasPattern(password) && Validation.emptyEmail(email)) {
        this.errorMessage.setValue(LoginErrorMessEnum.EMAIL_EMPTY_AND_PASSWORD_PATTERN_WRONG);
    // login-error-case 4: password is empty and email pattern is wrong
    } else if(!Validation.emptyEmail(email) && !Validation.emailHasPattern(email) && Validation.emptyPassword(password)) {
        this.errorMessage.setValue(LoginErrorMessEnum.EMAIL_PATTERN_WRONG_AND_PASSWORD_EMPTY);
    // login-error-case 5: email is empty
    } else if (Validation.emptyEmail(email)) {
        this.errorMessage.setValue(LoginErrorMessEnum.EMAIL_EMPTY);
    // login-error-case 6: password is empty
    } else if(Validation.emptyPassword(password)) {
        this.errorMessage.setValue(LoginErrorMessEnum.PASSWORD_EMPTY);
    // login-error-case 7: email pattern is wrong
    } else if (!Validation.emailHasPattern(email)) {
        this.errorMessage.setValue(LoginErrorMessEnum.EMAIL_WRONG_PATTERN);
    // login-error-case 8: password pattern is wrong
    } else if(!Validation.passwordHasPattern(password)) {
        this.errorMessage.setValue(LoginErrorMessEnum.PASSWORD_WRONG_PATTERN);
    } else {
    // login-input success: email and password aren´t empty and the pattern is correct
        this.errorMessage.setValue(LoginErrorMessEnum.NONE);
        this.authAppRepository.login(email, password, errorMessage);
    }
}
}
