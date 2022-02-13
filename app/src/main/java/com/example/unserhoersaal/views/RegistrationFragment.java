package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.LoginRegisterViewModel;
import com.google.firebase.auth.FirebaseUser;

/**
 * Initiates the UI of the registration area, the registration function
 * and the navigation to the course page.
 */
public class RegistrationFragment extends Fragment {

  private static final String TAG = "RegistrationFragment";

  private EditText userEmailEditText;
  private EditText passwordEditText;
  private Button registrationButton;
  private TextView loginTextView;

  private LoginRegisterViewModel loginRegisterViewModel;

  private NavController navController;

  public RegistrationFragment() {
      // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_registration, container, false);
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();
    this.initUi(view);
    this.setupNavigation(view);
  }

  private void initViewModel() {
    this.loginRegisterViewModel = new ViewModelProvider(requireActivity())
            .get(LoginRegisterViewModel.class);
    this.loginRegisterViewModel.init();
    this.loginRegisterViewModel
            .getUserLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
              @Override
              public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                  navController.navigate(R.id.action_registrationFragment_to_coursesFragment);
                }
              }
            });
  }

  private void initUi(View view) {
    this.userEmailEditText = view.findViewById(R.id.registrationFragmentUserEmailEditText);
    this.passwordEditText = view.findViewById(R.id.registrationFragmentPasswordEditText);
    this.registrationButton = view.findViewById(R.id.registrationFragmentRegistrationButton);
    this.loginTextView = view.findViewById(R.id.registrationFragmentLoginTextView);
  }

  private void setupNavigation(View view) {
    this.navController = Navigation.findNavController(view);

    this.loginTextView.setOnClickListener(v -> {
      navController.navigate(R.id.action_registrationFragment_to_loginFragment);
    });

    this.registrationButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (email.length() > 0 && password.length() > 0) {
          loginRegisterViewModel.register(email, password);
          KeyboardUtil.hideKeyboard(getActivity());
        }
      }
    });
  }
}