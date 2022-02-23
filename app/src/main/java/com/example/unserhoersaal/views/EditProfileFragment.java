package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentEditProfileBinding;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;
import com.example.unserhoersaal.viewmodel.RegistrationViewModel;
import com.google.android.material.appbar.MaterialToolbar;

/** Fragment for Editing a profile.*/
public class EditProfileFragment extends Fragment {

  private MaterialToolbar toolbar;
  private NavController navController;
  private FragmentEditProfileBinding binding;
  private ProfileViewModel profileViewModel;

  public EditProfileFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);
    this.toolbar = view.findViewById(R.id.editProfileFragmentToolbar);

    this.initViewModel();
    this.connectBinding();
    this.setupToolbar();
  }

  private void initViewModel() {
    this.profileViewModel = new ViewModelProvider(requireActivity())
            .get(ProfileViewModel.class);
    this.profileViewModel.init();
    /*
    this.profileViewModel
            .getProfileLiveData().observe(getViewLifecycleOwner(), updatedProfile -> {
        navController.navigate(R.id.action_editProfileFragment_to_profileFragment);
    });

     */
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.profileViewModel);
  }

  private void setupToolbar() {
    this.toolbar.inflateMenu(R.menu.edit_profile_fragment_toolbar);
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
    this.toolbar.setNavigationOnClickListener(v -> {
      this.navController.navigate(R.id.action_editProfileFragment_to_profileFragment);
    });
    this.toolbar.setOnMenuItemClickListener(item -> {
      if (item.getItemId() == R.id.editProfileToolbarSave) {
        //save changes and navigate back to profile page
        profileViewModel.saveNewProfileData();
      }
      return false;
    });
  }
}