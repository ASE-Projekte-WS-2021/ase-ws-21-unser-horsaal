package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;
import java.util.List;

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

    this.courseHistoryViewModel.getMeetings().observe(getViewLifecycleOwner(),
            this::meetingsLiveDataCallback);

    this.courseHistoryViewModel.getMeetings().observe(getViewLifecycleOwner(), meetingsModels -> {
      this.courseHistoryViewModel.sortMeetings(meetingsModels, "oldest");
      meetingAdapter.notifyDataSetChanged();
      if (meetingsModels.size() == 0) {
        this.binding.coursesHistoryFragmentTitleTextView.setVisibility(View.VISIBLE);
      } else {
        this.binding.coursesHistoryFragmentTitleTextView.setVisibility(View.GONE);
      }
    });
    this.courseHistoryViewModel.getMeetingsModel()
            .observe(getViewLifecycleOwner(), this::meetingModelInputStateCallback);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void meetingsLiveDataCallback(StateData<List<MeetingsModel>> listStateData) {
    if (listStateData == null) {
      return;
    }
    this.resetBindings();
    this.meetingAdapter.notifyDataSetChanged();

    if (listStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.coursesHistoryFragmentProgressSpinner.setVisibility(View.VISIBLE);
    } else if (listStateData.getStatus() == StateData.DataStatus.ERROR) {
      Toast.makeText(getContext(),
              listStateData.getError().getMessage(), Toast.LENGTH_SHORT).show();
    }
    if (listStateData.getData().size() == 0) {
      this.binding.coursesHistoryFragmentTitleTextView.setVisibility(View.VISIBLE);
    } else {
      this.binding.coursesHistoryFragmentTitleTextView.setVisibility(View.GONE);
    }
  }

  private void meetingModelInputStateCallback(StateData<MeetingsModel> meetingsModelStateData) {
    if (meetingsModelStateData != null) {
      KeyboardUtil.hideKeyboard(getActivity());
    }
  }

  private void resetBindings() {
    this.binding.coursesHistoryFragmentProgressSpinner.setVisibility(View.GONE);
  }


  private void connectAdapter() {
    this.meetingAdapter =
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
    this.courseHistoryViewModel.resetMeetingData();
  }

}