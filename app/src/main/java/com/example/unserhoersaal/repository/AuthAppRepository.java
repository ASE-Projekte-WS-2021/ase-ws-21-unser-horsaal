package com.example.unserhoersaal.repository;

import android.util.Log;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

// source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/tree/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/model [30.12.2021]

/** This repository manages the authentication related work. */
public class AuthAppRepository {

  private static final String TAG = "AuthAppRepo";

  private static AuthAppRepository instance;
  private final DatabaseReference databaseReference;
  private final FirebaseAuth firebaseAuth;
  private FirebaseUser firebaseUser = null;
  private final StateLiveData<FirebaseUser> firebaseUserRepoState = new StateLiveData<>();
  private final StateLiveData<Boolean> emailSentStatus = new StateLiveData<>();

  /** Gives back an Instance of AuthAppRepository. */
  public static AuthAppRepository getInstance() {
    if (instance == null) {
      instance = new AuthAppRepository();
    }
    return instance;
  }

  /** Constructor which is only accessible in class to achieve singleton design patterns. */
  private AuthAppRepository() {
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.firebaseAuth = FirebaseAuth.getInstance();

    if (this.firebaseAuth.getCurrentUser() != null) {
      this.firebaseUserRepoState.postCreate(this.firebaseAuth.getCurrentUser());
    }

    this.emailSentStatus.postCreate(Boolean.FALSE);
  }

  public StateLiveData<FirebaseUser> getFirebaseUserRepoState() {
    return this.firebaseUserRepoState;
  }

  public StateLiveData<Boolean> getEmailSentStatus() {
    return this.emailSentStatus;
  }

  /** This method is logging in the user.*/
  public void login(String email, String password) {
    this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                if (this.firebaseAuth.getCurrentUser() == null) {
                  Log.e(TAG, Config.AUTH_LOGIN_FIREBASE_USER_NULL);
                  this.firebaseUserRepoState.postError(
                          new Error(Config.AUTH_LOGIN_FAILED), ErrorTag.REPO);
                } else {
                  Log.d(TAG, Config.AUTH_LOGIN_SUCCESSFUL);
                  this.firebaseUserRepoState.postUpdate(this.firebaseAuth.getCurrentUser());
                }
              } else {
                Log.e(TAG, Config.AUTH_LOGIN_FAILED);
                this.firebaseUserRepoState.postError(
                        new Error(Config.AUTH_LOGIN_FAILED), ErrorTag.REPO);
              }
            });
  }

  /** This method registers a new user.*/
  public void register(String username, String email, String password) {
    this.firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                this.firebaseUser = this.firebaseAuth.getCurrentUser();
                if (this.firebaseUser == null) {
                  Log.e(TAG, Config.AUTH_LOGIN_FIREBASE_USER_NULL);
                }
                Log.d(TAG, Config.AUTH_REGISTRATION_SUCCESSFUL);
                this.createNewUser(username, email, this.firebaseUser.getUid());
              } else {
                Log.e(TAG, Config.AUTH_REGISTRATION_FAILED);
                this.firebaseUserRepoState.postError(
                        new Error(Config.AUTH_REGISTRATION_FAILED), ErrorTag.REPO);
              }
            });
  }

  /** Method creates a new user. **/
  private void createNewUser(String username, String email, String uid) {
    UserModel newUser = new UserModel();
    newUser.setDisplayName(username);
    newUser.setEmail(email);

    this.databaseReference
            .child(Config.CHILD_USER)
            .child(uid)
            .setValue(newUser)
            .addOnSuccessListener(unused -> {
              Log.d(TAG, Config.AUTH_REGISTRATION_USER_CREATED);
              this.sendVerificationEmail();
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, Config.AUTH_REGISTRATION_NO_USER_CREATED);
              this.firebaseUserRepoState.postError(
                      new Error(Config.AUTH_REGISTRATION_FAILED), ErrorTag.REPO);
            });
  }

  /** sends a verification email to the email address that the user provided after a user is created
   * in the database. */
  private void sendVerificationEmail() {
    this.firebaseUser.sendEmailVerification()
            .addOnCompleteListener(task1 -> {
              if (task1.isSuccessful()) {
                Log.d(TAG, Config.AUTH_VERIFICATION_EMAIL_SENT);
                this.firebaseUserRepoState.postUpdate(this.firebaseUser);
                this.firebaseAuth.addAuthStateListener(this.authStateListenerVerification());
              } else {
                Log.e(TAG, Config.AUTH_VERIFICATION_EMAIL_NOT_SENT);
                this.firebaseUserRepoState.postError(
                        new Error(Config.AUTH_VERIFICATION_EMAIL_NOT_SENT), ErrorTag.REPO);
              }
            });
  }

  /** listener: changes on firebase user status. */
  private FirebaseAuth.AuthStateListener authStateListenerVerification() {
    return firebaseAuth1 -> {
      Log.d(TAG, Config.AUTH_LISTENER_STATE_CHANGE);
      this.firebaseUser = firebaseAuth1.getCurrentUser();
      if (this.firebaseUser != null && this.firebaseUser.isEmailVerified()) {
        Log.d(TAG, Config.AUTH_USER_VERIFIED);
        this.firebaseUserRepoState.postUpdate(this.firebaseUser);
        this.firebaseAuth.removeAuthStateListener(this.authStateListenerVerification());
      } else {
        Log.w(TAG, Config.AUTH_USER_NOT_VERIFIED);
      }
    };
  }

  /** Method to reset the password via password reset email.*/
  public void resetPassword(String mail) {
    this.firebaseUserRepoState.postLoading();

    this.firebaseAuth
            .sendPasswordResetEmail(mail)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                Log.d(TAG, Config.AUTH_EDIT_PASSWORD_CHANGE_SUCCESS);
                this.firebaseUserRepoState.postUpdate(this.firebaseUser);
              } else {
                Log.d(TAG, Config.AUTH_EDIT_PASSWORD_CHANGE_FAILED);
                this.firebaseUserRepoState.postError(
                        new Error(Config.AUTH_EDIT_PASSWORD_CHANGE_FAILED), ErrorTag.REPO);
              }
            });
  }

  /** Method to (re)send a email verification.*/
  public void resendVerificationEmail() {
    if (this.firebaseUser == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.firebaseUserRepoState.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
    } else {
      this.firebaseUser
              .sendEmailVerification()
              .addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                  Log.d(TAG, Config.AUTH_VERIFICATION_EMAIL_SENT);
                  this.firebaseUserRepoState.postUpdate(this.firebaseUser);
                  //add an authstatelistener so we can live observe when user clicks the email
                  this.firebaseAuth.addAuthStateListener(this.authStateListenerVerification());
                } else {
                  Log.e(TAG, Config.AUTH_VERIFICATION_EMAIL_NOT_SENT);
                  this.firebaseUserRepoState.postError(
                          new Error(Config.AUTH_VERIFICATION_EMAIL_NOT_SENT), ErrorTag.REPO);
                }
              });
    }
  }

  /** JavaDoc. */
  public void sendPasswordResetMail(String email) {
    this.firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                Log.d(TAG, Config.AUTH_PASSWORD_RESET_MAIL_SENT);
                this.emailSentStatus.postUpdate(Boolean.TRUE);
                this.emailSentStatus.postCreate(Boolean.FALSE);
              } else {
                Log.e(TAG, Config.AUTH_PASSWORD_RESET_MAIL_NOT_SENT);
                this.emailSentStatus.postError(
                        new Error(Config.AUTH_PASSWORD_RESET_MAIL_NOT_SENT), ErrorTag.REPO);
              }
            });
  }

  /** Logging out the current user. */
  public void logOut() {
    this.firebaseAuth.signOut();
    this.firebaseUserRepoState.postUpdate(this.firebaseAuth.getCurrentUser());
  }

  /** Method to delete an user account. */
  public void deleteAccount() {
    //TODO: maybe replace argument for this.firebaseAuth.getCurrentUser(); see logout method
    this.firebaseUserRepoState.postLoading();

    if (this.firebaseUser == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.firebaseUserRepoState.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
      return;
    }
    String uid = this.firebaseUser.getUid();
    this.databaseReference
            .child(Config.CHILD_USER)
            .child(uid)
            .removeValue()
            .addOnSuccessListener(unused -> removeCourses(uid));
    //TODO: maybe remove user data in likes and blocked
  }

  /** Delete all connections between a user and his courses. */
  private void removeCourses(String uid) {
    List<String> courseIdList = new ArrayList<>();

    this.databaseReference
            .child(Config.CHILD_USER_COURSES)
            .child(uid)
            .get()
            .addOnSuccessListener(dataSnapshot -> {
              for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                courseIdList.add(snapshot.getKey());
              }
              this.databaseReference.child(Config.CHILD_USER_COURSES)
                      .child(uid)
                      .removeValue()
                      .addOnSuccessListener(unused -> removeUserFromCourses(courseIdList, uid));
            });
  }

  /** Remove the user from all courses the user is signed in. */
  private void removeUserFromCourses(List<String> courseIdList, String uid) {
    for (String courseId : courseIdList) {
      this.databaseReference.child(Config.CHILD_COURSES_USER)
              .child(courseId)
              .child(uid)
              .removeValue();
    }
    //TODO wait for removing the user from courses
    deleteUser();

  }

  /** Delete the user data in firebase auth. */
  //TODO@Julian: https://stackoverflow.com/a/38114865/13620136
  private void deleteUser() {
    //FirebaseUser user = this.firebaseAuth.getCurrentUser();
    this.firebaseAuth.signOut();
    this.firebaseUser.delete().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        this.firebaseUserRepoState.postUpdate(null);
      }
    });
  }

  /** Reloads the current FirebaseUser object so that we can see if the email was verified. */
  public void reloadFirebaseUser() {
    if (this.firebaseAuth.getCurrentUser() !=  null) {
      Log.d(TAG, Config.AUTH_CURRENT_USER_RELOADED);
      this.firebaseAuth.getCurrentUser().reload();
      this.firebaseAuth.addAuthStateListener(this.authStateListenerVerification());
    }
  }

}