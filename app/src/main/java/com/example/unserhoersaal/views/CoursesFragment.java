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
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;

import java.util.ArrayList;

/** Courses. */
public class CoursesFragment extends Fragment implements CoursesAdapter.OnNoteListener {
  private RecyclerView courseListRecycler;
  private Button enterNewCourseButton;
  private Button createNewCourseButton;
  private TextView titelTextView;
  private CoursesViewModel coursesViewModel;
  private CreateCourseViewModel createCourseViewModel;
  private ArrayList<UserCourse> userCourses;
  private NavController navController;

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
    createCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CreateCourseViewModel.class);
    coursesViewModel.getUserCourses().observe(getViewLifecycleOwner(), userCourses -> {
      this.userCourses = userCourses;
      updateUi(view);
    });
    initUi(view);
    setupNavigation(view);
  }

  private void initUi(View view) {
    enterNewCourseButton = view.findViewById(R.id.coursesFragmentEnterNewCourseButton);
    createNewCourseButton = view.findViewById(R.id.coursesFragmentCreateNewCourseButton);
    titelTextView = view.findViewById(R.id.coursesFragmentTitleTextView);

    courseListRecycler = view.findViewById(R.id.coursesFragmentRecyclerView);
    CoursesAdapter coursesAdapter = new CoursesAdapter(coursesViewModel.getEmptyCourses(), this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    courseListRecycler.setLayoutManager(layoutManager);
    courseListRecycler.setItemAnimator(new DefaultItemAnimator());
    courseListRecycler.setAdapter(coursesAdapter);
    titelTextView.setText("Du bist noch keinen Kursen beigetreten");

  }

  private void setupNavigation(View view) {
    navController = Navigation.findNavController(view);
    enterNewCourseButton.setOnClickListener(v -> navController.navigate(
            R.id.action_coursesFragment_to_enterCourseFragment));
    createNewCourseButton.setOnClickListener(v -> navController.navigate(
            R.id.action_coursesFragment_to_createCourseFragment)
    );

    KeyboardUtil.hideKeyboard(getActivity());
  }

  private void updateUi(View view) {
    if (userCourses.size() == 0){
      titelTextView.setText("Du bist noch keinen Kursen beigetreten");
    }else {
      titelTextView.setText("Bereits beigetretene Kurse");
    }
    if (userCourses != null) {
      CoursesAdapter coursesAdapter = new CoursesAdapter(this.userCourses, this);
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
      courseListRecycler.setLayoutManager(layoutManager);
      courseListRecycler.setItemAnimator(new DefaultItemAnimator());
      courseListRecycler.setAdapter(coursesAdapter);
      courseListRecycler.scrollToPosition(this.userCourses.size() - 1);
    }
  }

  @Override
  public void onNoteClick(int position) {
    createCourseViewModel.setCourseId(userCourses.get(position).key);
    navController.navigate(R.id.action_coursesFragment_to_currentCourseFragment);

  }
}