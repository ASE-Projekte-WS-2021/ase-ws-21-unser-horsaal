package com.example.unserhoersaal.views;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentCourseThreadBinding;

/**Course Thread.*/
public class CourseThreadFragment extends Fragment {

  private NavController navController;
  private FragmentCourseThreadBinding binding;

  public CourseThreadFragment() {
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
            R.layout.fragment_course_thread, container, false);
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

  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    //this.binding.setVm();
  }

  private void initToolbar() {
    this.binding.courseThreadFragmentToolbar
            .inflateMenu(R.menu.course_thread_fragment_toolbar);
    this.binding.courseThreadFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseThreadFragmentToolbar
            .setNavigationOnClickListener(v ->
      navController.navigate(R.id.action_courseThreadFragment_to_courseMeetingFragment)
    );
  }
}