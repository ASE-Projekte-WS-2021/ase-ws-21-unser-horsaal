package com.example.unserhoersaal.views;

import android.graphics.Color;
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
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
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
    this.connectBinding();
    this.initToolbar();
  }

  private void initViewModel() {
    this.courseHistoryViewModel = new ViewModelProvider(requireActivity())
            .get(CourseHistoryViewModel.class);
    this.courseHistoryViewModel.init();
    this.courseHistoryViewModel.getCurrentMeetingRepoState()
            .observe(getViewLifecycleOwner(), this::currentMeetingRepoStateCallback);
  }

  private void currentMeetingRepoStateCallback(StateData<MeetingsModel> meetingsModelStateData) {
    this.resetBindings();
    KeyboardUtil.hideKeyboard(getActivity());

    if (meetingsModelStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.createCourseMeetingFragmentProgressSpinner.setVisibility(View.VISIBLE);
      this.binding.createCourseMeetingFragmentButton.setEnabled(false);
      this.binding.createCourseMeetingFragmentButton.setBackgroundColor(Color.GRAY);
    } else if (meetingsModelStateData.getStatus() == StateData.DataStatus.ERROR) {
      if (meetingsModelStateData.getErrorTag() == ErrorTag.TITLE) {
        this.binding.createCourseMeetingFragmentTitleErrorText
                .setText(meetingsModelStateData.getError().getMessage());
        this.binding.createCourseMeetingFragmentTitleErrorText.setVisibility(View.VISIBLE);
      }
    } else if (meetingsModelStateData.getStatus() == StateData.DataStatus.UPDATE) {
      this.navController.navigate(R.id.action_createCourseMeetingFragment_to_courseHistoryFragment);
    }
  }

  private void resetBindings() {
    this.binding.createCourseMeetingFragmentTitleErrorText.setVisibility(View.GONE);
    this.binding.createCourseMeetingFragmentGeneralErrorText.setVisibility(View.GONE);
    this.binding.createCourseMeetingFragmentProgressSpinner.setVisibility(View.GONE);
    this.binding.createCourseMeetingFragmentButton.setEnabled(true);
    this.binding.createCourseMeetingFragmentButton.setTextAppearance(R.style.wideBlueButton);
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