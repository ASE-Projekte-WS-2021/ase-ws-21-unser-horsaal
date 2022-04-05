package com.example.unserhoersaal.viewmodel;

import android.util.Log;
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
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.TimeUtil;
import com.example.unserhoersaal.utils.Validation;
import java.util.Date;
import java.util.List;

/** ViewModel for the CourseHistoryFragment. */
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
  private String timeInputForDisplay;
  private String endTimeInputForDisplay;

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

  public void makeEditable(MeetingsModel meetingsModel) {
    this.isEditing = true;

    MeetingsModel m = new MeetingsModel();
    m.setKey(meetingsModel.getKey());
    m.setTitle(meetingsModel.getTitle());

    this.meetingModelInputState.postCreate(m);
  }

  public void resetMeetingData() {
    this.meetingModelInputState.postCreate(new MeetingsModel());
    this.calendarModelStateLiveData.postCreate(new CalendarModel());
    this.meetingsModelMutableLiveData.postCreate(new MeetingsModel());
  }

  public StateLiveData<List<MeetingsModel>> getMeetings() {
    return this.meetings;
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
    this.meetingsModelMutableLiveData.postLoading();

    MeetingsModel meetingsModel = Validation.checkStateLiveData(this.meetingModelInputState, TAG);
    if (meetingsModel == null) {
      Log.e(TAG, "meetingsModel is null.");
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    CalendarModel calendarModel = Validation.checkStateLiveData(
            this.calendarModelStateLiveData, TAG);
    if (calendarModel == null) {
      Log.e(TAG, "meetingsModel is null.");
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (meetingsModel.getTitle() == null) {
      Log.d(TAG, "title is null.");
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.TITLE);
      return;
    } else if (!Validation.stringHasPattern(meetingsModel.getTitle(), Config.REGEX_PATTERN_TITLE)) {
      Log.d(TAG, "title wrong pattern.");
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.TITLE);
      return;
    }

    if (calendarModel.getYearInput() == -1
            || calendarModel.getMonthInput() == -1
            || calendarModel.getDayOfMonthInput() == -1) {
      Log.d(TAG, "user did not pick a date");
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.CREATE_MEETING_DATE_WRONG), ErrorTag.TIME_PICKER_DATE);
      return;
    }

    if (calendarModel.getHourInput() == -1 || calendarModel.getMinuteInput() == -1) {
      Log.d(TAG, "user did not pick a start time");
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.CREATE_MEETING_TIME_WRONG), ErrorTag.TIME_PICKER_TIME);
      return;
    }

    if (calendarModel.getHourDuration() == null || calendarModel.getHourDuration().equals("")) {
      Log.d(TAG, "user did not pick hour duration");
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.CREATE_MEETING_HOUR_DURATION_WRONG),
              ErrorTag.TIME_PICKER_HOUR_DURATION);
      return;
    }

    if (calendarModel.getMinuteDuration() == null || calendarModel.getMinuteDuration().equals("")) {
      Log.d(TAG, "user did not pick minute duration");
      this.meetingsModelMutableLiveData.postError(
              new Error(Config.CREATE_MEETING_MINUTE_DURATION_WRONG),
              ErrorTag.TIME_PICKER_MINUTE_DURATION);
      return;
    }

    meetingsModel.setEventTime(TimeUtil.parseEventTime(calendarModel));
    meetingsModel.setEventEndTime(TimeUtil.parseEventEndTime(meetingsModel, calendarModel));
    meetingsModel.setMeetingDate(TimeUtil.parseMeetingDate(meetingsModel));
    meetingsModel.setCreationTime(new Date().getTime());

    this.meetingModelInputState.postCreate(new MeetingsModel());
    this.calendarModelStateLiveData.postCreate(new CalendarModel());
    
    if (isEditing) {
      this.courseHistoryRepository.editMeeting(meetingsModel);
    } else {
      this.courseHistoryRepository.createMeeting(meetingsModel);
    }
  }

  public String  getCreatorId() {
    return courseHistoryRepository.
            getCourse().getValue().getData().getCreatorId();
  }

  public void setTimeInputForDisplay(int hour, int minute) {
    String timeInputForDisplay = DateTimePicker.formatTime(hour, minute);
    this.timeInputForDisplay = timeInputForDisplay;
  }

  public void setEndTimeInputForDisplay(int hour, int minute) {
    String endTimeInputForDisplay = DateTimePicker.formatTime(hour, minute);
    this.endTimeInputForDisplay = endTimeInputForDisplay;
  }

  public String getTimeInputForDisplay() {
    return timeInputForDisplay;
  }

  public String getEndTimeInputForDisplay() {
    return endTimeInputForDisplay;
  }

  public String getUid() {
    return courseHistoryRepository.getUid();
  }

  public void setMeetingModelInputState(StateLiveData<MeetingsModel> meetingModelInputState) {
    this.meetingModelInputState = meetingModelInputState;
  }

  public void setIsEditing(Boolean isEditing) {
    this.isEditing = isEditing;
  }

  public Boolean getIsEditing() {
    return isEditing;
  }

}
