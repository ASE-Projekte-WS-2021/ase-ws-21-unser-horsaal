package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.viewmodel.LoggedInViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** Profile page. */
public class ProfileFragment extends Fragment {
  private TextView userEmail;
  private EditText userPassword;
  private ImageView togglePasswordIcon;
  private boolean passwordMask;
  private MaterialToolbar toolbar;
  private NavController navController;
  private MenuItem logout;
  private FloatingActionButton fab;
  private MaterialButton deleteAccountButton;

  private LoggedInViewModel loggedInViewModel;

  public ProfileFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    loggedInViewModel = new ViewModelProvider(this).get(LoggedInViewModel.class);
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
    initUi(view);
    initToolbar();
    passwordMask = true;
  }

  private void initUi(View view) {
    userEmail = view.findViewById(R.id.profileFragmentUserEmail);
    userPassword = view.findViewById(R.id.profileFragmentPassword);
    togglePasswordIcon = view.findViewById(R.id.profileFragmentTogglePasswordIcon);
    toolbar = view.findViewById(R.id.profileFragmentToolbar);
    navController = Navigation.findNavController(view);
    logout = view.findViewById(R.id.profileFragmentToolbarLogout);
    deleteAccountButton = view.findViewById(R.id.profileFragmentDeleteAccountButton);
    fab = view.findViewById(R.id.profileFragmentFab);
    fab.setOnClickListener(v -> {
      navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
    });
    togglePasswordIcon.setOnClickListener(v -> {
      togglePassword();
    });
  }

  private void initToolbar() {
    toolbar.inflateMenu(R.menu.profile_fragment_toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    toolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_profileFragment_to_coursesFragment);
    });
    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.profileFragmentToolbarLogout) {
          loggedInViewModel.logOut();
          navController.navigate(R.id.action_profileFragment_to_loginFragment);
        }
        return false;
      }
    });
  }

  private void togglePassword() {
    if (passwordMask) {
      userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
      togglePasswordIcon.setColorFilter(getResources().getColor(R.color.app_blue));
      passwordMask = false;
    } else {
      userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
      togglePasswordIcon.setColorFilter(getResources().getColor(R.color.grey));
      passwordMask = true;
    }
  }
}