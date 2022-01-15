package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.unserhoersaal.R;

/** Courses. */
public class CoursesFragment extends Fragment {
  private RecyclerView courseListRecycler;
  private Button enterNewCourseButton;

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
    initUi(view);
  }

  private void initUi(View view) {
    courseListRecycler = view.findViewById(R.id.coursesFragmentRecyclerView);
    enterNewCourseButton = view.findViewById(R.id.coursesFragmentEnterNewCourseButton);
  }
}