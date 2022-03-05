package com.example.unserhoersaal.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentRegistrationBinding;
import com.example.unserhoersaal.enums.EmailVerificationEnum;
import com.example.unserhoersaal.enums.LogRegErrorMessEnum;
import com.example.unserhoersaal.utils.DialogBuilder;
import com.example.unserhoersaal.viewmodel.RegistrationViewModel;

/**
 * Initiates the UI of the registration area, the registration function
 * and the navigation to the course page.
 */
public class RegistrationFragment extends Fragment {

  private static final String TAG = "RegistrationFragment";

  private RegistrationViewModel registrationViewModel;
  private NavController navController;
  private FragmentRegistrationBinding binding;

  public RegistrationFragment() {
      // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
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

    this.initViewModel();
    this.connectBinding();
  }

  private void initViewModel() {
    this.registrationViewModel = new ViewModelProvider(requireActivity())
            .get(RegistrationViewModel.class);
    this.registrationViewModel.init();
    this.registrationViewModel.resetErrorMessageLiveData();
    this.registrationViewModel.resetDatabindingData();

    //Todo: automatic redirect to the course page after verification.
    /*
    this.registrationViewModel
            .getUserLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
              if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                navController.navigate(R.id.action_registrationFragment_to_coursesFragment);
              }
            });
     */

    /** Open email-verify-dialog (with resend option) after registration input.*/
    this.registrationViewModel
            .verificationStatus.observe(getViewLifecycleOwner(), status -> {
              if (status == EmailVerificationEnum.SEND_EMAIL_VERIFICATION) {
                DialogBuilder dialog = new DialogBuilder();
                AlertDialog.Builder verificationDialog =
                        dialog.verifyEmailDialogRegistration(getView(), registrationViewModel);
                /** If user don`t want to resend verification email switch to the login screen.*/
                verificationDialog.setNegativeButton(Config.DIALOG_CANCEL_BUTTON,
                        new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    registrationViewModel.setVerificationStatusOnNull();
                    navController.navigate(R.id.action_registrationFragment_to_loginFragment);
                  }
                });
                verificationDialog.show();
              }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.registrationViewModel);
  }

}