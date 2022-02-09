package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

/** Courses. */
public class CoursesFragment extends Fragment implements CoursesAdapter.OnNoteListener {
  private RecyclerView courseListRecycler;
  private TextView titelTextView;
  private CoursesViewModel coursesViewModel;
  private CreateCourseViewModel createCourseViewModel;
  private ArrayList<UserCourse> userCourses;
  private NavController navController;
  private FloatingActionButton fab;
  private FloatingActionButton fabEnterCourse;
  private FloatingActionButton fabCreateCourse;
  private LinearLayout enterCourseLinearLayout;
  private LinearLayout createCourseLinearLayout;
  private Boolean isFabOpen;
  private MaterialToolbar toolbar;

  public CoursesFragment() {
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
    return inflater.inflate(R.layout.fragment_courses, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    coursesViewModel = new ViewModelProvider(requireActivity())
            .get(CoursesViewModel.class);
    createCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CreateCourseViewModel.class);
    coursesViewModel.getUserCourses().observe(getViewLifecycleOwner(), userCourses -> {
      this.userCourses = userCourses;
      updateUi(view);
    });
    initUi(view);
    setupNavigation(view);
    setupToolbar();
    setupFabs();
  }

  private void initUi(View view) {
    titelTextView = view.findViewById(R.id.coursesFragmentTitleTextView);
    fab = view.findViewById(R.id.coursesFragmentFab);
    fabEnterCourse = view.findViewById(R.id.coursesFragmentFabEnterCourse);
    fabCreateCourse = view.findViewById(R.id.coursesFragmentFabCreateCourse);
    enterCourseLinearLayout = view.findViewById(R.id.coursesFragmentEnterCourseFabLayout);
    createCourseLinearLayout = view.findViewById(R.id.coursesFragmentCreateCourseFabLayout);
    toolbar = (MaterialToolbar) view.findViewById(R.id.coursesFragmentToolbar);

    courseListRecycler = view.findViewById(R.id.coursesFragmentRecyclerView);
    CoursesAdapter coursesAdapter = new CoursesAdapter(coursesViewModel.getEmptyCourses(),
            this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    courseListRecycler.setLayoutManager(layoutManager);
    courseListRecycler.setItemAnimator(new DefaultItemAnimator());
    courseListRecycler.setAdapter(coursesAdapter);
    titelTextView.setText("Du bist noch keinen Kursen beigetreten");

  }

  private void setupNavigation(View view) {
    navController = Navigation.findNavController(view);
    fabEnterCourse.setOnClickListener(v -> navController.navigate(
            R.id.action_coursesFragment_to_enterCourseFragment));
    fabCreateCourse.setOnClickListener(v -> navController.navigate(
            R.id.action_coursesFragment_to_createCourseFragment)
    );

    KeyboardUtil.hideKeyboard(getActivity());
  }

  private void updateUi(View view) {
    if (userCourses.size() == 0) {
      titelTextView.setText("Du bist noch keinen Kursen beigetreten");
      titelTextView.setVisibility(View.VISIBLE);
    } else {
      titelTextView.setText("Bereits beigetretene Kurse");
      titelTextView.setVisibility(View.INVISIBLE);
    }
    if (userCourses != null) {
      CoursesAdapter coursesAdapter = new CoursesAdapter(this.userCourses, this);
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
      courseListRecycler.setLayoutManager(layoutManager);
      courseListRecycler.setItemAnimator(new DefaultItemAnimator());
      courseListRecycler.setAdapter(coursesAdapter);
      courseListRecycler.scrollToPosition(this.userCourses.size() - 1);
    }
  }

  @Override
  public void onNoteClick(int position) {
    createCourseViewModel.setCourseId(userCourses.get(position).key);
    navController.navigate(R.id.action_coursesFragment_to_currentCourseFragment);

  }

  private void setupToolbar() {
    toolbar.inflateMenu(R.menu.courses_fragment_toolbar);
    Menu menu = toolbar.getMenu();
    toolbar.setNavigationIcon(R.drawable.ic_baseline_account_circle_24);
    toolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_coursesFragment_to_profileFragment);
    });
  }

  /** Source: https://mobikul.com/expandable-floating-action-button-fab-menu
   * /#:~:text=A%20Floating%20Action%20Button(fab,common%20actions%20within%20an%20app.*/

  private void setupFabs() {
    isFabOpen = false;
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!isFabOpen) {
          showFabMenu();
        } else {
          closeFabMenu();
        }
      }
    });
  }

  private void showFabMenu() {
    isFabOpen = true;
    createCourseLinearLayout.setVisibility(View.VISIBLE);
    enterCourseLinearLayout.setVisibility(View.VISIBLE);
    enterCourseLinearLayout.animate().translationY(-getResources()
            .getDimension(R.dimen.standard_55));
    createCourseLinearLayout.animate().translationY(-getResources()
            .getDimension(R.dimen.standard_105));
  }

  private void closeFabMenu() {
    isFabOpen = false;
    enterCourseLinearLayout.animate().translationY(0);
    createCourseLinearLayout.animate().translationY(0);
    createCourseLinearLayout.setVisibility(View.INVISIBLE);
    enterCourseLinearLayout.setVisibility(View.INVISIBLE);
  }
}