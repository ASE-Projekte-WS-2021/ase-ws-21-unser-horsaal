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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.MeetingAdapter;
import com.example.unserhoersaal.databinding.FragmentCourseHistoryBinding;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;

/** Fragment contains list of course-meetings.*/
public class CourseHistoryFragment extends Fragment {

  private static final String TAG = "CourseHistoryFragment";

  private FragmentCourseHistoryBinding binding;
  private CourseHistoryViewModel courseHistoryViewModel;
  private CourseMeetingViewModel courseMeetingViewModel;
  private CourseDescriptionViewModel courseDescriptionViewModel;
  private NavController navController;
  private MeetingAdapter meetingAdapter;

  public CourseHistoryFragment() {
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
            R.layout.fragment_course_history, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.navController = Navigation.findNavController(view);

    this.initViewModel();
    this.connectAdapter();
    this.connectBinding();
    this.initToolbar();
  }

  @SuppressLint("NotifyDataSetChanged")
  private void initViewModel() {
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.courseMeetingViewModel = new ViewModelProvider(requireActivity())
            .get(CourseMeetingViewModel.class);
    this.courseDescriptionViewModel = new ViewModelProvider(requireActivity())
            .get(CourseDescriptionViewModel.class);
    this.courseHistoryViewModel.init();
    this.courseMeetingViewModel.init();
    this.courseDescriptionViewModel.init();

    this.courseHistoryViewModel.getMeetings().observe(getViewLifecycleOwner(), meetingsModels -> {
      meetingAdapter.notifyDataSetChanged();
      if (meetingsModels.getData().size() == 0) {
        this.binding.coursesHistoryFragmentTitleTextView.setVisibility(View.VISIBLE);
      } else {
        this.binding.coursesHistoryFragmentTitleTextView.setVisibility(View.GONE);
      }
    });
    this.courseHistoryViewModel.getMeetingsModel()
            .observe(getViewLifecycleOwner(), meetingsModel -> {
              if (meetingsModel != null) {
                KeyboardUtil.hideKeyboard(getActivity());
                this.courseHistoryViewModel.resetMeetingData();
              }
            });
  }

  private void connectAdapter() {
    this.meetingAdapter =
            //TODO: assert !=  null
            new MeetingAdapter(this.courseHistoryViewModel.getMeetings().getValue().getData());
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.courseHistoryViewModel);
    this.binding.setAdapter(this.meetingAdapter);
  }

  private void initToolbar() {
    this.binding.courseHistoryFragmentToolbar
            .inflateMenu(R.menu.course_history_fragment_toolbar);
    this.binding.courseHistoryFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseHistoryFragmentToolbar.setNavigationOnClickListener(v ->
            navController.navigate(R.id.action_courseHistoryFragment_to_coursesFragment));
  }

  @Override
  public void onResume() {
    super.onResume();
    courseHistoryViewModel.resetMeetingData();
  }

}