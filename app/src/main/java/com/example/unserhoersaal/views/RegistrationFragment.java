package com.example.unserhoersaal.views;

import android.graphics.Color;
import android.os.Bundle;
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
import com.example.unserhoersaal.databinding.FragmentRegistrationBinding;
import com.example.unserhoersaal.enums.DeepLinkEnum;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.utils.DeepLinkMode;
import com.example.unserhoersaal.utils.KeyboardUtil;
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
            .getUserStateLiveData().observe(getViewLifecycleOwner(), this::userLiveStateCallback);
  }

  private void userLiveStateCallback(StateData<FirebaseUser> firebaseUserStateData) {
    this.resetBindings();
    KeyboardUtil.hideKeyboard(getActivity());

    if (firebaseUserStateData == null) { //-> move to other method
      Log.e(TAG, "FirebaseUserStateData is null");
      this.binding.registrationFragmentGeneralErrorText.setText(Config.UNSPECIFIC_ERROR);
      this.binding.registrationFragmentGeneralErrorText.setVisibility(View.VISIBLE);
      return;
    }

    if (firebaseUserStateData.getStatus() == StateData.DataStatus.UPDATE
            && firebaseUserStateData.getData() != null) {
      navController.navigate(R.id.action_registrationFragment_to_verificationFragment);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.registrationFragmentProgressSpinner.setVisibility(View.VISIBLE);
      this.binding.registrationFragmentButton.setEnabled(false);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.ERROR) {
      if (firebaseUserStateData.getErrorTag() == ErrorTag.EMAIL) {
        this.binding.registrationFragmentUserEmailErrorText
                .setText(firebaseUserStateData.getError().getMessage());
        this.binding.registrationFragmentUserEmailErrorText.setVisibility(View.VISIBLE);
      } else if (firebaseUserStateData.getErrorTag() == ErrorTag.USERNAME) {
        this.binding.registrationFragmentUserErrorText
                .setText(firebaseUserStateData.getError().getMessage());
        this.binding.registrationFragmentUserErrorText.setVisibility(View.VISIBLE);
      } else if (firebaseUserStateData.getErrorTag() == ErrorTag.CURRENT_PASSWORD) {
        this.binding.registrationFragmentPasswordErrorText
                .setText(firebaseUserStateData.getError().getMessage());
        this.binding.registrationFragmentPasswordErrorText.setVisibility(View.VISIBLE);
      } else {
        this.binding.registrationFragmentGeneralErrorText
                .setText(firebaseUserStateData.getError().getMessage());
        this.binding.registrationFragmentGeneralErrorText.setVisibility(View.VISIBLE);
      }
    }

  }

  private void resetBindings() {
    this.binding.registrationFragmentGeneralErrorText.setVisibility(View.GONE);
    this.binding.registrationFragmentUserEmailErrorText.setVisibility(View.GONE);
    this.binding.registrationFragmentUserErrorText.setVisibility(View.GONE);
    this.binding.registrationFragmentPasswordErrorText.setVisibility(View.GONE);
    this.binding.registrationFragmentProgressSpinner.setVisibility(View.GONE);
    this.binding.registrationFragmentButton.setEnabled(true);
  }


  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.registrationViewModel);
  }

  @Override
  public void onResume() {
    super.onResume();
    this.resetBindings();
  }
}