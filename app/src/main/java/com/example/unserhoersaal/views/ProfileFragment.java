package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentProfileBinding;
import com.example.unserhoersaal.utils.SelectPhotoLifeCycleObs;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;

/** Profile page. */
public class ProfileFragment extends Fragment {

  private static final String TAG = "ProfileFragment";

  private ProfileViewModel profileViewModel;
  private NavController navController;
  private FragmentProfileBinding binding;
  private SelectPhotoLifeCycleObs selectPhotoLifeCycleObs;
  public ProfileFragment() {
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
            R.layout.fragment_profile, container, false);

    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);

    this.initViewModel();
    this.initLifeCycleObs();
    this.connectBinding();
    this.initToolbar();

  }

  private void initViewModel() {
    this.profileViewModel
            = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    this.profileViewModel.init();
    this.profileViewModel
            .getUserLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
              if (firebaseUser.getData() == null) {
                navController.navigate(R.id.action_profileFragment_to_loginFragment);
              }
            });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.profileViewModel);
    this.binding.setSelectPhotoLifeCycleObs(this.selectPhotoLifeCycleObs);
  }

  private void initToolbar() {
    this.binding.profileFragmentToolbar.inflateMenu(R.menu.profile_fragment_toolbar);
    this.binding.profileFragmentToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.profileFragmentToolbar.setNavigationOnClickListener(v ->
            navController.navigateUp());
    this.binding.profileFragmentToolbar.setOnMenuItemClickListener(item -> {
      if (item.getItemId() == R.id.profileFragmentToolbarLogout) {
        profileViewModel.logout();
      }
      return false;
    });
  }

  private void initLifeCycleObs() {
    selectPhotoLifeCycleObs = new SelectPhotoLifeCycleObs(
            requireActivity().getActivityResultRegistry(), getContext(), profileViewModel);
    getLifecycle().addObserver(selectPhotoLifeCycleObs);
  }

}