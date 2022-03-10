package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
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
import com.example.unserhoersaal.adapter.ChatAdapter;
import com.example.unserhoersaal.databinding.FragmentCourseThreadBinding;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;

/**Course Thread.*/
public class CourseThreadFragment extends Fragment {

  private static final String TAG = "CourseThreadFragment";
  private NavController navController;
  private FragmentCourseThreadBinding binding;
  private ChatAdapter chatAdapter;
  private CurrentCourseViewModel currentCourseViewModel;

  public CourseThreadFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_course_thread, container, false);
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

  @SuppressLint("NotifyDataSetChanged")
  private void initViewModel() {
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.currentCourseViewModel.init();
    this.currentCourseViewModel.getMessages()
            .observe(getViewLifecycleOwner(), messageModels -> {
              KeyboardUtil.hideKeyboard(getActivity());
              chatAdapter.notifyDataSetChanged();
              this.currentCourseViewModel.messageModelInputState.postValue(new StateData<>(new MessageModel()));
            });
  }

  private void connectAdapter() {
    this.chatAdapter =
            //TODO: assert != null
            new ChatAdapter(this.currentCourseViewModel.getMessages().getValue().getData(),
                    this.currentCourseViewModel);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.currentCourseViewModel);
    this.binding.setAdapter(this.chatAdapter);
  }

  private void initToolbar() {
    this.binding.currentCourseFragmentToolbar
            .inflateMenu(R.menu.course_thread_fragment_toolbar);
    this.binding.currentCourseFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.currentCourseFragmentToolbar
            .setNavigationOnClickListener(v ->
                    navController.navigate(
                            R.id.action_courseThreadFragment_to_courseMeetingFragment)
    );
  }

}