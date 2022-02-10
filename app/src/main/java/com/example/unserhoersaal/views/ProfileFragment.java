package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.viewmodel.LoginRegisterViewModel;
import com.google.firebase.auth.FirebaseUser;

/** Profile page. */
public class ProfileFragment extends Fragment {

  private static final String TAG = "ProfileFragment";

  private EditText userEmailEditText;
  private EditText userPasswordEditText;
  private Button editProfileButton;
  private Button logoutButton;

  private NavController navController;

  private LoginRegisterViewModel loginRegisterViewModel;

  public ProfileFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    loginRegisterViewModel = new ViewModelProvider(this).get(LoginRegisterViewModel.class);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loginRegisterViewModel.init();
    loginRegisterViewModel
            .getUserLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
              @Override
              public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser == null) {
                  navController.navigate(R.id.action_profileFragment_to_loginFragment);
                }
              }
            });
    initUi(view);
    initLogoutButton(view);
  }

  private void initUi(View view) {
    userEmailEditText = view.findViewById(R.id.profileFragmentUserEmail);
    userPasswordEditText = view.findViewById(R.id.profileFragmentExamplePassword);
    editProfileButton = view.findViewById(R.id.profileFragmentEditProfileButton);
    logoutButton = view.findViewById(R.id.profileFragmentLogoutButton);
  }

  /** This method initializes the logout button. */
  public void initLogoutButton(View view) {
    navController = Navigation.findNavController(view);

    logoutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        loginRegisterViewModel.logOut();
      }
    });
  }
}