package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** Course-Meeting. */
public class CourseMeetingFragment extends Fragment {

  MaterialToolbar toolbar;
  ConstraintLayout generalThreadContainer;
  TextView generalThreadContributions;
  RecyclerView threadRecycler;
  FloatingActionButton floatingActionButton;
  NavController navController;

  public CourseMeetingFragment() {
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
    return inflater.inflate(R.layout.fragment_course_meeting, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initUi(view);
    initToolbar();
  }

  private void initUi(View view) {
    toolbar = view.findViewById(R.id.courseMeetingFragmentToolbar);
    generalThreadContainer = view.findViewById(R.id.courseMeetingFragmentGeneralThreadContainer);
    generalThreadContributions = view
            .findViewById(R.id.courseMeetingFragmentGeneralThreadContributions);
    threadRecycler = view.findViewById(R.id.courseMeetingFragmentThreadRecycler);
    floatingActionButton = view.findViewById(R.id.courseMeetingFragmentFab);
    navController = Navigation.findNavController(view);
  }

  private void initToolbar() {
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    toolbar.setNavigationOnClickListener(v -> {

    });
  }
}