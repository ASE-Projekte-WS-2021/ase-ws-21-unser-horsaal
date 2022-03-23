package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentCreateCourseInformationBinding;

/**Information screen for course creation.*/
public class CreateCourseInformationFragment extends Fragment {

  private FragmentCreateCourseInformationBinding binding;
  private NavController navController;

  public CreateCourseInformationFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_course_information,
            container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.navController = Navigation.findNavController(view);
    this.initToolbar();
  }

  private void initToolbar() {
    this.binding.createCourseInformationFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.createCourseInformationFragmentToolbar
            .setNavigationOnClickListener(v ->
                    navController.navigate(R.id
                            .action_createCourseInformationFragment_to_coursesFragment));
  }
}