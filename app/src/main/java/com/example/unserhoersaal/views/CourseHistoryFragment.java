package com.example.unserhoersaal.views;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** Fragment contains list of course-meetings.*/
public class CourseHistoryFragment extends Fragment {

  MaterialToolbar toolbar;
  RecyclerView coursesRecyclerView;
  FloatingActionButton floatingActionButton;
  NavController navController;
  ScrollView createMeetingContainer;
  EditText createMeetingTitle;
  EditText createMeetingDate;
  EditText createMeetingTime;
  MaterialButton createMeetingCreateButton;

  public CourseHistoryFragment() {
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
    return inflater.inflate(R.layout.fragment_course_history, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initUi(view);
    setupToolbar();
  }

  private void initUi(View view) {
    toolbar = view.findViewById(R.id.courseHistoryFragmentToolbar);
    coursesRecyclerView = view.findViewById(R.id.courseHistoryFragmentCoursesRecyclerView);
    floatingActionButton = view.findViewById(R.id.courseHistoryFragmentFab);
    floatingActionButton.setOnClickListener(v -> {
      onFloatingActionButtonClicked();
    });
    navController = Navigation.findNavController(view);
    createMeetingContainer = view.findViewById(R.id.courseHistoryFragmentCreateMeetingContainer);
    createMeetingTitle = view.findViewById(R.id.courseHistoryFragmentCreateMeetingTitleEditText);
    createMeetingDate = view.findViewById(R.id.courseHistoryFragmentCreateMeetingDateEditText);
    createMeetingTime = view.findViewById(R.id.courseHistoryFragmentCreateMeetingTimeEditText);
    createMeetingCreateButton = view.findViewById(R.id
            .courseHistoryFragmentCreateMeetingCreateButton);
  }

  private void setupNavigation() {

  }

  private void setupToolbar() {
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    toolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_courseHistoryFragment_to_coursesFragment);
    });
  }

  private void onFloatingActionButtonClicked() {
    if (createMeetingContainer.getVisibility() == View.VISIBLE) {
      KeyboardUtil.hideKeyboard(getActivity());
      createMeetingContainer.setVisibility(View.GONE);
      floatingActionButton.setImageResource(R.drawable.ic_baseline_add_24);
      floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources()
              .getColor(R.color.orange, null)));
      createMeetingTitle.getText().clear();
      createMeetingDate.getText().clear();
      createMeetingTime.getText().clear();
    } else {
      createMeetingContainer.setVisibility(View.VISIBLE);
      floatingActionButton.setImageResource(R.drawable.ic_baseline_close_24);
      floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources()
              .getColor(R.color.red, null)));
    }
  }
}