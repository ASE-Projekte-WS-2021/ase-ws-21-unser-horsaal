package com.example.unserhoersaal.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.model.AuthAppRepository;
import com.google.firebase.auth.FirebaseUser;

// source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/tree/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/viewmodel [30.12.2021]

/** Class description. */
public class LoggedInViewModel extends AndroidViewModel {
  private AuthAppRepository authAppRepository;
  private MutableLiveData<FirebaseUser> userLiveData;
  private MutableLiveData<Boolean> loggedOutLiveData;

  /** Constructor. */
  public LoggedInViewModel(@NonNull Application application) {
    super(application);

    authAppRepository = new AuthAppRepository(application);
    userLiveData = authAppRepository.getUserLiveData();
    loggedOutLiveData = authAppRepository.getLoggedOutLiveData();
  }

  public void logOut() {
    authAppRepository.logOut();
  }

  public MutableLiveData<FirebaseUser> getUserLiveData() {
    return userLiveData;
  }

  public MutableLiveData<Boolean> getLoggedOutLiveData() {
    return loggedOutLiveData;
  }
}
