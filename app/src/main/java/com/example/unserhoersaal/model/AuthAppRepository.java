package com.example.unserhoersaal.model;

import android.app.Application;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

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

  /** Performs a login with email and password using firebase to authenticate the user, load
   * user information (userid and user email) and check the registered courses. */
  public void login(String email, String password) {
    this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.application.getMainExecutor(), task -> {
              if (task.isSuccessful()) {
                this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
              } else {
                String exception = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(this.application.getApplicationContext(),
                        "Login Failure: " + exception,
                        Toast.LENGTH_SHORT).show();
              }
            });
  }

  /** Performs a registration with firebase to get a google account which is required
   * to use our services. */
  public void register(String email, String password) {
    this.firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.application.getMainExecutor(), task -> {
              if (task.isSuccessful()) {
                this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
              } else {
                String exception = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(this.application.getApplicationContext(),
                        "Registration Failure: " + exception,
                        Toast.LENGTH_SHORT).show();
              }
            });
  }

  public void logOut() {
    this.firebaseAuth.signOut();
    this.loggedOutLiveData.postValue(true);
  }

  public MutableLiveData<FirebaseUser> getUserLiveData() {
    return this.userLiveData;
  }

  public MutableLiveData<Boolean> getLoggedOutLiveData() {
    return this.loggedOutLiveData;
  }
}
