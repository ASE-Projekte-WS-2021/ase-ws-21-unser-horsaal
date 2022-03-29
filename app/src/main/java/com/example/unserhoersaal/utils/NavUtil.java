package com.example.unserhoersaal.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
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
import com.example.unserhoersaal.viewmodel.CourseParticipantsViewModel;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;

/** Class for Navigation. */
public class NavUtil {

  @BindingAdapter("navigate")
  public static void navigate(View view, int navAction) {
    NavController navController = Navigation.findNavController(view);
    navController.navigate(navAction);
  }

  /** Navigates to CourseHistory. */
  @BindingAdapter("navigateToCourse")
  public static void navigateToCourse(View view, CourseModel model) {
    if (model == null) {
      return;
    }
    CourseHistoryViewModel courseHistoryViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
            .get(CourseHistoryViewModel.class);
    courseHistoryViewModel.init();
    Log.d("test", model.getKey());
    courseHistoryViewModel.setCourse(model);

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_coursesFragment_to_courseHistoryFragment);
  }

  /** Navigates to CourseMeeting. */
  @BindingAdapter("navigateToMeeting")
  public static void navigateToMeeting(View view, MeetingsModel model) {
    if (model == null) {
      return;
    }
    CourseMeetingViewModel courseMeetingViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
            .get(CourseMeetingViewModel.class);
    courseMeetingViewModel.init();
    courseMeetingViewModel.setMeeting(model);
    CurrentCourseViewModel currentCourseViewModel =
            new ViewModelProvider((ViewModelStoreOwner)
            view.getContext()).get(CurrentCourseViewModel.class);
    currentCourseViewModel.init();
    currentCourseViewModel.setMeeting(model);

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_courseHistoryFragment_to_courseMeetingFragment);
  }

  /** Navigates to CourseThread. */
  @BindingAdapter("navigateToThread")
  public static void navigateToThread(View view, ThreadModel model) {
    if (model == null) {
      return;
    }
    CurrentCourseViewModel currentCourseViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
            .get(CurrentCourseViewModel.class);
    currentCourseViewModel.init();
    currentCourseViewModel.setThreadId(model.getKey());

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_courseMeetingFragment_to_courseThreadFragment);
  }

  /** Navigates to CourseDescription. */
  @BindingAdapter("navigateToDescription")
  public static void navigateToDescription(View view, String courseId) {
    if (courseId == null) {
      return;
    }
    CourseDescriptionViewModel courseDescriptionViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
            .get(CourseDescriptionViewModel.class);
    courseDescriptionViewModel.init();
    Log.d("NAVUTIL", courseId);
    courseDescriptionViewModel.setCourseId(courseId);

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_courseHistoryFragment_to_courseDescriptionFragment);
  }

  /** Shows Confirmation Dialog when User clicks on delete Account in Profile. */
  //reference: https://developer.android.com/guide/topics/ui/dialogs
  @BindingAdapter("viewModel")
  public static void deleteAccountDialog(View view, ProfileViewModel viewModel) {
    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
    builder.setMessage(R.string.dialog_delete_account_message)
            .setTitle(R.string.dialog_delete_account_title)
            .setPositiveButton(R.string.dialog_delete_account_true,
                    (dialog, which) -> {
                      viewModel.deleteAccount();
                      dialog.dismiss();
                    })
            .setNegativeButton(R.string.dialog_delete_account_false,
                    (dialog, which) -> dialog.dismiss());

    AlertDialog dialog = builder.create();
    dialog.show();
  }

  /** Navigates to CourseHistory. */
  //reference: https://developer.android.com/guide/topics/ui/dialogs
  @BindingAdapter("unregisterFromCourse")
  public static void unregisterFromCourse(View view, CourseDescriptionViewModel viewModel) {
    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
    builder.setMessage(R.string.unregister_from_course_message)
            .setTitle(R.string.unregister_from_course_title)
            .setPositiveButton(R.string.dialog_delete_account_true,
                    (dialog, which) -> {
                      viewModel.unregisterFromCourse();
                      dialog.dismiss();
                    })
            .setNegativeButton(R.string.dialog_delete_account_false,
                    (dialog, which) -> dialog.dismiss());

    AlertDialog dialog = builder.create();
    dialog.show();
  }


  @BindingAdapter("navigateToCourseEdit")
  public static void navigateToCourseEdit(View view, CourseModel model) {
    if (model == null) {
      return;
    }
    CreateCourseViewModel createCourseViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
                    .get(CreateCourseViewModel.class);
    createCourseViewModel.init();
    Log.d("test", model.getKey());
    StateLiveData<CourseModel> courseModelStateLiveData = new StateLiveData<>();
    courseModelStateLiveData.postCreate(model);
    createCourseViewModel.setCourseModelInputState(courseModelStateLiveData);
    createCourseViewModel.setIsEditing(true);

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_courseDescriptionFragment_to_createCourseFragment);
  }

  @BindingAdapter("navigateToMeetingEdit")
  public static void navigateToMeetingEdit(View view, MeetingsModel model) {
    if (model == null) {
      return;
    }
    CourseHistoryViewModel courseHistoryViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
                    .get(CourseHistoryViewModel.class);
    courseHistoryViewModel.init();
    Log.d("test", model.getKey());
    StateLiveData<MeetingsModel> meetingsModelStateLiveData = new StateLiveData<>();
    meetingsModelStateLiveData.postCreate(model);
    courseHistoryViewModel.setMeetingModelInputState(meetingsModelStateLiveData);
    courseHistoryViewModel.setIsEditing(true);

    MeetingsModel meetingsModel = meetingsModelStateLiveData.getValue().getData();



    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_courseHistoryFragment_to_createCourseMeetingFragment);
  }

}
