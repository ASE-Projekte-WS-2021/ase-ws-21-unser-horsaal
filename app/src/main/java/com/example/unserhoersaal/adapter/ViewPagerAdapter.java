package com.example.unserhoersaal.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.views.AllCoursesFragment;
import com.example.unserhoersaal.views.OwnedCoursesFragment;
import com.example.unserhoersaal.views.TodaysCoursesFragment;

/** This class is the adapter for the Tabview of the CoursesFragment. */
public class ViewPagerAdapter extends FragmentStateAdapter {

  private static final String TAG = "ViewPagerAdapter";

  public ViewPagerAdapter(Fragment fragment) {
    super(fragment);
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Fragment fragment;
    switch (position) {
      case Config.TAB_TODAY:
        fragment = new TodaysCoursesFragment();
        break;
      case Config.TAB_ALL:
        fragment = new AllCoursesFragment();
        break;
      case Config.TAB_OWNED:
        fragment = new OwnedCoursesFragment();
        break;
      default:
        fragment = new AllCoursesFragment();
        break;
    }
    return fragment;
  }

  @Override
  public int getItemCount() {
    return Config.COURSES_NUMBER_OF_TABS;
  }
}


