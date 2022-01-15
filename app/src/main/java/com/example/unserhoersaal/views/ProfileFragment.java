package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.unserhoersaal.R;

/** Profile page. */
public class ProfileFragment extends Fragment {
  private EditText userEmailEditText;
  private EditText userPasswordEditText;
  private Button editProfileButton;
  private Button logoutButton;

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
    initUi(view);
  }

  private void initUi(View view) {
    userEmailEditText = view.findViewById(R.id.profileFragmentUserEmail);
    userPasswordEditText = view.findViewById(R.id.profileFragmentExamplePassword);
    editProfileButton = view.findViewById(R.id.profileFragmentEditProfileButton);
    logoutButton = view.findViewById(R.id.profileFragmentLogoutButton);
  }
}