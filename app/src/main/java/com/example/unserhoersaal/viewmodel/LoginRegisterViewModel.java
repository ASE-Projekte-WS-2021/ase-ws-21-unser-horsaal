package com.example.unserhoersaal.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.example.unserhoersaal.views.LoginFragment;
import com.google.firebase.auth.FirebaseUser;

//source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/blob/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/view/LoginRegisterFragment.java [30.12.2021]

/** Class Description. */
public class LoginRegisterViewModel extends AndroidViewModel {

  private static final String TAG = "LoginRegisterViewModel";

  private final AuthAppRepository authAppRepository;
  private final MutableLiveData<FirebaseUser> userLiveData;

  /** Constructor Description. */
  public LoginRegisterViewModel(@NonNull Application application) {
    super(application);
    this.authAppRepository = new AuthAppRepository(application);
    this.userLiveData = this.authAppRepository.getUserLiveData();
  }

    public void login(String email, String password, LoginFragment listener) {
        authAppRepository.login(email, password, listener);
    }

  public void register(String email, String password) {
    this.authAppRepository.register(email, password);
  }

  public LiveData<FirebaseUser> getUserLiveData() {
    return this.userLiveData;
  }
}
