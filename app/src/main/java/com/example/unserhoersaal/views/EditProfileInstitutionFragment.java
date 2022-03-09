package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentEditProfileInstitutionBinding;
import com.example.unserhoersaal.utils.StateData;
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
    this.profileViewModel.profileChanged.observe(getViewLifecycleOwner(), change -> {
      if (change.getStatus() == StateData.DataStatus.SUCCESS) {
        this.binding.loginFragmentProgressSpinner.setVisibility(View.GONE);
        navController.navigate(R.id.action_editProfileInstitutionFragment_to_profileFragment);
      } else if (change.getStatus() == StateData.DataStatus.ERROR) {
        this.binding.loginFragmentProgressSpinner.setVisibility(View.GONE);
        Toast.makeText(getContext(),
                change.getError().getMessage(),
                Toast.LENGTH_LONG)
                .show();
      }
      else if (change.getStatus() == StateData.DataStatus.LOADING) {
        this.binding.loginFragmentProgressSpinner.setVisibility(View.VISIBLE);
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