package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.enums.LoginErrorMessEnum;
import com.example.unserhoersaal.enums.RegistrationErrorMessEnum;
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
  public MutableLiveData<UserModel> user;
  public MutableLiveData<String> password;
  public MutableLiveData<RegistrationErrorMessEnum> errorMessageRegistration;

  /** Initialize the LoginRegisterViewModel. */
  public void init() {
    if (this.userLiveData != null) {
      return;
    }
    this.authAppRepository = AuthAppRepository.getInstance();
    this.userLiveData = this.authAppRepository.getUserLiveData();
    this.errorMessageRegistration = new MutableLiveData<>();
    this.errorMessageRegistration.setValue(RegistrationErrorMessEnum.NONE);

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
  }

  public void register() {
    if (this.user.getValue() == null) return;

    String userName = this.user.getValue().getDisplayName();
    String email = this.user.getValue().getEmail();
    String password= this.password.getValue();

    if (Validation.emptyEmail(email)) {
      //TODO: change ENUM livedata to email may not be empty -> view get changes by observing
    }
    else if (Validation.emptyUserName(userName)) {
      //TODO: change ENUM livedata to userName may not be empty -> view get changes by observing
    }
    else if (Validation.emptyPassword(password)) {
      //TODO: change ENUM livedata to password may not be empty -> view get changes by observing
    }
    else if (!Validation.emailHasPattern(email)) {
      //TODO: email does not match pattern
    }
    else if (!Validation.userNameHasPattern(userName)) {
      //TODO: userName does not match pattern
    }
    else if (!Validation.passwordHasPattern(password)) {
      //TODO: password does not match pattern
    }
    else {
      this.authAppRepository.register(email, password);
    }
  }

}
