package com.example.unserhoersaal.views;

import android.os.Bundle;
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
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentLoginBinding;
import com.example.unserhoersaal.viewmodel.LoginRegisterViewModel;

/**
 * initiates the UI of the login area, the login function
 * and the navigation to the course page.
 */
public class LoginFragment extends Fragment {

  private static final String TAG = "LoginFragment";

  private LoginRegisterViewModel loginRegisterViewModel;
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
    this.loginRegisterViewModel = new ViewModelProvider(requireActivity())
            .get(LoginRegisterViewModel.class);
    this.loginRegisterViewModel.init();
    this.loginRegisterViewModel
            .getUserLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
              if (firebaseUser != null) {
                navController.navigate(R.id.action_loginFragment_to_coursesFragment);
              }
            });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.loginRegisterViewModel);
  }

}