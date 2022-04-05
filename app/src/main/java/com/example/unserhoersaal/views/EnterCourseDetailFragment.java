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
import com.example.unserhoersaal.databinding.FragmentEnterCourseDetailBinding;
import com.example.unserhoersaal.enums.DeepLinkEnum;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.utils.DeepLinkMode;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;

/**Detail Screen when course found.*/
public class EnterCourseDetailFragment extends Fragment {

  private NavController navController;
  private EnterCourseViewModel enterCourseViewModel;
  private CourseHistoryViewModel courseHistoryViewModel;
  private FragmentEnterCourseDetailBinding binding;
  private DeepLinkMode deepLinkMode;

  public EnterCourseDetailFragment() {
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
            R.layout.fragment_enter_course_detail, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);
    this.deepLinkMode = DeepLinkMode.getInstance();

    this.initViewModel();
    this.connectBinding();
    this.initToolbar();
  }

  private void initViewModel() {
    this.enterCourseViewModel = new ViewModelProvider(requireActivity())
            .get(EnterCourseViewModel.class);
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.enterCourseViewModel.init();
    this.courseHistoryViewModel.init();
    this.enterCourseViewModel.getEnteredCourse().observe(getViewLifecycleOwner(), model -> {
      if (model.getData() != null) {
        openNewCourse(model.getData());
      }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.enterCourseViewModel);
  }


  /** Creates a new course if the code is correct. */
  public void openNewCourse(CourseModel model) {
    KeyboardUtil.hideKeyboard(getActivity());
    this.courseHistoryViewModel.setCourse(model);
    this.enterCourseViewModel.resetEnterCourseId();
    this.deepLinkMode.setDeepLinkMode(DeepLinkEnum.DEFAULT);
    this.navController.navigate(R.id.action_enterCourseDetailFragment_to_courseHistoryFragment);
  }

  @Override
  public void onPause() {
    super.onPause();
    this.enterCourseViewModel.resetEnterCourse();
    this.deepLinkMode.setDeepLinkMode(DeepLinkEnum.DEFAULT);
  }

  private void initToolbar() {
    this.binding.enterCourseDetailFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.enterCourseDetailFragmentToolbar
            .setNavigationOnClickListener(v -> navController.navigateUp());
  }

}