package views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unser_hoersaal.R;

public class EnterOrCreateCourseFragment extends Fragment {

    CardView enterCourseCardView;
    CardView createCourseCardView;

    public EnterOrCreateCourseFragment() {
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
        return inflater.inflate(R.layout.fragment_enter_or_create_course, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        setupNavigation(view);
    }

    private void initUI(View view){
        enterCourseCardView = view.findViewById(R.id.enterCourseCardView);
        createCourseCardView = view.findViewById(R.id.createCourseCardView);
    }

    //setup Navigation to corresponding fragments
    private void setupNavigation(View view){
        NavController navController = Navigation.findNavController(view);
        enterCourseCardView.setOnClickListener(v -> navController.navigate(R.id.action_enterOrCreateCourseFragment_to_enterCourseFragment));
        createCourseCardView.setOnClickListener(v -> navController.navigate(R.id.action_enterOrCreateCourseFragment_to_createCourseFragment));
    }
}