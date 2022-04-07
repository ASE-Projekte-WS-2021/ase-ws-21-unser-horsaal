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
import com.example.unserhoersaal.adapter.ViewPagerAdapter;
import com.example.unserhoersaal.databinding.FragmentCoursesBinding;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.viewmodel.AllCoursesViewModel;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;
import com.example.unserhoersaal.viewmodel.OwnedCoursesViewModel;
import com.example.unserhoersaal.viewmodel.TodaysCoursesViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/** Courses. */
public class CoursesFragment extends Fragment {

  private static final String TAG = "CoursesFragment";

  private ViewPagerAdapter viewPagerAdapter;
  private FragmentCoursesBinding binding;
  private CoursesViewModel coursesViewModel;
  private TodaysCoursesViewModel todaysCoursesViewModel;
  private AllCoursesViewModel allCoursesViewModel;
  private OwnedCoursesViewModel ownedCoursesViewModel;
  private NavController navController;

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

    this.navController = Navigation.findNavController(view);

    this.initViewModel();
    this.connectAdapter();
    this.connectBinding();
    this.setTabs();
    this.initToolbar();
  }

  private void initViewModel() {
    this.coursesViewModel = new ViewModelProvider(requireActivity())
            .get(CoursesViewModel.class);
    this.todaysCoursesViewModel = new ViewModelProvider(requireActivity())
            .get(TodaysCoursesViewModel.class);
    this.allCoursesViewModel = new ViewModelProvider(requireActivity())
            .get(AllCoursesViewModel.class);
    this.ownedCoursesViewModel = new ViewModelProvider(requireActivity())
            .get(OwnedCoursesViewModel.class);
    this.coursesViewModel.init();
    this.todaysCoursesViewModel.init();
    this.allCoursesViewModel.init();
    this.ownedCoursesViewModel.init();
    this.coursesViewModel.getUserId().observe(getViewLifecycleOwner(), this::setCoursesForTabs);
  }

  private void setCoursesForTabs(StateData<String> userStateData) {
    this.todaysCoursesViewModel.setUserId();
    this.allCoursesViewModel.setUserId();
    this.ownedCoursesViewModel.setUserId();
  }

  private void connectAdapter() {
    this.viewPagerAdapter = new ViewPagerAdapter(this);
    this.binding.coursesFragmentViewPager.setAdapter(this.viewPagerAdapter);
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.coursesViewModel);
  }

  private void setTabs() {
    new TabLayoutMediator(this.binding.coursesFragmentTabLayout,
            this.binding.coursesFragmentViewPager, this::designTab).attach();
  }

  private void designTab(TabLayout.Tab tab, int position) {
    tab.setText(coursesViewModel.getTabTitle(position));
  }

  private void initToolbar() {
    this.binding.coursesFragmentToolbar.inflateMenu(R.menu.courses_fragment_toolbar);
    this.binding.coursesFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_account_circle_24);
    this.binding.coursesFragmentToolbar
            .setNavigationOnClickListener(v ->
                    navController.navigate(R.id.action_coursesFragment_to_profileFragment));
  }

}

