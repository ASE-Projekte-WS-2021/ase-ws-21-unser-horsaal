package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.CoursesAdapter;
import com.example.unserhoersaal.databinding.FragmentOwnedCoursesBinding;
import com.example.unserhoersaal.viewmodel.OwnedCoursesViewModel;

/** Fragment for displaying the owned courses. */
public class OwnedCoursesFragment extends Fragment {

  private static final String TAG = "OwnedCoursesFragment";

  private OwnedCoursesViewModel ownedCoursesViewModel;
  private CoursesAdapter coursesAdapter;
  private FragmentOwnedCoursesBinding binding;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_owned_courses, container,
            false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();
    this.connectAdapter();
    this.connectBinding();
    this.initSearchView();
  }

  @SuppressLint("NotifyDataSetChanged")
  private void initViewModel() {
    this.ownedCoursesViewModel = new ViewModelProvider(getActivity())
            .get(OwnedCoursesViewModel.class);
    this.ownedCoursesViewModel.init();
    this.ownedCoursesViewModel.loadOwnedCourses();
    this.ownedCoursesViewModel.getOwnedCourses()
            .observe(getViewLifecycleOwner(), ownedCourses -> {
              this.coursesAdapter.notifyDataSetChanged();
              if (ownedCourses.size() == 0) {
                this.binding.ownedCoursesFragmentTitleTextView.setVisibility(View.VISIBLE);
              } else {
                this.binding.ownedCoursesFragmentTitleTextView.setVisibility(View.GONE);
              }
            });
  }

  private void connectAdapter() {
    this.coursesAdapter = new CoursesAdapter(
            this.ownedCoursesViewModel.getOwnedCourses().getValue());
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.ownedCoursesViewModel);
    this.binding.setAdapter(this.coursesAdapter);
  }

  private void initSearchView() {
    this.binding
            .ownedCoursesFragmentSearchView
            .setOnQueryTextListener(new SearchView.OnQueryTextListener() {
              @Override
              public boolean onQueryTextSubmit(String query) {
                return false;
              }

              @Override
              public boolean onQueryTextChange(String newText) {
                coursesAdapter.getFilter().filter(newText);
                return false;
              }
            });
  }
}