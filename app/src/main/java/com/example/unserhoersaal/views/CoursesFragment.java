package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.CoursesAdapter;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import java.util.List;

/** Courses. */
public class CoursesFragment extends Fragment implements CoursesAdapter.OnNoteListener {

  private static final String TAG = "CoursesFragment";

  private RecyclerView courseListRecycler;
  private Button enterNewCourseButton;
  private Button createNewCourseButton;
  private TextView titelTextView;

  private CoursesAdapter coursesAdapter;

  private NavController navController;

  private CoursesViewModel coursesViewModel;
  private CurrentCourseViewModel currentCourseViewModel;


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
    this.initViewModel();
    this.initUi(view);
    this.initRecyclerView();
    this.setupNavigation(view);
  }

  private void initViewModel() {
    this.coursesViewModel = new ViewModelProvider(requireActivity())
            .get(CoursesViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.coursesViewModel.init();
    this.coursesViewModel.getUserCourses()
            .observe(getViewLifecycleOwner(), new Observer<List<UserCourse>>() {
              @SuppressLint("NotifyDataSetChanged")
              @Override
              public void onChanged(@Nullable List<UserCourse> userCourses) {
                coursesAdapter.notifyDataSetChanged();
              }
            });
  }

  private void initUi(View view) {
    this.enterNewCourseButton = view.findViewById(R.id.coursesFragmentEnterNewCourseButton);
    this.createNewCourseButton = view.findViewById(R.id.coursesFragmentCreateNewCourseButton);
    this.titelTextView = view.findViewById(R.id.coursesFragmentTitleTextView);
    this.courseListRecycler = view.findViewById(R.id.coursesFragmentRecyclerView);
    this.titelTextView.setText(Config.COURSES_TITLE);
  }

  private void initRecyclerView() {
    this.coursesAdapter =
            new CoursesAdapter(this.coursesViewModel.getUserCourses().getValue(), this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    this.courseListRecycler.setLayoutManager(layoutManager);
    this.courseListRecycler.setItemAnimator(new DefaultItemAnimator());
    this.courseListRecycler.setAdapter(this.coursesAdapter);
  }

  private void setupNavigation(View view) {
    this.navController = Navigation.findNavController(view);

    this.enterNewCourseButton.setOnClickListener(v -> this.navController.navigate(
            R.id.action_coursesFragment_to_enterCourseFragment));
    this.createNewCourseButton.setOnClickListener(v -> this.navController.navigate(
            R.id.action_coursesFragment_to_createCourseFragment)
    );

    KeyboardUtil.hideKeyboard(getActivity());
  }

  @Override
  public void onNoteClick(int position) {
    String id = this.coursesViewModel.getUserCourses().getValue().get(position).getKey();
    this.currentCourseViewModel.setCourseId(id);
    this.navController.navigate(R.id.action_coursesFragment_to_currentCourseFragment);
  }
}