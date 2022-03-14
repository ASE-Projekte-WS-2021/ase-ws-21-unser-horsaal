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
import com.example.unserhoersaal.databinding.FragmentRegistrationBinding;
import com.example.unserhoersaal.enums.DeepLinkEnum;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.utils.DeepLinkMode;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.RegistrationViewModel;
import com.google.firebase.auth.FirebaseUser;

/**
 * Initiates the UI of the registration area, the registration function
 * and the navigation to the course page.
 */
public class RegistrationFragment extends Fragment {

  private static final String TAG = "RegistrationFragment";

  private FragmentRegistrationBinding binding;
  private RegistrationViewModel registrationViewModel;
  private NavController navController;
  private DeepLinkMode deepLinkMode;

  public RegistrationFragment() {
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
            R.layout.fragment_registration, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);
    this.deepLinkMode = DeepLinkMode.getInstance();

    this.initViewModel();
    this.connectBinding();
  }

  private void initViewModel() {
    this.registrationViewModel = new ViewModelProvider(requireActivity())
            .get(RegistrationViewModel.class);
    this.registrationViewModel.init();
    this.registrationViewModel
            .getUserStateLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
              if (firebaseUser.getStatus() == StateData.DataStatus.UPDATE) {
                if (firebaseUser.getData() == null) {
                  return;
                }
                this.binding.loginFragmentProgressSpinner.setVisibility(View.GONE);
                this.binding.registrationFragmentGeneralErrorText.setVisibility(View.GONE);
                FirebaseUser firebaseUser1 = firebaseUser.getData();

                if (firebaseUser1.isEmailVerified()
                        && deepLinkMode.getDeepLinkMode() == DeepLinkEnum.ENTER_COURSE) {
                  navController.navigate(R.id.action_registrationFragment_to_enterCourseFragment);
                } else if (firebaseUser1.isEmailVerified()) {
                  navController.navigate(R.id.action_registrationFragment_to_coursesFragment);
                } else if (!firebaseUser1.isEmailVerified()) {
                  navController.navigate(R.id.action_registrationFragment_to_verificationFragment);
                }
              }
              else if (firebaseUser.getStatus() == StateData.DataStatus.LOADING) {
                this.binding.loginFragmentProgressSpinner.setVisibility(View.VISIBLE);
                this.binding.registrationFragmentGeneralErrorText.setVisibility(View.GONE);
              }
              else if (firebaseUser.getStatus() == StateData.DataStatus.ERROR) {
                this.binding.loginFragmentProgressSpinner.setVisibility(View.GONE);
                this.binding.registrationFragmentGeneralErrorText.setText(firebaseUser.getError().getMessage());
                this.binding.registrationFragmentGeneralErrorText.setVisibility(View.VISIBLE);
              }
            });
    this.registrationViewModel
            .getUserInputState()
            .observe(getViewLifecycleOwner(), userModelStateData -> {
              if (userModelStateData.getStatus() == StateData.DataStatus.ERROR) {
                if (userModelStateData.getErrorTag() == ErrorTag.EMAIL) {
                  this.binding.registrationFragmentUserErrorText
                          .setText(userModelStateData.getError().getMessage());
                  this.binding.registrationFragmentUserErrorText
                          .setVisibility(View.VISIBLE);
                } else if (userModelStateData.getErrorTag() == ErrorTag.USERNAME) {
                  this.binding.registrationFragmentUserEmailErrorText
                          .setText(userModelStateData.getError().getMessage());
                  this.binding.registrationFragmentUserEmailErrorText
                          .setVisibility(View.VISIBLE);
                }

              }/* else if (userModelStateData.getStatus() == StateData.DataStatus.COMPLETE) {
                this.binding.registrationFragmentUserErrorText
                        .setVisibility(View.GONE);
              }*/
            });
    this.registrationViewModel
            .getPasswordInputState()
            .observe(getViewLifecycleOwner(), passwordModelStateData -> {
              if (passwordModelStateData.getStatus() == StateData.DataStatus.ERROR) {
                this.binding.registrationFragmentPasswordErrorText
                        .setText(passwordModelStateData.getError().getMessage());
                this.binding.registrationFragmentPasswordErrorText
                        .setVisibility(View.VISIBLE);
              }/*
              else if (passwordModelStateData.getStatus() == StateData.DataStatus.COMPLETE) {
                this.binding.registrationFragmentPasswordErrorText
                        .setVisibility(View.GONE);
              }*/
            });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.registrationViewModel);
  }

}