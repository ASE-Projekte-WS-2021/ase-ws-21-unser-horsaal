package com.example.unserhoersaal.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
    this.registrationViewModel
            .getUserLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
              if (firebaseUser != null) {
                navController.navigate(R.id.action_registrationFragment_to_coursesFragment);
              }
            });
    this.registrationViewModel
            .verificationStatus.observe(getViewLifecycleOwner(), status -> {
              if (status == EmailVerificationEnum.SEND_EMAIL_VERIFICATION) {
                AlertDialog.Builder emailVerificationDialog = new AlertDialog.Builder(getContext(),
                        AlertDialog.THEME_HOLO_DARK);
                emailVerificationDialog.setTitle(Config.DIALOG_VERIFICATION_TITLE);
                emailVerificationDialog.setMessage(Config.DIALOG_VERIFICATION_MESSAGE);
                emailVerificationDialog.setCancelable(false);
                emailVerificationDialog.setPositiveButton(Config.DIALOG_SEND_BUTTON,
                        new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int arg1) {
                    registrationViewModel.sendEmailVerification();
                    Toast.makeText(getContext(), Config.REG_VERIFY_EMAIL, Toast.LENGTH_LONG).show();
                    emailVerificationDialog.show();
                  }
                });
                emailVerificationDialog.show();
              }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.registrationViewModel);
  }

}