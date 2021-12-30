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
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.unser_hoersaal.R;

public class RegistrationFragment extends Fragment {

    EditText userNameEditText;
    EditText passwordEditText;
    EditText repeatPassowordEditText;
    CheckBox checkBox;
    Button registrationButton;

    public RegistrationFragment() {
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
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        setupNavigation(view);
    }

    private void initUI(View view){
        userNameEditText = view.findViewById(R.id.registrationFragmentUserNameEditText);
        passwordEditText = view.findViewById(R.id.registrationFragmentPasswordEditText);
        repeatPassowordEditText = view.findViewById(R.id.registrationFragmentRepeatPasswordEditText);
        checkBox = view.findViewById(R.id.registrationFragmentCheckBox);
        registrationButton = view.findViewById(R.id.registrationFragmentRegistrationButton);
    }

    //setup Navigation to corresponding fragments
    private void setupNavigation(View view){
        NavController navController = Navigation.findNavController(view);

        //todo add logic to registration
        registrationButton.setOnClickListener(v -> navController.navigate(R.id.action_registrationFragment_to_enterOrCreateCourseFragment));
    }
}