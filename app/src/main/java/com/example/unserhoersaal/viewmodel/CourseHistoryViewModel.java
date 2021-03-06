package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CalendarModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.repository.CourseHistoryRepository;
import com.example.unserhoersaal.utils.ArrayListUtil;
import com.example.unserhoersaal.utils.DateTimePicker;
import com.example.unserhoersaal.utils.NavUtil;
import com.example.unserhoersaal.utils.PreventDoubleClick;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.TimeUtil;
import com.example.unserhoersaal.utils.Validation;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** ViewModel for the CourseHistoryFragment. Used to create and edit meeting - only accessible for
 * the creator of the course.*/
public class CourseHistoryViewModel extends ViewModel {

  private static final String TAG = "CourseHistoryViewModel";

  private CourseHistoryRepository courseHistoryRepository;
  private StateLiveData<CourseModel> course = new StateLiveData<>();
  private StateLiveData<List<MeetingsModel>> meetings;
  private StateLiveData<MeetingsModel> meetingsModelMutableLiveData;
  public StateLiveData<MeetingsModel> meetingModelInputState = new StateLiveData<>();
  public StateLiveData<CalendarModel> calendarModelStateLiveData = new StateLiveData<>();
  public StateLiveData<String> userId;
  private ArrayListUtil arrayListUtil;
  private Boolean isEditing = false;

  /** Initialise the ViewModel. */
  public void init() {
    if (this.meetings != null) {
      return;
    }

    this.courseHistoryRepository = CourseHistoryRepository.getInstance();
    this.course = this.courseHistoryRepository.getCourse();
    this.meetingsModelMutableLiveData
            = this.courseHistoryRepository.getMeetingsModelMutableLiveData();
    this.courseHistoryRepository.setUserId();
    this.userId = this.courseHistoryRepository.getUserId();

    if (this.course.getValue() != null) {
      this.meetings = this.courseHistoryRepository.getMeetings();
    }
    this.meetingModelInputState.postCreate(new MeetingsModel());
    this.calendarModelStateLiveData.postCreate(new CalendarModel());
    this.arrayListUtil = new ArrayListUtil();
  }

  /** Makes the values in CreateMeetingFragment editable.
   *
   * @param meetingsModel set this meetingModel to load into CreateMeetingFragment
   *                      and make it editable. */
  public void makeEditable(MeetingsModel meetingsModel) {
    this.isEditing = true;

    MeetingsModel m = new MeetingsModel();
    m.setKey(meetingsModel.getKey());
    m.setTitle(meetingsModel.getTitle());
    m.setEventTime(meetingsModel.getEventTime());
    m.setEventEndTime(meetingsModel.getEventEndTime());

    CalendarModel c = this.getTimeInputs(m);

    this.meetingModelInputState.postCreate(m);
    this.calendarModelStateLiveData.postCreate(c);
  }

  /** Converts start and end timestamps to year, month, day, hour, minute and duration for edit.
   *
   * @param meetingsModel contains eventStartTime and eventEndTime that are used
   *                      to convert to CalendarModel
   * @return returns a CalendarModel that has the converted values for a Date and TimePicker
   * @see DateTimePicker
   */
  private CalendarModel getTimeInputs(MeetingsModel meetingsModel) {
    CalendarModel c = new CalendarModel();
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(meetingsModel.getEventTime());

    c.setYearInput(calendar.get(Calendar.YEAR));
    c.setMonthInput(calendar.get(Calendar.MONTH));
    c.setDayOfMonthInput(calendar.get(Calendar.DAY_OF_MONTH));
    c.setHourInput(calendar.get(Calendar.HOUR_OF_DAY));
    c.setMinuteInput(calendar.get(Calendar.MINUTE));

    long timeDifference = meetingsModel.getEventEndTime() - meetingsModel.getEventTime();
    c.setHourDuration(String.valueOf(timeDifference
                / Config.TIME_HOUR_TO_MILLI % Config.TIME_HOUR_PER_DAY));
    c.setMinuteDuration(String.valueOf(timeDifference
                / Config.TIME_MINUTE_TO_MILLI % Config.TIME_MINUTE_PER_HOUR));

    return c;
  }

  /** Reset the input data and live data with postCreate onPause lifecylce method. */
  public void resetMeetingData() {
    this.meetingModelInputState.postCreate(new MeetingsModel());
    this.calendarModelStateLiveData.postCreate(new CalendarModel());
    this.meetingsModelMutableLiveData.postCreate(new MeetingsModel());
  }

  /** Sets the input data and live data to status complete to reset datastatus onPause
   * lifecycle if there is still a running status. */
  public void setLiveDataComplete() {
    this.meetingModelInputState.postComplete();
    this.calendarModelStateLiveData.postComplete();
    this.meetingsModelMutableLiveData.postComplete();
  }

  public StateLiveData<List<MeetingsModel>> getMeetings() {
    return this.meetings;
  }

  public StateLiveData<CalendarModel> getCalendarModelStateLiveData() {
    return this.calendarModelStateLiveData;
  }

  /** Sort the meetings list.
   *
   * @param meetingsModelList list of meetingmodels to filter
   */
  public void sortMeetingsByNewest(List<MeetingsModel> meetingsModelList) {
    this.arrayListUtil.sortMeetingListByEventTime(meetingsModelList);
  }

  public StateLiveData<CourseModel> getCourse() {
    return this.course;
  }

  public StateLiveData<MeetingsModel> getMeetingsModel() {
    return this.meetingsModelMutableLiveData;
  }

  public StateLiveData<String> getUserId() {
    return this.userId;
  }

  public void setUserId() {
    this.courseHistoryRepository.setUserId();
  }

  public void setCourse(CourseModel course) {
    this.courseHistoryRepository.setCourse(course);
  }

  /** Create a new Meeting. */
  public void createMeeting() {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.meetingModelInputState.postLoading();

    MeetingsModel meetingsModel = Validation.checkStateLiveData(this.meetingModelInputState, TAG);
    if (meetingsModel == null) {
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    CalendarModel calendarModel = Validation
            .checkStateLiveData(this.calendarModelStateLiveData, TAG);
    if (calendarModel == null) {
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    this.checkMeetingInput(meetingsModel, calendarModel);
  }

  private void checkMeetingInput(MeetingsModel meetingsModel, CalendarModel calendarModel) {
    if (Validation.emptyString(meetingsModel.getTitle())) {
      this.meetingModelInputState.postError(
              new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.TITLE);
    } else if (!Validation.stringHasPattern(meetingsModel.getTitle(), Config.REGEX_PATTERN_TITLE)) {
      this.meetingModelInputState.postError(
              new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.TITLE);
    } else if (calendarModel.getYearInput() == -1
            || calendarModel.getMonthInput() == -1
            || calendarModel.getDayOfMonthInput() == -1) {
      this.meetingModelInputState.postError(
              new Error(Config.CREATE_MEETING_DATE_WRONG), ErrorTag.TIME_PICKER_DATE);
    } else if (calendarModel.getHourInput() == -1 || calendarModel.getMinuteInput() == -1) {
      this.meetingModelInputState.postError(
              new Error(Config.CREATE_MEETING_TIME_WRONG), ErrorTag.TIME_PICKER_TIME);

    } else if (Validation.emptyString(calendarModel.getHourDuration())) {
      this.meetingModelInputState.postError(
              new Error(Config.CREATE_MEETING_HOUR_DURATION_WRONG),
              ErrorTag.TIME_PICKER_HOUR_DURATION);
    } else if (Validation.emptyString(calendarModel.getMinuteDuration())) {
      this.meetingModelInputState.postError(
              new Error(Config.CREATE_MEETING_MINUTE_DURATION_WRONG),
              ErrorTag.TIME_PICKER_MINUTE_DURATION);
    } else {
      this.passInputToRepo(meetingsModel, calendarModel);
    }
  }

  private void passInputToRepo(MeetingsModel meetingsModel, CalendarModel calendarModel) {
    meetingsModel.setEventTime(TimeUtil.parseEventTime(calendarModel));
    meetingsModel.setEventEndTime(TimeUtil.parseEventEndTime(meetingsModel, calendarModel));
    meetingsModel.setCreationTime(new Date().getTime());

    this.meetingModelInputState.postComplete();
    this.calendarModelStateLiveData.postComplete();

    if (this.isEditing) {
      this.courseHistoryRepository.editMeeting(meetingsModel);
    } else {
      this.courseHistoryRepository.createMeeting(meetingsModel);
    }
  }

  public String  getCreatorId() {
    return courseHistoryRepository.getCourse().getValue().getData().getCreatorId();
  }

  public String getUid() {
    return courseHistoryRepository.getUid();
  }

  public void setIsEditing(Boolean isEditing) {
    this.isEditing = isEditing;
  }

  public Boolean getIsEditing() {
    return isEditing;
  }

}
