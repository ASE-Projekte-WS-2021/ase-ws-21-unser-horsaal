package com.example.unserhoersaal.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.databinding.FragmentCourseThreadBinding;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.ChatAdapter;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**Course Thread.*/
public class CourseThreadFragment extends Fragment implements ChatAdapter.OnNoteListener{

  private static final String TAG = "CourseThreadFragment";

  private NavController navController;

  private FragmentCourseThreadBinding binding;

  private ChatAdapter chatAdapter;

  private CurrentCourseViewModel currentCourseViewModel;

  public CourseThreadFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_course_thread, container, false);
    return this.binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);


    this.navController = Navigation.findNavController(view);

    this.recyclerView = view.findViewById(R.id.chatRecyclerView);
    
    this.initViewModel();
    this.connectBinding();
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
    this.currentCourseViewModel.getThread().observe(getViewLifecycleOwner(), threadModel -> {
      //todo enter thread information to ui
    });
  }

  private void connectBinding() {
    this.binding.setLifecycleOwner(getViewLifecycleOwner());
    //this.binding.setVm();
  }

  private void initRecyclerView() {
    this.chatAdapter =
            new ChatAdapter(this.currentCourseViewModel.getMessages().getValue(), this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    this. recyclerView.setLayoutManager(layoutManager);
    this.recyclerView.setItemAnimator(new DefaultItemAnimator());
    this.recyclerView.setAdapter(this.chatAdapter);
  }

  private void initToolbar() {
    this.binding.courseThreadFragmentToolbar
            .inflateMenu(R.menu.course_thread_fragment_toolbar);
    this.binding.courseThreadFragmentToolbar
            .setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
    this.binding.courseThreadFragmentToolbar
            .setNavigationOnClickListener(v ->
      navController.navigate(R.id.action_courseThreadFragment_to_courseMeetingFragment)
    );
  }

  @Override
  public void onLikeClicked(int position) {
    MessageModel message = this.currentCourseViewModel.getMessages().getValue().get(position);
    this.currentCourseViewModel.like(message);
  }

  @Override
  public void onDislikeClicked(int position) {
    MessageModel message = this.currentCourseViewModel.getMessages().getValue().get(position);
    this.currentCourseViewModel.dislike(message);
  }

  @Override
  public void onSolvedClicked(int position) {
    String id = this.currentCourseViewModel.getMessages().getValue().get(position).getKey();
    this.currentCourseViewModel.solved(id);
  }
}