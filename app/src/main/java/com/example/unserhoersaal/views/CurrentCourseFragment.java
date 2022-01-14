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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.Message;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModelFactory;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import java.util.ArrayList;

/** Class Description. */
public class CurrentCourseFragment extends Fragment {

  private EditText questionEditText;
  private Button sendQuestionButton;
  private CurrentCourseViewModel currentCourseViewModel;
  private RecyclerView recyclerView;
  private Message[] testArray;

  /** Constructor Description. */
  public CurrentCourseFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    testArray = new Message[]{};
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_current_course,
            container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    CreateCourseViewModel createCourseViewModel = new ViewModelProvider(
            requireActivity()).get(CreateCourseViewModel.class);
    String courseId = createCourseViewModel.getCourseId();

    this.currentCourseViewModel = new ViewModelProvider(requireActivity(),
            new CurrentCourseViewModelFactory(courseId)).get(CurrentCourseViewModel.class);

    initUi(view);
    pluginListeners();
    pluginAdapters();
  }

  private void initUi(View view) {
    int a = R.id.currentCourseFragment;
    this.questionEditText = view.findViewById(R.id.currentCourseFragmentQuestionEditText);
    this.sendQuestionButton = view.findViewById(R.id.currentCourseFragmentSendQuestionButton);
    this.recyclerView = view.findViewById(R.id.chatRecyclerView);
  }

  private void pluginListeners() {
    this.sendQuestionButton.setOnClickListener(v -> {
      this.currentCourseViewModel.sendMessage(this.questionEditText.getText().toString());
      this.questionEditText.getText().clear();
    });

    this.currentCourseViewModel.getMessages().observe(getViewLifecycleOwner(), this::updateUi);
  }

  private void pluginAdapters() {
    ChatAdapter chatAdapter = new ChatAdapter(this.testArray);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

    this.recyclerView.setLayoutManager(layoutManager);
    this.recyclerView.setItemAnimator(new DefaultItemAnimator());
    this.recyclerView.setAdapter(chatAdapter);
  }

  private void updateUi(ArrayList<Message> messages) {
    if (messages != null) {
      Message[] messagesArray = new Message[messages.size()];
      messages.toArray(messagesArray);
      ChatAdapter chatAdapter = new ChatAdapter(messagesArray);
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

      this.recyclerView.setLayoutManager(layoutManager);
      this.recyclerView.setItemAnimator(new DefaultItemAnimator());
      this.recyclerView.setAdapter(chatAdapter);
    }
  }
}