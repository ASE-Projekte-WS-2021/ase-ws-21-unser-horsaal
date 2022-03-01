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
import com.example.unserhoersaal.adapter.ParticipantAdapter;
import com.example.unserhoersaal.databinding.FragmentCourseParticipantsBinding;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.example.unserhoersaal.viewmodel.CourseParticipantsViewModel;

/** Course-Participants.*/
public class CourseParticipantsFragment extends Fragment {

  private static final String TAG = "CourseParticipantsFragment";

  private CourseParticipantsViewModel courseParticipantsViewModel;
  private FragmentCourseParticipantsBinding binding;
  private ParticipantAdapter participantAdapter;
  private NavController navController;

  public CourseParticipantsFragment() {
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
            R.layout.fragment_course_participants, container,false);
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

  private void initViewModel() {
    this.courseParticipantsViewModel = new ViewModelProvider(requireActivity())
            .get(CourseParticipantsViewModel.class);
    this.courseParticipantsViewModel.init();
  }

  private void connectAdapter() {
    this.participantAdapter = new ParticipantAdapter();
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.courseParticipantsViewModel);
    this.binding.setAdapter(this.participantAdapter);
  }

  private void initToolbar() {
    this.binding.courseParticipantsFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseParticipantsFragmentToolbar
            .setNavigationOnClickListener(v -> navController
            .navigate(R.id.action_courseParticipantsFragment_to_courseDescriptionFragment));
  }
}