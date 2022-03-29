package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import com.example.unserhoersaal.adapter.ThreadAdapter;
import com.example.unserhoersaal.databinding.FragmentCourseMeetingBinding;
import com.example.unserhoersaal.enums.FilterEnum;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.enums.SortEnum;
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
    this.initSearchView();
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
    this.courseMeetingViewModel.getSortEnum().observe(getViewLifecycleOwner(),
            this::sortEnumCallback);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void meetingsLiveStateCallback(StateData<List<ThreadModel>> listStateData) {
    this.resetBindings();
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

  private void resetBindings() {
    this.binding.courseMeetingFragmentProgressSpinner.setVisibility(View.GONE);
  }

  private void connectAdapter() {
    this.threadAdapter =
            new ThreadAdapter(this.courseMeetingViewModel.getThreads().getValue().getData(),
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
    this.binding.courseMeetingFragmentToolbar.setOnMenuItemClickListener(item -> {
      if (item.getItemId() == R.id.courseMeetingToolbarFilter){
        Log.d(TAG, "there");
        toggleFilterContainer();
      }
      return true;
    });
  }

  private void initSearchView() {
    SearchView searchView = (SearchView) this.binding
            .courseMeetingFragmentToolbar
            .getMenu()
            .findItem(R.id.courseMeetingToolbarSearch)
            .getActionView();
    searchView.setQueryHint(Config.SEARCH_VIEW_HINT);
    searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        threadAdapter.getFilter().filter(newText);
        return false;
      }
    });
  }

  private void toggleFilterContainer(){
    View container = this.binding.courseMeetingFragmentAvailableChipsContainer;
    if (container.getVisibility() == View.GONE) {
      container.setVisibility(View.VISIBLE);
    } else {
      container.setVisibility(View.GONE);
    }
  }

  //Todo: in Arbeit
  private void filterThreads(StateData<FilterEnum> filterEnum) {
    this.courseMeetingViewModel.getThreads().postUpdate(this.courseMeetingViewModel.getFullList());
  }

  @Override
  public void onResume() {
    super.onResume();
    this.courseMeetingViewModel.resetThreadModelInput();
  }

}