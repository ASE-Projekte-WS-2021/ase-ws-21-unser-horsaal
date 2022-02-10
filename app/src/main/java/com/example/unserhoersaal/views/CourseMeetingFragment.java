package com.example.unserhoersaal.views;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** Course-Meeting. */
public class CourseMeetingFragment extends Fragment {

  MaterialToolbar toolbar;
  ConstraintLayout generalThreadContainer;
  TextView generalThreadContributions;
  RecyclerView threadRecycler;
  FloatingActionButton floatingActionButton;
  NavController navController;
  ScrollView createThreadContainer;
  EditText createThreadTitle;
  EditText createThreadText;
  MaterialButton sendThreadButton;

  public CourseMeetingFragment() {
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
    return inflater.inflate(R.layout.fragment_course_meeting, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initUi(view);
    initToolbar();
  }

  private void initUi(View view) {
    toolbar = view.findViewById(R.id.courseMeetingFragmentToolbar);
    generalThreadContainer = view.findViewById(R.id.courseMeetingFragmentGeneralThreadContainer);
    generalThreadContributions = view
            .findViewById(R.id.courseMeetingFragmentGeneralThreadContributions);
    threadRecycler = view.findViewById(R.id.courseMeetingFragmentThreadRecycler);
    floatingActionButton = view.findViewById(R.id.courseMeetingFragmentFab);
    floatingActionButton.setOnClickListener(v -> {
      onFloatingActionButtonClicked();
    });
    navController = Navigation.findNavController(view);
    createThreadContainer = view.findViewById(R.id.courseMeetingFragmentCreateThreadContainer);
    createThreadTitle = view.findViewById(R.id.courseMeetingFragmentQuestionTitleEditText);
    createThreadText = view.findViewById(R.id.courseMeetingFragmentQuestionTextEditText);
    sendThreadButton = view.findViewById(R.id.courseMeetingFragmentSendThreadButton);
  }

  private void initToolbar() {
    toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    toolbar.setNavigationOnClickListener(v -> {

    });
  }

  private void onFloatingActionButtonClicked() {
    if (createThreadContainer.getVisibility() == View.VISIBLE) {
      KeyboardUtil.hideKeyboard(getActivity());
      createThreadContainer.setVisibility(View.GONE);
      floatingActionButton.setImageResource(R.drawable.ic_baseline_add_24);
      floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources()
              .getColor(R.color.orange, null)));
      createThreadTitle.getText().clear();
      createThreadText.getText().clear();
    } else {
      createThreadContainer.setVisibility(View.VISIBLE);
      floatingActionButton.setImageResource(R.drawable.ic_baseline_close_24);
      floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources()
              .getColor(R.color.red, null)));
    }
  }
}