package com.example.unserhoersaal.views;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentCourseDescriptionDetailBinding;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;

/**Screen with only course description.*/
public class CourseDescriptionDetailFragment extends Fragment {

  private static final String TAG = "CourseDescriptionDetailFragment";

  private CourseDescriptionViewModel courseDescriptionViewModel;
  private FragmentCourseDescriptionDetailBinding binding;
  private NavController navController;

  public CourseDescriptionDetailFragment() {
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
            R.layout.fragment_course_description_detail, container,false);
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
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.courseDescriptionViewModel);
  }

  private void initToolbar() {
    this.binding.courseDescriptionDetailFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseDescriptionDetailFragmentToolbar
            .setNavigationOnClickListener(v ->
            navController.navigate(R.id.action_courseDescriptionDetailFragment_to_courseDescriptionFragment)
            );
  }
}