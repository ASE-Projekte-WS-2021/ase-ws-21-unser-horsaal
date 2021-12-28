package com.example.unser_hoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.unser_hoersaal.R;


public class LoginFragment extends Fragment {

    TextView registrationTextView;
    TextView forgotPasswordTextView;
    CheckBox keepLoggedInCheckBox;
    Button loginButton;

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
        initUI(view);
        setupNavigation(view);
    }

    private void initUI(View view){
        registrationTextView = view.findViewById(R.id.loginFragmentRegistrationTextView);
        forgotPasswordTextView = view.findViewById(R.id.loginFragmentForgotPasswortTextView);
        keepLoggedInCheckBox = view.findViewById(R.id.loginFragmentCheckBox);
        loginButton = view.findViewById(R.id.loginFragmentLoginButton);
    }

    //setup Navigation to corresponding fragments
    private void setupNavigation(View view){
        NavController navController = Navigation.findNavController(view);

        //todo add logic to login
        registrationTextView.setOnClickListener(v -> navController.navigate(R.id.action_loginFragment_to_registrationFragment));
        loginButton.setOnClickListener(v -> navController.navigate(R.id.action_loginFragment_to_enterOrCreateCourseFragment));
    }
}