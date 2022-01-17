package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.utils.KeyboardUtil;

/** Courses. */
public class CoursesFragment extends Fragment {
  private RecyclerView courseListRecycler;
  private Button enterNewCourseButton;
  private Button createNewCourseButton;

  public CoursesFragment() {
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
    return inflater.inflate(R.layout.fragment_courses, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initUi(view);
    setupNavigation(view);
  }

  private void initUi(View view) {
    courseListRecycler = view.findViewById(R.id.coursesFragmentRecyclerView);
    enterNewCourseButton = view.findViewById(R.id.coursesFragmentEnterNewCourseButton);
    createNewCourseButton = view.findViewById(R.id.coursesFragmentCreateNewCourseButton);
  }

  private void setupNavigation(View view) {
    NavController navController = Navigation.findNavController(view);
    enterNewCourseButton.setOnClickListener(v -> navController.navigate(
            R.id.action_coursesFragment_to_enterCourseFragment));
    createNewCourseButton.setOnClickListener(v -> navController.navigate(
            R.id.action_coursesFragment_to_createCourseFragment)
    );
    KeyboardUtil.hideKeyboard(getActivity());
  }
}