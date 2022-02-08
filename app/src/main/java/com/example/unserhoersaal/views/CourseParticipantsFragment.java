package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unserhoersaal.R;
import com.google.android.material.appbar.MaterialToolbar;

/** Course-Participants.*/
public class CourseParticipantsFragment extends Fragment {

  MaterialToolbar toolbar;
  RecyclerView participantsList;
  NavController navController;

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
    initUi(view);
    initToolbar();
  }

  private void initUi(View view){
    toolbar = view.findViewById(R.id.courseParticipantsFragmentToolbar);
    participantsList = view.findViewById(R.id.courseParticipantsParticipantsRecycler);
    navController = Navigation.findNavController(view);
  }

  private void initToolbar(){
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    toolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_courseParticipantsFragment_to_courseDescriptionFragment);
    });
  }
}