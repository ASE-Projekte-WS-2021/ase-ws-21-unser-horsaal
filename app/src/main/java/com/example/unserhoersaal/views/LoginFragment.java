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
import com.example.unserhoersaal.enums.EmailVerificationEnum;
import com.example.unserhoersaal.enums.ResetPasswordEnum;
import com.example.unserhoersaal.utils.DialogBuilder;
import com.example.unserhoersaal.viewmodel.LoginViewModel;
import com.example.unserhoersaal.databinding.FragmentLoginBinding;


/**
 * initiates the UI of the login area, the login function, password reset
 * and the navigation to the course page.
 */
public class LoginFragment extends Fragment {

  private static final String TAG = "LoginFragment";

  private LoginViewModel loginViewModel;
  private NavController navController;
  private FragmentLoginBinding binding;

  public LoginFragment() {
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
            R.layout.fragment_login, container, false);
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
    this.loginViewModel = new ViewModelProvider(requireActivity())
            .get(LoginViewModel.class);
    this.loginViewModel.init();
    /**
     * If user successfully logged in (login input correct and email is verified)
     *  navigate to the course screen.
     */
    this.loginViewModel
            .getUserLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
              if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                navController.navigate(R.id.action_loginFragment_to_coursesFragment);
              }
            });
    /** If user tries to log in but its email isn`t verified yet open verify-email-dialog.*/
    this.loginViewModel
            .verificationStatus.observe(getViewLifecycleOwner(), toastMessage -> {
      if (toastMessage == EmailVerificationEnum.REQUEST_EMAIL_VERIFICATION) {
        DialogBuilder verificationDialog = new DialogBuilder();
        verificationDialog.verifyEmailDialogLogin(getView(), loginViewModel);
      }});
    /** If user requests password reset open password-reset-dialog.*/
    this.loginViewModel.resetPasswordStatus.observe(getViewLifecycleOwner(), status ->{
      if (status == ResetPasswordEnum.RESET_PASSWORD_YES) {
        DialogBuilder passwordResetDialog = new DialogBuilder();
        passwordResetDialog.passwordResetDialog(getView(), loginViewModel);
      }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.loginViewModel);
  }

}