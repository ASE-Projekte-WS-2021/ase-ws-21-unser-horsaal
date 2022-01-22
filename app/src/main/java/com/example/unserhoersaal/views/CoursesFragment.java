package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.Message;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Courses. */
public class CoursesFragment extends Fragment {
  private RecyclerView courseListRecycler;
  private Button enterNewCourseButton;
  private Button createNewCourseButton;
  private TextView titelTextView;
  private CoursesViewModel coursesViewModel;

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
    coursesViewModel = new ViewModelProvider(requireActivity())
            .get(CoursesViewModel.class);
    coursesViewModel.getUserCourses().observe(getViewLifecycleOwner(), userCourses -> {
      updateUi(view, userCourses);
    });
    initUi(view);
    setupNavigation(view);
  }

  private void initUi(View view) {
    enterNewCourseButton = view.findViewById(R.id.coursesFragmentEnterNewCourseButton);
    createNewCourseButton = view.findViewById(R.id.coursesFragmentCreateNewCourseButton);
    titelTextView = view.findViewById(R.id.coursesFragmentTitleTextView);

    courseListRecycler = view.findViewById(R.id.coursesFragmentRecyclerView);
    CoursesAdapter coursesAdapter = new CoursesAdapter(coursesViewModel.getEmptyCourses());
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    courseListRecycler.setLayoutManager(layoutManager);
    courseListRecycler.setItemAnimator(new DefaultItemAnimator());
    courseListRecycler.setAdapter(coursesAdapter);
    titelTextView.setText("Du bist noch keinen Kursen beigetreten");

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

  private void updateUi(View view, HashMap userCourses) {
    if (userCourses.size() == 0){
      titelTextView.setText("Du bist noch keinen Kursen beigetreten");
    }else {
      titelTextView.setText("Bereits beigetretene Kurse");
    }
    if (userCourses != null) {
      CoursesAdapter coursesAdapter = new CoursesAdapter(userCourses);
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
      courseListRecycler.setLayoutManager(layoutManager);
      courseListRecycler.setItemAnimator(new DefaultItemAnimator());
      courseListRecycler.setAdapter(coursesAdapter);
      courseListRecycler.scrollToPosition(userCourses.size() - 1);
    }
  }
}