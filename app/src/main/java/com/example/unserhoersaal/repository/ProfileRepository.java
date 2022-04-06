package com.example.unserhoersaal.repository;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.utils.StateLiveData;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Repository for the ProfileViewModel.
 */
public class ProfileRepository {

  private static final String TAG = "ProfileRepo";

  private static ProfileRepository instance;
  private final DatabaseReference databaseReference;
  private final FirebaseAuth firebaseAuth;
  private final StorageReference storageReference;
  private final StateLiveData<UserModel> user = new StateLiveData<>();
  private final StateLiveData<Boolean> profileChanged = new StateLiveData<>();
  private String userId;

  /**
   * Constructor.
   */
  public ProfileRepository() {
    this.firebaseAuth = FirebaseAuth.getInstance();
    this.databaseReference = FirebaseDatabase.getInstance().getReference();
    this.storageReference = FirebaseStorage.getInstance().getReference();
    this.profileChanged.postCreate(Boolean.FALSE);
  }

  /**
   * Generate an instance of the class.
   *
   * @return Instance of the ProfileRepository
   * */
  public static ProfileRepository getInstance() {
    if (instance == null) {
      instance = new ProfileRepository();
    }
    return instance;
  }

  public StateLiveData<UserModel> getUser() {
    return this.user;
  }

  public StateLiveData<Boolean> getProfileChanged() {
    return this.profileChanged;
  }

  /**
   * Sets the new userId if a new user has logged in.
   */
  public void setUserId() {
    String uid;
    if (this.firebaseAuth.getCurrentUser() == null) {
      return;
    }
    uid = this.firebaseAuth.getCurrentUser().getUid();
    if (this.userId == null || !this.userId.equals(uid)) {
      this.userId = uid;
      this.loadUser();
    }
  }

  /**
   * Loads an user from the database.
   */
  private void loadUser() {
    this.user.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.user.postError(new Error(Config.PROFILE_FAILED_TO_LOAD_USER), ErrorTag.REPO);
      return;
    }

    String id = this.firebaseAuth.getCurrentUser().getUid();

    Query query = this.databaseReference.child(Config.CHILD_USER).child(id);
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        UserModel userModel = snapshot.getValue(UserModel.class);

        if (userModel == null) {
          user.postError(new Error(Config.PROFILE_FAILED_TO_LOAD_USER), ErrorTag.REPO);
          return;
        }
        userModel.setKey(snapshot.getKey());
        user.postUpdate(userModel);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        user.postError(new Error(Config.PROFILE_FAILED_TO_LOAD_USER), ErrorTag.REPO);
      }
    });
  }

  /**
   * Changes the name of the currently logged in user.
   *
   * @param displayName new chosen name of the user
   */
  public void changeDisplayName(String displayName) {
    this.profileChanged.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.profileChanged.postError(
              new Error(Config.AUTH_EDIT_USERNAME_CHANGE_FAILED), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getUid();

    if (uid == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.profileChanged.postError(
              new Error(Config.AUTH_EDIT_USERNAME_CHANGE_FAILED), ErrorTag.REPO);
      return;
    }

    this.databaseReference
            .child(Config.CHILD_USER)
            .child(uid)
            .child(Config.CHILD_DISPLAY_NAME)
            .setValue(displayName)
            .addOnSuccessListener(unused -> {
              profileChanged.postUpdate(Boolean.TRUE);
              profileChanged.postCreate(Boolean.FALSE);
            })
            .addOnFailureListener(e -> {
              Log.e(TAG, e.getMessage());
              profileChanged.postError(
                      new Error(Config.AUTH_EDIT_USERNAME_CHANGE_FAILED), ErrorTag.REPO);
            });
  }

  /**
   * Changes the institution of the currently logged in user.
   *
   * @param institution new institution of the user
   */
  public void changeInstitution(String institution) {
    this.profileChanged.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.profileChanged.postError(
              new Error(Config.AUTH_EDIT_INSTITUTION_CHANGE_FAILED), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getUid();

    if (uid == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.profileChanged.postError(
              new Error(Config.AUTH_EDIT_INSTITUTION_CHANGE_FAILED), ErrorTag.REPO);
      return;
    }

    this.databaseReference.child(Config.CHILD_USER)
            .child(uid)
            .child(Config.CHILD_INSTITUTION)
            .setValue(institution)
            .addOnSuccessListener(unused -> {
              profileChanged.postUpdate(Boolean.TRUE);
              profileChanged.postCreate(Boolean.FALSE);
            })
            .addOnFailureListener(e ->
                    profileChanged.postError(
                            new Error(Config.AUTH_EDIT_INSTITUTION_CHANGE_FAILED), ErrorTag.REPO));
  }

  /**
   * Changes the password of the currently logged in user.
   *
   * @param oldPassword old password
   * @param newPassword new password
   */
  public void changePassword(String oldPassword, String newPassword) {
    this.profileChanged.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.profileChanged.postError(
              new Error(Config.AUTH_EDIT_PASSWORD_CHANGE_FAILED), ErrorTag.REPO);
      return;
    }

    String email = this.firebaseAuth.getCurrentUser().getEmail();

    if (email == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.profileChanged.postError(
              new Error(Config.AUTH_EDIT_PASSWORD_CHANGE_FAILED), ErrorTag.REPO);
      return;
    }

    AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

    this.firebaseAuth
            .getCurrentUser()
            .reauthenticate(credential)
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                this.firebaseAuth
                        .getCurrentUser()
                        .updatePassword(newPassword)
                        .addOnCompleteListener(task1 -> {
                          if (task1.isSuccessful()) {
                            profileChanged.postUpdate(Boolean.TRUE);
                            profileChanged.postCreate(Boolean.FALSE);
                          } else {
                            profileChanged.postError(
                                    new Error(Config.AUTH_EDIT_PASSWORD_CHANGE_FAILED),
                                    ErrorTag.REPO);
                          }
                        });
              } else {
                profileChanged.postError(
                        new Error(Config.AUTH_EDIT_PASSWORD_CHANGE_FAILED), ErrorTag.REPO);
              }
            });
  }

  /**
   * Changes the profile picture of the currently logged in user.
   *
   * @param uri new profile picture
   */
  public void uploadImageToFirebase(Uri uri) {
    this.profileChanged.postLoading();

    if (this.firebaseAuth.getCurrentUser() == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.profileChanged.postError(
              new Error(Config.AUTH_EDIT_PROFILE_PICTURE_CHANGE_FAILED), ErrorTag.REPO);
      return;
    }

    String uid = this.firebaseAuth.getUid();

    if (uid == null) {
      Log.e(TAG, Config.FIREBASE_USER_NULL);
      this.profileChanged.postError(
              new Error(Config.AUTH_EDIT_PROFILE_PICTURE_CHANGE_FAILED), ErrorTag.REPO);
      return;
    }
    StorageReference userPhotoRef = storageReference.child(Config.STORAGE_USER
            + uid + Config.STORAGE_FILENAME);

    userPhotoRef.putFile(uri).addOnSuccessListener(unused -> userPhotoRef.getDownloadUrl()
            .addOnSuccessListener(downloadUri -> {
              String downloadUriString = downloadUri.toString();
              changePhotoUrl(downloadUriString, uid);
            }).addOnFailureListener(e -> {
              Log.e(TAG, e.getMessage());
              profileChanged.postError(
                      new Error(Config.AUTH_EDIT_PROFILE_PICTURE_CHANGE_FAILED), ErrorTag.REPO);
            })).addOnFailureListener(e -> {
              Log.e(TAG, e.getMessage());
              profileChanged.postError(
                      new Error(Config.AUTH_EDIT_PROFILE_PICTURE_CHANGE_FAILED), ErrorTag.REPO);
            });
  }

  /**
   * Change the url of the profile picture.
   *
   * @param uri profile picture
   * @param uid userId
   */
  private void changePhotoUrl(String uri, String uid) {
    this.databaseReference.child(Config.CHILD_USER)
            .child(uid)
            .child(Config.CHILD_PHOTO_URL)
            .setValue(uri).addOnSuccessListener(unused -> {
              profileChanged.postUpdate(Boolean.TRUE);
              profileChanged.postCreate(Boolean.FALSE);
            }).addOnFailureListener(e ->
            profileChanged.postError(
                    new Error(Config.AUTH_EDIT_PROFILE_PICTURE_CHANGE_FAILED), ErrorTag.REPO));
  }

}
