package com.example.unserhoersaal.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.widget.TextView;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.MeetingsModel;
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
      MeetingsModel meetingModel =
              Validation.checkStateLiveData(courseHistoryViewModel.meetingModelInputState, TAG);
      if (meetingModel == null) {
        Log.e(TAG, "MeetingModel is null");
        return;
      }

      meetingModel.setYearInput(year);
      meetingModel.setMonthInput(month);
      meetingModel.setDayOfMonthInput(day);
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
      MeetingsModel meetingModel =
              Validation.checkStateLiveData(courseHistoryViewModel.meetingModelInputState, TAG);
      if (meetingModel == null) {
        Log.e(TAG, "MeetingModel is null");
        return;
      }

      //TODO maybe dont save everything extra in the model and push to database
      if (view.getId() == R.id.createCourseMeetingTimePicker) {
        meetingModel.setHourInput(hour);
        meetingModel.setMinuteInput(minute);
      } else if (view.getId() == R.id.createCourseMeetingEndTimePicker) {
        meetingModel.setHourEndInput(hour);
        meetingModel.setMinuteEndInput(minute);
      }
      String time = formatTime(hour,minute);
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

  public static String formatTime(int hour, int minute) {
    String hourString = String.valueOf(hour);
    String minuteString = String.valueOf(minute);
    if (hour < 10) {
      hourString = "0" + hourString;
    }
    if (minute < 10) {
      minuteString = "0" + minuteString;
    }
    return hourString + ":" + minuteString;
  }

}
