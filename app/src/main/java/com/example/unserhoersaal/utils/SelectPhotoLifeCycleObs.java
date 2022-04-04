package com.example.unserhoersaal.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;

/** JavaDoc. */
public class SelectPhotoLifeCycleObs implements DefaultLifecycleObserver {

  private static final String TAG = "SelectPhotoLifeCycleObs";

  private final ActivityResultRegistry registry;
  private final Context context;
  private ActivityResultLauncher<String> activityResultLauncher;
  private final ProfileViewModel profileViewModel;

  /** JavaDoc. */
  public SelectPhotoLifeCycleObs(@NonNull ActivityResultRegistry registry,
                                 Context context, ProfileViewModel profileViewModel) {
    this.registry = registry;
    this.context = context;
    this.profileViewModel = profileViewModel;
  }

  /** JavaDoc. */
  public void onCreate(@NonNull LifecycleOwner owner) {
    this.activityResultLauncher = this.registry.register("key", owner,
            new ActivityResultContracts.GetContent(),
            uri -> {
              if (uri == null) {
                Toast.makeText(this.context, Config.NO_PICUTRE_SELECTED, Toast.LENGTH_SHORT).show();
              } else {
                this.profileViewModel.uploadImageToFireStore(uri);
              }
            });
  }

  public void selectImage() {
    // Open the activity to select an image
    this.activityResultLauncher.launch("image/*");
  }
}

