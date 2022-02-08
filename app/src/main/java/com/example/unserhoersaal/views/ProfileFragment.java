package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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

  private TextView userEmail;
  private EditText userPassword;
  private Button editProfileButton;
  private ImageView togglePasswordIcon;
  private boolean passwordMask;
  private NavController navController;
  private MenuItem logout;
  private FloatingActionButton fab;
  private MaterialButton deleteAccountButton;
  private MaterialToolbar toolbar;

  private LoginRegisterViewModel loginRegisterViewModel;

  public ProfileFragment() {
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
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();
    this.initUi(view);
    this.initToolbar();
    this.initLogout(view);
    this.passwordMask = true;
  }

  private void initViewModel() {
    this.loginRegisterViewModel
            = new ViewModelProvider(requireActivity()).get(LoginRegisterViewModel.class);
    this.loginRegisterViewModel.init();
    this.loginRegisterViewModel
            .getUserLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
              @Override
              public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser == null) {
                  navController.navigate(R.id.action_profileFragment_to_loginFragment);
                }
              }
            });
  }

  private void initUi(View view) {
    this.userEmail = view.findViewById(R.id.profileFragmentUserEmail);
    this.userPassword = view.findViewById(R.id.profileFragmentExamplePassword);
    this.togglePasswordIcon = view.findViewById(R.id.profileFragmentTogglePasswordIcon);
    this.editProfileButton = view.findViewById(R.id.profileFragmentEditProfileButton);
    this.toolbar = view.findViewById(R.id.profileFragmentToolbar);
    this.navController = Navigation.findNavController(view);
    this.logout = view.findViewById(R.id.profileFragmentToolbarLogout);
    this.deleteAccountButton = view.findViewById(R.id.profileFragmentDeleteAccountButton);
    this.fab = view.findViewById(R.id.profileFragmentFab);
    this.fab.setOnClickListener(v -> {
      navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
    });
    this.togglePasswordIcon.setOnClickListener(v -> {
      this.togglePassword();
    });
  }

  /** This method initializes the logout button. */
  public void initLogout(View view) {
    this.navController = Navigation.findNavController(view);

    this.logout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        loginRegisterViewModel.logOut();
      }
    });
  }

  private void togglePassword(){
    if (this.passwordMask){
      this.userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
      this.togglePasswordIcon.setColorFilter(getResources().getColor(R.color.app_blue));
      this.passwordMask = false;
    }else {
      this.userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
      this.togglePasswordIcon.setColorFilter(getResources().getColor(R.color.grey));
      this.passwordMask = true;
    }
  }
}