package com.example.unserhoersaal.repository;

import android.util.Log;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/tree/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/model [30.12.2021]

/** This class manages the data base access for the user identification. */
public class AuthAppRepository {

  private static final String TAG = "AuthAppRepo";

  private static AuthAppRepository instance;
  private FirebaseAuth firebaseAuth;
  private DatabaseReference databaseReference;
  private FirebaseUser firebaseUser = null;
  private StateLiveData<FirebaseUser> userLiveData = new StateLiveData<>();
  private StateLiveData<Boolean> emailSentLiveData = new StateLiveData<>();

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
    this.databaseReference = FirebaseDatabase.getInstance().getReference();

    if (this.firebaseAuth.getCurrentUser() != null) {
      this.userLiveData.postCreate(this.firebaseAuth.getCurrentUser());
    }

    this.emailSentLiveData.postCreate(Boolean.FALSE);
  }

  /** Gives back the current UserModel. */
  public StateLiveData<FirebaseUser> getUserStateLiveData() {
    return this.userLiveData;
  }

  public StateLiveData<Boolean> getEmailSentLiveData() {
    return this.emailSentLiveData;
  }

  /** This method is logging in the user.*/
  public void login(String email, String password) {
    this.userLiveData.postLoading();
    this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                if (this.firebaseAuth.getCurrentUser() == null) {
                  Log.e(TAG, "firebase did not authenticated a user.");
                  this.userLiveData.postError(new Error(Config.AUTH_LOGIN_FAILED), ErrorTag.REPO);
                } else {
                  Log.d(TAG, "Login with firebase auth was successful");
                  this.userLiveData.postUpdate(this.firebaseAuth.getCurrentUser());
                }
              } else {
                Log.e(TAG, "authentication failed.");
                this.userLiveData.postError(new Error(Config.AUTH_LOGIN_FAILED), ErrorTag.REPO);
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
                  Log.e(TAG, "firebase did not authenticated a user.");
                }
                Log.d(TAG, "Successfully registered with firebase auth.");
                this.createNewUser(username, email, this.firebaseUser.getUid());
              } else {
                Log.e(TAG, "user registration failed.");
                this.userLiveData.postError(
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
              Log.d(TAG, "A new user was created in the database");
              this.sendVerificationEmail();
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, "Could not create a new user!");
              this.userLiveData.postError(
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
                this.userLiveData.postUpdate(this.firebaseUser);
                this.firebaseAuth.addAuthStateListener(this.authStateListenerVerification());
              } else {
                Log.e(TAG,  Config.AUTH_VERIFICATION_EMAIL_NOT_SENT);
                this.userLiveData.postError(
                        new Error(Config.AUTH_VERIFICATION_EMAIL_NOT_SENT), ErrorTag.REPO);
              }
            });
  }

  /** listenr: changes on firebase user status. */
  private FirebaseAuth.AuthStateListener authStateListenerVerification() {
    return firebaseAuth1 -> {
      Log.d(TAG, "Firebase Auth State changed");
      this.firebaseUser = firebaseAuth1.getCurrentUser();
      if (this.firebaseUser != null && this.firebaseUser.isEmailVerified()) {
        Log.d(TAG, "User verified.");
        this.userLiveData.postUpdate(this.firebaseUser);
        this.firebaseAuth.removeAuthStateListener(this.authStateListenerVerification());
      } else {
        Log.w(TAG, "User konnte nicht verifiziert werden.");
      }
    };
  }

  /** Method to reset the password via password reset email.*/
  public void resetPassword(String mail) {
    this.userLiveData.postLoading();

    this.firebaseAuth
            .sendPasswordResetEmail(mail)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                Log.d(TAG, Config.AUTH_EDIT_PASSWORD_CHANGE_SUCCESS);
                this.userLiveData.postUpdate(this.firebaseUser);
              } else {
                Log.d(TAG, Config.AUTH_EDIT_PASSWORD_CHANGE_FAILED);
                this.userLiveData.postError(
                        new Error(Config.AUTH_EDIT_PASSWORD_CHANGE_FAILED), ErrorTag.REPO);
              }
            });
  }

  /** Method to (re)send a email verification.*/
  public void resendVerificationEmail() {
    this.userLiveData.postLoading();

    if (this.firebaseUser == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.userLiveData.postError(new Error(Config.FIREBASE_USER_NULL), ErrorTag.REPO);
    } else {
      this.firebaseUser
              .sendEmailVerification()
              .addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                  Log.d(TAG, Config.AUTH_VERIFICATION_EMAIL_SENT);
                  this.userLiveData.postUpdate(this.firebaseUser);
                  //add an authstatelistener so we can live observe when user clicks the email
                  this.firebaseAuth.addAuthStateListener(this.authStateListenerVerification());
                } else {
                  Log.e(TAG, Config.AUTH_VERIFICATION_EMAIL_NOT_SENT);
                  this.userLiveData.postError(
                          new Error(Config.AUTH_VERIFICATION_EMAIL_NOT_SENT), ErrorTag.REPO);
                }
              });
    }
  }

  /** JavaDoc. */
  public void sendPasswordResetMail(String email) {
    this.emailSentLiveData.postLoading();

    this.firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                Log.d(TAG, Config.AUTH_PASSWORD_RESET_MAIL_SENT);

                this.emailSentLiveData.postUpdate(Boolean.TRUE);
                this.emailSentLiveData.postCreate(Boolean.FALSE);
              } else {
                Log.e(TAG, Config.AUTH_PASSWORD_RESET_MAIL_NOT_SENT);
                this.emailSentLiveData.postError(
                        new Error(Config.AUTH_PASSWORD_RESET_MAIL_NOT_SENT), ErrorTag.REPO);
              }
            });
  }

  /** Logging out the current user. */
  public void logOut() {
    this.firebaseAuth.signOut();
    this.userLiveData.postUpdate(this.firebaseAuth.getCurrentUser());
  }

  /** Method to delete an user account. */
  public void deleteAccount() {
    //TODO: maybe replace argument for this.firebaseAuth.getCurrentUser(); see logout method
    this.userLiveData.postLoading();
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    this.databaseReference.child(Config.CHILD_USER)
            .child(uid)
            .removeValue()
            .addOnSuccessListener(unused -> removeCourses(uid));
    //TODO: maybe remove user data in likes and blocked
  }

  /** Delete all connections between a user and his courses. */
  private void removeCourses(String uid) {
    List<String> courseIdList = new ArrayList<>();

    this.databaseReference.child(Config.CHILD_USER_COURSES)
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
  private void deleteUser() {
    FirebaseUser user = this.firebaseAuth.getCurrentUser();
    this.firebaseAuth.signOut();
    user.delete().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        this.userLiveData.postUpdate(null);
      }
    });
  }


  /** Reloads the current FirebaseUser object so that we can see if the email was verified. */
  public void reloadFirebaseUser() {
    if (this.firebaseAuth.getCurrentUser() !=  null) {
      Log.d(TAG, "reloaded current user");
      this.firebaseAuth.getCurrentUser().reload();
      this.firebaseAuth.addAuthStateListener(this.authStateListenerVerification());
    }
  }

}