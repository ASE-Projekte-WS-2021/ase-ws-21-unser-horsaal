package com.example.unserhoersaal.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.unserhoersaal.viewmodel.CourseParticipantsViewModel;

/** Course-Description.*/
public class CourseDescriptionFragment extends Fragment {

  private static final String TAG = "CourseDescriptionFragment";

  private NavController navController;
  private CourseDescriptionViewModel courseDescriptionViewModel;
  private CourseParticipantsViewModel courseParticipantsViewModel;
  private FragmentCourseDescriptionBinding binding;

  public CourseDescriptionFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
              @Override
              public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                  // There are no request codes
                  Intent data = result.getData();
                 // doSomeOperations();
                }
              }
            });
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding =  DataBindingUtil.inflate(inflater,
            R.layout.fragment_course_description, container, false);
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
    this.courseParticipantsViewModel = new ViewModelProvider(requireActivity())
            .get(CourseParticipantsViewModel.class);
    this.courseDescriptionViewModel.init();
    this.courseParticipantsViewModel.init();
    this.courseDescriptionViewModel.getCourseModel()
            .observe(getViewLifecycleOwner(), courseModel -> {

              //use to navigate the user out of the fragment
              //course navigate back to course history
              if (courseModel == null) {
                navController.navigate(R.id.action_courseDescriptionFragment_to_coursesFragment);
                //TODO: assert != null
              } else if (courseModel.getData().getKey() != null) {
                courseParticipantsViewModel.setCourseId(courseModel.getData().getKey());
              }

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