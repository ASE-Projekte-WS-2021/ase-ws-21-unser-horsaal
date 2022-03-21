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
import androidx.viewpager2.widget.ViewPager2;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.CoursesAdapter;
import com.example.unserhoersaal.adapter.ViewPagerAdapter;
import com.example.unserhoersaal.databinding.FragmentCoursesBinding;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/** Courses. */
public class CoursesFragment extends Fragment {

  private static final String TAG = "CoursesFragment";

  private ViewPagerAdapter viewPagerAdapter;
  private ViewPager2 viewPager;
  private FragmentCoursesBinding binding;
  private CoursesViewModel coursesViewModel;
  private NavController navController;
  private CoursesAdapter coursesAdapter;

  public CoursesFragment() {
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
            R.layout.fragment_courses, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();

    this.viewPager = view.findViewById(R.id.coursesFragmentViewPager);
    this.viewPagerAdapter = new ViewPagerAdapter(this);
    this.viewPager.setAdapter(viewPagerAdapter);
    TabLayout tabLayout = view.findViewById(R.id.coursesFragmentTabLayout);
    new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> tab.setText(coursesViewModel.getTabTitle(position))
    ).attach();

    this.navController = Navigation.findNavController(view);

    this.connectAdapter();
    this.connectBinding();
    this.initToolbar();
  }

  private void initViewModel() {
    this.coursesViewModel = new ViewModelProvider(requireActivity())
            .get(CoursesViewModel.class);
    this.coursesViewModel.init();

    /*this.coursesViewModel.getUserCourses()
            .observe(getViewLifecycleOwner(), this::coursesLiveDataCallback);*/
  }

  /*@SuppressLint("NotifyDataSetChanged")
  private void coursesLiveDataCallback(StateData<List<CourseModel>> listStateData) {
    this.resetBindings();
    this.coursesAdapter.notifyDataSetChanged();

    if (listStateData.getStatus() == StateData.DataStatus.LOADING) {
      this.binding.coursesFragmentProgressSpinner.setVisibility(View.VISIBLE);
    } else if (listStateData.getStatus() == StateData.DataStatus.ERROR) {
      Toast.makeText(getContext(),
              listStateData.getError().getMessage(), Toast.LENGTH_SHORT).show();
    }
    if (listStateData.getData().size() == 0) {
      this.binding.coursesFragmentTitleTextView.setVisibility(View.VISIBLE);
    } else {
      this.binding.coursesFragmentTitleTextView.setVisibility(View.GONE);
    }
  }*/

  private void resetBindings() {
    this.binding.coursesFragmentProgressSpinner.setVisibility(View.GONE);
  }

  private void connectAdapter() {
    this.coursesAdapter = new CoursesAdapter(
            this.coursesViewModel.getAllCoursesRepoState().getValue().getData());
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.coursesViewModel);
    this.binding.setAdapter(this.coursesAdapter);
  }

  private void initToolbar() {
    this.binding.coursesFragmentToolbar.inflateMenu(R.menu.courses_fragment_toolbar);
    this.binding.coursesFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_account_circle_24);
    this.binding.coursesFragmentToolbar
            .setNavigationOnClickListener(v ->
                    navController.navigate(R.id.action_coursesFragment_to_profileFragment));
  }

  //For closing the Floating Action Buttons when returning to
  //this Fragment and the Buttons were not closed before.
  @Override
  public void onResume() {
    super.onResume();
    this.binding.coursesFragmentEnterCourseFabLayout.setVisibility(View.GONE);
    this.binding.coursesFragmentCreateCourseFabLayout.setVisibility(View.GONE);
  }
}

