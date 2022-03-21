package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.ThreadAdapter;
import com.example.unserhoersaal.databinding.FragmentCourseMeetingBinding;
import com.example.unserhoersaal.enums.SortEnum;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;

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
  @SuppressLint("NotifyDataSetChanged")
  public void initViewModel() {
    this.courseMeetingViewModel = new ViewModelProvider(requireActivity())
            .get(CourseMeetingViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.courseMeetingViewModel.init();
    this.currentCourseViewModel.init();
    this.courseMeetingViewModel.getThreads().observe(getViewLifecycleOwner(), messageList -> {
      this.courseMeetingViewModel.sortThreadsByLikes(messageList);
      threadAdapter.notifyDataSetChanged();
      if (messageList.size() == 0) {
        this.binding.coursesMeetingFragmentTitleTextView.setVisibility(View.VISIBLE);
      } else {
        this.binding.coursesMeetingFragmentTitleTextView.setVisibility(View.GONE);
      }
    });
    //TODO better way than observing
    this.courseMeetingViewModel.getSortEnum().observe(getViewLifecycleOwner(), this::toggleChips);
  }

  private void connectAdapter() {
    this.threadAdapter =
            new ThreadAdapter(this.courseMeetingViewModel.getThreads().getValue(),
                    currentCourseViewModel);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.courseMeetingViewModel);
    this.binding.setAdapter(this.threadAdapter);
  }

  private void initToolbar() {
    this.binding.courseMeetingFragmentToolbar.inflateMenu(R.menu.course_meeting_menu);
    this.binding.courseMeetingFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseMeetingFragmentToolbar
            .setNavigationOnClickListener(v ->
                    this.navController.navigate(
                            R.id.action_courseMeetingFragment_to_courseHistoryFragment)
    );
    this.binding.courseMeetingFragmentToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.courseMeetingToolbarFilter){
          toggleFilterContainer();
        }
        return true;
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    this.courseMeetingViewModel.resetThreadModelInput();
  }

  private void toggleFilterContainer(){
    View container = this.binding.courseMeetingFragmentAvailableChipsContainer;
    if (container.getVisibility() == View.GONE) {
      container.setVisibility(View.VISIBLE);
    } else {
      container.setVisibility(View.GONE);
    }
  }

  private void toggleChips(SortEnum sortEnum) {
    if (sortEnum != SortEnum.NEWEST) {
      this.binding.courseMeetingChipNewest.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipNewestActivated.setVisibility(View.GONE);
    }
    if (sortEnum != SortEnum.MOST_LIKES) {
      this.binding.courseMeetingChipMostLiked.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipMostLikedActivated.setVisibility(View.GONE);
    }
    if (sortEnum != SortEnum.MOST_COMMENTED) {
      this.binding.courseMeetingChipMostCommented.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipMostCommentedActivated.setVisibility(View.GONE);
    }
    if (sortEnum != SortEnum.PAGE_COUNT_UP) {
      this.binding.courseMeetingChipPageCountUp.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipPageCountUpActivated.setVisibility(View.GONE);
    }
    if (sortEnum != SortEnum.PAGE_COUNT_DOWN) {
      this.binding.courseMeetingChipPageCountDown.setChecked(Boolean.FALSE);
      this.binding.courseMeetingChipPageCountDownActivated.setVisibility(View.GONE);
    }
  }
}