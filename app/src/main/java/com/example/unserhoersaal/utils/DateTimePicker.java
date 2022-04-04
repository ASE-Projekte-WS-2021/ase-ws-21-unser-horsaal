package com.example.unserhoersaal.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.model.CalendarModel;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import java.util.Calendar;
import java.util.Date;

/** BindingAdapters used in CreateMeetingFragment that lets the user choose a date and time. */
public class DateTimePicker {

  private static final String TAG = "DateTimePicker";

  /** opens a date picker dialog. */
  //code reference: https://github.com/codeWithCal/DatePickerTutorial/blob/master/app/src/main/java/codewithcal/au/datepickertutorial/MainActivity.java
  @BindingAdapter("datePicker")
  public static void datePicker(TextView view, CourseHistoryViewModel courseHistoryViewModel) {
    DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
      CalendarModel calendarModel =
              Validation.checkStateLiveData(courseHistoryViewModel.calendarModelStateLiveData, TAG);
      if (calendarModel == null) {
        Log.e(TAG, "calendarModel is null");
        return;
      }

      calendarModel.setYearInput(year);
      calendarModel.setMonthInput(month);
      calendarModel.setDayOfMonthInput(day);

      String dayAsString = day < 10 ? "0" + day : String.valueOf(day);
      month++;
      String monthAsString = month < 10 ? "0" + month : String.valueOf(month);

      String time = dayAsString + "." + monthAsString + "." + year;
      view.setText(time);
    };

    Calendar cal = Calendar.getInstance();
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);

    DatePickerDialog dialog = new DatePickerDialog(view.getContext(),
            R.style.dateTimePickerStyle,
            dateSetListener, year, month, day);
    dialog.getDatePicker().setMinDate(new Date().getTime());
    dialog.getDatePicker().init(year, month, day, null);
    dialog.show();
  }

  /** opens a time picker dialog. */
  @BindingAdapter("timePicker")
  public static void timePicker(TextView view, CourseHistoryViewModel courseHistoryViewModel) {
    TimePickerDialog.OnTimeSetListener timeSetListener = (datePicker, hour, minute) -> {
      CalendarModel calendarModel =
              Validation.checkStateLiveData(courseHistoryViewModel.calendarModelStateLiveData, TAG);
      if (calendarModel == null) {
        Log.e(TAG, "MeetingModel is null");
        return;
      }

      calendarModel.setHourInput(hour);
      calendarModel.setMinuteInput(minute);

      String hourAsString = hour < 10 ? "0" + hour : String.valueOf(hour);
      String minuteAsString = minute < 10 ? "0" + minute : String.valueOf(minute);

      String time = hourAsString + ":" + minuteAsString;
      view.setText(time);
    };

    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR);
    int minute = calendar.get(Calendar.MINUTE);

    TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
            R.style.dateTimePickerStyle,
            timeSetListener, hour, minute, true
            );
    timePickerDialog.updateTime(hour, minute);
    timePickerDialog.show();
  }

}
