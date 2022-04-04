package com.example.unserhoersaal.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.viewpager2.widget.ViewPager2;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.OnboardingPagerAdapter;
import com.example.unserhoersaal.databinding.FragmentOnboardingWrapperBinding;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.RegistrationViewModel;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseUser;

/** Used in OnboardingWrapperFragment in ViewPager. */
public class OnBoardingWrapperFragment extends Fragment {

  private static final String TAG = "OnBoardingWrapperFragment";

  private FragmentOnboardingWrapperBinding binding;
  private OnboardingPagerAdapter adapter;
  private RegistrationViewModel registrationViewModel;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_onboarding_wrapper, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);

    this.initViewModel();
    this.connectAdapter();
    this.connectBinding();
    this.connectViewPager();
  }

  private void initViewModel() {
    this.registrationViewModel = new ViewModelProvider(requireActivity())
            .get(RegistrationViewModel.class);
    this.registrationViewModel.init();
    this.registrationViewModel
            .getUserStateLiveData().observe(getViewLifecycleOwner(), this::userLiveStateCallback);
  }

  private void userLiveStateCallback(StateData<FirebaseUser> firebaseUserStateData) {
    this.resetBindings();
    KeyboardUtil.hideKeyboard(getActivity());

    if (firebaseUserStateData == null) { //-> move to other method
      Log.e(TAG, "FirebaseUserStateData is null");
      Toast.makeText(getContext(), Config.UNSPECIFIC_ERROR, Toast.LENGTH_SHORT).show();
      return;
    }

    if (firebaseUserStateData.getStatus() == StateData.DataStatus.UPDATE
            && firebaseUserStateData.getData() != null) {
      this.finishOnboarding();
      this.navController.navigate(R.id.action_onboardingFragment_to_verificationFragment);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.onboardingFragmentFinishProgressSpinner.setVisibility(View.VISIBLE);
      this.binding.onboardingFragmentFinishButton.setEnabled(false);
      this.binding.onboardingFragmentFinishButton.setBackgroundColor(Color.GRAY);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.ERROR) {
      if (firebaseUserStateData.getErrorTag() == ErrorTag.USERNAME) {
        this.binding.onboardingFragmentViewPager
                .setCurrentItem(Config.ONBOARDING_USERNAME_FRAGMENT_POSITION);
      } else if (firebaseUserStateData.getErrorTag() == ErrorTag.EMAIL
              || firebaseUserStateData.getErrorTag() == ErrorTag.CURRENT_PASSWORD) {
        this.binding.onboardingFragmentViewPager
                .setCurrentItem(Config.ONBOARDING_ACCOUNT_FRAGMENT_POSITION);
      } else {
        Toast.makeText(getContext(), Config.AUTH_REGISTRATION_FAILED, Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void resetBindings() {
    this.binding.onboardingFragmentFinishProgressSpinner.setVisibility(View.GONE);
    this.binding.onboardingFragmentFinishButton.setEnabled(true);
    this.binding.onboardingFragmentFinishButton.setTextAppearance(R.style.wideBlueButton);
  }

  private void connectAdapter() {
    this.adapter = new OnboardingPagerAdapter(this);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.onboardingFragmentViewPager.setAdapter(this.adapter);
    this.binding.setVm(this.registrationViewModel);
  }

  private void connectViewPager() {
    new TabLayoutMediator(
            this.binding.onboardingFragmentTabLayout,
            this.binding.onboardingFragmentViewPager,
            (tab, position) -> {}).attach();
    this.binding.onboardingFragmentViewPager.registerOnPageChangeCallback(this.viewPageCallback());
    this.binding.onboardingFragmentNextButton.setOnClickListener(click -> {
      if (this.binding.onboardingFragmentViewPager.getCurrentItem()
              < Config.ONBOARDING_FRAGMENT_TAB_LENGTH - 1) {
        this.binding.onboardingFragmentViewPager.setCurrentItem(
                this.binding.onboardingFragmentViewPager.getCurrentItem() + 1);
      }
      if (this.binding.onboardingFragmentViewPager.getCurrentItem()
              == Config.ONBOARDING_FRAGMENT_TAB_LENGTH - 1) {
        this.binding.onboardingFragmentNextButton.setVisibility(View.GONE);
        binding.onboardingFragmentFinishButton.setVisibility(View.VISIBLE);
      } else {
        this.binding.onboardingFragmentFinishButton.setVisibility(View.VISIBLE);
        binding.onboardingFragmentFinishButton.setVisibility(View.GONE);
      }
    });
  }

  private ViewPager2.OnPageChangeCallback viewPageCallback() {
    return new ViewPager2.OnPageChangeCallback() {
      @Override
      public void onPageSelected(int position) {
        super.onPageSelected(position);
        if (position == Config.ONBOARDING_FRAGMENT_TAB_LENGTH - 1) {
          binding.onboardingFragmentFinishButton.setVisibility(View.VISIBLE);
          binding.onboardingFragmentNextButton.setVisibility(View.GONE);
        } else {
          binding.onboardingFragmentFinishButton.setVisibility(View.GONE);
          binding.onboardingFragmentNextButton.setVisibility(View.VISIBLE);
        }
      }
    };
  }

  private void finishOnboarding() {
    SharedPreferences sharedPreferences = getActivity()
            .getSharedPreferences(Config.SHARED_PREF_KEY, Context.MODE_PRIVATE);
    sharedPreferences.edit().putBoolean(Config.SHARED_PREF_ONBOARDING_KEY, true).apply();
  }

  @Override
  public void onPause() {
    super.onPause();
    this.binding.onboardingFragmentViewPager
            .unregisterOnPageChangeCallback(this.viewPageCallback());
    this.registrationViewModel.setDefaultInputState();
  }
}
