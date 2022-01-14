package com.example.unserhoersaal.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.model.AuthAppRepository;
import com.google.firebase.auth.FirebaseUser;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** Class Description. */
public class LoginRegisterViewModel extends AndroidViewModel {
  private final AuthAppRepository authAppRepository;
  private final MutableLiveData<FirebaseUser> userLiveData;

  /** Constructor Description. */
  public LoginRegisterViewModel(@NonNull Application application) {
    super(application);
    this.authAppRepository = new AuthAppRepository(application);
    this.userLiveData = this.authAppRepository.getUserLiveData();
  }

  public void login(String email, String password) {
    this.authAppRepository.login(email, password);
  }

  public void register(String email, String password) {
    this.authAppRepository.register(email, password);
  }

  public MutableLiveData<FirebaseUser> getUserLiveData() {
    return this.userLiveData;
  }
}
