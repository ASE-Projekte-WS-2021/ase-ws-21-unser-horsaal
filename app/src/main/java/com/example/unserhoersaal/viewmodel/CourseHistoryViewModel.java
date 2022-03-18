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
import com.example.unserhoersaal.utils.ArrayListUtil;

import java.util.Calendar;
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
  public StateLiveData<String> userId;
  private ArrayListUtil arrayListUtil = new ArrayListUtil();

  private MutableLiveData<CourseModel> course = new MutableLiveData<>();
  private MutableLiveData<List<MeetingsModel>> meetings;
  private MutableLiveData<MeetingsModel> meetingsModelMutableLiveData;
  public MutableLiveData<MeetingsModel> dataBindingMeetingInput;
  public MutableLiveData<String> userId;

  /** Initialise the ViewModel. */
  public void init() {
    Log.d(TAG, "init: ");
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
  }

  public void resetMeetingData() {
    this.meetingModelInputState.postCreate(new MeetingsModel());
    this.meetingsModelMutableLiveData.postCreate(new MeetingsModel());
  }

  public StateLiveData<List<MeetingsModel>> getMeetings() {
    return this.meetings;
  }

  /** Sort the meetings list.
   *  First parameter is the meeting list to sort.
   *  The second parameter is a sort option.
   *  Sort options: newest, oldest
   */
  public void sortMeetings(List<MeetingsModel> meetingsModelList, String sortOption) {
   this.arrayListUtil.sortMeetingList(meetingsModelList, sortOption);
  }

  public StateLiveData<CourseModel> getCourse() {
    return this.course;
  }

  public StateLiveData<MeetingsModel> getMeetingsModel() {
    return this.meetingsModelMutableLiveData;
  }

  public LiveData<String> getUser() {return this.userId;}

  public void setCourse(CourseModel course) {
    Log.d(TAG, course.getKey());
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
    //TODO: handle error if all values are 0
    //TODO: handle empty fields
    //TODO: handle end time for start time
    //TODO: Use only eventTime and eventEndTime; remove meetingDate and ...Input

    meetingsModel.setEventTime(this.parseEventTime(meetingsModel));
    meetingsModel.setEventEndTime(this.parseEventEndTime(meetingsModel));
    meetingsModel.setMeetingDate(this.parseMeetingDate(meetingsModel));
    meetingsModel.setCreationTime(new Date().getTime());

    this.meetingModelInputState.postCreate(new MeetingsModel());
    this.courseHistoryRepository.createMeeting(meetingsModel);
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
