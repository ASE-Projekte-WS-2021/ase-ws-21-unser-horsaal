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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.DatabaseEnterCourse;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/** Fragment for entering a course.*/
public class EnterCourseFragment extends Fragment {

  EditText enterCourseEditText;
  Button enterCourseButton;
  EnterCourseViewModel enterCourseViewModel;
  CreateCourseViewModel createCourseViewModel;
  MaterialToolbar toolbar;
  NavController navController;

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

    enterCourseViewModel = new ViewModelProvider(requireActivity()).get(EnterCourseViewModel.class);
    createCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CreateCourseViewModel.class);
    initUi(view);
    setupNavigation(view);
    setupToolbar();
  }

  private void initUi(View view) {
    enterCourseEditText = view.findViewById(R.id.enterCourseFragmentCourseNumberEditText);
    enterCourseButton = view.findViewById(R.id.enterCourseFragmentEnterButton);
    navController = Navigation.findNavController(view);
    toolbar = view.findViewById(R.id.enterCourseFragmentToolbar);
  }

  //setup Navigation to corresponding fragments
  private void setupNavigation(View view) {
    //todo add logic to entering course
    enterCourseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createCourseViewModel.setCourseId(enterCourseEditText.getText().toString());
        enterCourseViewModel.saveUserCourses(enterCourseEditText.getText().toString())
                .observe(getViewLifecycleOwner(), courseIdIsCorrect -> {
                  if (courseIdIsCorrect == DatabaseEnterCourse.ThreeState.TRUE) {
                    if (navController.getCurrentDestination().getId() == R.id.enterCourseFragment){
                      navController.navigate(R.id.action_enterCourseFragment_to_currentCourseFragment);
                    }
                  }
                  if (courseIdIsCorrect == DatabaseEnterCourse.ThreeState.FALSE){
                    Toast.makeText(getActivity(), "Incorrect key",
                            Toast.LENGTH_LONG).show();
                  }
        });
        KeyboardUtil.hideKeyboard(getActivity());
      }
    });
  }

  private void setupToolbar(){
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    toolbar.setNavigationOnClickListener(v -> {
      navController.navigate(R.id.action_enterCourseFragment_to_coursesFragment);
    });
  }
}