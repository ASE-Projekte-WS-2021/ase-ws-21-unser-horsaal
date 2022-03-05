package com.example.unserhoersaal.views;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentEditProfileInstitutionBinding;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;

/**Edit Institution.*/
public class EditProfileInstitutionFragment extends Fragment {

  private ProfileViewModel profileViewModel;
  private NavController navController;
  private FragmentEditProfileInstitutionBinding binding;

  public EditProfileInstitutionFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_edit_profile_institution, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);

    this.initViewModel();
    this.connectBinding();
    this.initToolbar();
  }

  private void initViewModel() {
    this.profileViewModel
            = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    this.profileViewModel.init();
    //TODO: @Julian onDisplayNameChanged -> navigate back to profile
    this.profileViewModel.profileChanged.observe(getViewLifecycleOwner(), change -> {
      if (change) {
        navController.navigate(R.id.action_editProfileInstitutionFragment_to_profileFragment);
      }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.profileViewModel);
  }

  private void initToolbar() {
    this.binding.editProfileInstitutionFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.editProfileInstitutionFragmentToolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_editProfileInstitutionFragment_to_profileFragment);
    });
  }

  @Override
  public void onPause() {
    super.onPause();
    this.profileViewModel.resetProfileInput();
  }
}