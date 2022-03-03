package com.example.unserhoersaal.views;

<<<<<<< HEAD
import android.annotation.SuppressLint;
=======
>>>>>>> dev
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
<<<<<<< HEAD
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
=======
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
>>>>>>> dev

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.Button;
import android.widget.EditText;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.ChatAdapter;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**Course Thread.*/
public class CourseThreadFragment extends Fragment implements ChatAdapter.OnNoteListener{

  private static final String TAG = "CourseThreadFragment";

  private MaterialToolbar toolbar;
  private NavController navController;
  private EditText questionEditText;
  private FloatingActionButton sendQuestionButton;
  private RecyclerView recyclerView;

  private ChatAdapter chatAdapter;

  private CurrentCourseViewModel currentCourseViewModel;
=======

import com.example.unserhoersaal.R;
import com.google.android.material.appbar.MaterialToolbar;

/**Course Thread.*/
public class CourseThreadFragment extends Fragment {

  private MaterialToolbar toolbar;
  private NavController navController;
>>>>>>> dev

  public CourseThreadFragment() {
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
    return inflater.inflate(R.layout.fragment_course_thread, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.toolbar = view.findViewById(R.id.courseThreadFragmentToolbar);
    this.navController = Navigation.findNavController(view);

<<<<<<< HEAD
    this.initViewModel();
    this.initUI(view);
    this.initRecyclerView();
    this.initToolbar();
  }

  private void initViewModel() {
    this.currentCourseViewModel = new ViewModelProvider(requireActivity())
            .get(CurrentCourseViewModel.class);
    this.currentCourseViewModel.init();
    this.currentCourseViewModel.getMessages()
            .observe(getViewLifecycleOwner(), new Observer<List<MessageModel>>() {
              @SuppressLint("NotifyDataSetChanged")
              @Override
              public void onChanged(List<MessageModel> messageModels) {
                chatAdapter.notifyDataSetChanged();
              }
            });
  }

  private void initUI(View view) {
    this.questionEditText = view.findViewById(R.id.courseThreadFragmentEditText);
    this.sendQuestionButton = view.findViewById(R.id.courseThreadFragmentSendFab);
    this.recyclerView = view.findViewById(R.id.courseThreadFragmentRecycler);

    this.sendQuestionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String text = questionEditText.getText().toString();
        if (text.length() > 0) {
          currentCourseViewModel.sendMessage(text);
          questionEditText.getText().clear();
        }
      }
    });
  }

  private void initRecyclerView() {
    this.chatAdapter =
            new ChatAdapter(this.currentCourseViewModel.getMessages().getValue(), this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    this. recyclerView.setLayoutManager(layoutManager);
    this.recyclerView.setItemAnimator(new DefaultItemAnimator());
    this.recyclerView.setAdapter(this.chatAdapter);
  }

=======
    this.initToolbar();
  }

>>>>>>> dev
  private void initToolbar() {
    this.toolbar.inflateMenu(R.menu.course_thread_fragment_toolbar);
    this.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.toolbar.setNavigationOnClickListener(v -> {
      this.navController.navigate(R.id.action_courseThreadFragment_to_courseMeetingFragment);
    });
  }
<<<<<<< HEAD

  @Override
  public void onLikeClicked(int position) {
    String id = this.currentCourseViewModel.getMessages().getValue().get(position).getKey();
    this.currentCourseViewModel.like(id);
  }

  @Override
  public void onDislikeClicked(int position) {
    String id = this.currentCourseViewModel.getMessages().getValue().get(position).getKey();
    this.currentCourseViewModel.dislike(id);
  }

  @Override
  public void onSolvedClicked(int position) {
    String id = this.currentCourseViewModel.getMessages().getValue().get(position).getKey();
    this.currentCourseViewModel.solved(id);
  }
=======
>>>>>>> dev
}