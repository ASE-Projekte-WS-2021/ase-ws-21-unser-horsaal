package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;

/** Fragment for entering a course.*/
public class EnterCourseFragment extends Fragment {

  private static final String TAG = "EnterCourseFragment";

  private EditText enterCourseEditText;
  private Button enterCourseButton;

  private NavController navController;

  private EnterCourseViewModel enterCourseViewModel;
  private CurrentCourseViewModel currentCourseViewModel;


  public EnterCourseFragment() {
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
    return inflater.inflate(R.layout.fragment_enter_course, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();
    this.initUi(view);
    this.setupNavigation(view);
  }

  private void initViewModel() {
    this.enterCourseViewModel = new ViewModelProvider(requireActivity())
            .get(EnterCourseViewModel.class);
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.enterCourseViewModel.init();
    this.currentCourseViewModel.init();
    this.enterCourseViewModel.getCourseId()
            .observe(getViewLifecycleOwner(), new Observer<String>() {
              @Override
              public void onChanged(String id) {
                openNewCourse(id);
              }
            });
  }

  private void initUi(View view) {
    this.enterCourseEditText = view.findViewById(R.id.enterCourseFragmentCourseNumberEditText);
    this.enterCourseButton = view.findViewById(R.id.enterCourseFragmentEnterButton);
  }

  private void setupNavigation(View view) {
    this.navController = Navigation.findNavController(view);
    this.enterCourseButton.setOnClickListener(v -> enterCode());
  }

  /** Enters the code and checks if it is correct. */
  public void enterCode() {
    String id = enterCourseEditText.getText().toString();
    if (id.length() > 0) {
      this.enterCourseViewModel.checkCode(id);
    }
  }

  /** Creates a new course if the code is correct. */
  public void openNewCourse(String id) {
    KeyboardUtil.hideKeyboard(getActivity());
    this.currentCourseViewModel.setCourseId(id);
    this.navController.navigate(R.id.action_enterCourseFragment_to_currentCourseFragment);
  }
}