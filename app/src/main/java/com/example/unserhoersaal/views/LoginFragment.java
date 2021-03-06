package com.example.unserhoersaal.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
import com.example.unserhoersaal.utils.DeepLinkMode;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unserhoersaal.viewmodel.LiveChatViewModel;
import com.example.unserhoersaal.viewmodel.LoginViewModel;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseUser;

/**
 * initiates the UI of the login area, the login function, password reset
 * and the navigation to the course page.
 */
public class LoginFragment extends Fragment {

  public static final String TAG = "LoginFragment";

  private LoginViewModel loginViewModel;
  private CoursesViewModel coursesViewModel;
  private ProfileViewModel profileViewModel;
  private CourseHistoryViewModel courseHistoryViewModel;
  private CurrentCourseViewModel currentCourseViewModel;
  private LiveChatViewModel liveChatViewModel;
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

    this.initOnboarding();
    this.initDeepLinkMode();
    this.initViewModel();
    this.connectBinding();
  }

  private void initOnboarding() {
    SharedPreferences sharedPreferences = getActivity()
            .getSharedPreferences(Config.SHARED_PREF_KEY, Context.MODE_PRIVATE);
    if (!sharedPreferences.getBoolean(Config.SHARED_PREF_ONBOARDING_KEY, false)) {
      this.navController.navigate(R.id.action_loginFragment_to_onboardingFragment);
    }
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
    this.coursesViewModel = new ViewModelProvider(requireActivity())
            .get(CoursesViewModel.class);
    this.profileViewModel = new ViewModelProvider(requireActivity())
            .get(ProfileViewModel.class);
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.liveChatViewModel = new ViewModelProvider(requireActivity())
            .get(LiveChatViewModel.class);
    this.loginViewModel.init();
    this.profileViewModel.init();
    this.courseHistoryViewModel.init();
    this.currentCourseViewModel.init();
    this.liveChatViewModel.init();
    this.loginViewModel
            .getUserLiveData()
            .observe(getViewLifecycleOwner(), this::userLiveDataCallback);
  }

  private void userLiveDataCallback(StateData<FirebaseUser> firebaseUserStateData) {
    KeyboardUtil.hideKeyboard(getActivity());

    if (firebaseUserStateData == null) {
      Toast.makeText(getContext(), Config.UNSPECIFIC_ERROR, Toast.LENGTH_SHORT).show();
      return;
    }

    FirebaseUser firebaseUser = firebaseUserStateData.getData();
    this.navigateUser(firebaseUserStateData, firebaseUser);
  }

  private void navigateUser(StateData<FirebaseUser> firebaseUserStateData,
                            FirebaseUser firebaseUser) {
    if (firebaseUser != null && (firebaseUserStateData.getStatus() == StateData.DataStatus.CREATED
            || firebaseUserStateData.getStatus() == StateData.DataStatus.UPDATE)) {
      if (firebaseUser.isEmailVerified()
              && deepLinkMode.getDeepLinkMode() == DeepLinkEnum.ENTER_COURSE) {
        this.setUserIds(firebaseUser);
        this.navController.navigate(R.id.action_loginFragment_to_enterCourseFragment);
      } else if (firebaseUser.isEmailVerified()) {
        this.setUserIds(firebaseUser);
        this.navController.navigate(R.id.action_loginFragment_to_coursesFragment);
      } else if (!firebaseUser.isEmailVerified()) {
        this.navController.navigate(R.id.action_loginFragment_to_verificationFragment);
      }
    }
  }

  private void setUserIds(FirebaseUser firebaseUser) {
    this.coursesViewModel.setUserId(firebaseUser.getUid());
    this.profileViewModel.setUserId();
    this.courseHistoryViewModel.setUserId();
    this.currentCourseViewModel.setUserId();
    this.liveChatViewModel.setUserId();
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.loginViewModel);
  }

  @Override
  public void onPause() {
    super.onPause();
    this.loginViewModel.setDefaultInputState();
    this.loginViewModel.setLiveDataComplete();
  }

}