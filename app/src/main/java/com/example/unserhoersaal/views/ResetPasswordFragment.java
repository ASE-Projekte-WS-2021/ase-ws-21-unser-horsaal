package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentResetPasswordBinding;
import com.example.unserhoersaal.enums.ResetPasswordEnum;
import com.example.unserhoersaal.viewmodel.LoginViewModel;

public class ResetPasswordFragment extends Fragment {

  private static final String TAG = "ResetPasswordFragment";

  private FragmentResetPasswordBinding binding;
  private NavController navController;
  private LoginViewModel loginViewModel;

  public ResetPasswordFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container,
            false);
    // Inflate the layout for this fragment
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
    this.loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    this.loginViewModel.init();

    this.loginViewModel
            .getEmailExistency().observe(getViewLifecycleOwner(), existency -> {
              if (existency == ResetPasswordEnum.SUCCESS) {
                loginViewModel.sendPasswordResetMail();
                Toast.makeText(getContext(), Config.DIALOG_PASSWORD_RESET_SUCCESS,
                        Toast.LENGTH_LONG).show();
              } else if (existency == ResetPasswordEnum.ERROR){
                Toast.makeText(getContext(), Config.DIALOG_EMAIL_PATTERN_WRONG, Toast.LENGTH_LONG)
                        .show();
              }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.loginViewModel);
  }

  @Override
  public void onPause() {
    super.onPause();
    this.loginViewModel.resetPasswordReseter();
  }
}