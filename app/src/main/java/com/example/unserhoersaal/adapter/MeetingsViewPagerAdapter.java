package com.example.unserhoersaal.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.views.LiveChatFragment;
import com.example.unserhoersaal.views.QuestionsFragment;
import com.example.unserhoersaal.views.VotingFragment;

/** This class is the adapter for the TabView of the MeetingsFragment. */
public class MeetingsViewPagerAdapter extends FragmentStateAdapter {

  private static final String TAG = "MeetingsViewPagerAdapter";

  public MeetingsViewPagerAdapter(Fragment fragment) {
    super(fragment);
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Fragment fragment;
    switch (position) {
      case Config.TAB_LIVE_CHAT:
        fragment = new LiveChatFragment();
        break;
      case Config.TAB_VOTING:
        fragment = new VotingFragment();
        break;
      case Config.TAB_QUESTIONS:
        fragment = new QuestionsFragment();
        break;
      default:
        fragment = new LiveChatFragment();
        break;
    }
    return fragment;
  }

  @Override
  public int getItemCount() {
    return Config.COURSES_NUMBER_OF_TABS;
  }
}
