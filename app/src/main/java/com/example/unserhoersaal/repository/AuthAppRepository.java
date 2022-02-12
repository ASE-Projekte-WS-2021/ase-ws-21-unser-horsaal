package com.example.unserhoersaal.repository;

import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/tree/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/model [30.12.2021]

/** This class manages the data base access for the user identification. */
public class AuthAppRepository {

  private static final String TAG = "AuthAppRepo";

  private static AuthAppRepository instance;

  private FirebaseAuth firebaseAuth;
  private FirebaseUser user = null;

  private MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();

  /** Gives back an Instance of AuthAppRepository. */
  public static AuthAppRepository getInstance() {
    if (instance == null) {
      instance = new AuthAppRepository();
    }
    return instance;
  }

  /** Constructor Description. */
  public AuthAppRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();

    if (this.firebaseAuth.getCurrentUser() != null) {
      this.userLiveData.postValue(firebaseAuth.getCurrentUser());
    }
  }

  /** Gives back the current User. */
  public MutableLiveData<FirebaseUser> getUserLiveData() {
    if (this.user == null) {
      this.user = this.firebaseAuth.getCurrentUser();
    }
    this.userLiveData.setValue(this.user);
    return this.userLiveData;
  }

  /** This method is logging in the user. */
  public void login(String email, String password) {
    this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
              }
            });
  }

  /** This method registers a new user. */
  public void register(String email, String password) {
    this.firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
              }
            });
  }

  /** Logging out the current user. */
  public void logOut() {
    this.firebaseAuth.signOut();
    //todo postValue(null) better?
    this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
  }
}
