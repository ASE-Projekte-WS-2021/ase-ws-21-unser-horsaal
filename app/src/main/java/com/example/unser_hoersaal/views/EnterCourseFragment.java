package views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.unser_hoersaal.R;

public class EnterCourseFragment extends Fragment {

    EditText enterCourseEditText;
    Button enterCourseButton;

    public EnterCourseFragment() {
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
        return inflater.inflate(R.layout.fragment_enter_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        setupNavigation(view);
    }

    private void initUI(View view){
        enterCourseEditText = view.findViewById(R.id.enterCourseFragmentCourseNumberEditText);
        enterCourseButton = view.findViewById(R.id.enterCourseFragmentEnterButton);
    }

    //setup Navigation to corresponding fragments
    private void setupNavigation(View view){
        NavController navController = Navigation.findNavController(view);

        //todo add logic to entering course
        enterCourseButton.setOnClickListener(v -> navController.navigate(R.id.action_enterCourseFragment_to_currentCourseFragment));
    }
}