package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.ChatAdapter;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import java.util.List;

/** Fragment for current course.*/
public class CurrentCourseFragment extends Fragment {

  private static final String TAG = "CurrentCourseFragment";

  private EditText questionEditText;
  private Button sendQuestionButton;
  private TextView courseKeyTextView;
  private RecyclerView recyclerView;

  private ChatAdapter chatAdapter;

  private CurrentCourseViewModel currentCourseViewModel;

  public CurrentCourseFragment() {
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
    return inflater.inflate(R.layout.fragment_current_course, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.initViewModel();
    this.initUi(view);
    this.initRecyclerView();
  }

  @SuppressLint("NotifyDataSetChanged")
  private void initViewModel() {
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.currentCourseViewModel.init();
    this.currentCourseViewModel.getThreadId().observe(getViewLifecycleOwner(), id -> {
      this.courseKeyTextView.setText(id);
    });
    this.currentCourseViewModel.getMessages()
            .observe(getViewLifecycleOwner(), (Observer<List>) list -> chatAdapter.notifyDataSetChanged());
  }

  private void initUi(View view) {
    this.questionEditText = view.findViewById(R.id.currentCourseFragmentQuestionEditText);
    this.sendQuestionButton = view.findViewById(R.id.currentCourseFragmentSendQuestionButton);
    this.courseKeyTextView = view.findViewById(R.id.courseKeyTextView);
    this.recyclerView = view.findViewById(R.id.chatRecyclerView);

    this.sendQuestionButton.setOnClickListener(view1 -> {
      String text = questionEditText.getText().toString();
      if (text.length() > 0) {
        currentCourseViewModel.sendMessage(text);
        questionEditText.getText().clear();
      }
    });
  }

  private void initRecyclerView() {
    this.chatAdapter = new ChatAdapter(this.currentCourseViewModel.getMessages().getValue());
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    this. recyclerView.setLayoutManager(layoutManager);
    this.recyclerView.setItemAnimator(new DefaultItemAnimator());
    this.recyclerView.setAdapter(this.chatAdapter);
  }
}