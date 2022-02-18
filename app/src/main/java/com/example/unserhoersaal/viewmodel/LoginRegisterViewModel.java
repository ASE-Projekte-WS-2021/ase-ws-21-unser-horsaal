package com.example.unserhoersaal.viewmodel;

import android.util.Patterns;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.enums.LoginErrorMessEnum;
import com.example.unserhoersaal.model.LoginUser;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** Class Description. */
public class LoginRegisterViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private MutableLiveData<FirebaseUser> userLiveData;
  public MutableLiveData<LoginUser> loginUser;
  //neu
  public MutableLiveData<LoginErrorMessEnum> errorMessage;

  /** Initialize the LoginRegisterViewModel. */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserLiveData();
    this.loginUser = new MutableLiveData<>();
    this.loginUser.setValue(new LoginUser());
  }

  public LiveData<FirebaseUser> getUserLiveData() {
    //remove login data from mutablelivedata after successful login
    this.loginUser.setValue(new LoginUser());

    return this.userLiveData;
  }

  public void login() {
    if (loginUser.getValue() == null) return;

    String email = loginUser.getValue().getEmail();
    String password= loginUser.getValue().getPassword();

    if (email == null || email.equals("")) {
      //TODO: change ENUM livedata to email may not be empty -> view get changes by observing
    }
    else if (password == null || password.equals("")) {
      //TODO: change ENUM livedata to password may not be empty -> view get changes by observing
    }
    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      //TODO: email does not match pattern
    }
    else {
      this.authAppRepository.login(email, password);
    }
  }

  public void register() {
    if (loginUser.getValue() == null) return;

    String email = loginUser.getValue().getEmail();
    String password= loginUser.getValue().getPassword();

    this.authAppRepository.register(email, password);
  }

  public void logOut() {
    this.authAppRepository.logOut();
  }

}
