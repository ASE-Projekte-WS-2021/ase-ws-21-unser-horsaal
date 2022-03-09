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
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentNoCourseFoundBinding;
import com.example.unserhoersaal.enums.DeepLinkEnum;
import com.example.unserhoersaal.utils.DeepLinkMode;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;

/**When no course found.*/
public class NoCourseFoundFragment extends Fragment {

  private FragmentNoCourseFoundBinding binding;
  private EnterCourseViewModel enterCourseViewModel;
  private DeepLinkMode deepLinkMode;

  public NoCourseFoundFragment() {
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
            R.layout.fragment_no_course_found, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.deepLinkMode = DeepLinkMode.getInstance();

    this.initViewModel();
    this.connectBinding();
  }

  private void initViewModel() {
    this.enterCourseViewModel =
            new ViewModelProvider(requireActivity()).get(EnterCourseViewModel.class);
    this.enterCourseViewModel.init();
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
  }

  @Override
  public void onPause() {
    super.onPause();
    this.enterCourseViewModel.resetEnterCourse();
    this.deepLinkMode.setDeepLinkMode(DeepLinkEnum.DEFAULT);
  }

}