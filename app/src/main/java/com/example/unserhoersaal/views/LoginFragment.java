package com.example.unserhoersaal.views;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
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
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
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
  private CoursesViewModel coursesViewModel;
  private NavController navController;
  private FragmentLoginBinding binding;
  private DeepLinkMode deepLinkMode;

  public LoginFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        // do nothing
      }
    };
    requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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
            .observe(getViewLifecycleOwner(), this::userLiveDataCallback);
  }

  private void userLiveDataCallback(StateData<FirebaseUser> firebaseUserStateData) {
    this.resetBindings();
    KeyboardUtil.hideKeyboard(getActivity());

    if (firebaseUserStateData == null) { //-> move to other method
      Log.e(TAG, "FirebaseUser object is null");
      this.binding.loginFragmentGeneralErrorMessage.setText(Config.UNSPECIFIC_ERROR);
      this.binding.loginFragmentGeneralErrorMessage.setVisibility(View.VISIBLE);
      return;
    }
    FirebaseUser firebaseUser = firebaseUserStateData.getData();

    if (firebaseUser != null && (firebaseUserStateData.getStatus() == StateData.DataStatus.CREATED
            || firebaseUserStateData.getStatus() == StateData.DataStatus.UPDATE)) {
      if (firebaseUser.isEmailVerified()
              && deepLinkMode.getDeepLinkMode() == DeepLinkEnum.ENTER_COURSE) {
        navController.navigate(R.id.action_loginFragment_to_enterCourseFragment);
      } else if (firebaseUser.isEmailVerified()) {
        navController.navigate(R.id.action_loginFragment_to_coursesFragment);
      } else if (!firebaseUser.isEmailVerified()) {
        navController.navigate(R.id.action_loginFragment_to_verificationFragment);
      }
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.loginFragmentProgressSpinner.setVisibility(View.VISIBLE);
      this.binding.loginFragmentLoginButton.setEnabled(false);
      this.binding.loginFragmentLoginButton.setBackgroundColor(Color.GRAY);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.ERROR) {
      if (firebaseUserStateData.getErrorTag() == ErrorTag.EMAIL) {
        this.binding.loginFragmentUserEmailErrorText
                .setText(firebaseUserStateData.getError().getMessage());
        this.binding.loginFragmentUserEmailErrorText.setVisibility(View.VISIBLE);
      }
      if (firebaseUserStateData.getErrorTag() == ErrorTag.CURRENT_PASSWORD) {
        this.binding.loginFragmentPasswordErrorText
                .setText(firebaseUserStateData.getError().getMessage());
        this.binding.loginFragmentPasswordErrorText.setVisibility(View.VISIBLE);
      }
      if (firebaseUserStateData.getErrorTag() == ErrorTag.REPO
              || firebaseUserStateData.getErrorTag() == ErrorTag.VM){
        this.binding.loginFragmentGeneralErrorMessage
                .setText(firebaseUserStateData.getError().getMessage());
        this.binding.loginFragmentGeneralErrorMessage.setVisibility(View.VISIBLE);
      }
    }
  }

  private void resetBindings() {
    this.binding.loginFragmentUserEmailErrorText.setVisibility(View.GONE);
    this.binding.loginFragmentPasswordErrorText.setVisibility(View.GONE);
    this.binding.loginFragmentGeneralErrorMessage.setVisibility(View.GONE);
    this.binding.loginFragmentProgressSpinner.setVisibility(View.GONE);
    this.binding.loginFragmentLoginButton.setEnabled(true);
    this.binding.loginFragmentLoginButton.setTextAppearance(R.style.wideBlueButton);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.loginViewModel);
  }

}