package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.google.android.material.appbar.MaterialToolbar;

/** Fragment for course creation. */
public class CreateCourseFragment extends Fragment {

  private static final String TAG = "CreateCourseFragment";

  private MaterialToolbar toolbar;
  private EditText courseTitleEditText;
  private EditText courseDescriptionEditText;
  private EditText courseInstitutionEditText;
  private Button createCourseButton;

  private CreateCourseViewModel createCourseViewModel;
  private CourseHistoryViewModel courseHistoryViewModel;

  private NavController navController;

  public CreateCourseFragment() {
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
    return inflater.inflate(R.layout.fragment_create_course, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();
    this.initUi(view);
    this.setupNavigation(view);
    this.setupToolbar();
  }

  private void initViewModel() {
    this.createCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CreateCourseViewModel.class);
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.createCourseViewModel.init();
    this.courseHistoryViewModel.init();
    this.createCourseViewModel
            .getCourseModel().observe(getViewLifecycleOwner(), new Observer<CourseModel>() {
              @Override
              public void onChanged(CourseModel course) {
                courseCreated(course);
              }
            });
  }

  private void initUi(View view) {
    this.courseTitleEditText = view.findViewById(R.id.createCourseFragmentCourseTitleEditText);
    this.courseDescriptionEditText =
            view.findViewById(R.id.createCourseFragmentCourseDescriptionEditText);
    this.courseInstitutionEditText =
            view.findViewById(R.id.createCourseFragmentCourseInstitutionEditText);
    this.createCourseButton = view.findViewById(R.id.createCourseFragmentCreateButton);
    this.toolbar = view.findViewById(R.id.createCourseToolbar);
  }

  //setup Navigation to corresponding fragments
  private void setupNavigation(View view) {
    this.navController = Navigation.findNavController(view);
    this.createCourseButton.setOnClickListener(v -> createCourse());
  }

  /** Creates a new course. */
  public void createCourse() {
    String courseTitle = this.courseTitleEditText.getText().toString();
    String courseDescription = this.courseDescriptionEditText.getText().toString();
    String courseInstitution = this.courseInstitutionEditText.getText().toString();

    if (courseTitle.length() > 0) {
      CourseModel courseModel = new CourseModel(courseTitle, courseDescription, courseInstitution);
      this.createCourseViewModel.createCourse(courseModel);
      KeyboardUtil.hideKeyboard(getActivity());
    }
  }

  private void setupToolbar() {
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.toolbar.setNavigationOnClickListener(v -> {
      this.navController.navigate(R.id.action_createCourseFragment_to_coursesFragment);
    });
  }

  /** Signs the creator in the course. */
  public void courseCreated(CourseModel course) {
    String key = course.getKey();
    this.courseHistoryViewModel.setCourseId(key);
    this.navController.navigate(R.id.action_createCourseFragment_to_courseHistoryFragment);
    Toast.makeText(getActivity(), key, Toast.LENGTH_LONG).show();
  }
}