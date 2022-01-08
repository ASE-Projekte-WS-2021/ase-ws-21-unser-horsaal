package com.example.unser_hoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unser_hoersaal.R;
import com.example.unser_hoersaal.model.CourseModel;
import com.example.unser_hoersaal.viewmodel.CreateCourseViewModel;
import com.example.unser_hoersaal.viewmodel.LoginRegisterViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentCourseFragment extends Fragment {

    EditText questionEditText;
    Button sendQuestionButton;
    ValueEventListener postListener;
    CreateCourseViewModel createCourseViewModel;

    public CurrentCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCourseViewModel = new ViewModelProvider(this).get(CreateCourseViewModel.class);
        System.out.println(createCourseViewModel.currentCourseID);
        String test = createCourseViewModel.currentCourseID;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                CourseModel post = dataSnapshot.getValue(CourseModel.class);
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);

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
        initUi(view);
    }

    private void initUi(View view){
        questionEditText = view.findViewById(R.id.currentCourseFragmentQuestionEditText);
        sendQuestionButton = view.findViewById(R.id.currentCourseFragmentSendQuestionButton);
        sendQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }
}