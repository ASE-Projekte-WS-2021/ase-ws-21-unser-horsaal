package com.example.unser_hoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unser_hoersaal.R;
import com.example.unser_hoersaal.model.CourseModel;
import com.example.unser_hoersaal.model.Message;
import com.example.unser_hoersaal.viewmodel.CreateCourseViewModel;
import com.example.unser_hoersaal.viewmodel.CurrentCourseVMFactory;
import com.example.unser_hoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unser_hoersaal.viewmodel.LoginRegisterViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CurrentCourseFragment extends Fragment {

    EditText questionEditText;
    Button sendQuestionButton;
    CurrentCourseViewModel currentCourseViewModel;
    String courseId;
    RecyclerView recyclerView;
    Message[] testArray = {};

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
        CreateCourseViewModel createCourseViewModel = new ViewModelProvider(requireActivity()).get(CreateCourseViewModel.class);
        courseId = createCourseViewModel.getCourseId();
        currentCourseViewModel = new ViewModelProvider(requireActivity(), new CurrentCourseVMFactory(courseId)).get(CurrentCourseViewModel.class);
        currentCourseViewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            updateUi(view, messages);
        });

        initUi(view);
    }

    private void initUi(View view){
        questionEditText = view.findViewById(R.id.currentCourseFragmentQuestionEditText);
        sendQuestionButton = view.findViewById(R.id.currentCourseFragmentSendQuestionButton);

        recyclerView = view.findViewById(R.id.chatRecyclerView);
        ChatAdapter chatAdapter = new ChatAdapter(testArray);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);


        sendQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCourseViewModel.sendMessage(questionEditText.getText().toString());
            }
        });
    }

    private void updateUi(View view, ArrayList messages){
        if(messages != null){
            Message[] messagesArray = new Message[messages.size()];
            messages.toArray(messagesArray);
            ChatAdapter chatAdapter = new ChatAdapter(messagesArray);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(chatAdapter);
        }
    }
}