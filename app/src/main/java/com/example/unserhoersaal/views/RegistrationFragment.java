package com.example.unserhoersaal.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.unserhoersaal.model.PasswordModel;
import com.example.unserhoersaal.model.UserModel;
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

    //Todo: automatic redirect to the course page after verification.
    this.registrationViewModel
            .getUserStateLiveData().observe(getViewLifecycleOwner(), this::userLiveStateCallback);
    this.registrationViewModel
            .getUserInputState()
            .observe(getViewLifecycleOwner(), this::userInputStateCallback);
    this.registrationViewModel
            .getPasswordInputState()
            .observe(getViewLifecycleOwner(), this::passwordInputStateCallback);
  }

  private void userLiveStateCallback(StateData<FirebaseUser> firebaseUserStateData) {
    this.resetBindings();

    if (firebaseUserStateData == null) {
      Log.e(TAG, "FirebaseUser object is null");
      this.binding.registrationFragmentGeneralErrorText.setText(Config.UNSPECIFIC_ERROR);
      this.binding.registrationFragmentGeneralErrorText.setVisibility(View.VISIBLE);
      return;
    }

    if (firebaseUserStateData.getStatus() == StateData.DataStatus.UPDATE) {
      if (firebaseUserStateData.getData() == null) {
        return;
      }
      FirebaseUser firebaseUser = firebaseUserStateData.getData();

      if (firebaseUser.isEmailVerified()
              && deepLinkMode.getDeepLinkMode() == DeepLinkEnum.ENTER_COURSE) {
        navController.navigate(R.id.action_registrationFragment_to_enterCourseFragment);
      } else if (firebaseUser.isEmailVerified()) {
        navController.navigate(R.id.action_registrationFragment_to_coursesFragment);
      } else if (!firebaseUser.isEmailVerified()) {
        navController.navigate(R.id.action_registrationFragment_to_verificationFragment);
      }
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.registrationFragmentProgressSpinner.setVisibility(View.VISIBLE);
    } else if (firebaseUserStateData.getStatus() == StateData.DataStatus.ERROR) {
      this.binding.registrationFragmentGeneralErrorText
              .setText(firebaseUserStateData.getError().getMessage());
      this.binding.registrationFragmentGeneralErrorText.setVisibility(View.VISIBLE);
    }

  }

  private void userInputStateCallback(StateData<UserModel> userModelStateData) {
    this.resetBindings();

    if (userModelStateData.getStatus() == StateData.DataStatus.ERROR) {
      if (userModelStateData.getErrorTag() == ErrorTag.EMAIL) {
        this.binding.registrationFragmentUserEmailErrorText
                .setText(userModelStateData.getError().getMessage());
        this.binding.registrationFragmentUserEmailErrorText.setVisibility(View.VISIBLE);
      } else if (userModelStateData.getErrorTag() == ErrorTag.USERNAME) {
        this.binding.registrationFragmentUserErrorText
                .setText(userModelStateData.getError().getMessage());
        this.binding.registrationFragmentUserErrorText.setVisibility(View.VISIBLE);
      }

    }
  }

  private void passwordInputStateCallback(StateData<PasswordModel> passwordModelStateData) {
    this.resetBindings();

    if (passwordModelStateData.getStatus() == StateData.DataStatus.ERROR) {
      this.binding.registrationFragmentPasswordErrorText
              .setText(passwordModelStateData.getError().getMessage());
      this.binding.registrationFragmentPasswordErrorText.setVisibility(View.VISIBLE);
    }
  }

  private void resetBindings() {
    this.binding.registrationFragmentGeneralErrorText.setVisibility(View.GONE);
    this.binding.registrationFragmentUserEmailErrorText.setVisibility(View.GONE);
    this.binding.registrationFragmentUserErrorText.setVisibility(View.GONE);
    this.binding.registrationFragmentPasswordErrorText.setVisibility(View.GONE);
    this.binding.registrationFragmentProgressSpinner.setVisibility(View.GONE);
  }


  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.registrationViewModel);
  }

}