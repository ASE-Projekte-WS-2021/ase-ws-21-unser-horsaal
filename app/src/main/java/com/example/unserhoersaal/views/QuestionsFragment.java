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
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.ThreadAdapter;
import com.example.unserhoersaal.databinding.FragmentQuestionsBinding;
import com.example.unserhoersaal.enums.FilterEnum;
import com.example.unserhoersaal.enums.SortEnum;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unserhoersaal.viewmodel.QuestionsViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Displays the questions during the Meeting. */
public class QuestionsFragment extends Fragment {

  private static final String TAG = "QuestionFragment";

  private FragmentQuestionsBinding binding;
  private QuestionsViewModel questionsViewModel;
  private CurrentCourseViewModel currentCourseViewModel;
  private ThreadAdapter threadAdapter;

  public QuestionsFragment() {
    //Required empty public constructor
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_questions, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.initViewModel();
    this.connectAdapter();
    this.connectBinding();
  }

  private void initViewModel() {
    this.questionsViewModel = new ViewModelProvider(requireActivity())
            .get(QuestionsViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.questionsViewModel.init();
    this.currentCourseViewModel.init();

    this.questionsViewModel.getThreads().observe(getViewLifecycleOwner(),
            this::meetingsLiveStateCallback);
    /*this.questionsViewModel.getThreadModel().observe(getViewLifecycleOwner(),
            this::threadLiveStateCallback);*/
    this.questionsViewModel.getSortEnum().observe(getViewLifecycleOwner(),
            this::sortEnumCallback);
    this.questionsViewModel.getFilterEnum().observe(getViewLifecycleOwner(),
            this::filterEnumCallback);
  }

  @SuppressLint("NotifyDataSetChanged")
  private void meetingsLiveStateCallback(StateData<List<ThreadModel>> listStateData) {
    this.resetBindings();
    this.questionsViewModel.filterThreads(listStateData.getData());
    this.questionsViewModel.sortThreads(listStateData.getData());
    this.threadAdapter.notifyDataSetChanged();

    if (listStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.questionFragmentProgressSpinner.setVisibility(View.VISIBLE);
    } else if (listStateData.getStatus() == StateData.DataStatus.ERROR) {
      Toast.makeText(getContext(),
              listStateData.getError().getMessage(), Toast.LENGTH_SHORT).show();
    }
    if (listStateData.getData().size() == 0) {
      this.binding.questionFragmentTitleTextView.setVisibility(View.VISIBLE);
    } else {
      this.binding.questionFragmentTitleTextView.setVisibility(View.GONE);
    }
  }

  //TODO why here? changed to in createThread
  private void threadLiveStateCallback(StateData<ThreadModel> threadModelStateData) {
    this.resetBindings();

    if (threadModelStateData.getData() != null) {
      KeyboardUtil.hideKeyboard(getActivity());
      this.currentCourseViewModel.setThreadId(threadModelStateData.getData().getKey());
      this.questionsViewModel.resetThreadModelInput();
      this.binding.questionFragmentFab.setVisibility(View.VISIBLE);
      //this.navController.navigate(R.id.action_courseMeetingFragment_to_courseThreadFragment);
    }
  }

  private void sortEnumCallback(StateData<SortEnum> sortEnum) {
    if (sortEnum.getData() != SortEnum.NEWEST) {
      this.binding.questionChipNewest.setChecked(Boolean.FALSE);
      this.binding.questionChipNewestActivated.setVisibility(View.GONE);
    }
    if (sortEnum.getData() != SortEnum.MOST_LIKES) {
      this.binding.questionChipMostLiked.setChecked(Boolean.FALSE);
      this.binding.questionChipMostLikedActivated.setVisibility(View.GONE);
    }
    if (sortEnum.getData() != SortEnum.MOST_COMMENTED) {
      this.binding.questionChipMostCommented.setChecked(Boolean.FALSE);
      this.binding.questionChipMostCommentedActivated.setVisibility(View.GONE);
    }
    if (sortEnum.getData() != SortEnum.PAGE_COUNT_UP) {
      this.binding.questionChipPageCountUp.setChecked(Boolean.FALSE);
      this.binding.questionChipPageCountUpActivated.setVisibility(View.GONE);
    }
    if (sortEnum.getData() != SortEnum.PAGE_COUNT_DOWN) {
      this.binding.questionChipPageCountDown.setChecked(Boolean.FALSE);
      this.binding.questionChipPageCountDownActivated.setVisibility(View.GONE);
    }

    this.questionsViewModel.getThreads().postUpdate(this.questionsViewModel.getThreads()
            .getValue().getData());
  }

  private void filterEnumCallback(StateData<FilterEnum> filterEnum) {
    if (filterEnum.getData() == FilterEnum.SOLVED) {
      this.binding.questionChipUnanswered.setChecked(Boolean.FALSE);
      this.binding.questionChipUnansweredActivated.setVisibility(View.GONE);
    }
    if (filterEnum.getData() == FilterEnum.UNSOLVED) {
      this.binding.questionChipAnswered.setChecked(Boolean.FALSE);
      this.binding.questionChipAnsweredActivated.setVisibility(View.GONE);
    }

    this.questionsViewModel.getThreads().postUpdate(this.questionsViewModel
            .getThreads().getValue().getData());
  }

  private void resetBindings() {
    this.binding.questionFragmentProgressSpinner.setVisibility(View.GONE);
  }

  private void resetFilterChips() {
    if (this.binding.questionChipAnswered.isChecked()) {
      this.binding.questionChipAnswered.setChecked(Boolean.FALSE);
      this.binding.questionChipAnsweredActivated.setVisibility(View.GONE);
    }
    if (this.binding.questionChipUnanswered.isChecked()) {
      this.binding.questionChipUnanswered.setChecked(Boolean.FALSE);
      this.binding.questionChipUnansweredActivated.setVisibility(View.GONE);
    }
    if (this.binding.questionChipOwn.isChecked()) {
      this.binding.questionChipOwn.setChecked(Boolean.FALSE);
      this.binding.questionChipOwnActivated.setVisibility(View.GONE);
    }
    if (this.binding.questionChipCreator.isChecked()) {
      this.binding.questionChipCreator.setChecked(Boolean.FALSE);
      this.binding.questionChipCreatorActivated.setVisibility(View.GONE);
    }
    if (this.binding.questionChipLecturePeriod.isChecked()) {
      this.binding.questionChipLecturePeriod.setChecked(Boolean.FALSE);
      this.binding.questionChipLecturePeriodActivated.setVisibility(View.GONE);
    }
    if (this.binding.questionChipSubjectMatter.isChecked()) {
      this.binding.questionChipSubjectMatter.setChecked(Boolean.FALSE);
      this.binding.questionChipSubjectMatterActivated.setVisibility(View.GONE);
    }
    if (this.binding.questionChipOrganisation.isChecked()) {
      this.binding.questionChipOrganisation.setChecked(Boolean.FALSE);
      this.binding.questionChipOrganisationActivated.setVisibility(View.GONE);
    }
    if (this.binding.questionChipMistake.isChecked()) {
      this.binding.questionChipMistake.setChecked(Boolean.FALSE);
      this.binding.questionChipMistakeActivated.setVisibility(View.GONE);
    }
    if (this.binding.questionChipExamination.isChecked()) {
      this.binding.questionChipExamination.setChecked(Boolean.FALSE);
      this.binding.questionChipExaminationActivated.setVisibility(View.GONE);
    }
    if (this.binding.questionChipOther.isChecked()) {
      this.binding.questionChipOther.setChecked(Boolean.FALSE);
      this.binding.questionChipOtherActivated.setVisibility(View.GONE);
    }
  }

  private void connectAdapter() {
    this.threadAdapter =
            new ThreadAdapter(this.questionsViewModel.getThreads().getValue().getData(),
                    this.currentCourseViewModel);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.questionsViewModel);
    this.binding.setAdapter(this.threadAdapter);
  }

  @Override
  public void onResume() {
    super.onResume();
    this.questionsViewModel.resetThreadModelInput();
    this.questionsViewModel.setSortEnum(SortEnum.NEWEST);
  }

  @Override
  public void onPause() {
    super.onPause();
    this.questionsViewModel.resetFilters();
    this.resetFilterChips();
  }
}