package com.example.unserhoersaal.views;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.ThreadAdapter;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** Course-Meeting. */
public class CourseMeetingFragment extends Fragment implements ThreadAdapter.OnNoteListener {

  private static final String TAG = "CourseMeetingFragment";

  private MaterialToolbar toolbar;
  private ConstraintLayout generalThreadContainer;
  private TextView generalThreadContributions;
  private RecyclerView threadRecycler;
  private FloatingActionButton floatingActionButton;
  private ScrollView createThreadContainer;
  private EditText createThreadTitle;
  private EditText createThreadText;
  private MaterialButton sendThreadButton;

  private ThreadAdapter threadAdapter;

  private CourseMeetingViewModel courseMeetingViewModel;

  private NavController navController;

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
    this.initViewModel();
    this.initUi(view);
    this.initToolbar();
  }

  public void initViewModel() {
    this.courseMeetingViewModel = new ViewModelProvider(requireActivity())
            .get(CourseMeetingViewModel.class);
    courseMeetingViewModel.init();
    courseMeetingViewModel.getThreads().observe(getViewLifecycleOwner(), messageList -> {
      Log.d(TAG, "initViewModel: " + messageList.size());
      threadAdapter.notifyDataSetChanged();
    });
  }

  private void initUi(View view) {
    this.toolbar = view.findViewById(R.id.courseMeetingFragmentToolbar);
    this.generalThreadContainer =
            view.findViewById(R.id.courseMeetingFragmentGeneralThreadContainer);
    this.generalThreadContributions = view
            .findViewById(R.id.courseMeetingFragmentGeneralThreadContributions);
    this.threadRecycler = view.findViewById(R.id.courseMeetingFragmentThreadRecycler);
    this.floatingActionButton = view.findViewById(R.id.courseMeetingFragmentFab);
    this.floatingActionButton.setOnClickListener(v -> {
      onFloatingActionButtonClicked();
    });
    this.navController = Navigation.findNavController(view);
    this.createThreadContainer = view.findViewById(R.id.courseMeetingFragmentCreateThreadContainer);
    this.createThreadTitle = view.findViewById(R.id.courseMeetingFragmentQuestionTitleEditText);
    this.createThreadText = view.findViewById(R.id.courseMeetingFragmentQuestionTextEditText);
    this.sendThreadButton = view.findViewById(R.id.courseMeetingFragmentSendThreadButton);

    this.threadAdapter =
            new ThreadAdapter(this.courseMeetingViewModel.getThreads().getValue(), this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    this.threadRecycler.setLayoutManager(layoutManager);
    this.threadRecycler.setItemAnimator(new DefaultItemAnimator());
    this.threadRecycler.setAdapter(this.threadAdapter);
  }

  private void initToolbar() {
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.toolbar.setNavigationOnClickListener(v -> {

    });
  }

  private void onFloatingActionButtonClicked() {
    if (createThreadContainer.getVisibility() == View.VISIBLE) {
      KeyboardUtil.hideKeyboard(getActivity());
      this.createThreadContainer.setVisibility(View.GONE);
      this.floatingActionButton.setImageResource(R.drawable.ic_baseline_add_24);
      this.floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources()
              .getColor(R.color.orange, null)));
      this.createThreadTitle.getText().clear();
      this.createThreadText.getText().clear();
    } else {
      this.createThreadContainer.setVisibility(View.VISIBLE);
      this.floatingActionButton.setImageResource(R.drawable.ic_baseline_close_24);
      this.floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources()
              .getColor(R.color.red, null)));
    }
  }

  @Override
  public void onNoteClick(int position) {
    String id = this.courseMeetingViewModel.getThreads().getValue().get(position).getKey();
    Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
  }
}