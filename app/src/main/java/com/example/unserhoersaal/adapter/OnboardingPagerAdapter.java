package com.example.unserhoersaal.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.views.OnBoardingAccountFragment;
import com.example.unserhoersaal.views.OnBoardingCourseOverviewFragment;
import com.example.unserhoersaal.views.OnBoardingFinishFragment;
import com.example.unserhoersaal.views.OnBoardingMeetingOverviewFragment;
import com.example.unserhoersaal.views.OnBoardingUserNameFragment;
import com.example.unserhoersaal.views.OnBoardingWelcomeFragment;

/**
 * Used in OnboardingWrapperFragment in ViewPager.
 */
public class OnboardingPagerAdapter extends FragmentStateAdapter {

  public OnboardingPagerAdapter(@NonNull Fragment fragment) {
    super(fragment);
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Fragment fragment;
    switch (position) {
      case 1:
        fragment = new OnBoardingUserNameFragment();
        break;
      case 2:
        fragment = new OnBoardingCourseOverviewFragment();
        break;
      case 3:
        fragment = new OnBoardingAccountFragment();
        break;
      case 4:
        fragment = new OnBoardingMeetingOverviewFragment();
        break;
      case 5:
        fragment = new OnBoardingFinishFragment();
        break;
      default:
        fragment = new OnBoardingWelcomeFragment();
        break;
    }
    return fragment;
  }

  @Override
  public int getItemCount() {
    return Config.ONBOARDING_FRAGMENT_TAB_LENGTH;
  }

}
