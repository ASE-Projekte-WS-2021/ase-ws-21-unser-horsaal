package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/** Fragment for entering a course.*/
public class EnterCourseFragment extends Fragment {

  private static final String TAG = "EnterCourseFragment";

  private EditText enterCourseEditText;
  private Button enterCourseButton;
  private EnterCourseViewModel enterCourseViewModel;
  private CurrentCourseViewModel currentCourseViewModel;
  private MaterialToolbar toolbar;
  private NavController navController;

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
    this.setupToolbar();
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
    this.toolbar = view.findViewById(R.id.enterCourseFragmentToolbar);
  }

  //setup Navigation to corresponding fragments
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

  private void setupToolbar(){
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.toolbar.setNavigationOnClickListener(v -> {
      this.navController.navigate(R.id.action_enterCourseFragment_to_coursesFragment);
    });
  }

  private void showDialog() {
    EnterCourseDialogFragment dialogFragment = new EnterCourseDialogFragment();
    dialogFragment.show(getActivity().getSupportFragmentManager(), "enter dialog");
  }
}