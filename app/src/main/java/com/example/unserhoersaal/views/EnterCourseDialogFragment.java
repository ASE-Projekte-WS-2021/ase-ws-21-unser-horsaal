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

  View view;
  MaterialButton cancelButton;
  TextView courseTitel;
  TextView courseCreator;
  TextView courseInstitution;
  TextView courseDescription;

  public EnterCourseDialogFragment() {

  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    initUi();
    cancelButton.setOnClickListener(v -> dismiss());
    Dialog builder = new Dialog(getActivity());
    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
    builder.setContentView(view);
    builder.setCancelable(true);
    return builder;
  }

  private void initUi() {
    view = getActivity().getLayoutInflater().inflate(R.layout.enter_course_detail_screen, new
            LinearLayout(getActivity()), false);
    cancelButton = view.findViewById(R.id.enterCourseDialogDeclineButton);
    courseTitel = view.findViewById(R.id.enterCourseDialogCourseTitle);
    courseCreator = view.findViewById(R.id.enterCourseDialogCourseCreator);
    courseInstitution = view.findViewById(R.id.enterCourseDialogCourseInstitution);
    courseDescription = view.findViewById(R.id.enterCourseDialogCourseDescription);
  }

  /**Comment.*/
  public void setContent(String title, String creator, String institution, String description) {
    courseTitel.setText(title);
    courseCreator.setText(creator);
    courseInstitution.setText(institution);
    courseDescription.setText(description);
  }
}
