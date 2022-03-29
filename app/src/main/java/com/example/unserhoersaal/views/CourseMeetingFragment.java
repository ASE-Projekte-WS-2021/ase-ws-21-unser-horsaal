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
import com.example.unserhoersaal.adapter.MeetingsViewPagerAdapter;
import com.example.unserhoersaal.databinding.FragmentCourseMeetingBinding;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;
import com.example.unserhoersaal.viewmodel.LiveChatViewModel;
import com.example.unserhoersaal.viewmodel.PollViewModel;
import com.example.unserhoersaal.viewmodel.QuestionsViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/** Course-Meeting. */
public class CourseMeetingFragment extends Fragment {

  private static final String TAG = "CourseMeetingFragment";

  private MeetingsViewPagerAdapter meetingsViewPagerAdapter;

  private FragmentCourseMeetingBinding binding;
  private CourseMeetingViewModel courseMeetingViewModel;
  private QuestionsViewModel questionsViewModel;
  private PollViewModel pollViewModel;
  private LiveChatViewModel liveChatViewModel;
  private NavController navController;

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
    this.setTabs();
    this.initToolbar();
  }

  /** Initialise all ViewModels for the Fragment. */
  public void initViewModel() {
    this.courseMeetingViewModel = new ViewModelProvider(requireActivity())
            .get(CourseMeetingViewModel.class);
    this.questionsViewModel = new ViewModelProvider(requireActivity())
            .get(QuestionsViewModel.class);
    this.pollViewModel = new ViewModelProvider(requireActivity())
            .get(PollViewModel.class);
    this.liveChatViewModel = new ViewModelProvider(requireActivity())
            .get(LiveChatViewModel.class);
    this.courseMeetingViewModel.init();

  /* filter
    this.currentCourseViewModel.init();

    this.courseMeetingViewModel.getThreads().observe(getViewLifecycleOwner(),
            this::meetingsLiveStateCallback);
    this.courseMeetingViewModel.getThreadModel().observe(getViewLifecycleOwner(),
            this::threadLiveStateCallback);
    this.courseMeetingViewModel.getSortEnum().observe(getViewLifecycleOwner(),
            this::sortEnumCallback);
    this.courseMeetingViewModel.getFilterEnum().observe(getViewLifecycleOwner(),
            this::filterEnumCallback);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void meetingsLiveStateCallback(StateData<List<ThreadModel>> listStateData) {
    this.resetBindings();
    this.courseMeetingViewModel.filterThreads(listStateData.getData());
    this.courseMeetingViewModel.sortThreads(listStateData.getData());
    this.threadAdapter.notifyDataSetChanged();

    if (listStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.courseMeetingFragmentProgressSpinner.setVisibility(View.VISIBLE);
    } else if (listStateData.getStatus() == StateData.DataStatus.ERROR) {
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
      this.binding.courseMeetingFragmentFab.setVisibility(View.VISIBLE);
      this.navController.navigate(R.id.action_courseMeetingFragment_to_courseThreadFragment);
    }
  }

  private void sortEnumCallback(StateData<SortEnum> sortEnum) {
    if (sortEnum.getData() != SortEnum.NEWEST) {
      this.binding.courseMeetingChipNewest.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipNewestActivated.setVisibility(View.GONE);
    }
    if (sortEnum.getData() != SortEnum.MOST_LIKES) {
      this.binding.courseMeetingChipMostLiked.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipMostLikedActivated.setVisibility(View.GONE);
    }
    if (sortEnum.getData() != SortEnum.MOST_COMMENTED) {
      this.binding.courseMeetingChipMostCommented.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipMostCommentedActivated.setVisibility(View.GONE);
    }
    if (sortEnum.getData() != SortEnum.PAGE_COUNT_UP) {
      this.binding.courseMeetingChipPageCountUp.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipPageCountUpActivated.setVisibility(View.GONE);
    }
    if (sortEnum.getData() != SortEnum.PAGE_COUNT_DOWN) {
      this.binding.courseMeetingChipPageCountDown.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipPageCountDownActivated.setVisibility(View.GONE);
    }

    //TODO: is there a better solution to trigger the callback funtion for threads?
    this.courseMeetingViewModel.getThreads().postUpdate(this.courseMeetingViewModel.getThreads()
            .getValue().getData());
  }

  private void filterEnumCallback(StateData<FilterEnum> filterEnum) {
    if (filterEnum.getData() != FilterEnum.SOLVED) {
      this.binding.courseMeetingChipAnswered.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipAnsweredActivated.setVisibility(View.GONE);
    }
    if (filterEnum.getData() != FilterEnum.UNSOLVED) {
      this.binding.courseMeetingChipUnanswered.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipUnansweredActivated.setVisibility(View.GONE);
    }
    if (filterEnum.getData() != FilterEnum.OWN) {
      this.binding.courseMeetingChipOwn.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipOwnActivated.setVisibility(View.GONE);
    }
    if (filterEnum.getData() != FilterEnum.CREATOR) {
      this.binding.courseMeetingChipCreator.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipCreatorActivated.setVisibility(View.GONE);
    }
    if (filterEnum.getData() != FilterEnum.SUBJECT_MATTER) {
      this.binding.courseMeetingChipSubjectMatter.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipSubjectMatterActivated.setVisibility(View.GONE);
    }
    if (filterEnum.getData() != FilterEnum.ORGANISATION) {
      this.binding.courseMeetingChipOrganisation.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipOrganisationActivated.setVisibility(View.GONE);
    }
    if (filterEnum.getData() != FilterEnum.MISTAKE) {
      this.binding.courseMeetingChipMistake.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipMistakeActivated.setVisibility(View.GONE);
    }
    if (filterEnum.getData() != FilterEnum.EXAMINATION) {
      this.binding.courseMeetingChipExamination.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipExaminationActivated.setVisibility(View.GONE);
    }
    if (filterEnum.getData() != FilterEnum.OTHER) {
      this.binding.courseMeetingChipOther.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipOtherActivated.setVisibility(View.GONE);
    }

    //TODO: is there a better solution to trigger the callback funtion for threads?
    this.courseMeetingViewModel.getThreads().postUpdate(this.courseMeetingViewModel
            .getThreads().getValue().getData());
  }

  private void resetBindings() {
    this.binding.courseMeetingFragmentProgressSpinner.setVisibility(View.GONE);*/

    this.questionsViewModel.init();
    this.pollViewModel.init();
    this.liveChatViewModel.init();
    this.courseMeetingViewModel.getMeeting().observe(getViewLifecycleOwner(),
            this::setMeetingForTabs);
  }

  private void setMeetingForTabs(StateData<MeetingsModel> meetingStateData) {
    this.questionsViewModel.setMeeting(meetingStateData.getData());
    this.pollViewModel.setMeeting(meetingStateData.getData());
    this.liveChatViewModel.setMeeting(meetingStateData.getData());

  }

  private void connectAdapter() {
    this.meetingsViewPagerAdapter = new MeetingsViewPagerAdapter(this);
    this.binding.courseMeetingFragmentViewPager.setAdapter(meetingsViewPagerAdapter);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.courseMeetingViewModel);
  }

  private void setTabs() {
    new TabLayoutMediator(this.binding.courseMeetingFragmentTabLayout,
            this.binding.courseMeetingFragmentViewPager, this::designTab).attach();
  }

  private void designTab(TabLayout.Tab tab, int position) {
    tab.setText(courseMeetingViewModel.getTabTitle(position));
  }

  /*filter
  @Override
  public void onResume() {
    super.onResume();
    this.courseMeetingViewModel.resetThreadModelInput();
    this.courseMeetingViewModel.setFilterEnum(FilterEnum.NONE);
    this.courseMeetingViewModel.setSortEnum(SortEnum.NEWEST);
  }*/

  private void initToolbar() {
    this.binding.courseMeetingFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseMeetingFragmentToolbar
            .setNavigationOnClickListener(v -> this.navController.navigateUp());
  }

}