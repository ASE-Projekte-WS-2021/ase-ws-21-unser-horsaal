package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;



import com.example.unserhoersaal.R;
import com.example.unserhoersaal.viewmodel.LoggedInViewModel;
import com.example.unserhoersaal.viewmodel.LoginRegisterViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginFragment extends Fragment {

    EditText userEmailEditView;
    EditText userPasswordEditView;
    TextView registrationTextView;
    TextView forgotPasswordTextView;
    CheckBox keepLoggedInCheckBox;
    Button loginButton;
    String firebaseResult;

    private LoggedInViewModel loggedInViewModel;
    private LoginRegisterViewModel loginRegisterViewModel;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        loginRegisterViewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);
        super.onCreate(savedInstanceState);
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://unser-horsaal-default-rtdb.europe-west1.firebasedatabase.app/");

        //firebaseRef.child("Courses").push().setValue("null");

        firebaseRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    String.valueOf(task.getResult().getValue());
                    saveDate(String.valueOf(task.getResult().getValue()));
                }
            }
        });

    }

    public void saveDate(String data){
        String string1 = data;
        System.out.println(data);
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
        userEmailEditView = view.findViewById(R.id.loginFragmentUserEmailEditText);
        userPasswordEditView = view.findViewById(R.id.loginFragmentPasswordEditText);
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
        //loginButton.setOnClickListener(v -> navController.navigate(R.id.action_loginFragment_to_enterOrCreateCourseFragment));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = userEmailEditView.getText().toString();
                String password = userPasswordEditView.getText().toString();
                if (email.length() > 0 && password.length() > 0) {
                    loginRegisterViewModel.login(email, password);
                    //TODO: onsuccess
                    navController.navigate(R.id.action_loginFragment_to_enterOrCreateCourseFragment);
                } else {
                    Toast.makeText(getContext(), "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}