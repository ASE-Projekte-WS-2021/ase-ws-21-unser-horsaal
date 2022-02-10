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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * initiates the UI of the login area, the login function
 * and the navigation to the course page.
 */
public class LoginFragment extends Fragment {

  private static final String TAG = "LoginFragment";

  EditText userEmailEditView;
  EditText userPasswordEditView;
  TextView registrationTextView;
  TextView forgotPasswordTextView;
  CheckBox keepLoggedInCheckBox;
  Button loginButton;
  NavController navController;

  private LoginRegisterViewModel loginRegisterViewModel;

  public LoginFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loginRegisterViewModel = new ViewModelProvider(requireActivity())
            .get(LoginRegisterViewModel.class);

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
    loginRegisterViewModel.init();
    loginRegisterViewModel.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
      @Override
      public void onChanged(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
          Log.d(TAG, "onChanged: " + firebaseUser.getEmail());
          navController.navigate(R.id.action_loginFragment_to_coursesFragment);
        }
      }
    });
    initUi(view);
    setupNavigation(view);
    //checkIfLogged();
  }

  private void initUi(View view) {
    userEmailEditView = view.findViewById(R.id.loginFragmentUserEmailEditText);
    userPasswordEditView = view.findViewById(R.id.loginFragmentPasswordEditText);
    registrationTextView = view.findViewById(R.id.loginFragmentRegistrationTextView);
    forgotPasswordTextView = view.findViewById(R.id.loginFragmentForgotPasswortTextView);
    keepLoggedInCheckBox = view.findViewById(R.id.loginFragmentCheckBox);
    loginButton = view.findViewById(R.id.loginFragmentLoginButton);
  }

  //setup Navigation to corresponding fragments
  private void setupNavigation(View view) {
    navController = Navigation.findNavController(view);

    int toRegistrationFragment = R.id.action_loginFragment_to_registrationFragment;
    registrationTextView.setOnClickListener(v -> navController.navigate(toRegistrationFragment));
    loginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = userEmailEditView.getText().toString();
            String password = userPasswordEditView.getText().toString();
            if (email.length() > 0 && password.length() > 0) {
              loginRegisterViewModel.login(email, password);
            } else {
              String emptyInputMessage = "Email Address and Password Must Be Entered";
              Toast.makeText(getContext(), emptyInputMessage, Toast.LENGTH_SHORT).show();
            }
        }
    });
  }

}