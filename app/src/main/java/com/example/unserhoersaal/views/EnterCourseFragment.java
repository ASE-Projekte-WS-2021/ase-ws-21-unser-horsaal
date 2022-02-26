package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentEnterCourseBinding;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;
import com.google.android.material.appbar.MaterialToolbar;

/** Fragment for entering a course.*/
public class EnterCourseFragment extends Fragment {

  private static final String TAG = "EnterCourseFragment";

  private MaterialToolbar toolbar;
  private EnterCourseViewModel enterCourseViewModel;
  private CourseHistoryViewModel courseHistoryViewModel;
  private NavController navController;
  private FragmentEnterCourseBinding binding;

  public EnterCourseFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_enter_course, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);
    this.toolbar = view.findViewById(R.id.enterCourseFragmentToolbar);

    this.initViewModel();
    this.connectBinding();
    this.setupToolbar();
  }

  private void initViewModel() {
    this.enterCourseViewModel = new ViewModelProvider(requireActivity())
            .get(EnterCourseViewModel.class);
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.enterCourseViewModel.init();
    this.courseHistoryViewModel.init();
    this.enterCourseViewModel.getCourse()
            .observe(getViewLifecycleOwner(), model -> {
              //openNewCourse(id);
              if (model != null) {
                showDialog();
              }
            });
    this.enterCourseViewModel.getCourseId().observe(getViewLifecycleOwner(), id -> {
      if (id != null) {
        openNewCourse(id);
      }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.enterCourseViewModel);
  }

  /** Creates a new course if the code is correct. */
  public void openNewCourse(String id) {
    KeyboardUtil.hideKeyboard(getActivity());
    this.courseHistoryViewModel.setCourseId(id);
    this.enterCourseViewModel.resetEnterCourseId();
    this.navController.navigate(R.id.action_enterCourseFragment_to_courseHistoryFragment);
  }

  private void setupToolbar() {
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.toolbar.setNavigationOnClickListener(v -> {
      this.navController.navigate(R.id.action_enterCourseFragment_to_coursesFragment);
    });
  }

  private void showDialog() {
    EnterCourseDialogFragment dialogFragment = new EnterCourseDialogFragment();
    dialogFragment.show(getActivity().getSupportFragmentManager(), "enter dialog");
  }
}