package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.repository.CourseHistoryRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/** ViewModel for the CourseHistoryFragment. */
public class CourseHistoryViewModel extends ViewModel {

  private static final String TAG = "CourseHistoryViewModel";

  private CourseHistoryRepository courseHistoryRepository;
  private StateLiveData<CourseModel> courseRepoState = new StateLiveData<>();
  private StateLiveData<List<MeetingsModel>> allMeetingsRepoState;
  private StateLiveData<MeetingsModel> currentMeetingRepoState;
  public StateLiveData<MeetingsModel> meetingInputState = new StateLiveData<>();
  public StateLiveData<String> currentUserIdRepoState;


  /** Initialise the ViewModel. */
  public void init() {
    if (this.allMeetingsRepoState != null) {
      return;
    }

    this.courseHistoryRepository = CourseHistoryRepository.getInstance();
    this.courseRepoState = this.courseHistoryRepository.getCourseRepoState();
    this.currentMeetingRepoState
            = this.courseHistoryRepository.getCurrentMeetingRepoState();
    this.courseHistoryRepository.setUserId();
    this.currentUserIdRepoState = this.courseHistoryRepository.getCurrentUserIdRepoState();

    if (this.courseRepoState.getValue() != null) {
      this.allMeetingsRepoState = this.courseHistoryRepository.getAllMeetingsRepoState();
    }
    this.meetingInputState.postCreate(new MeetingsModel());
  }

  public void resetMeetingData() {
    this.meetingInputState.postCreate(new MeetingsModel());
    this.currentMeetingRepoState.postCreate(new MeetingsModel());
  }

  public StateLiveData<List<MeetingsModel>> getAllMeetingsRepoState() {
    return this.allMeetingsRepoState;
  }

  public StateLiveData<CourseModel> getCourseRepoState() {
    return this.courseRepoState;
  }

  public StateLiveData<MeetingsModel> getCurrentMeetingRepoState() {
    return this.currentMeetingRepoState;
  }

  /** Sort the meetings list by event time. */
  public void sortMeetingByEventTime(List<MeetingsModel> meetingsModelList) {
    Collections.sort(meetingsModelList, new Comparator<MeetingsModel>() {
      @Override
      public int compare(MeetingsModel meetingsModel, MeetingsModel t1) {
        return meetingsModel.getEventTime().compareTo(t1.getEventTime());
      }
    });
  }

  public void setCourseRepoState(CourseModel courseRepoState) {
    Log.d(TAG, courseRepoState.getKey());
    this.courseHistoryRepository.setCourseRepoState(courseRepoState);
  }

  /** Create a new Meeting. */
  public void createMeeting() {
    this.currentMeetingRepoState.postLoading();

    MeetingsModel meetingsModel = Validation.checkStateLiveData(this.meetingInputState, TAG);
    if (meetingsModel == null) {
      Log.e(TAG, Config.HISTORY_NO_MEETING_MODEL);
      this.currentMeetingRepoState.postError(
              new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (this.validateTitle(meetingsModel.getTitle())) {
      return;
    }
    //TODO: handle error if all values are 0
    //TODO: handle empty fields
    //TODO: handle end time for start time
    //TODO: Use only eventTime and eventEndTime; remove meetingDate and ...Input

    meetingsModel.setEventTime(this.parseEventTime(meetingsModel));
    meetingsModel.setEventEndTime(this.parseEventEndTime(meetingsModel));
    meetingsModel.setMeetingDate(this.parseMeetingDate(meetingsModel));
    meetingsModel.setCreationTime(new Date().getTime());

    this.meetingInputState.postCreate(new MeetingsModel());
    this.courseHistoryRepository.createMeeting(meetingsModel);
  }

  private boolean validateTitle(String title) {
    if (title == null) {
      Log.d(TAG, Config.HISTORY_NO_TITLE);
      this.currentMeetingRepoState.postError(
              new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.TITLE);
      return true;
    } else if (!Validation.stringHasPattern(title, Config.REGEX_PATTERN_TITLE)) {
      Log.d(TAG, Config.HISTORY_WRONG_TITLE_PATTERN);
      this.currentMeetingRepoState.postError(
              new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.TITLE);
      return true;
    }
    return false;
  }

  private String parseMeetingDate(MeetingsModel meetingsModel) {
    Long eventTime = meetingsModel.getEventTime();

    return Config.DATE_FORMAT.format(eventTime);
  }

  /** convert the input from date and time picker to unix timestamp. */
  private long parseEventTime(MeetingsModel meetingsModel) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, meetingsModel.getYearInput());
    calendar.set(Calendar.MONTH, meetingsModel.getMonthInput());
    calendar.set(Calendar.DAY_OF_MONTH, meetingsModel.getDayOfMonthInput());
    calendar.set(Calendar.HOUR_OF_DAY, meetingsModel.getHourInput());
    calendar.set(Calendar.MINUTE, meetingsModel.getMinuteInput());

    return calendar.getTimeInMillis();
  }

  private long parseEventEndTime(MeetingsModel meetingsModel) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, meetingsModel.getYearInput());
    calendar.set(Calendar.MONTH, meetingsModel.getMonthInput());
    calendar.set(Calendar.DAY_OF_MONTH, meetingsModel.getDayOfMonthInput());
    calendar.set(Calendar.HOUR_OF_DAY, meetingsModel.getHourEndInput());
    calendar.set(Calendar.MINUTE, meetingsModel.getMinuteEndInput());

    return calendar.getTimeInMillis();
  }

}
