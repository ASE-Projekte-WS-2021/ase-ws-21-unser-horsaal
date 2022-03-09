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
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentLoginBinding;
import com.example.unserhoersaal.enums.DeepLinkEnum;
import com.example.unserhoersaal.utils.DeepLinkMode;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.LoginViewModel;
import com.google.firebase.auth.FirebaseUser;

/**
 * initiates the UI of the login area, the login function, password reset
 * and the navigation to the course page.
 */
public class LoginFragment extends Fragment {

  private static final String TAG = "LoginFragment";

  private LoginViewModel loginViewModel;
  private NavController navController;
  private FragmentLoginBinding binding;
  private DeepLinkMode deepLinkMode;

  public LoginFragment() {
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
            R.layout.fragment_login, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);

    this.initDeepLinkMode();
    this.initViewModel();
    this.connectBinding();
  }

  private void initDeepLinkMode() {
    this.deepLinkMode = DeepLinkMode.getInstance();

    if (getArguments() != null && getArguments()
            .getString(Config.CODE_MAPPING_DEEP_LINK_KEY) != null) {
      String key = getArguments().getString(Config.CODE_MAPPING_DEEP_LINK_KEY);
      this.deepLinkMode.setCodeMapping(key);
      this.deepLinkMode.setDeepLinkMode(DeepLinkEnum.ENTER_COURSE);
    }
  }

  private void initViewModel() {
    this.loginViewModel = new ViewModelProvider(requireActivity())
            .get(LoginViewModel.class);
    this.loginViewModel.init();
    this.loginViewModel
            .getUserLiveData()
            .observe(getViewLifecycleOwner(), firebaseUser -> {
              if (firebaseUser.getStatus() == StateData.DataStatus.SUCCESS) {
                if (firebaseUser.getData() == null) {
                  return;
                }
                this.binding.loginFragmentProgressSpinner.setVisibility(View.GONE);
                this.binding.loginFragmentGeneralErrorMessage.setVisibility(View.GONE);
                FirebaseUser firebaseUser1 = firebaseUser.getData();

                if (firebaseUser1.isEmailVerified()
                        && deepLinkMode.getDeepLinkMode() == DeepLinkEnum.ENTER_COURSE) {
                  navController.navigate(R.id.action_loginFragment_to_enterCourseFragment);
                } else if (firebaseUser1.isEmailVerified()) {
                  navController.navigate(R.id.action_loginFragment_to_coursesFragment);
                }
                else if (!firebaseUser1.isEmailVerified()) {
                  navController.navigate(R.id.action_loginFragment_to_verificationFragment);
                }
              } else if (firebaseUser.getStatus() == StateData.DataStatus.LOADING) {
                this.binding.loginFragmentProgressSpinner.setVisibility(View.VISIBLE);
                this.binding.loginFragmentGeneralErrorMessage.setVisibility(View.GONE);
              } else if (firebaseUser.getStatus() == StateData.DataStatus.ERROR) {
                this.binding.loginFragmentProgressSpinner.setVisibility(View.GONE);
                this.binding.loginFragmentGeneralErrorMessage.setText(firebaseUser.getError().getMessage());
                this.binding.loginFragmentGeneralErrorMessage.setVisibility(View.VISIBLE);
              }
            });
    this.loginViewModel
            .getUserInputState()
            .observe(getViewLifecycleOwner(), userModelStateData -> {
              if (userModelStateData.getStatus() == StateData.DataStatus.ERROR) {
                this.binding.loginFragmentUserEmailErrorText
                        .setText(userModelStateData.getError().getMessage());
                this.binding.loginFragmentUserEmailErrorText
                        .setVisibility(View.VISIBLE);
              } else if (userModelStateData.getStatus() == StateData.DataStatus.COMPLETE) {
                this.binding.loginFragmentUserEmailErrorText
                        .setVisibility(View.GONE);
              }
            });
    this.loginViewModel
            .getPasswordInputState()
            .observe(getViewLifecycleOwner(), passwordModelStateData -> {
              if (passwordModelStateData.getStatus() == StateData.DataStatus.ERROR) {
                this.binding.loginFragmentPasswordErrorText
                        .setText(passwordModelStateData.getError().getMessage());
                this.binding.loginFragmentPasswordErrorText
                        .setVisibility(View.VISIBLE);
              }
              else if (passwordModelStateData.getStatus() == StateData.DataStatus.COMPLETE) {
                this.binding.loginFragmentPasswordErrorText
                        .setVisibility(View.GONE);
              }
            });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.loginViewModel);
  }

}