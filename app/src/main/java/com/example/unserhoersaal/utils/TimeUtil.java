package com.example.unserhoersaal.utils;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.model.CalendarModel;
import com.example.unserhoersaal.model.MeetingsModel;
import java.util.Calendar;

public class TimeUtil {

  /** convert the input from date and time picker to unix timestamp. */
  public static long parseEventTime(CalendarModel calendarModel) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, calendarModel.getYearInput());
    calendar.set(Calendar.MONTH, calendarModel.getMonthInput());
    calendar.set(Calendar.DAY_OF_MONTH, calendarModel.getDayOfMonthInput());
    calendar.set(Calendar.HOUR_OF_DAY, calendarModel.getHourInput());
    calendar.set(Calendar.MINUTE, calendarModel.getMinuteInput());

    return calendar.getTimeInMillis();
  }

  public static long parseEventEndTime(MeetingsModel meetingsModel, CalendarModel calendarModel) {
    return meetingsModel.getEventTime()
            + (Long.parseLong(calendarModel.getHourDuration()) * Config.TIME_HOUR_TO_MILLI)
            + (Long.parseLong(calendarModel.getMinuteDuration()) * Config.TIME_MINUTE_TO_MILLI);
  }

}
