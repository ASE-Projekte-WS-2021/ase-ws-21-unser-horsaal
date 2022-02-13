package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

/**
 * initiates the UI of the login area, the login function
 * and the navigation to the course page.
 */
public class LoginFragment extends Fragment {

  private static final String TAG = "LoginFragment";

  private EditText userEmailEditView;
  private EditText userPasswordEditView;
  private TextView registrationTextView;
  private TextView forgotPasswordTextView;
  private Button loginButton;
  private String firebaseResult;
  private NavController navController;
  private LoginRegisterViewModel loginRegisterViewModel;

  public LoginFragment() {
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
    return inflater.inflate(R.layout.fragment_login, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();
    this.initUi(view);
    this.setupNavigation(view);
  }

  private void initUi(View view) {
    this.userEmailEditView = view.findViewById(R.id.loginFragmentUserEmailEditText);
    this.userPasswordEditView = view.findViewById(R.id.loginFragmentPasswordEditText);
    this.registrationTextView = view.findViewById(R.id.loginFragmentRegistrationTextView);
    this.forgotPasswordTextView = view.findViewById(R.id.loginFragmentForgotPasswortTextView);
    this.loginButton = view.findViewById(R.id.loginFragmentLoginButton);
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
          navController.navigate(R.id.action_loginFragment_to_coursesFragment);
        }
      }
    });
  }

  //setup Navigation to corresponding fragments
  private void setupNavigation(View view) {
    this.navController = Navigation.findNavController(view);

    this.registrationTextView.setOnClickListener(v ->
            navController.navigate(R.id.action_loginFragment_to_registrationFragment));

    this.loginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = userEmailEditView.getText().toString();
            String password = userPasswordEditView.getText().toString();
            if (email.length() > 0 && password.length() > 0) {
              loginRegisterViewModel.login(email, password);
            }
        }
    });
  }

}