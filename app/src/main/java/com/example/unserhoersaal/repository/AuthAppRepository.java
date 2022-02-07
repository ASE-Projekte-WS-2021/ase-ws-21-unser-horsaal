package com.example.unserhoersaal.repository;

import android.app.Application;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.views.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/tree/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/model [30.12.2021]

/** Class Description. */
public class AuthAppRepository {
  private final Application application;
  private final FirebaseAuth firebaseAuth;
  private final MutableLiveData<FirebaseUser> userLiveData;
  private final MutableLiveData<Boolean> loggedOutLiveData;

  /** Constructor Description. */
  public AuthAppRepository(Application application) {
    this.application = application;
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.userLiveData = new MutableLiveData<>();
    this.loggedOutLiveData = new MutableLiveData<>();

    if (firebaseAuth.getCurrentUser() != null) {
      this.userLiveData.postValue(firebaseAuth.getCurrentUser());
      this.loggedOutLiveData.postValue(false);
    }
  }

    public void login(String email, String password, LoginFragment listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                        listener.loginResult(true);
                    } else {
                        //Toast.makeText(application.getApplicationContext(), "Login Failure: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        listener.loginResult(false);
                    }
                });
    }

    public void register(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                    } else {
                        Toast.makeText(application.getApplicationContext(), "Registration Failure: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

  public void logOut() {
    this.firebaseAuth.signOut();
    this.loggedOutLiveData.postValue(true);
  }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

  public MutableLiveData<Boolean> getLoggedOutLiveData() {
    return this.loggedOutLiveData;
  }
}
