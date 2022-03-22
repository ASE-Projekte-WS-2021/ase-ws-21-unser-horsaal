package com.example.unserhoersaal.utils;

import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.unserhoersaal.viewmodel.ProfileViewModel;

public class SelectPhotoLifeCycleObs implements DefaultLifecycleObserver {
  private final ActivityResultRegistry mRegistry;
  private ActivityResultLauncher<String> mGetContent;
  private ProfileViewModel profileViewModel;

  public SelectPhotoLifeCycleObs(@NonNull ActivityResultRegistry registry, ProfileViewModel profileViewModel) {
    mRegistry = registry;
    this.profileViewModel = profileViewModel;
  }

  public void onCreate(@NonNull LifecycleOwner owner) {


    mGetContent = mRegistry.register("key", owner, new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
              @Override
              public void onActivityResult(Uri uri) {
                //profileViewModel.loadImageUp
              }
            });
  }

  public void selectImage() {
    // Open the activity to select an image
    mGetContent.launch("image/*");
  }
}

