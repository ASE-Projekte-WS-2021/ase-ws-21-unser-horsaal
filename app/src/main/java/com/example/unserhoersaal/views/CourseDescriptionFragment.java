package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.unserhoersaal.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.NavigableMap;

/** Course-Description.*/
public class CourseDescriptionFragment extends Fragment {

  TextView courseCreator;
  TextView courseInstitution;
  TextView courseDescription;
  RelativeLayout courseDescriptionContainer;
  ImageView expandParticipantsIcon;
  FloatingActionButton shareCourseFab;
  MaterialToolbar toolbar;
  NavController navController;
  Button unsubscribeButton;

  public CourseDescriptionFragment() {
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
    return inflater.inflate(R.layout.fragment_course_description, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initUi(view);
    initToolbar();
  }

  private void initUi(View view){
    courseCreator = view.findViewById(R.id.courseDescriptionCreator);
    courseInstitution = view.findViewById(R.id.courseDescriptionFragmentInstitution);
    courseDescription = view.findViewById(R.id.courseDescriptionFragmentCourseDescription);
    courseDescriptionContainer = view.findViewById(R.id.courseDescriptionFragmentDescriptionContainer);
    expandParticipantsIcon = view.findViewById(R.id.courseDescriptionFragmentExpandParticipantsIcon);
    shareCourseFab = view.findViewById(R.id.courseDescriptionFragmentShareCourseFab);
    toolbar = view.findViewById(R.id.courseDescriptionFragmentToolbar);
    navController = Navigation.findNavController(view);
    unsubscribeButton = view.findViewById(R.id.courseDescriptionFragmentUnsubscribeButton);
  }

  private void initToolbar(){
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    toolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_courseDescriptionFragment_to_courseHistoryFragment);
    });
  }

}