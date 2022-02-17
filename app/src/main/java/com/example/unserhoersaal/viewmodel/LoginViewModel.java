package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
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

  /** Initialize the LoginRegisterViewModel. */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserLiveData();

    //Databinding containers
    this.user = new MutableLiveData<>();
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
  }

  public void login() {
    if (this.user.getValue() == null) return;

    String email = this.user.getValue().getEmail();
    String password= this.user.getValue().getPassword();

    if (Validation.emptyEmail(email)) {
      //TODO: change ENUM livedata to email may not be empty -> view get changes by observing
      System.out.println(1);
    }
    else if (Validation.emptyPassword(password)) {
      //TODO: change ENUM livedata to password may not be empty -> view get changes by observing
      System.out.println(2);
    }
    else if (!Validation.emailHasPattern(email)) {
      //TODO: email does not match pattern
      System.out.println(3);
    }
    else if (!Validation.passwordHasPattern(password)) {
      //TODO: password does not match pattern
      System.out.println(4);
    }
    else {
      System.out.println(5);
      this.authAppRepository.login(email, password);
    }
  }

}
