package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.example.unserhoersaal.databinding.FragmentVerificationBinding;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.LoginViewModel;
import com.google.firebase.auth.FirebaseUser;

/** TODO. */
public class VerificationFragment extends Fragment {

  private static final String TAG = "ResetPasswordFragment";

  private FragmentVerificationBinding binding;
  private NavController navController;
  private LoginViewModel loginViewModel;
  private Handler handler;
  private Runnable runnable;

  public VerificationFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verification, container,
            false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);

    this.initViewModel();
    this.connectBinding();
    this.emailVerifiedChecker();
  }

  private void emailVerifiedChecker() {
    this.handler = new Handler();
    this.handler.postDelayed(this.runnable, Config.VERIFICATION_EMAIL_VERIFIED_CHECK_INTERVAL);
    this.runnable = () -> {
      Log.d(TAG, "checking for authstatechange");
      loginViewModel.reloadFirebaseUser();
      handler.postDelayed(this.runnable, Config.VERIFICATION_EMAIL_VERIFIED_CHECK_INTERVAL);
    };
    this.handler.post(this.runnable);
  }

  private void initViewModel() {
    this.loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    this.loginViewModel.init();
    this.loginViewModel.getUserLiveData()
            .observe(getViewLifecycleOwner(), this::userLiveStateCallback);
  }

  private void userLiveStateCallback(StateData<FirebaseUser> firebaseUserStateData) {
    this.resetBindings();

    if (firebaseUserStateData == null) {
      Log.e(TAG, "FirebaseUser object is null");
      this.binding.verificationFragmentErrorText.setText(Config.UNSPECIFIC_ERROR);
      this.binding.verificationFragmentErrorText.setVisibility(View.VISIBLE);
      return;
    }

    if (firebaseUserStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.verificationFragmentSpinner.setVisibility(View.VISIBLE);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.ERROR) {
      this.binding.verificationFragmentErrorText
              .setText(firebaseUserStateData.getError().getMessage());
      this.binding.verificationFragmentErrorText.setVisibility(View.VISIBLE);
    } else {
      FirebaseUser firebaseUser = firebaseUserStateData.getData();

      if (firebaseUser == null) {
        Log.d(TAG, "firebase user is null");
        navController.navigate(R.id.action_verificationFragment_to_loginFragment);
      } else if (firebaseUser.isEmailVerified()) {
        navController.navigate(R.id.action_verificationFragment_to_coursesFragment);
      }
    }
  }

  private void resetBindings() {
    this.binding.verificationFragmentErrorText.setVisibility(View.GONE);
    this.binding.verificationFragmentSpinner.setVisibility(View.GONE);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.loginViewModel);
  }

  @Override
  public void onPause() {
    super.onPause();
    this.loginViewModel.setDefaultInputState();
    this.handler.removeCallbacks(this.runnable);
  }
}
