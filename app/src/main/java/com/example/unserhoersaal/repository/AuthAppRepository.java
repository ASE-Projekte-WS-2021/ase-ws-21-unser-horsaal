package com.example.unserhoersaal.repository;

import android.util.Log;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/tree/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/model [30.12.2021]

/** This class manages the data base access for the user identification. */
public class AuthAppRepository {

  private static final String TAG = "AuthAppRepo";

  private static AuthAppRepository instance;
  private final FirebaseAuth firebaseAuth;
  private FirebaseUser firebaseUser = null;
  private final StateLiveData<FirebaseUser> userLiveData = new StateLiveData<>();


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
      this.userLiveData.postValue(new StateData<>(this.firebaseAuth.getCurrentUser()));
    }
  }

  /** Gives back the current UserModel. */
  public StateLiveData<FirebaseUser> getUserStateLiveData() {
    /* TODO: remove completley to remove register issue!!!!
    //TODO: why 2 times this.firebaseuser === null
    if (this.firebaseUser == null) {
      this.firebaseUser = this.firebaseAuth.getCurrentUser();
    }
    if (this.firebaseUser == null) {
      //TODO: review
      Log.e(TAG, "user data is null");
      return null;
    }
    this.userLiveData.setValue(new StateData<>(this.firebaseUser));*/
    return this.userLiveData;
  }

  /** This method is logging in the user.*/
  public void login(String email, String password) {
    this.userLiveData.postLoading();
    this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                if (this.firebaseAuth.getCurrentUser() == null) {
                  //TODO: review
                  Log.e(TAG, "firebaseauth is null");
                  this.userLiveData.postError(new Error(Config.LOGIN_FAILED), ErrorTag.REPO);
                }
                else {
                  //TODO: check in login fragment if email is verified
                  this.userLiveData.postSuccess(this.firebaseAuth.getCurrentUser());
                }
              } else {
                Log.e(TAG, "firebaseauth failed.");
                this.userLiveData.postError(new Error(Config.LOGIN_FAILED), ErrorTag.REPO);
              }
            });
  }

  /** This method registers a new user.*/
  public void register(String username, String email, String password) {
    this.userLiveData.postLoading();
    this.firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                this.firebaseUser = this.firebaseAuth.getCurrentUser();
                if (this.firebaseUser == null) {
                  Log.w(TAG, "firebase user is null.");
                }
                this.createNewUser(username, email, this.firebaseUser.getUid());
                //TODO: await the result of createNewUser before sending a verification email
                //TODO: important!!!
                /* if registration process succeeded initiate email verification process */
                this.firebaseUser.sendEmailVerification()
                        .addOnCompleteListener(task1 -> {
                          if (task1.isSuccessful()) {
                            this.userLiveData.postSuccess(this.firebaseUser);
                          } else {
                            this.userLiveData.postError(
                                    new Error(Config.VERIFICATION_EMAIL_NOT_SENT), ErrorTag.REPO);
                          }
                        });

                //TODO: if this does not work, https://stackoverflow.com/a/46580326/13620136
                // suggests reloading the firebase user object
                this.firebaseAuth.addAuthStateListener(this.authStateListener());
              }
              else {
                Log.e(TAG, "User registration failed.");
                this.userLiveData.postError(new Error(Config.REG_FAILED), ErrorTag.REPO);
              }
            });
  }

  /**Method creates a new user.**/
  private void createNewUser(String username, String email, String uid) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    UserModel newUser = new UserModel();
    newUser.setDisplayName(username);
    newUser.setEmail(email);

    databaseReference
            .child(Config.CHILD_USER)
            .child(uid)
            .setValue(newUser)
            .addOnSuccessListener(unused -> {
              //TODO: pass success result to method register
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, "Could not create a new user!");
              //TODO: assert! deny access to user! pass result to method register
            });
  }

  /** listenr: changes on firebase user status */
  private FirebaseAuth.AuthStateListener authStateListener() {
    return firebaseAuth1 -> {
      this.firebaseUser = firebaseAuth1.getCurrentUser();
      if (this.firebaseUser != null && this.firebaseUser.isEmailVerified()) {
        Log.d(TAG, "User verified.");
        this.firebaseUser.reload();
        this.userLiveData.postSuccess(this.firebaseUser);
        this.firebaseAuth.removeAuthStateListener(this.authStateListener());
      } else {
        Log.w(TAG, "User konnte nicht verifiziert werden.");
      }
    };
  }

  /** Method to reset the password via password reset email.*/
  public void resetPassword(String mail) {
    this.userLiveData.postLoading();
    this.firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        Log.d(TAG, "password was reset.");
        this.userLiveData.postSuccess(this.firebaseUser);
      } else {
        Log.d(TAG, "password could not be reset.");
        this.userLiveData.postError(new Error(Config.PASSWORD_RESET_FAILED), ErrorTag.REPO);
      }
    });
  }

  /** Method to (re)send a email verification.*/
  public void resendEmailVerification() {
    this.userLiveData.postLoading();
    if (this.firebaseUser == null) {
      this.userLiveData.postError(new Error(Config.VERIFICATION_FIREBASE_USER_NULL), ErrorTag.REPO);
    } else {
      this.firebaseUser.sendEmailVerification()
              .addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                  this.userLiveData.postSuccess(this.firebaseUser);
                  //add an authstatelistener so we can live observe when user clicks the email
                  this.firebaseAuth.addAuthStateListener(this.authStateListener());
                } else {
                  this.userLiveData.postError(new Error(Config.VERIFICATION_EMAIL_NOT_SENT), ErrorTag.REPO);
                }
              });
    }
  }

  public void sendPasswordResetMail(String email) {
    this.userLiveData.postLoading();
    this.firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        this.userLiveData.postSuccess(this.firebaseUser);
      } else {
        this.userLiveData.postError(new Error(Config.VERIFICATION_EMAIL_NOT_SENT), ErrorTag.REPO);
      }
    });
  }

  /** Logging out the current user. */
  public void logOut() {
    this.firebaseAuth.signOut();
    this.userLiveData.postSuccess(null);
  }

  /** Method to delete an user account. */
  public void deleteAccount() {
    //TODO: delete Account in real time database and firebase authentication
    //TODO: maybe replace argument for this.firebaseAuth.getCurrentUser(); see logout method
  }

  /** Checks FirebaseAuth if the email that the user tries to register with exists. */
  public void emailExist(String email) {
    this.userLiveData.postLoading();
    this.firebaseAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener(task -> {
              boolean emailNotExists = task.getResult().getSignInMethods().isEmpty();
              if (emailNotExists) {
                //existency.setValue(ResetPasswordEnum.ERROR);
              } else {
                //existency.setValue(ResetPasswordEnum.SUCCESS);
              }
            });
  }

}

