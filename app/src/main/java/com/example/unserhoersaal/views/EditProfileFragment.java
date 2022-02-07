package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.unserhoersaal.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** Fragment for Editing a profile.*/
public class EditProfileFragment extends Fragment {

  private MaterialToolbar toolbar;
  private ImageView profileImage;
  private EditText username;
  private EditText institution;
  private EditText currentPassword;
  private EditText newPassword;
  private NavController navController;
  private MenuItem saveButton;

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
    initUi(view);
    setupToolbar();
  }

  private void initUi(View view){
    toolbar = view.findViewById(R.id.editProfileFragmentToolbar);
    profileImage = view.findViewById(R.id.editProfileFragmentProfileImage);
    username = view.findViewById(R.id.editProfileFragmentUserName);
    institution = view.findViewById(R.id.editProfileFragmentInstitution);
    currentPassword = view.findViewById(R.id.editProfileFragmentCurrentPassword);
    newPassword = view.findViewById(R.id.editProfileFragmentNewPassword);
    saveButton = view.findViewById(R.id.editProfileToolbarSave);
    navController = Navigation.findNavController(view);
  }

  private void setupToolbar(){
    toolbar.inflateMenu(R.menu.edit_profile_fragment_toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
    toolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_editProfileFragment_to_profileFragment);
    });
    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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