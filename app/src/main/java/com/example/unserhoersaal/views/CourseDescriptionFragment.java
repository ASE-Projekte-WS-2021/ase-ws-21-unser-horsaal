package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** Course-Description.*/
public class CourseDescriptionFragment extends Fragment {

  private static final String TAG = "CourseDescriptionFragment";

  TextView courseCreator;
  TextView courseInstitution;
  TextView courseDescription;
  TextView courseTitle;
  TableRow courseDescriptionContainer;
  TableRow courseParticipantsContainer;
  FloatingActionButton shareCourseFab;
  MaterialToolbar toolbar;
  NavController navController;
  TextView unsubscribeTextView;

  private CourseDescriptionViewModel courseDescriptionViewModel;

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
    initViewModel();
    initUi(view);
    initToolbar();
  }

  private void initViewModel() {
    this.courseDescriptionViewModel = new ViewModelProvider(requireActivity())
            .get(CourseDescriptionViewModel.class);
    this.courseDescriptionViewModel.init();
    this.courseDescriptionViewModel.getCourseModel()
            .observe(getViewLifecycleOwner(), courseModel -> {
              courseInstitution.setText(courseModel.getInstitution());
              courseDescription.setText(courseModel.getDescription());
              courseCreator.setText(courseModel.getCreatorId());
              courseTitle.setText(courseModel.getTitle());
            });
  }

  private void initUi(View view) {
    courseCreator = view.findViewById(R.id.courseDescriptionCreator);
    courseInstitution = view.findViewById(R.id.courseDescriptionFragmentInstitution);
    courseDescription = view.findViewById(R.id.courseDescriptionFragmentCourseDescription);
    courseTitle = view.findViewById(R.id.courseDescriptionCourseTitle);
    courseDescriptionContainer = view
            .findViewById(R.id.courseDescriptionFragmentCourseDescriptionContainer);
    courseParticipantsContainer = view
            .findViewById(R.id.courseDescriptionFragmentCourseParticipantsContainer);
    shareCourseFab = view.findViewById(R.id.courseDescriptionFragmentShareCourseFab);
    toolbar = view.findViewById(R.id.courseDescriptionFragmentToolbar);
    navController = Navigation.findNavController(view);
    unsubscribeTextView = view.findViewById(R.id.courseDescriptionFragmentUnsubscribeTextView);
  }

  private void initToolbar() {
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    toolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_courseDescriptionFragment_to_courseHistoryFragment);
    });
  }

}