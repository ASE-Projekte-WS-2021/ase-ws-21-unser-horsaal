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
import com.example.unserhoersaal.databinding.FragmentCoursesObjectBinding;
import com.example.unserhoersaal.viewmodel.CoursesViewModel;

/** View for all joined courses. */
public class CoursesObjectFragment extends Fragment {

  private static final String TAG = "CoursesObjectFragment";

  private CoursesViewModel coursesViewModel;
  private CoursesAdapter coursesAdapter;
  private FragmentCoursesObjectBinding binding;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_courses_object, container,
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
    this.coursesViewModel = new ViewModelProvider(getActivity())
            .get(CoursesViewModel.class);
    this.coursesViewModel.init();
    this.coursesViewModel.loadUserCourses();
    this.coursesViewModel.getUserCourses()
            .observe(getViewLifecycleOwner(), userCourses -> {
              this.coursesAdapter.notifyDataSetChanged();
              if (userCourses.getData().size() == 0) {
                this.binding.coursesFragmentTitleTextView.setVisibility(View.VISIBLE);
              } else {
                this.binding.coursesFragmentTitleTextView.setVisibility(View.GONE);
              }
            });
  }

  private void connectAdapter() {
    this.coursesAdapter =
            new CoursesAdapter(this.coursesViewModel.getUserCourses().getValue().getData());
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.coursesViewModel);
    this.binding.setAdapter(this.coursesAdapter);
  }

  private void initSearchView() {
    this.binding
            .courseFragmentObjectSearchView
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
