package com.example.unserhoersaal.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.adapter.LiveChatAdapter;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.MessageModel;
import com.example.unserhoersaal.model.ThreadModel;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;
import com.example.unserhoersaal.viewmodel.CourseParticipantsViewModel;
import com.example.unserhoersaal.viewmodel.CreateCourseViewModel;
import com.example.unserhoersaal.viewmodel.CurrentCourseViewModel;
import com.example.unserhoersaal.viewmodel.EnterCourseViewModel;
import com.example.unserhoersaal.viewmodel.LiveChatViewModel;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;
import com.google.zxing.WriterException;

import java.io.IOException;

/** Class for Navigation. */
public class NavUtil {

  private static final String TAG = "NavUtil";

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
    LiveChatViewModel liveChatViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
                    .get(LiveChatViewModel.class);
    liveChatViewModel.init();
    liveChatViewModel.setMeeting(model);

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
    currentCourseViewModel.setThread(model);

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_courseMeetingFragment_to_courseThreadFragment);
  }

  /** Navigates to CourseDescription. */
  @BindingAdapter("navigateToDescription")
  public static void navigateToDescription(View view, String courseId, String creatorId) {
    if (courseId == null) {
      return;
    }
    CourseDescriptionViewModel courseDescriptionViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
            .get(CourseDescriptionViewModel.class);
    courseDescriptionViewModel.init();
    courseDescriptionViewModel.setCourseId(courseId);
    courseDescriptionViewModel.setCreatorId(creatorId);

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

  /** Shows Confirmation Dialog when User clicks on unregister from course. */
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

  /** Shows Confirmation Dialog when User deletes a Thread Message. */
  //reference: https://developer.android.com/guide/topics/ui/dialogs
  @BindingAdapter({"viewmodel", "model"})
  public static Boolean deleteMessageText(View view,
                                       CurrentCourseViewModel vm,
                                       ThreadModel model) {

    String creatorId = vm.getThread().getValue().getData().getCreatorId();
    String uid = vm.getUserId().getValue().getData();

    if (creatorId.equals(uid)) {
      AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
      builder.setMessage(R.string.dialog_delete_message_text)
              .setTitle(R.string.dialog_delete_message_title)
              .setPositiveButton(R.string.dialog_delete_account_true,
                      (dialog, which) -> {
                        vm.deleteThreadText(model);
                        dialog.dismiss();
                      })
              .setNegativeButton(R.string.dialog_delete_account_false,
                      (dialog, which) -> dialog.dismiss());

      AlertDialog dialog = builder.create();
      dialog.show();

    }
    return false;
  }

  /** Shows Confirmation Dialog when User deletes a Thread Message. */
  //reference: https://developer.android.com/guide/topics/ui/dialogs
  @BindingAdapter({"viewmodel", "model"})
  public static Boolean deleteAnswerText(View view,
                                       CurrentCourseViewModel vm,
                                       MessageModel model) {

    String creatorId = model.getCreatorId();
    String uid = vm.getUserId().getValue().getData();

    if (creatorId.equals(uid)) {
      AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
      builder.setMessage(R.string.dialog_delete_message_text)
              .setTitle(R.string.dialog_delete_message_title)
              .setPositiveButton(R.string.dialog_delete_account_true,
                      (dialog, which) -> {
                        vm.deleteAnswerText(model);
                        dialog.dismiss();
                      })
              .setNegativeButton(R.string.dialog_delete_account_false,
                      (dialog, which) -> dialog.dismiss());

      AlertDialog dialog = builder.create();
      dialog.show();

    }
    return false;
  }

  /** Navigates to CourseEdit. */
  @BindingAdapter("navigateToCourseEdit")
  public static void navigateToCourseEdit(View view, CourseModel model) {
    if (model == null) {
      return;
    }
    CreateCourseViewModel createCourseViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
                    .get(CreateCourseViewModel.class);
    createCourseViewModel.init();
    StateLiveData<CourseModel> courseModelStateLiveData = new StateLiveData<>();
    courseModelStateLiveData.postCreate(model);
    createCourseViewModel.setCourseModelInputState(courseModelStateLiveData);
    createCourseViewModel.setIsEditing(true);

    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_courseDescriptionFragment_to_createCourseFragment);
  }

  /** Navigates to MeetingEdit. */
  @BindingAdapter("navigateToMeetingEdit")
  public static void navigateToMeetingEdit(View view, MeetingsModel model) {
    if (model == null) {
      return;
    }
    CourseHistoryViewModel courseHistoryViewModel =
            new ViewModelProvider((ViewModelStoreOwner) view.getContext())
                    .get(CourseHistoryViewModel.class);
    courseHistoryViewModel.init();
    StateLiveData<MeetingsModel> meetingsModelStateLiveData = new StateLiveData<>();
    meetingsModelStateLiveData.postCreate(model);
    courseHistoryViewModel.setMeetingModelInputState(meetingsModelStateLiveData);
    courseHistoryViewModel.setIsEditing(true);

    MeetingsModel meetingsModel = meetingsModelStateLiveData.getValue().getData();
    //TODO anpassen an meeting changes
    /*courseHistoryViewModel.setTimeInputForDisplay(meetingsModel
            .getHourInput(), meetingsModel.getMinuteInput());
    courseHistoryViewModel.setEndTimeInputForDisplay(meetingsModel
            .getHourEndInput(), meetingsModel.getMinuteEndInput());*/


    NavController navController = Navigation.findNavController(view);
    navController.navigate(R.id.action_courseHistoryFragment_to_createCourseMeetingFragment);
  }

  /** Used in OnboadingWrapperFragment for users that do not want to experience the onboarding.
   * They skip directly to the registration fragment. Setting onboarding_complete to true so
   * that it will not be displayed on the next opening of the app. */
  @BindingAdapter("skipOnboarding")
  public static void skipOnboarding(View view, int navAction) {
    SharedPreferences sharedPreferences = view.getContext()
            .getSharedPreferences(Config.SHARED_PREF_KEY, Context.MODE_PRIVATE);
    sharedPreferences.edit().putBoolean(Config.SHARED_PREF_ONBOARDING_KEY, true).apply();
    NavController navController = Navigation.findNavController(view);
    navController.navigate(navAction);
  }

  @BindingAdapter("openBrowser")
  public static void openBrowser(View view, String destination) {
    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(destination));
    view.getContext().startActivity(i);

  }

  /** Opens Camera App. */
  @BindingAdapter("navigateToCameraApp")
  public static void openCameraApp(View view, EnterCourseViewModel vm) {

      Intent camera_intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
      Context context = view.getContext();
    try {
      context.startActivity(camera_intent);
    }
    catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(context,Config.CAMERA_INTENT_ERROR_TOAST, Toast.LENGTH_LONG).show();
    }


  }

  /** Opens Camera App. */
  @BindingAdapter("navigateToCameraApp")
  public static void shareCourseCode(View view, CourseDescriptionViewModel vm) {

    Intent camera_intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
    Context context = view.getContext();
    try {
      context.startActivity(camera_intent);
    }
    catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(context,Config.CAMERA_INTENT_ERROR_TOAST, Toast.LENGTH_LONG).show();
    }


  }

}
