package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unserhoersaal.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** Fragment contains list of course-meetings.*/
public class CourseHistoryFragment extends Fragment {

  MaterialToolbar toolbar;
  RecyclerView coursesRecyclerView;
  FloatingActionButton floatingActionButton;
  NavController navController;

  public CourseHistoryFragment() {
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
    return inflater.inflate(R.layout.fragment_course_history, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initUi(view);
    setupToolbar();
  }

  private void initUi(View view){
    toolbar = view.findViewById(R.id.courseHistoryFragmentToolbar);
    coursesRecyclerView = view.findViewById(R.id.courseHistoryFragmentCoursesRecyclerView);
    floatingActionButton = view.findViewById(R.id.courseHistoryFragmentFab);
    navController = Navigation.findNavController(view);
  }

  private void setupNavigation(){

  }

  private void setupToolbar(){
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    toolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_courseHistoryFragment_to_coursesFragment);
    });
  }
}