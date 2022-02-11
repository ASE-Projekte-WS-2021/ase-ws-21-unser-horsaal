package com.example.unserhoersaal.repository;

import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/tree/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/model [30.12.2021]

/** This class maanages the data base access for the user identification. */
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

    if (firebaseAuth.getCurrentUser() != null) {
      this.userLiveData.postValue(firebaseAuth.getCurrentUser());
    }
  }

  /** This method is logging in the user. */
  public void login(String email, String password) {
    firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                userLiveData.postValue(firebaseAuth.getCurrentUser());
              }
            });
  }

  /** This method registers a new user. */
  public void register(String email, String password) {
    firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                userLiveData.postValue(firebaseAuth.getCurrentUser());
              }
            });
  }

  public void logOut() {
    this.firebaseAuth.signOut();
    this.userLiveData.postValue(firebaseAuth.getCurrentUser());
  }

  /** Gives back the current User. */
  public MutableLiveData<FirebaseUser> getUserLiveData() {
    if (user == null) {
      user = firebaseAuth.getCurrentUser();
    }
    userLiveData.setValue(user);
    return userLiveData;
  }

}
