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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentRegistrationBinding;
import com.example.unserhoersaal.enums.LogRegToastEnum;
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
            .regToastMessages.observe(getViewLifecycleOwner(), toastMessage -> {
              if (toastMessage == LogRegToastEnum.SEND_EMAIL_VERIFICATION) {
                Toast.makeText(getContext(), Config.REG_VERIFY_EMAIL, Toast.LENGTH_LONG).show();
              } else if (toastMessage == LogRegToastEnum.EMAIL_VERIFICATION_ALREADY_SEND) {
                Toast.makeText(getContext(), Config.REG_VERIFICATION_EMAIL_ALREADY_SEND,
                        Toast.LENGTH_LONG).show();
              }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.registrationViewModel);
  }

}