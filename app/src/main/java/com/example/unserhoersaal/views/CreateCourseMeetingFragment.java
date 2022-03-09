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
import com.example.unserhoersaal.databinding.FragmentCreateCourseMeetingBinding;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;

/**Create Course Meeting.*/
public class CreateCourseMeetingFragment extends Fragment {

  private FragmentCreateCourseMeetingBinding binding;
  private CourseHistoryViewModel courseHistoryViewModel;
  private NavController navController;

  public CreateCourseMeetingFragment() {
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
            R.layout.fragment_create_course_meeting, container, false);
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
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.courseHistoryViewModel.init();
    this.courseHistoryViewModel.getMeetingsModel()
            .observe(getViewLifecycleOwner(), meetingsModel -> {
              if (meetingsModel != null) {
                KeyboardUtil.hideKeyboard(getActivity());
                this.navController.navigate(
                        R.id.action_createCourseMeetingFragment_to_courseHistoryFragment);
              }
            });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.courseHistoryViewModel);
  }

  private void initToolbar() {
    this.binding.createCourseMeetingFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.createCourseMeetingFragmentToolbar.setNavigationOnClickListener(v ->
            this.navController.navigate(
                    R.id.action_createCourseMeetingFragment_to_courseHistoryFragment)
    );
  }

}