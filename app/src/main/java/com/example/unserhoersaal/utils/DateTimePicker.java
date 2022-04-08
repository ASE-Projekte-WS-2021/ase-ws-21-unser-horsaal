package com.example.unserhoersaal.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.CalendarModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import java.util.Calendar;
import java.util.Date;

/**
 * BindingAdapters used in CreateMeetingFragment that lets the user choose a date and time.
 */
public class DateTimePicker {

  private static final String TAG = "DateTimePicker";

  /** opens a date picker dialog.
   *
   * @param view The Button that got clicked.
   * @param courseHistoryViewModel The ViewModel to set the Date in.
   */
  //code reference: https://github.com/codeWithCal/DatePickerTutorial/blob/master/app/src/main/java/codewithcal/au/datepickertutorial/MainActivity.java
  @BindingAdapter("datePicker")
  public static void datePicker(TextView view, CourseHistoryViewModel courseHistoryViewModel) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
      CalendarModel calendarModel =
              Validation.checkStateLiveData(courseHistoryViewModel.calendarModelStateLiveData, TAG);
      if (calendarModel == null) {
        return;
      }

      calendarModel.setYearInput(year);
      calendarModel.setMonthInput(month);
      calendarModel.setDayOfMonthInput(day);
      courseHistoryViewModel.calendarModelStateLiveData.postUpdate(calendarModel);
    };
    MeetingsModel meetingsModel = Validation
            .checkStateLiveData(courseHistoryViewModel.meetingModelInputState, TAG);
    Calendar calendar = Calendar.getInstance();
    if (meetingsModel != null && meetingsModel.getEventTime() != null) {
      calendar.setTimeInMillis(meetingsModel.getEventTime());
    }
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    DatePickerDialog dialog = new DatePickerDialog(view.getContext(),
            R.style.dateTimePickerStyle,
            dateSetListener, year, month, day);
    dialog.getDatePicker().setMinDate(new Date().getTime());
    dialog.getDatePicker().init(year, month, day, null);
    dialog.show();
  }

  /** opens a time picker dialog.
   *
   * @param view Button that got clicked.
   * @param courseHistoryViewModel ViewModel to set the Time in
   */
  @BindingAdapter("timePicker")
  public static void timePicker(TextView view, CourseHistoryViewModel courseHistoryViewModel) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    TimePickerDialog.OnTimeSetListener timeSetListener = (datePicker, hour, minute) -> {
      CalendarModel calendarModel =
              Validation.checkStateLiveData(courseHistoryViewModel.calendarModelStateLiveData, TAG);
      if (calendarModel == null) {
        Log.e(TAG, "MeetingModel is null");
        return;
      }

      calendarModel.setHourInput(hour);
      calendarModel.setMinuteInput(minute);
      courseHistoryViewModel.calendarModelStateLiveData.postUpdate(calendarModel);
    };
    MeetingsModel meetingsModel = Validation
            .checkStateLiveData(courseHistoryViewModel.meetingModelInputState, TAG);
    Calendar calendar = Calendar.getInstance();
    if (meetingsModel != null && meetingsModel.getEventTime() != null) {
      calendar.setTimeInMillis(meetingsModel.getEventTime());
    }
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
            R.style.dateTimePickerStyle,
            timeSetListener, hour, minute, true
            );
    timePickerDialog.updateTime(hour, minute);
    timePickerDialog.show();
  }

}
