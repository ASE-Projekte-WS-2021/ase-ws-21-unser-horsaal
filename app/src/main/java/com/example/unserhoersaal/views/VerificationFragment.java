package com.example.unserhoersaal.views;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentVerificationBinding;
import com.example.unserhoersaal.enums.DeepLinkEnum;
import com.example.unserhoersaal.utils.DeepLinkMode;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unserhoersaal.viewmodel.LiveChatViewModel;
import com.example.unserhoersaal.viewmodel.LoginViewModel;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseUser;

/** TODO. */
public class VerificationFragment extends Fragment {

  private static final String TAG = "VerificationFragment";

  private FragmentVerificationBinding binding;
  private NavController navController;
  private LoginViewModel loginViewModel;
  private CoursesViewModel coursesViewModel;
  private ProfileViewModel profileViewModel;
  private CurrentCourseViewModel currentCourseViewModel;
  private CourseHistoryViewModel courseHistoryViewModel;
  private LiveChatViewModel liveChatViewModel;
  private Handler handler;
  private Runnable runnable;
  private DeepLinkMode deepLinkMode;

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
    this.deepLinkMode = DeepLinkMode.getInstance();

    this.initViewModel();
    this.connectBinding();
    this.emailVerifiedChecker();
  }

  /** checks if the user has verified his email so the live data can update and navigate the user
   * into the application. */
  private void emailVerifiedChecker() {
    this.handler = new Handler();
    this.runnable = () -> {
      loginViewModel.isUserEmailVerified();
      handler.postDelayed(this.runnable, Config.VERIFICATION_EMAIL_VERIFIED_CHECK_INTERVAL);
    };
    this.handler.postDelayed(this.runnable, Config.VERIFICATION_EMAIL_VERIFIED_CHECK_INTERVAL);
  }

  private void initViewModel() {
    this.loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    this.coursesViewModel = new ViewModelProvider(requireActivity()).get(CoursesViewModel.class);
    this.profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.liveChatViewModel = new ViewModelProvider(requireActivity())
            .get(LiveChatViewModel.class);
    this.loginViewModel.init();
    this.coursesViewModel.init();
    this.profileViewModel.init();
    this.courseHistoryViewModel.init();
    this.currentCourseViewModel.init();
    this.liveChatViewModel.init();
    this.loginViewModel.getEmailSentLiveData().observe(getViewLifecycleOwner(),
            this::emailSentCallback);
    this.loginViewModel.getUserLiveData()
            .observe(getViewLifecycleOwner(), this::userLiveStateCallback);
  }

  private void emailSentCallback(StateData<Boolean> booleanStateData) {
    this.resetBindings();

    if (booleanStateData == null) {
      Log.e(TAG, "FirebaseUser object is null");
      this.navController.navigate(R.id.action_verificationFragment_to_loginFragment);
    } else if (booleanStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.verificationFragmentSpinner.setVisibility(View.VISIBLE);
      this.binding.verificationFragmentResendEmailButton.setEnabled(false);
    } else if (booleanStateData.getStatus() == StateData.DataStatus.ERROR) {
      this.binding.verificationFragmentErrorText
              .setText(booleanStateData.getError().getMessage());
      this.binding.verificationFragmentErrorText.setVisibility(View.VISIBLE);
    } else if (booleanStateData.getStatus() == StateData.DataStatus.UPDATE) {
      Toast.makeText(getContext(), Config.AUTH_VERIFICATION_EMAIL_SENT, Toast.LENGTH_SHORT).show();
    }
  }

  private void userLiveStateCallback(StateData<FirebaseUser> firebaseUserStateData) {
    this.resetBindings();

    if (firebaseUserStateData == null) {
      Log.e(TAG, "FirebaseUser object is null");
      this.navController.navigate(R.id.action_verificationFragment_to_loginFragment);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.verificationFragmentSpinner.setVisibility(View.VISIBLE);
      this.binding.verificationFragmentResendEmailButton.setEnabled(false);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.ERROR) {
      this.binding.verificationFragmentErrorText
              .setText(firebaseUserStateData.getError().getMessage());
      this.binding.verificationFragmentErrorText.setVisibility(View.VISIBLE);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.UPDATE) {
      FirebaseUser firebaseUser = firebaseUserStateData.getData();

      if (firebaseUser == null) {
        navController.navigate(R.id.action_verificationFragment_to_loginFragment);
      } else if (firebaseUser.isEmailVerified()
              && this.deepLinkMode.getDeepLinkMode() == DeepLinkEnum.ENTER_COURSE) {
        this.coursesViewModel.setUserId(firebaseUser.getUid());
        this.profileViewModel.setUserId();
        this.courseHistoryViewModel.setUserId();
        this.currentCourseViewModel.setUserId();
        this.liveChatViewModel.setUserId();
        this.navController.navigate(R.id.action_verificationFragment_to_enterCourseFragment);
      } else if (firebaseUser.isEmailVerified()
              && this.deepLinkMode.getDeepLinkMode() == DeepLinkEnum.DEFAULT) {
        this.coursesViewModel.setUserId(firebaseUser.getUid());
        this.profileViewModel.setUserId();
        this.courseHistoryViewModel.setUserId();
        this.currentCourseViewModel.setUserId();
        this.liveChatViewModel.setUserId();
        this.navController.navigate(R.id.action_verificationFragment_to_coursesFragment);
      }
    }
  }

  private void resetBindings() {
    this.binding.verificationFragmentErrorText.setVisibility(View.GONE);
    this.binding.verificationFragmentSpinner.setVisibility(View.GONE);
    this.binding.verificationFragmentResendEmailButton.setEnabled(true);
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
