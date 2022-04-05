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
import com.example.unserhoersaal.databinding.FragmentEditProfilePasswordBinding;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;

/**Edit Password.*/
public class EditProfilePasswordFragment extends Fragment {

  private ProfileViewModel profileViewModel;
  private NavController navController;
  private FragmentEditProfilePasswordBinding binding;

  public EditProfilePasswordFragment() {
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
            R.layout.fragment_edit_profile_password, container, false);
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
    this.profileViewModel.profileChanged.observe(getViewLifecycleOwner(),
            this::profileChangedCallback);
  }

  private void profileChangedCallback(StateData<Boolean> booleanStateData) {
    this.resetBindings();
    KeyboardUtil.hideKeyboard(getActivity());

    if (booleanStateData.getStatus() == StateData.DataStatus.UPDATE) {
      this.navController.navigate(R.id.action_editProfilePasswordFragment_to_profileFragment);
    } else if (booleanStateData.getStatus() == StateData.DataStatus.ERROR) {
      if (booleanStateData.getErrorTag() == ErrorTag.CURRENT_PASSWORD) {
        this.binding.editProfilePasswordCurrentPasswordErrorText
                .setText(booleanStateData.getError().getMessage());
        this.binding.editProfilePasswordCurrentPasswordErrorText.setVisibility(View.VISIBLE);
      } else if (booleanStateData.getErrorTag() == ErrorTag.NEW_PASSWORD) {
        this.binding.editProfilePasswordNewPasswordErrorText
                .setText(booleanStateData.getError().getMessage());
        this.binding.editProfilePasswordNewPasswordErrorText.setVisibility(View.VISIBLE);
      } else {
        this.binding.editProfilePasswordGeneralErrorText
                .setText(booleanStateData.getError().getMessage());
        this.binding.editProfilePasswordGeneralErrorText.setVisibility(View.VISIBLE);
      }
    } else if (booleanStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.editProfilePasswordSaveSpinner.setVisibility(View.VISIBLE);
    }
  }

  private void resetBindings() {
    this.binding.editProfilePasswordCurrentPasswordErrorText.setVisibility(View.GONE);
    this.binding.editProfilePasswordNewPasswordErrorText.setVisibility(View.GONE);
    this.binding.editProfilePasswordSaveSpinner.setVisibility(View.GONE);
    this.binding.editProfilePasswordGeneralErrorText.setVisibility(View.GONE);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.profileViewModel);
  }

  private void initToolbar() {
    this.binding.editProfilePasswordFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.editProfilePasswordFragmentToolbar.setNavigationOnClickListener(v ->
            navController.navigateUp());
  }

  @Override
  public void onPause() {
    super.onPause();
    this.profileViewModel.resetPasswordInput();
  }

}