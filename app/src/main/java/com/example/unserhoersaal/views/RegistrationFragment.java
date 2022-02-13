package com.example.unserhoersaal.views;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;

/**
 * Initiates the UI of the registration area, the registration function
 * and the navigation to the course page.
 */
public class RegistrationFragment extends Fragment {
  EditText userEmailEditText;
  EditText passwordEditText;
  Button registrationButton;
  TextView loginTextView;

  private LoginRegisterViewModel loginRegisterViewModel;

  public RegistrationFragment() {
      // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    loginRegisterViewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);

    loginRegisterViewModel.getUserLiveData().observe(this, new Observer<FirebaseUser>() {
      @Override
      public void onChanged(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
          var backToLogin = R.id.action_registrationFragment_to_loginFragment;
          Navigation.findNavController(getView()).navigate(backToLogin);
          KeyboardUtil.hideKeyboard(getActivity());
        }
      }
    });
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
    initUi(view);
    setupNavigation(view);
  }

  private void initUi(View view) {
    userEmailEditText = view.findViewById(R.id.registrationFragmentUserEmailEditText);
    passwordEditText = view.findViewById(R.id.registrationFragmentPasswordEditText);
    registrationButton = view.findViewById(R.id.registrationFragmentRegistrationButton);
    loginTextView = view.findViewById(R.id.registrationFragmentLoginTextView);
  }


  //setup Navigation to corresponding fragments
  private void setupNavigation(View view) {
    NavController navController = Navigation.findNavController(view);

    loginTextView.setOnClickListener(v -> {
      navController.navigate(R.id.action_registrationFragment_to_loginFragment);
    });

    // todo add logic to registration
    registrationButton.setOnClickListener(new View.OnClickListener() {
      @Override
    public void onClick(View view) {
        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (email.length() > 0 && password.length() > 0) {
          loginRegisterViewModel.register(email, password);
          navController.navigate(R.id.action_registrationFragment_to_loginFragment);
          KeyboardUtil.hideKeyboard(getActivity());
        } else {
          String emptyInputMessage = "Email Address and Password Must Be Entered";
          Toast.makeText(getContext(), emptyInputMessage, Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

}