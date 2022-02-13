package com.example.unserhoersaal.views;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.unserhoersaal.R;
import com.google.android.material.button.MaterialButton;

/** Dialog that contains Course-Information and option to enter the course. Source:
 * https://stackoverflow.com/questions/25887373/calling-dialogfragment-from-fragment-not-
 * fragmentactivity.*/
public class EnterCourseDialogFragment extends DialogFragment {

  private View view;

  private MaterialButton cancelButton;
  private TextView courseTitle;
  private TextView courseCreator;
  private TextView courseInstitution;
  private TextView courseDescription;

  public EnterCourseDialogFragment() {

  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    this.initUi();
    this.cancelButton.setOnClickListener(v -> dismiss());
    Dialog builder = new Dialog(getActivity());
    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
    builder.setContentView(this.view);
    builder.setCancelable(true);
    return builder;
  }

  private void initUi() {
    this.view = getActivity().getLayoutInflater().inflate(R.layout.enter_course_detail_screen, new
            LinearLayout(getActivity()), false);
    this.cancelButton = this.view.findViewById(R.id.enterCourseDialogDeclineButton);
    this.courseTitle = this.view.findViewById(R.id.enterCourseDialogCourseTitle);
    this.courseCreator = this.view.findViewById(R.id.enterCourseDialogCourseCreator);
    this.courseInstitution = this.view.findViewById(R.id.enterCourseDialogCourseInstitution);
    this.courseDescription = this.view.findViewById(R.id.enterCourseDialogCourseDescription);
  }

  /**Comment.*/
  public void setContent(String title, String creator, String institution, String description) {
    this.courseTitle.setText(title);
    this.courseCreator.setText(creator);
    this.courseInstitution.setText(institution);
    this.courseDescription.setText(description);
  }
}
