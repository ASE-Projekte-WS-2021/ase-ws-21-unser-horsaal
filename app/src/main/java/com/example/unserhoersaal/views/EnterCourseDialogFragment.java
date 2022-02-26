/*package com.example.unserhoersaal.views;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.EnterCourseDetailScreenBinding;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;


 Dialog that contains Course-Information and option to enter the course. Source:
  https://stackoverflow.com/questions/25887373/calling-dialogfragment-from-fragment-not-
  fragmentactivity.
public class EnterCourseDialogFragment extends Fragment {

  private static final String TAG = "EnterCourseDialogFragment";

  private EnterCourseViewModel enterCourseViewModel;
  private EnterCourseDetailScreenBinding binding;

  public EnterCourseDialogFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding =  DataBindingUtil.inflate(inflater,
            R.layout.enter_course_detail_screen, container,false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.initViewModel();
    this.connectBinding();
  }

  public void initViewModel() {
    this.enterCourseViewModel = new ViewModelProvider(requireActivity())
            .get(EnterCourseViewModel.class);
    this.enterCourseViewModel.init();
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    this.binding.setVm(this.enterCourseViewModel);
  }

}
*/