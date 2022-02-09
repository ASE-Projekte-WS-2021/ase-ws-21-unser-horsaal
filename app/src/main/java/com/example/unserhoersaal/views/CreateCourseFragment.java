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
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

/** Fragment for course creation. */
public class CreateCourseFragment extends Fragment {

  private static final String TAG = "CreateCourseFragment";

  private EditText courseTitelEditText;
  private EditText courseDescriptionEditText;
  private EditText courseInstitutionEditText;
  private Button createCourseButton;
  private EnterCourseViewModel enterCourseViewModel;
  private MaterialToolbar toolbar;
  private NavController navController;
  private CreateCourseViewModel createCourseViewModel;
  private CurrentCourseViewModel currentCourseViewModel;
  private FirebaseAuth firebaseAuth;

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
    this.enterCourseViewModel = new ViewModelProvider(requireActivity())
            .get(EnterCourseViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.createCourseViewModel.init();
    this.enterCourseViewModel.init();
    this.currentCourseViewModel.init();
    this.createCourseViewModel
            .getUserCourse().observe(getViewLifecycleOwner(), new Observer<UserCourse>() {
      @Override
      public void onChanged(UserCourse course) {
        courseCreated(course);
      }
    });
  }

  private void initUi(View view) {
    this.courseTitelEditText = view.findViewById(R.id.createCourseFragmentCourseTitleEditText);
    this.courseDescriptionEditText = view.findViewById(R.id.createCourseFragmentCourseDescriptionEditText);
    this.courseInstitutionEditText = view.findViewById(R.id.createCourseFragmentCourseInstitutionEditText);
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
    String courseTitle = this.courseTitelEditText.getText().toString();
    String courseDescription = "";

    if (courseTitle.length() > 0) {
      this.createCourseViewModel.createCourse(courseTitle, courseDescription);
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
  public void courseCreated(UserCourse course) {
    String key = course.getKey();
    String name = course.getName();
    this.enterCourseViewModel.addUserToCourse(key, name);
    this.currentCourseViewModel.setCourseId(key);
    this.navController.navigate(R.id.action_createCourseFragment_to_currentCourseFragment);
  }
}