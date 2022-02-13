package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.google.android.material.appbar.MaterialToolbar;

/** Fragment for Editing a profile.*/
public class EditProfileFragment extends Fragment {

  private MaterialToolbar toolbar;
  private ImageView profileImage;
  private EditText username;
  private EditText institution;
  private EditText currentPassword;
  private EditText newPassword;
  private MenuItem saveButton;

  private NavController navController;

  public EditProfileFragment() {
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
    return inflater.inflate(R.layout.fragment_edit_profile, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initUi(view);
    this.setupToolbar();
  }

  private void initUi(View view) {
    this.toolbar = view.findViewById(R.id.editProfileFragmentToolbar);
    this.profileImage = view.findViewById(R.id.editProfileFragmentProfileImage);
    this.username = view.findViewById(R.id.editProfileFragmentUserName);
    this.institution = view.findViewById(R.id.editProfileFragmentInstitution);
    this.currentPassword = view.findViewById(R.id.editProfileFragmentCurrentPassword);
    this.newPassword = view.findViewById(R.id.editProfileFragmentNewPassword);
    this.saveButton = view.findViewById(R.id.editProfileToolbarSave);
    this.navController = Navigation.findNavController(view);
  }

  private void setupToolbar() {
    this.toolbar.inflateMenu(R.menu.edit_profile_fragment_toolbar);
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
    this.toolbar.setNavigationOnClickListener(v -> {
      this.navController.navigate(R.id.action_editProfileFragment_to_profileFragment);
    });
    this.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.editProfileToolbarSave) {
          //save changes and navigate back to profile page
        }
        return false;
      }
    });
  }
}