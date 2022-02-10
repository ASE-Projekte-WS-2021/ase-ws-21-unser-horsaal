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

/** Fragment for course creation. */
public class CreateCourseFragment extends Fragment {

  private static final String TAG = "CreateCourseFragment";

  private EditText courseTitelEditText;
  private Button createCourseButton;
  private EnterCourseViewModel enterCourseViewModel;
  private CreateCourseViewModel createCourseViewModel;
  private CurrentCourseViewModel currentCourseViewModel;
  private NavController navController;

  public CreateCourseFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    createCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CreateCourseViewModel.class);
    enterCourseViewModel = new ViewModelProvider(requireActivity())
            .get(EnterCourseViewModel.class);
    currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
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
    createCourseViewModel.getUserCourse().observe(getViewLifecycleOwner(), new Observer<UserCourse>(){
      @Override
      public void onChanged(UserCourse course) {
        courseCreated(course);
      }
    });
    initUi(view);
    setupNavigation(view);
  }

  private void initUi(View view) {
    courseTitelEditText = view.findViewById(R.id.createCourseFragmentCourseTitleEditText);
    createCourseButton = view.findViewById(R.id.createCourseFragmentCreateButton);
  }

  //setup Navigation to corresponding fragments
  private void setupNavigation(View view) {
    navController = Navigation.findNavController(view);
    createCourseButton.setOnClickListener(v -> createCourse());
  }

  public void createCourse(){
    String courseTitle = courseTitelEditText.getText().toString();
    String courseDescription = "";

    if (courseTitle.length() > 0) {
      createCourseViewModel.createCourse(courseTitle, courseDescription);
      KeyboardUtil.hideKeyboard(getActivity());
    } else {
      Toast.makeText(getContext(), "The course name should be 6 characters or longer.",
              Toast.LENGTH_SHORT).show();
    }
  }

  public void courseCreated(UserCourse course) {
    enterCourseViewModel.addUserToCourse(course.key, course.name);
    currentCourseViewModel.setCourseId(course.key);
    navController.navigate(R.id.action_createCourseFragment_to_currentCourseFragment);
  }
}