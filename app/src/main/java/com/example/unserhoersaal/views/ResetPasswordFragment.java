package com.example.unserhoersaal.views;

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
import com.example.unserhoersaal.databinding.FragmentResetPasswordBinding;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.LoginViewModel;

/** JavaDoc for this Fragment. */
public class ResetPasswordFragment extends Fragment {

  private static final String TAG = "ResetPasswordFragment";

  private FragmentResetPasswordBinding binding;
  private NavController navController;
  private LoginViewModel loginViewModel;

  public ResetPasswordFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container,
            false);
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
            .getUserLiveData()
            .observe(getViewLifecycleOwner(), userStateData -> {
                if (userStateData.getStatus() == StateData.DataStatus.UPDATE) {
                  this.binding.resetPasswordFragmentSpinner.setVisibility(View.GONE);
                  this.binding.resetPasswordErrorText.setVisibility(View.GONE);

                  if (userStateData.getData() == null) {
                    navController.navigate(R.id.action_verificationFragment_to_loginFragment);
                  }
                  else if (userStateData.getData() != null
                          && userStateData.getData().isEmailVerified()) {
                    navController.navigate(R.id.action_verificationFragment_to_coursesFragment);
                  } else if (!userStateData.getData().isEmailVerified()) {
                    Toast.makeText(getContext(),
                            Config.AUTH_EDIT_PASSWORD_CHANGE_SUCCESS,
                            Toast.LENGTH_LONG)
                            .show();
                  }
                } else if (userStateData.getStatus() == StateData.DataStatus.LOADING) {
                  this.binding.resetPasswordErrorText.setVisibility(View.GONE);
                  this.binding.resetPasswordFragmentSpinner.setVisibility(View.VISIBLE);
                } else if (userStateData.getStatus() == StateData.DataStatus.ERROR) {
                  this.binding.resetPasswordFragmentSpinner.setVisibility(View.GONE);
                  this.binding.resetPasswordErrorText
                          .setText(userStateData.getError().getMessage());
                  this.binding.resetPasswordErrorText.setVisibility(View.VISIBLE);
                }
            });
    this.loginViewModel
            .getUserInputState()
            .observe(getViewLifecycleOwner(), userModelStateData -> {
              if (userModelStateData.getStatus() == StateData.DataStatus.ERROR) {
                this.binding.resetPasswordErrorText.setText(userModelStateData.getError().getMessage());
                this.binding.resetPasswordErrorText.setVisibility(View.VISIBLE);
              }/* else if (userModelStateData.getStatus() == StateData.DataStatus.COMPLETE) {
                this.binding.resetPasswordErrorText.setVisibility(View.GONE);
              }*/
            });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.loginViewModel);
  }

}