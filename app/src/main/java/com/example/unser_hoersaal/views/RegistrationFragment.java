package views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import viewmodel.LoginRegisterViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.unser_hoersaal.R;


public class RegistrationFragment extends Fragment {

    EditText userEmailEditText;
    EditText passwordEditText;
    EditText repeatPasswordEditText;
    CheckBox checkBox;
    Button registrationButton;

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
                    Navigation.findNavController(getView()).navigate(R.id.action_registrationFragment_to_loginFragment);
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
        initUI(view);
        setupNavigation(view);
    }

    private void initUI(View view){
        userEmailEditText = view.findViewById(R.id.registrationFragmentUserEmailEditText);
        passwordEditText = view.findViewById(R.id.registrationFragmentPasswordEditText);
        repeatPasswordEditText = view.findViewById(R.id.registrationFragmentRepeatPasswordEditText);
        checkBox = view.findViewById(R.id.registrationFragmentCheckBox);
        registrationButton = view.findViewById(R.id.registrationFragmentRegistrationButton);
    }


    //setup Navigation to corresponding fragments
    private void setupNavigation(View view){
        NavController navController = Navigation.findNavController(view);

        //todo add logic to registration
        //registrationButton.setOnClickListener(v -> navController.navigate(R.id.action_registrationFragment_to_loginFragment));
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = userEmailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (email.length() > 0 && password.length() > 0) {
                    loginRegisterViewModel.register(email, password);
                    navController.navigate(R.id.action_registrationFragment_to_loginFragment);
                } else {
                    Toast.makeText(getContext(), "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}