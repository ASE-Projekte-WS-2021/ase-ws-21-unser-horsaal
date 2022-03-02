package com.example.unserhoersaal.repository;

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

  /** This method is logging in the user.
   * if login process fails show error message
   */
  public void login(String email, String password, MutableLiveData<LogRegErrorMessEnum>
          errorMessageLogin, MutableLiveData<EmailVerificationEnum> logToastMessages) {
    this.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                if (this.firebaseAuth.getCurrentUser().isEmailVerified()) {
                  this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
                  logToastMessages.setValue(EmailVerificationEnum.NONE);
                } else {
                  logToastMessages.setValue(EmailVerificationEnum.REQUEST_EMAIL_VERIFICATION);
                }
                //this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
                errorMessageLogin.setValue(LogRegErrorMessEnum.NONE);
              } else {
                errorMessageLogin.setValue(LogRegErrorMessEnum.WRONG_LOGIN_INPUT);
              }
            });
  }

  /** This method registers a new user.
   * if registration process fails show error message
   */
  public void register(String username, String email, String password,
                       MutableLiveData<LogRegErrorMessEnum> errorMessageRegistration,
                       MutableLiveData<EmailVerificationEnum> verificationStatus) {
    this.firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                createNewUser(username, this.firebaseAuth.getCurrentUser());
                errorMessageRegistration.setValue(LogRegErrorMessEnum.NONE);
                sendEmailVerification(verificationStatus);
              }
            });
  }

  /**Method creates a new user.**/
  private void createNewUser(String username, FirebaseUser regUser) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    newUser.setDisplayName(username);
    newUser.setEmail(regUser.getEmail());

    databaseReference.child("users").child(regUser.getUid()).setValue(newUser).addOnSuccessListener(
            new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                //userLiveData.postValue(firebaseAuth.getCurrentUser());
              }
            }
    );
  }

  public void sendEmailVerification(MutableLiveData<EmailVerificationEnum> verificationStatus) {
    this.firebaseAuth.getCurrentUser().sendEmailVerification()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  verificationStatus.setValue(EmailVerificationEnum.SEND_EMAIL_VERIFICATION);
                  logOut();
                }
              }
            });
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
}
