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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.interfaces.LoginInterface;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.LoggedInViewModel;
import com.example.unserhoersaal.viewmodel.LoginRegisterViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * initiates the UI of the login area, the login function
 * and the navigation to the course page.
 */
public class LoginFragment extends Fragment implements LoginInterface {

  private static final String TAG = "LoginFragment";

  EditText userEmailEditView;
  EditText userPasswordEditView;
  TextView registrationTextView;
  TextView forgotPasswordTextView;
  CheckBox keepLoggedInCheckBox;
  Button loginButton;
  NavController navController;

  private LoggedInViewModel loggedInViewModel;
  private LoginRegisterViewModel loginRegisterViewModel;

  public LoginFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loginRegisterViewModel = new ViewModelProvider(requireActivity())
            .get(LoginRegisterViewModel.class);
    loggedInViewModel = new ViewModelProvider(requireActivity()).get(LoggedInViewModel.class);

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
    initUi(view);
    setupNavigation(view);
    checkIfLogged();
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
              //todo find better way instead of LoginFragment.this + interface
              loginRegisterViewModel.login(email, password, LoginFragment.this);
            } else {
              String emptyInputMessage = "Email Address and Password Must Be Entered";
              Toast.makeText(getContext(), emptyInputMessage, Toast.LENGTH_SHORT).show();
            }
        }
    });
  }

  private void checkIfLogged() {
    if (loggedInViewModel.getCurrentUser() != null) {
      navController.navigate(R.id.action_loginFragment_to_coursesFragment);
      KeyboardUtil.hideKeyboard(getActivity());
    }
  }

  @Override
  public void loginResult(Boolean result) {
    if (result) {
      navController.navigate(R.id.action_loginFragment_to_coursesFragment);
      KeyboardUtil.hideKeyboard(getActivity());
    } else {
      String wrongInputMessage = "Email Address or Password is wrong!";
      Toast.makeText(getContext(), wrongInputMessage, Toast.LENGTH_SHORT).show();
    }
  }
}