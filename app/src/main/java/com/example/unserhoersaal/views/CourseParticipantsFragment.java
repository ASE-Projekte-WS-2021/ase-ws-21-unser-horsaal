package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.google.android.material.appbar.MaterialToolbar;

/** Course-Participants.*/
public class CourseParticipantsFragment extends Fragment {

  private static final String TAG = "CourseParticipantsFragment";

  private MaterialToolbar toolbar;
  private RecyclerView participantsList;


  private NavController navController;

  public CourseParticipantsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_course_participants, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initUi(view);
    this.initToolbar();
  }

  private void initUi(View view) {
    this.toolbar = view.findViewById(R.id.courseParticipantsFragmentToolbar);
    this.participantsList = view.findViewById(R.id.courseParticipantsParticipantsRecycler);
    this.navController = Navigation.findNavController(view);
  }

  private void initToolbar() {
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.toolbar.setNavigationOnClickListener(v -> {
      this.navController
              .navigate(R.id.action_courseParticipantsFragment_to_courseDescriptionFragment);
    });
  }
}