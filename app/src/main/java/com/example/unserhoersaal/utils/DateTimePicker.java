package com.example.unserhoersaal.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.viewmodel.CourseHistoryViewModel;
import java.util.Calendar;
import java.util.Date;

/** BindingAdapters used in CreateMeetingFragment that lets the user choose a date and time. */
public class DateTimePicker {

  /** opens a date picker dialog. */
  //code reference: https://github.com/codeWithCal/DatePickerTutorial/blob/master/app/src/main/java/codewithcal/au/datepickertutorial/MainActivity.java
  @BindingAdapter("datePicker")
  public static void datePicker(TextView view, CourseHistoryViewModel courseHistoryViewModel) {
    DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
      //TODO: handle error to view!
      if (courseHistoryViewModel.dataBindingMeetingInput.getValue() == null) {
        return;
      }

      courseHistoryViewModel.dataBindingMeetingInput.getValue().setYearInput(year);
      courseHistoryViewModel.dataBindingMeetingInput.getValue().setMonthInput(month);
      courseHistoryViewModel.dataBindingMeetingInput.getValue().setDayOfMonthInput(day);
      String time = day + "." + (month + 1) + "." + year;
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
    dialog.show();
  }

  /** opens a time picker dialog. */
  @BindingAdapter("timePicker")
  public static void timePicker(TextView view, CourseHistoryViewModel courseHistoryViewModel) {
    TimePickerDialog.OnTimeSetListener timeSetListener = (datePicker, hour, minute) -> {
      //TODO: handle error to view!
      if (courseHistoryViewModel.dataBindingMeetingInput.getValue() == null) {
        return;
      }

      //TODO maybe dont save everything extra in the model and push to database
      if (view.getId() == R.id.createCourseMeetingTimePicker) {
        courseHistoryViewModel.dataBindingMeetingInput.getValue().setHourInput(hour);
        courseHistoryViewModel.dataBindingMeetingInput.getValue().setMinuteInput(minute);
      } else if (view.getId() == R.id.createCourseMeetingEndTimePicker) {
        courseHistoryViewModel.dataBindingMeetingInput.getValue().setHourEndInput(hour);
        courseHistoryViewModel.dataBindingMeetingInput.getValue().setMinuteEndInput(minute);
      }
      String time = hour + ":" + minute;
      view.setText(time);
    };

    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR);
    int minute = calendar.get(Calendar.MINUTE);

    TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
            R.style.dateTimePickerStyle,
            timeSetListener, hour, minute, true
            );
    timePickerDialog.show();
  }

}
