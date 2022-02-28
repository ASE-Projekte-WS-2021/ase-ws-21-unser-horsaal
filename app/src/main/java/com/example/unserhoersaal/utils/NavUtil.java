package com.example.unserhoersaal.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;

/** Class for Navigation. */
public class NavUtil {

  @BindingAdapter("navigate")
  public static void navigate(View view, int navAction) {
    NavController navController = Navigation.findNavController(view);
    navController.navigate(navAction);
  }

  @BindingAdapter("navigateToCourse")
  public static void navigateToCourse(View view, CourseModel model) {
    if (model == null) return;
    CourseHistoryViewModel courseHistoryViewModel = new ViewModelProvider((ViewModelStoreOwner) view.getContext())
            .get(CourseHistoryViewModel.class);
    courseHistoryViewModel.init();
    courseHistoryViewModel.setCourseId(model.getKey());

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_coursesFragment_to_courseHistoryFragment);
  }

  @BindingAdapter("navigateToMeeting")
  public static void navigateToMeeting(View view, MeetingsModel model) {
    if (model == null) return;
    CourseMeetingViewModel courseMeetingViewModel = new ViewModelProvider((ViewModelStoreOwner) view.getContext())
            .get(CourseMeetingViewModel.class);
    courseMeetingViewModel.init();
    courseMeetingViewModel.setMeetingId(model.getKey());

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_coursesFragment_to_courseHistoryFragment);
  }

  @BindingAdapter("navigateToThread")
  public static void navigateToThread(View view, ThreadModel model) {
    if (model == null) return;
    CourseMeetingViewModel courseMeetingViewModel = new ViewModelProvider((ViewModelStoreOwner) view.getContext())
            .get(CourseMeetingViewModel.class);
    courseMeetingViewModel.init();
    courseMeetingViewModel.setMeetingId(model.getKey());

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_courseMeetingFragment_to_currentCourseFragment);
  }

  //reference: https://developer.android.com/guide/topics/ui/dialogs
  @BindingAdapter("viewModel")
  public static void deleteAccountDialog(View view, ProfileViewModel viewModel) {
    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
    builder.setMessage(R.string.dialog_delete_account_message)
            .setTitle(R.string.dialog_delete_account_title)
            .setPositiveButton(R.string.dialog_delete_account_true,
                    (DialogInterface.OnClickListener) (dialog, which) -> {
                      viewModel.deleteAccount();
                      dialog.dismiss();
                    })
            .setNegativeButton(R.string.dialog_delete_account_false,
                    (DialogInterface.OnClickListener) (dialog, which) -> {
                      dialog.dismiss();
                    });

    AlertDialog dialog = builder.create();
    dialog.show();
  }

  //reference: https://developer.android.com/guide/topics/ui/dialogs
  @BindingAdapter("unregisterFromCourse")
  public static void unregisterFromCourse(View view, CourseDescriptionViewModel viewModel) {
    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
    builder.setMessage(R.string.unregister_from_course_message)
            .setTitle(R.string.unregister_from_course_title)
            .setPositiveButton(R.string.dialog_delete_account_true,
                    (DialogInterface.OnClickListener) (dialog, which) -> {
                      viewModel.unregisterFromCourse();
                      dialog.dismiss();
                    })
            .setNegativeButton(R.string.dialog_delete_account_false,
                    (DialogInterface.OnClickListener) (dialog, which) -> {
                      dialog.dismiss();
                    });

    AlertDialog dialog = builder.create();
    dialog.show();
  }
  
}
