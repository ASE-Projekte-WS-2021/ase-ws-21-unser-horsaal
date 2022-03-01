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
import com.example.unserhoersaal.databinding.FragmentCourseDescriptionBinding;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;

/** Course-Description.*/
public class CourseDescriptionFragment extends Fragment {

  private static final String TAG = "CourseDescriptionFragment";

  private NavController navController;
  private CourseDescriptionViewModel courseDescriptionViewModel;
  private FragmentCourseDescriptionBinding binding;

  public CourseDescriptionFragment() {
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
            R.layout.fragment_course_description, container,false);
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
    this.courseDescriptionViewModel = new ViewModelProvider(requireActivity())
            .get(CourseDescriptionViewModel.class);
    this.courseDescriptionViewModel.init();
    this.courseDescriptionViewModel.getCourseModel()
            .observe(getViewLifecycleOwner(), courseModel -> {
              /*
              use to navigate the user out of the fragment / course navigate back to course history
              if (courseModel == null) {
                navController.navigate(R.id.action_courseDescriptionFragment_to_courseHistoryFragment);
              }
              */
            });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.courseDescriptionViewModel);
  }


  private void initToolbar() {
    this.binding.courseDescriptionFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseDescriptionFragmentToolbar
            .setNavigationOnClickListener(v ->
            navController.navigate(R.id.action_courseDescriptionFragment_to_courseHistoryFragment));
  }

}