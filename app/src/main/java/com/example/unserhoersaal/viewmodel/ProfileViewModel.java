package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.repository.ProfileRepository;

public class ProfileViewModel extends ViewModel {

  private static final String TAG = "ProfileViewModel";

  private ProfileRepository profileRepository;

  private MutableLiveData<String> name;
  private MutableLiveData<String> institution;
  private MutableLiveData<String> mail;

  public void init() {
    if (this.mail != null) {
      return;
    }
    this.profileRepository = ProfileRepository.getInstance();
    this.name = this.profileRepository.getName();
    this.institution = this.profileRepository.getInstitution();
    this.mail = this.profileRepository.getMail();
  }

  public LiveData<String> getName() {
    return this.name;
  }

  public LiveData<String> getInstitution() {
    return this.institution;
  }

  public LiveData<String> getMail() {
    return this.mail;
  }
}
