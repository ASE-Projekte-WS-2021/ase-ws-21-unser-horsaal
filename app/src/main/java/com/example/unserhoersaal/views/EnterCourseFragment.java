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
  private EnterCourseViewModel enterCourseViewModel;
  private CurrentCourseViewModel currentCourseViewModel;
  private NavController navController;

  public EnterCourseFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    enterCourseViewModel = new ViewModelProvider(requireActivity()).get(EnterCourseViewModel.class);
    currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
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

    enterCourseViewModel.init();

    enterCourseViewModel.getCourseId().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String id) {
          openNewCourse(id);
      }
    });

    initUi(view);
    setupNavigation(view);
  }

  private void initUi(View view) {
    enterCourseEditText = view.findViewById(R.id.enterCourseFragmentCourseNumberEditText);
    enterCourseButton = view.findViewById(R.id.enterCourseFragmentEnterButton);
  }

  //setup Navigation to corresponding fragments
  private void setupNavigation(View view) {
    navController = Navigation.findNavController(view);
    enterCourseButton.setOnClickListener(v -> enterCode());
  }

  //todo add interface
  public void enterCode(){
    String id = enterCourseEditText.getText().toString();
    if(id.length() > 0) {
      enterCourseViewModel.checkCode(id);
    }
  }

  public void openNewCourse(String id){
    KeyboardUtil.hideKeyboard(getActivity());
    currentCourseViewModel.setCourseId(id);
    navController.navigate(R.id.action_enterCourseFragment_to_currentCourseFragment);
  }

}