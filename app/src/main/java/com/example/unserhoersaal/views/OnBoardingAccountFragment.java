package com.example.unserhoersaal.views;

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

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentOnboardingAccountBinding;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.RegistrationViewModel;
import com.google.firebase.auth.FirebaseUser;

public class OnBoardingAccountFragment extends Fragment {

  private static final String TAG = "OnBoardingAccountFragment";

  private FragmentOnboardingAccountBinding binding;
  private RegistrationViewModel registrationViewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_onboarding_account, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();
    this.connectBinding();
  }

  private void initViewModel() {
    this.registrationViewModel = new ViewModelProvider(requireActivity())
            .get(RegistrationViewModel.class);
    this.registrationViewModel.init();
    this.registrationViewModel.getUserStateLiveData().observe(getViewLifecycleOwner(), this::userLiveStateCallback);
  }

  private void userLiveStateCallback(StateData<FirebaseUser> firebaseUserStateData) {
    this.resetBindings();
    KeyboardUtil.hideKeyboard(getActivity());

    if (firebaseUserStateData == null) { //-> move to other method
      Log.e(TAG, "FirebaseUserStateData is null");
      Toast.makeText(getContext(), Config.UNSPECIFIC_ERROR, Toast.LENGTH_SHORT).show();
      return;
    }

    if (firebaseUserStateData.getStatus() == StateData.DataStatus.ERROR) {
      if (firebaseUserStateData.getErrorTag() == ErrorTag.EMAIL) {
        this.binding.onboardingAccountFragmentUserErrorText
                .setText(firebaseUserStateData.getError().getMessage());
        this.binding.onboardingAccountFragmentUserErrorText.setVisibility(View.VISIBLE);
      } else if (firebaseUserStateData.getErrorTag() == ErrorTag.CURRENT_PASSWORD) {
        this.binding.onboardingAccountFragmentPasswordErrorText
                .setText(firebaseUserStateData.getError().getMessage());
        this.binding.onboardingAccountFragmentPasswordErrorText.setVisibility(View.VISIBLE);
      }
    }
  }

  private void resetBindings() {
    this.binding.onboardingAccountFragmentUserErrorText.setVisibility(View.GONE);
    this.binding.onboardingAccountFragmentPasswordErrorText.setVisibility(View.GONE);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.registrationViewModel);
  }
}
