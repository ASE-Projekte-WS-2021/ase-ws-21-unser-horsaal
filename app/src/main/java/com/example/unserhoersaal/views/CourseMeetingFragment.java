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

  private void initToolbar() {
    this.binding.courseMeetingFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseMeetingFragmentToolbar
            .setNavigationOnClickListener(v -> this.navController.navigateUp());
  }

}