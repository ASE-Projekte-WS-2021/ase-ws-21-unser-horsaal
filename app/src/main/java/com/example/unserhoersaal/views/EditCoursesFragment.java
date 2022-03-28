package com.example.unserhoersaal.views;

import android.graphics.Color;
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
import com.example.unserhoersaal.databinding.FragmentEditCourseBinding;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;

/** Fragment for course creation. */
public class EditCoursesFragment extends Fragment {

  private static final String TAG = "EditCourseFragment";

  private FragmentEditCourseBinding binding;
  private CreateCourseViewModel createCourseViewModel;
  private CourseHistoryViewModel courseHistoryViewModel;
  private NavController navController;

  public EditCoursesFragment() {
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
            R.layout.fragment_edit_course, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);

    this.initViewModel();
    this.connectBinding();
    this.initToolbar();
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
    this.resetBindings();

    if (courseModelStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.coursesEditFragmentProgressSpinner.setVisibility(View.VISIBLE);
      this.binding.editCourseFragmenteditButton.setEnabled(false);
      this.binding.editCourseFragmenteditButton.setBackgroundColor(Color.GRAY);
    } else if (courseModelStateData.getStatus() == StateData.DataStatus.ERROR) {
      if (courseModelStateData.getErrorTag() == ErrorTag.TITLE) {
        System.out.println(courseModelStateData.getError().getMessage());
        this.binding.editCourseFragmentCourseTitleErrorText
                .setText(courseModelStateData.getError().getMessage());
        this.binding.editCourseFragmentCourseTitleErrorText.setVisibility(View.VISIBLE);
      } else if (courseModelStateData.getErrorTag() == ErrorTag.DESCRIPTION) {
        this.binding.editCourseFragmentCourseDescriptionErrorText
                .setText(courseModelStateData.getError().getMessage());
        this.binding.editCourseFragmentCourseDescriptionErrorText.setVisibility(View.VISIBLE);
      } else if (courseModelStateData.getErrorTag() == ErrorTag.INSTITUTION) {
        this.binding.editCourseFragmentCourseInstitutionErrorText
                .setText(courseModelStateData.getError().getMessage());
        this.binding.editCourseFragmentCourseInstitutionErrorText.setVisibility(View.VISIBLE);
      } else {
        this.binding.editCourseFragmentCourseGeneralErrorText
                .setText(courseModelStateData.getError().getMessage());
        this.binding.editCourseFragmentCourseGeneralErrorText.setVisibility(View.VISIBLE);
      }
    }
    if (courseModelStateData.getStatus() == StateData.DataStatus.LOADING) {
      courseCreated(courseModelStateData.getData());
    }
  }

  private void resetBindings() {
    this.binding.coursesEditFragmentProgressSpinner.setVisibility(View.GONE);
    this.binding.editCourseFragmentCourseGeneralErrorText.setVisibility(View.GONE);
    this.binding.editCourseFragmentCourseInstitutionErrorText.setVisibility(View.GONE);
    this.binding.editCourseFragmentCourseDescriptionErrorText.setVisibility(View.GONE);
    this.binding.editCourseFragmentCourseTitleErrorText.setVisibility(View.GONE);
    this.binding.editCourseFragmenteditButton.setEnabled(true);
    this.binding.editCourseFragmenteditButton.setTextAppearance(R.style.wideBlueButton);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.createCourseViewModel);
  }

  private void initToolbar() {
    this.binding.editCourseToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.editCourseToolbar.setNavigationOnClickListener(v ->
            this.navController.navigate(R.id.action_createCourseFragment_to_coursesFragment)
    );
  }

  /** Signs the creator in the course. */
  public void courseCreated(CourseModel course) {
    //this.courseHistoryViewModel.setCourse(course);
    this.createCourseViewModel.resetCourseModelInput();
    //this.navController.navigate(R.id.action_createCourseFragment_to_courseHistoryFragment);
  }

  @Override
  public void onResume() {
    super.onResume();
    this.createCourseViewModel.resetCourseModelInput();
  }

}

