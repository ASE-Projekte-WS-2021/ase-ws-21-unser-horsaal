package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.CoursesAdapter;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

/** Courses. */
public class CoursesFragment extends Fragment implements CoursesAdapter.OnNoteListener {

  private static final String TAG = "CoursesFragment";

  private MaterialToolbar toolbar;
  private RecyclerView courseListRecycler;
  private TextView titelTextView;
  private FloatingActionButton fab;
  private FloatingActionButton fabEnterCourse;
  private FloatingActionButton fabCreateCourse;
  private LinearLayout enterCourseLinearLayout;
  private LinearLayout createCourseLinearLayout;

  private Boolean isFabOpen;

  private CoursesAdapter coursesAdapter;

  private CoursesViewModel coursesViewModel;
  private CurrentCourseViewModel currentCourseViewModel;

  private NavController navController;


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
    this.initViewModel();
    this.initUi(view);
    this.setupNavigation(view);
    this.setupToolbar();
    this.setupFabs();
  }

  private void initViewModel() {
    this.coursesViewModel = new ViewModelProvider(requireActivity())
            .get(CoursesViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.coursesViewModel.init();
    this.currentCourseViewModel.init();
    this.coursesViewModel.getUserCourses()
            .observe(getViewLifecycleOwner(), new Observer<List<CourseModel>>() {
              @SuppressLint("NotifyDataSetChanged")
              @Override
              public void onChanged(@Nullable List<CourseModel> userCourses) {
                coursesAdapter.notifyDataSetChanged();
              }
            });
  }

  private void initUi(View view) {
    this.titelTextView = view.findViewById(R.id.coursesFragmentTitleTextView);
    this.fab = view.findViewById(R.id.coursesFragmentFab);
    this.fabEnterCourse = view.findViewById(R.id.coursesFragmentFabEnterCourse);
    this.fabCreateCourse = view.findViewById(R.id.coursesFragmentFabCreateCourse);
    this.enterCourseLinearLayout = view.findViewById(R.id.coursesFragmentEnterCourseFabLayout);
    this.createCourseLinearLayout = view.findViewById(R.id.coursesFragmentCreateCourseFabLayout);
    this.toolbar = (MaterialToolbar) view.findViewById(R.id.coursesFragmentToolbar);

    this.courseListRecycler = view.findViewById(R.id.coursesFragmentRecyclerView);
    this.coursesAdapter =
            new CoursesAdapter(this.coursesViewModel.getUserCourses().getValue(), this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    this.courseListRecycler.setLayoutManager(layoutManager);
    this.courseListRecycler.setItemAnimator(new DefaultItemAnimator());
    this.courseListRecycler.setAdapter(this.coursesAdapter);
    this.titelTextView.setText(Config.COURSES_EMPTY);
  }

  private void setupNavigation(View view) {
    this.navController = Navigation.findNavController(view);
    this.fabEnterCourse.setOnClickListener(v -> this.navController.navigate(
            R.id.action_coursesFragment_to_enterCourseFragment));
    this.fabCreateCourse.setOnClickListener(v -> this.navController.navigate(
            R.id.action_coursesFragment_to_createCourseFragment)
    );

    KeyboardUtil.hideKeyboard(getActivity());
  }

  @Override
  public void onNoteClick(int position) {
    String id = this.coursesViewModel.getUserCourses().getValue().get(position).getTitle();
    //this.currentCourseViewModel.setCourseId(id);
    //this.navController.navigate(R.id.action_coursesFragment_to_currentCourseFragment);
    Toast.makeText(getActivity(), id, Toast.LENGTH_LONG).show();
  }

  private void setupToolbar() {
    this.toolbar.inflateMenu(R.menu.courses_fragment_toolbar);
    //Menu menu = this.toolbar.getMenu();
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_account_circle_24);
    this.toolbar.setNavigationOnClickListener(v -> {
      this.navController.navigate(R.id.action_coursesFragment_to_profileFragment);
    });
  }

  /** Source: https://mobikul.com/expandable-floating-action-button-fab-menu
   * /#:~:text=A%20Floating%20Action%20Button(fab,common%20actions%20within%20an%20app.*/

  private void setupFabs() {
    this.isFabOpen = false;
    this.fab.setOnClickListener(new View.OnClickListener() {
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
    this.isFabOpen = true;
    this.createCourseLinearLayout.setVisibility(View.VISIBLE);
    this.enterCourseLinearLayout.setVisibility(View.VISIBLE);
    this.enterCourseLinearLayout.animate().translationY(-getResources()
            .getDimension(R.dimen.standard_55));
    this.createCourseLinearLayout.animate().translationY(-getResources()
            .getDimension(R.dimen.standard_105));
  }

  private void closeFabMenu() {
    this.isFabOpen = false;
    this.enterCourseLinearLayout.animate().translationY(0);
    this.createCourseLinearLayout.animate().translationY(0);
    this.createCourseLinearLayout.setVisibility(View.INVISIBLE);
    this.enterCourseLinearLayout.setVisibility(View.INVISIBLE);
  }
}