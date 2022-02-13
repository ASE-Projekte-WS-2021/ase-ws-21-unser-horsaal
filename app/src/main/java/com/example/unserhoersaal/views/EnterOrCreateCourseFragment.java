package com.example.unserhoersaal.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;

/** Class description.*/
public class EnterOrCreateCourseFragment extends Fragment {

  private static final String TAG = "EnterOrCreateFragment";

  private CardView enterCourseCardView;
  private CardView createCourseCardView;

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
    this.initUi(view);
  }

  private void initUi(View view) {
    this.enterCourseCardView = view.findViewById(R.id.enterCourseCardView);
    this.createCourseCardView = view.findViewById(R.id.createCourseCardView);
  }
}