package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.google.firebase.auth.FirebaseUser;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** Class Description. */
public class LoginRegisterViewModel extends ViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private AuthAppRepository authAppRepository;
  private MutableLiveData<FirebaseUser> userLiveData;

  /** Initialize the LoginRegisterViewModel. */
  public void init() {
    if (userLiveData != null) {
      return;
    }
    authAppRepository = AuthAppRepository.getInstance();
    userLiveData = authAppRepository.getUserLiveData();
  }

  public void login(String email, String password) {
    authAppRepository.login(email, password);
  }

  public void register(String email, String password) {
    this.authAppRepository.register(email, password);
  }

  public void logOut() {
    this.authAppRepository.logOut();
  }

  public LiveData<FirebaseUser> getUserLiveData() {
    return this.userLiveData;
  }
}
