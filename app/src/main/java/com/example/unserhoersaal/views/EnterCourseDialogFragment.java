package com.example.unserhoersaal.views;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;
import com.google.android.material.button.MaterialButton;

/** Dialog that contains Course-Information and option to enter the course. Source:
 * https://stackoverflow.com/questions/25887373/calling-dialogfragment-from-fragment-not-
 * fragmentactivity.*/
public class EnterCourseDialogFragment extends DialogFragment {

  private static final String TAG = "EnterCourseDialogFragment";

  private View view;

  private MaterialButton cancelButton;
  private MaterialButton enterButton;
  private TextView courseTitle;
  private TextView courseCreator;
  private TextView courseInstitution;
  private TextView courseDescription;

  private EnterCourseViewModel enterCourseViewModel;

  public EnterCourseDialogFragment() {

  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    this.initViewModel();
    this.initUi();
    this.cancelButton.setOnClickListener(v -> dismiss());
    this.enterButton.setOnClickListener(v -> enter());
    Dialog builder = new Dialog(getActivity());
    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
    builder.setContentView(this.view);
    builder.setCancelable(true);
    return builder;
  }

  public void initViewModel() {
    this.enterCourseViewModel = new ViewModelProvider(requireActivity())
            .get(EnterCourseViewModel.class);
    this.enterCourseViewModel.init();
  }

  private void initUi() {
    this.view = getActivity().getLayoutInflater().inflate(R.layout.enter_course_detail_screen, new
            LinearLayout(getActivity()), false);

    this.cancelButton = this.view.findViewById(R.id.enterCourseDialogDeclineButton);
    this.enterButton = this.view.findViewById(R.id.enterCourseDialogEnterCourseButton);
    this.courseTitle = this.view.findViewById(R.id.enterCourseDialogCourseTitle);
    this.courseCreator = this.view.findViewById(R.id.enterCourseDialogCourseCreator);
    this.courseInstitution = this.view.findViewById(R.id.enterCourseDialogCourseInstitution);
    this.courseDescription = this.view.findViewById(R.id.enterCourseDialogCourseDescription);
    this.setContent(enterCourseViewModel.getCourse().getValue());
  }

  /**Comment.*/
  public void setContent(CourseModel course) {
    this.courseTitle.setText(course.getTitle());
    this.courseCreator.setText(course.getCreatorId());
    this.courseInstitution.setText(course.getInstitution());
    //TODO ALTERNATIVE FOR TOO LONG STRING
    String upToNCharacters = course.getDescription()
            .substring(0, Math.min(course.getDescription().length(), 200));
    this.courseDescription.setText(upToNCharacters);
  }

  public void enter(){
    this.enterCourseViewModel.enterCourse();
    dismiss();
  }
}
