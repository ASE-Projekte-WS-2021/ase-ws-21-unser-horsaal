package com.example.unserhoersaal.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.EmailVerificationEnum;
import com.example.unserhoersaal.enums.LogRegErrorMessEnum;
import com.example.unserhoersaal.enums.ResetPasswordEnum;
import com.example.unserhoersaal.model.UserModel;
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
  private FirebaseUser user = null;
  private UserModel newUser = new UserModel();

  private MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
  private MutableLiveData<ResetPasswordEnum> existency = new MutableLiveData<>();


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
                /* Check if email is verified. If yes log in.*/
                if (this.firebaseAuth.getCurrentUser().isEmailVerified()) {
                  this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
                  verificationStatus.setValue(EmailVerificationEnum.NONE);
                } else {
                  /* If not initiate resend-verification-email-process.*/
                  verificationStatus.setValue(EmailVerificationEnum.REQUEST_EMAIL_VERIFICATION);
                }
                errorMessageLogin.setValue(LogRegErrorMessEnum.NONE);
              } else {
                /* Wrong input (empty or false pattern).*/
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
                /* if succeeded save new user in database.*/
                createNewUser(username, this.firebaseAuth.getCurrentUser());
                errorMessageRegistration.setValue(LogRegErrorMessEnum.NONE);

                /* if registration process succeeded initiate email verification process */
                this.firebaseAuth.getCurrentUser().sendEmailVerification()
                        .addOnCompleteListener(task1 -> {
                          if (task1.isSuccessful()) {
                            /* Initiate resend-verification-email-option.*/
                            verificationStatus.setValue(
                                    EmailVerificationEnum.SEND_EMAIL_VERIFICATION);
                            // Todo: Automatic forwarding to the course
                            // page when email is verified
                            //this.userLiveData.postValue(firebaseAuth.getCurrentUser());
                          }
                          //Todo: onAuthStateListener??
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

  public void sendPasswordResetMail(String email) {
    this.firebaseAuth.sendPasswordResetEmail(email);
  }

  /** Logging out the current user. */
  public void logOut() {
    this.firebaseAuth.signOut();
    //todo postValue(null) better?
    this.userLiveData.postValue(this.firebaseAuth.getCurrentUser());
  }

  /** Method to delete an useraccount. */
  public void deleteAccount() {
    //TODO: maybe replace argument for this.firebaseAuth.getCurrentUser(); see logout method
    String uid = this.firebaseAuth.getCurrentUser().getUid();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    reference.child(Config.CHILD_USER)
            .child(uid)
            .removeValue()
            .addOnSuccessListener(unused -> removeCourses(uid));
    //TODO: maybe remove user data in likes and blocked
  }

  /** Delete all connections between a user and his courses. */
  private void removeCourses(String uid) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    List<String> courseIdList = new ArrayList<>();

    reference.child(Config.CHILD_USER_COURSES)
            .child(uid)
            .get()
            .addOnSuccessListener(dataSnapshot -> {
              for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                courseIdList.add(snapshot.getKey());
              }
              reference.child(Config.CHILD_USER_COURSES)
                      .child(uid)
                      .removeValue()
                      .addOnSuccessListener(unused -> removeUserFromCourses(courseIdList, uid));
            });

  }

  /** Remove the user from all courses the user is signed in. */
  private void removeUserFromCourses(List<String> courseIdList, String uid) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    for (String courseId : courseIdList) {
      reference.child(Config.CHILD_COURSES_USER)
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
        this.userLiveData.postValue(null);
      }
    });
  }

  /** Checks FirebaseAuth if the email that the user tries to register with exists. */
  public void emailExist(String email) {
    this.firebaseAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener(task -> {
              boolean emailNotExists = task.getResult().getSignInMethods().isEmpty();
              if (emailNotExists) {
                existency.setValue(ResetPasswordEnum.ERROR);
              } else {
                existency.setValue(ResetPasswordEnum.SUCCESS);
              }
            });
  }

  public MutableLiveData<ResetPasswordEnum> getExistency() {
    return this.existency;
  }

}

