package com.example.unserhoersaal.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.enums.LogRegErrorMessEnum;
import com.example.unserhoersaal.enums.EmailVerificationEnum;
import com.example.unserhoersaal.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

// source: https://github.com/learntodroid/FirebaseAuthLoginRegisterMVVM/tree/master/app/src/main/java/com/learntodroid/firebaseauthloginregistermvvm/model [30.12.2021]

/** This class manages the data base access for the user identification. */
public class AuthAppRepository {

  private static final String TAG = "AuthAppRepo";

  private static AuthAppRepository instance;

  private FirebaseAuth firebaseAuth;
  private FirebaseUser user = null;
  private UserModel newUser = new UserModel();

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

  /** Gives back the current UserModel. */
  public MutableLiveData<FirebaseUser> getUserLiveData() {
    if (this.user == null) {
      this.user = this.firebaseAuth.getCurrentUser();
    }
    this.userLiveData.setValue(this.user);
    return this.userLiveData;
  }

  /** This method is logging in the user.*/
  public void login(String email, String password, MutableLiveData<LogRegErrorMessEnum>
          errorMessageLogin, MutableLiveData<EmailVerificationEnum> verificationStatus) {
    this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                /** Check if email is verified. If yes log in.*/
                if (this.firebaseAuth.getCurrentUser().isEmailVerified()) {
                  this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
                  verificationStatus.setValue(EmailVerificationEnum.NONE);
                } else {
                  /** If not initiate resend-verification-email-process.*/
                  verificationStatus.setValue(EmailVerificationEnum.REQUEST_EMAIL_VERIFICATION);
                }
                errorMessageLogin.setValue(LogRegErrorMessEnum.NONE);
              } else {
                /** Wrong input (empty or false pattern).*/
                errorMessageLogin.setValue(LogRegErrorMessEnum.WRONG_LOGIN_INPUT);
              }
            });
  }

  /** This method registers a new user.*/
  public void register(String username, String email, String password,
                       MutableLiveData<LogRegErrorMessEnum> errorMessageRegistration,
                       MutableLiveData<EmailVerificationEnum> verificationStatus) {
    this.firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                /** if succeeded save new user in database.*/
                createNewUser(username, this.firebaseAuth.getCurrentUser());
                errorMessageRegistration.setValue(LogRegErrorMessEnum.NONE);

                /** if registration process succeeded initiate email verification process */
                this.firebaseAuth.getCurrentUser().sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                              /** Initiate resend-verification-email-option.*/
                              verificationStatus.setValue(
                                      EmailVerificationEnum.SEND_EMAIL_VERIFICATION);
                            // Todo: Automatic forwarding to the course page when email is verified
                              //this.userLiveData.postValue(firebaseAuth.getCurrentUser());
                            }
                            //Todo: onAuthStateListener??
                          }
                        });
              }
            });
  }

  /** Method to reset the password via password reset email.*/
  public void resetPassword(String mail) {
    this.firebaseAuth.sendPasswordResetEmail(mail);
  }

  /**Method creates a new user.**/
  private void createNewUser(String username, FirebaseUser regUser) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    newUser.setDisplayName(username);
    newUser.setEmail(regUser.getEmail());

    databaseReference.child("users").child(regUser.getUid()).setValue(newUser);
  }

  /** Method to (re)send a email verification.*/
  public void resendEmailVerification() {
    Objects.requireNonNull(this.firebaseAuth.getCurrentUser()).sendEmailVerification();
  }

  /** Logging out the current user. */
  public void logOut() {
    this.firebaseAuth.signOut();
    //todo postValue(null) better?
    this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
  }

  public void deleteAccount() {
    //TODO: delete Account in real time database and firebase authentication
    //TODO: maybe replace argument for this.firebaseAuth.getCurrentUser(); see logout method
    this.userLiveData.postValue(null);
  }

  public FirebaseAuth getFirebaseAuth() {
    return this.firebaseAuth;
  }

}

