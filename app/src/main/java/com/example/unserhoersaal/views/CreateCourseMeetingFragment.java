package com.example.unserhoersaal.views;

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
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentCreateCourseMeetingBinding;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.Format;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
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
    this.initEditing();
    this.connectBinding();
    this.initToolbar();
  }

  private void initViewModel() {
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.courseHistoryViewModel.init();
    this.courseHistoryViewModel.getMeetingsModel()
            .observe(getViewLifecycleOwner(), this::meetingModelLiveDataCallback);
  }


  private void initEditing() {
    if (courseHistoryViewModel.getIsEditing()) {
      this.changeTextToEdit();
    } else {
      this.changeTextToCreate();
    }
  }

  private void meetingModelLiveDataCallback(StateData<MeetingsModel> meetingsModelStateData) {
    KeyboardUtil.hideKeyboard(getActivity());

    if (meetingsModelStateData == null) {
      Toast.makeText(getContext(), Config.UNSPECIFIC_ERROR, Toast.LENGTH_SHORT).show();
      return;
    }

   if (meetingsModelStateData.getStatus() == StateData.DataStatus.UPDATE) {
      this.navController.navigate(R.id.action_createCourseMeetingFragment_to_courseHistoryFragment);
    }
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.courseHistoryViewModel);
  }

  private void initToolbar() {
    this.binding.createCourseMeetingFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.createCourseMeetingFragmentToolbar.setNavigationOnClickListener(v ->
            this.navController.navigateUp());
  }

  private void changeTextToEdit() {
    this.binding.createCourseMeetingFragmentToolbarText.setText(R.string.edit_course_meeting_toolbar_title);
    this.binding.createCourseMeetingFragmentButton.setText(R.string.edit_course_meeting_button);

    this.binding.createCourseMeetingDatePicker
            .setText(Config.DATE_FORMAT.format(courseHistoryViewModel.meetingModelInputState
            .getValue().getData().getEventTime()));
  }

  private void changeTextToCreate() {
    this.binding.createCourseMeetingFragmentToolbarText.setText(R.string.create_course_meeting_toolbar_title);
    this.binding.createCourseMeetingFragmentButton.setText(R.string.create_course_meeting_meeting_button);

    this.binding.createCourseMeetingDatePicker.setText(R.string.current_date_placeholder);
    this.binding.createCourseMeetingTimePicker.setText(R.string.startzeit);
  }

  @Override
  public void onPause() {
    super.onPause();
    this.courseHistoryViewModel.resetMeetingData();
    this.courseHistoryViewModel.setIsEditing(false);
    this.courseHistoryViewModel.setLiveDataComplete();
  }

}