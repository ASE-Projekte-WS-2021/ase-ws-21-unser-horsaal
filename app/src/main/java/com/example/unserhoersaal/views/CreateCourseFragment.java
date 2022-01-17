package com.example.unserhoersaal.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.utils.KeyboardUtil;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class CreateCourseFragment extends Fragment {

    EditText courseTitelEditText;
    Button createCourseButton;

    private CreateCourseViewModel createCourseViewModel;
    private FirebaseAuth firebaseAuth;

    public CreateCourseFragment() {
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
        return inflater.inflate(R.layout.fragment_create_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        createCourseViewModel = new ViewModelProvider(requireActivity()).get(CreateCourseViewModel.class);
        initUI(view);
        setupNavigation(view);
    }

    private void initUI(View view){
        courseTitelEditText = view.findViewById(R.id.createCourseFragmentCourseTitleEditText);
        createCourseButton = view.findViewById(R.id.createCourseFragmentCreateButton);
    }

    //setup Navigation to corresponding fragments
    private void setupNavigation(View view){
        NavController navController = Navigation.findNavController(view);

        //todo add logic to login
        //createCourseButton.setOnClickListener(v -> navController.navigate(R.id.action_createCourseFragment_to_currentCourseFragment));
        createCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseTitle = courseTitelEditText.getText().toString();
                String courseDescription = "";




                if (courseTitle.length() > 6) {
                    createCourseViewModel.createCourse(courseTitle, courseDescription);
                    navController.navigate(R.id.action_createCourseFragment_to_currentCourseFragment);
                    KeyboardUtil.hideKeyboard(getActivity());
                } else {
                    Toast.makeText(getContext(), "The course name should be 6 characters or longer.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}