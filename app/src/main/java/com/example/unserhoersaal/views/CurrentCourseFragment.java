package com.example.unserhoersaal.views;

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
import com.example.unserhoersaal.model.Message;
import com.example.unserhoersaal.repository.CoursesRepository;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import java.util.ArrayList;
import java.util.List;

/** Fragment for current course.*/
public class CurrentCourseFragment extends Fragment {

  private static final String TAG = "CurrentCourseFragment";

  private EditText questionEditText;
  private Button sendQuestionButton;
  private TextView courseKeyTextView;
  private CurrentCourseViewModel currentCourseViewModel;
  private CreateCourseViewModel createCourseViewModel;
  private String courseId;
  private RecyclerView recyclerView;
  private Message[] emptyArray = {};
  private ChatAdapter chatAdapter;

  public CurrentCourseFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    createCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CreateCourseViewModel.class);
    currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
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
    courseId = createCourseViewModel.getCourseId();
    currentCourseViewModel.setupCurrentCourseViewModel(courseId);

    currentCourseViewModel.init();
    currentCourseViewModel.getMessages().observe(getViewLifecycleOwner(), new Observer<List>() {
      @Override
      public void onChanged(List list) {
        chatAdapter.notifyDataSetChanged();
      }
    });
    initUi(view, courseId);
    initRecyclerView();
  }

  private void initUi(View view, String courseId) {
    questionEditText = view.findViewById(R.id.currentCourseFragmentQuestionEditText);
    sendQuestionButton = view.findViewById(R.id.currentCourseFragmentSendQuestionButton);
    courseKeyTextView = view.findViewById(R.id.courseKeyTextView);
    courseKeyTextView.setText(courseId);
    recyclerView = view.findViewById(R.id.chatRecyclerView);

    sendQuestionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        currentCourseViewModel.sendMessage(questionEditText.getText().toString());
        questionEditText.getText().clear();
      }
    });
  }

  private void initRecyclerView(){
    chatAdapter = new ChatAdapter(currentCourseViewModel.getMessages().getValue());
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(chatAdapter);
  }
}