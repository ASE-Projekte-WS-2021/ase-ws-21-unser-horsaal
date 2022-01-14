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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;

/** Class Description. */
public class EnterCourseFragment extends Fragment {

  private EditText enterCourseEditText;
  private Button enterCourseButton;
  //TODO: this instance variable could be converted to
  // a local variable although we need it later as a instance variable
  private EnterCourseViewModel enterCourseViewModel;
  private CreateCourseViewModel createCourseViewModel;

  /** Constructor Description. */
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

    this.enterCourseViewModel = new ViewModelProvider(
            requireActivity()).get(EnterCourseViewModel.class);
    this.createCourseViewModel = new ViewModelProvider(
            requireActivity()).get(CreateCourseViewModel.class);

    initUi(view);
    setupNavigation(view);
  }

  private void initUi(View view) {
    this.enterCourseEditText = view.findViewById(R.id.enterCourseFragmentCourseNumberEditText);
    this.enterCourseButton = view.findViewById(R.id.enterCourseFragmentEnterButton);
  }

  //setup Navigation to corresponding fragments
  private void setupNavigation(View view) {
    NavController navController = Navigation.findNavController(view);
    //TODO: add logic to entering course
    this.enterCourseButton.setOnClickListener(v -> {
      createCourseViewModel.setCourseId(enterCourseEditText.getText().toString());
      navController.navigate(R.id.action_enterCourseFragment_to_currentCourseFragment);
    });

    this.enterCourseButton.setOnClickListener(v ->
            navController.navigate(R.id.action_enterCourseFragment_to_currentCourseFragment));
  }

}