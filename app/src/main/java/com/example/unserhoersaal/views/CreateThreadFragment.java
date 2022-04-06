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
import com.example.unserhoersaal.databinding.FragmentCreateThreadBinding;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unserhoersaal.viewmodel.QuestionsViewModel;

/**Create Thread.*/
public class CreateThreadFragment extends Fragment {

  private static final String TAG = "CreateThreadFragment";

  private FragmentCreateThreadBinding binding;
  private NavController navController;
  private QuestionsViewModel questionsViewModel;
  private CurrentCourseViewModel currentCourseViewModel;

  public CreateThreadFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_thread, container,
            false);
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
    this.questionsViewModel = new ViewModelProvider(requireActivity())
            .get(QuestionsViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.questionsViewModel.init();
    this.currentCourseViewModel.init();
    this.questionsViewModel.getThreadModel().observe(getViewLifecycleOwner(), threadModel -> {
      if (threadModel.getData() != null) {
        KeyboardUtil.hideKeyboard(getActivity());
        this.currentCourseViewModel.setThread(threadModel.getData());
        this.questionsViewModel.resetThreadModelInput();
        this.navController.navigate(R.id.action_createThreadFragment_to_courseThreadFragment);
      }
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.questionsViewModel);
  }

  private void initToolbar() {
    this.binding.createThreadFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.createThreadFragmentToolbar
            .setNavigationOnClickListener(v -> navController.navigateUp());
  }
}