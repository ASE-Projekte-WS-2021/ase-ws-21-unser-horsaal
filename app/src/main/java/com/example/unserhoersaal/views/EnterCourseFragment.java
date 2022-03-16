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
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentEnterCourseBinding;
import com.example.unserhoersaal.enums.DeepLinkEnum;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.DeepLinkMode;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;

/** Fragment for entering a course.*/
public class EnterCourseFragment extends Fragment {

  private static final String TAG = "EnterCourseFragment";

  private EnterCourseViewModel enterCourseViewModel;
  private NavController navController;
  private FragmentEnterCourseBinding binding;
  private DeepLinkMode deepLinkMode;

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
    this.deepLinkMode = DeepLinkMode.getInstance();

    this.initViewModel();
    this.connectBinding();
    this.setupToolbar();

    if (this.deepLinkMode.getDeepLinkMode() == DeepLinkEnum.ENTER_COURSE) {
      CourseModel courseModel = new CourseModel();
      courseModel.setCodeMapping(this.deepLinkMode.getCodeMapping());
      this.enterCourseViewModel
              .courseIdInputState.postCreate(courseModel);
      this.enterCourseViewModel.checkCode();
    }
  }

  private void initViewModel() {
    this.enterCourseViewModel = new ViewModelProvider(requireActivity())
            .get(EnterCourseViewModel.class);
    this.enterCourseViewModel.init();
    this.enterCourseViewModel.getCourse()
            .observe(getViewLifecycleOwner(), this::courseLiveDataCallback);
    this.enterCourseViewModel.getCourseIdInputState()
            .observe(getViewLifecycleOwner(), this::courseIdInputStateCallback);
  }

  private void courseLiveDataCallback(StateData<CourseModel> courseModelStateData) {
    this.resetBindings();

    if (courseModelStateData == null) {
      this.binding.enterCourseFragmentPasswordErrorText.setText(Config.UNSPECIFIC_ERROR);
      this.binding.enterCourseFragmentPasswordErrorText.setVisibility(View.VISIBLE);
    }

    if (courseModelStateData.getStatus() == StateData.DataStatus.ERROR) {
      this.binding.enterCourseFragmentPasswordErrorText
              .setText(courseModelStateData.getError().getMessage());
      this.binding.enterCourseFragmentPasswordErrorText.setVisibility(View.VISIBLE);
    } else if (courseModelStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.enterCourseFragmentProgressSpinner.setVisibility(View.VISIBLE);
    } else {
      if (courseModelStateData.getData() == null) {
        return;
      }
      if (courseModelStateData.getData().getKey() != null) {
        KeyboardUtil.hideKeyboard(getActivity());
        navController.navigate(
                R.id.action_enterCourseFragment_to_enterCourseDetailFragment);
      } else {
        navController.navigate(
                R.id.action_enterCourseFragment_to_noCourseFoundFragment);
      }
    }
  }

  private void courseIdInputStateCallback(StateData<CourseModel> courseModelStateData) {
    this.resetBindings();

    if (courseModelStateData.getStatus() == StateData.DataStatus.ERROR) {
      this.binding.enterCourseFragmentPasswordErrorText
              .setText(courseModelStateData.getError().getMessage());
      this.binding.enterCourseFragmentPasswordErrorText.setVisibility(View.VISIBLE);
    }
  }

  private void resetBindings() {
    this.binding.enterCourseFragmentPasswordErrorText.setVisibility(View.GONE);
    this.binding.enterCourseFragmentProgressSpinner.setVisibility(View.GONE);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.enterCourseViewModel);
  }

  private void setupToolbar() {
    this.binding.enterCourseFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.enterCourseFragmentToolbar
            .setNavigationOnClickListener(v ->
                    this.navController.navigate(
                            R.id.action_enterCourseFragment_to_coursesFragment));
  }

  @Override
  public void onPause() {
    super.onPause();
    this.enterCourseViewModel.resetEnterCourseId();
  }

}