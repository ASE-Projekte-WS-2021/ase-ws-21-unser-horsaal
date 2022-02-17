package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.ProfileRepository;

public class ProfileViewModel extends ViewModel {

  private static final String TAG = "ProfileViewModel";

  private ProfileRepository profileRepository;

  private MutableLiveData<UserModel> user;

  public void init() {
    if (this.user != null) {
      return;
    }
    this.profileRepository = ProfileRepository.getInstance();
    this.user = this.profileRepository.getUser();
  }

  public LiveData<UserModel> getUser() {
    return this.user;
  }
}
