package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentCreateCourseBinding;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;

/** Fragment for course creation. */
public class CreateCourseFragment extends Fragment {

  private static final String TAG = "CreateCourseFragment";

  private FragmentCreateCourseBinding binding;
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
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding =  DataBindingUtil.inflate(inflater,
            R.layout.fragment_create_course, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);

    this.initViewModel();
    this.connectBinding();
    this.initToolbar();
    this.initEditable();
  }

  private void initViewModel() {
    this.createCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CreateCourseViewModel.class);
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.createCourseViewModel.init();
    this.courseHistoryViewModel.init();
    this.createCourseViewModel
            .getCourseModel().observe(getViewLifecycleOwner(), this::courseLiveDataCallback);
  }

  private void courseLiveDataCallback(StateData<CourseModel> courseModelStateData) {
    KeyboardUtil.hideKeyboard(getActivity());

    if (courseModelStateData.getStatus() == StateData.DataStatus.UPDATE) {
      courseCreated(courseModelStateData.getData());
    }
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.createCourseViewModel);
  }

  private void initToolbar() {
    this.binding.createCourseToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.createCourseToolbar.setNavigationOnClickListener(v ->
            this.navController.navigateUp());
  }

  /** Signs the creator in the course. */
  public void courseCreated(CourseModel course) {
    this.courseHistoryViewModel.setCourse(course);

    if (createCourseViewModel.getIsEditing()) {
      this.navController.navigate(R.id.action_createCourseFragment_to_courseDescriptionFragment);
    } else {
      this.navController.navigate(R.id.action_createCourseFragment_to_courseHistoryFragment);
    }
  }

  private void initEditable() {
    if (this.createCourseViewModel.getIsEditing()) {
      this.binding.createCourseFragmentToolbarTitle.setText(R.string.edit_course_string);
      this.binding.createCourseFragmentCreateButton.setText(R.string.edit_course_button);
    } else {
      this.binding.createCourseFragmentToolbarTitle.setText(R.string.create_course_string);
      this.binding.createCourseFragmentCreateButton.setText(R.string.create_course_create_button);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    this.createCourseViewModel.setIsEditing(false);
    this.createCourseViewModel.resetCourseModelInput();
    this.createCourseViewModel.setLiveDataComplete();
  }
}