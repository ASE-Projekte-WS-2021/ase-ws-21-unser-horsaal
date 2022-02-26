package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unserhoersaal.R;
import com.google.android.material.appbar.MaterialToolbar;

/**Course Thread.*/
public class CourseThreadFragment extends Fragment {

  private MaterialToolbar toolbar;
  private NavController navController;

  public CourseThreadFragment() {
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
    return inflater.inflate(R.layout.fragment_course_thread, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.toolbar = view.findViewById(R.id.courseThreadFragmentToolbar);
    this.navController = Navigation.findNavController(view);

    this.initToolbar();
  }

  private void initToolbar() {
    this.toolbar.inflateMenu(R.menu.course_thread_fragment_toolbar);
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.toolbar.setNavigationOnClickListener(v -> {
      this.navController.navigate(R.id.action_courseThreadFragment_to_courseMeetingFragment);
    });
  }
}