package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.CoursesAdapter;
import com.example.unserhoersaal.databinding.FragmentTodaysCoursesBinding;
import com.example.unserhoersaal.viewmodel.TodaysCoursesViewModel;

/** Fragment for displaying the courses with a meeting today. */
public class TodaysCoursesFragment extends Fragment {
  private TodaysCoursesViewModel todaysCoursesViewModel;
  private CoursesAdapter coursesAdapter;
  private FragmentTodaysCoursesBinding binding;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_todays_courses, container,
            false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();
    this.connectAdapter();
    this.connectBinding();
  }

  @SuppressLint("NotifyDataSetChanged")
  private void initViewModel() {
    this.todaysCoursesViewModel = new ViewModelProvider(getActivity())
            .get(TodaysCoursesViewModel.class);
    this.todaysCoursesViewModel.init();
    this.todaysCoursesViewModel.loadTodaysCourses();
    this.todaysCoursesViewModel.getAllCoursesRepoState()
            .observe(getViewLifecycleOwner(), todaysCourses -> {
              this.coursesAdapter.notifyDataSetChanged();
              if (todaysCourses.size() == 0) {
                this.binding.todaysCoursesFragmentTitleTextView.setVisibility(View.VISIBLE);
              } else {
                this.binding.todaysCoursesFragmentTitleTextView.setVisibility(View.GONE);
              }
            });
  }

  private void connectAdapter() {
    this.coursesAdapter = new CoursesAdapter(
            this.todaysCoursesViewModel.getAllCoursesRepoState().getValue());
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.todaysCoursesViewModel);
    this.binding.setAdapter(this.coursesAdapter);
  }
}