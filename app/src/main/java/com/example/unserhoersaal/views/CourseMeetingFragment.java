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
import com.example.unserhoersaal.adapter.ThreadAdapter;
import com.example.unserhoersaal.databinding.FragmentCourseMeetingBinding;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import java.util.List;

/** Course-Meeting. */
public class CourseMeetingFragment extends Fragment {

  private static final String TAG = "CourseMeetingFragment";

  private FragmentCourseMeetingBinding binding;
  private CourseMeetingViewModel courseMeetingViewModel;
  private CurrentCourseViewModel currentCourseViewModel;
  private NavController navController;
  private ThreadAdapter threadAdapter;

  public CourseMeetingFragment() {
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
            R.layout.fragment_course_meeting, container, false);
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

  /** Initialise all ViewModels for the Fragment. */
  public void initViewModel() {
    this.courseMeetingViewModel = new ViewModelProvider(requireActivity())
            .get(CourseMeetingViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.courseMeetingViewModel.init();
    this.currentCourseViewModel.init();
    this.courseMeetingViewModel.getThreads().observe(getViewLifecycleOwner(),
            this::meetingsLiveStateCallback);
    this.courseMeetingViewModel.getThreadModel().observe(getViewLifecycleOwner(),
            this::threadLiveStateCallback);
    this.courseMeetingViewModel.getThreadModelInputState().observe(getViewLifecycleOwner(),
            this::threadModelInputStateCallback);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void meetingsLiveStateCallback(StateData<List<ThreadModel>> listStateData) {
    this.resetBindings();
    this.threadAdapter.notifyDataSetChanged();

    if (listStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.courseMeetingFragmentProgressSpinner.setVisibility(View.VISIBLE);
    }
    else if (listStateData.getStatus() == StateData.DataStatus.ERROR) {
      Toast.makeText(getContext(),
              listStateData.getError().getMessage(), Toast.LENGTH_SHORT).show();
    }
    if (listStateData.getData().size() == 0) {
      this.binding.coursesMeetingFragmentTitleTextView.setVisibility(View.VISIBLE);
    } else {
      this.binding.coursesMeetingFragmentTitleTextView.setVisibility(View.GONE);
    }
  }

  private void threadLiveStateCallback(StateData<ThreadModel> threadModelStateData) {
    this.resetBindings();

    if (threadModelStateData.getData() != null) {
      KeyboardUtil.hideKeyboard(getActivity());
      this.currentCourseViewModel.setThreadId(threadModelStateData.getData().getKey());
      this.courseMeetingViewModel.resetThreadModelInput();
      this.binding.courseMeetingFragmentCreateThreadContainer.setVisibility(View.GONE);
      this.binding.courseMeetingFragmentFab.setVisibility(View.VISIBLE);
      this.navController.navigate(R.id.action_courseMeetingFragment_to_courseThreadFragment);
    }
  }

  private void threadModelInputStateCallback(StateData<ThreadModel> threadModelStateData) {
    this.resetBindings();

    if (threadModelStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.courseMeetingFragmentContainerProgressSpinner.setVisibility(View.VISIBLE);
    }
    else if (threadModelStateData.getStatus() == StateData.DataStatus.ERROR) {
      if (threadModelStateData.getErrorTag() == ErrorTag.TITLE) {
        this.binding.courseMeetingFragmentQuestionTitleErrorText
                .setText(threadModelStateData.getError().getMessage());
        this.binding.courseMeetingFragmentQuestionTitleErrorText.setVisibility(View.VISIBLE);
      }
      else if (threadModelStateData.getErrorTag() == ErrorTag.TEXT) {
        this.binding.courseMeetingFragmentQuestionTextErrorText
                .setText(threadModelStateData.getError().getMessage());
        this.binding.courseMeetingFragmentQuestionTextErrorText.setVisibility(View.VISIBLE);
      }
      else {
        this.binding.courseMeetingFragmentGeneralErrorText
                .setText(threadModelStateData.getError().getMessage());
        this.binding.courseMeetingFragmentGeneralErrorText.setVisibility(View.VISIBLE);
      }
    }
  }

  private void resetBindings() {
    this.binding.courseMeetingFragmentProgressSpinner.setVisibility(View.GONE);
    this.binding.courseMeetingFragmentQuestionTitleErrorText.setVisibility(View.GONE);
    this.binding.courseMeetingFragmentQuestionTextErrorText.setVisibility(View.GONE);
    this.binding.courseMeetingFragmentContainerProgressSpinner.setVisibility(View.GONE);
    this.binding.courseMeetingFragmentGeneralErrorText.setVisibility(View.GONE);
  }

  private void connectAdapter() {
    this.threadAdapter =
            new ThreadAdapter(this.courseMeetingViewModel.getThreads().getValue().getData());
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.courseMeetingViewModel);
    this.binding.setAdapter(this.threadAdapter);
  }

  private void initToolbar() {
    this.binding.courseMeetingFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseMeetingFragmentToolbar
            .setNavigationOnClickListener(v ->
                    this.navController.navigate(
                            R.id.action_courseMeetingFragment_to_courseHistoryFragment)
    );
  }

  @Override
  public void onResume() {
    super.onResume();
    this.courseMeetingViewModel.resetThreadModelInput();
  }

}