package com.example.unserhoersaal.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.model.UserModel;
import com.example.unserhoersaal.repository.CourseHistoryRepository;
import com.example.unserhoersaal.utils.StateData;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;

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
  public StateLiveData<MeetingsModel> meetingModelInputState = new StateLiveData<>();;
  public StateLiveData<String> userId;


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

    //TODO: assert != null
    if (this.course.getValue().getData() != null) {
      this.meetings = this.courseHistoryRepository.getMeetings();
    }
    this.meetingModelInputState.postValue(new StateData<>(new MeetingsModel()));
  }

  public void resetMeetingData() {
    this.meetingModelInputState.postValue(new StateData<>(new MeetingsModel()));
    this.meetingsModelMutableLiveData.postValue(new StateData<>(null));
  }

  public StateLiveData<List<MeetingsModel>> getMeetings() {
    return this.meetings;
  }

  public StateLiveData<CourseModel> getCourse() {
    return this.course;
  }

  public StateLiveData<MeetingsModel> getMeetingsModel() {
    return this.meetingsModelMutableLiveData;
  }

  public void setCourse(CourseModel course) {
    this.courseHistoryRepository.setCourse(course);
  }

  /** Create a new Meeting. */
  public void createMeeting() {
    MeetingsModel meetingsModel = Validation.checkStateLiveData(this.meetingModelInputState, TAG);
    if (meetingsModel == null) {
      Log.d(TAG, "CourseHistoryVM>createMeeting meetingsModel is null.");
      return;
    }

    if (meetingsModel.getTitle() == null) {
      this.meetingModelInputState.postError(new Error(Config.VM_TITLE_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.titleHasPattern(meetingsModel.getTitle())) {
      this.meetingModelInputState.postError(new Error(Config.VM_TITLE_WRONG_PATTERN), ErrorTag.VM);
      return;
    }

    meetingsModel.setEventTime(this.parseEventTime(meetingsModel));
    meetingsModel.setCreationTime(new Date().getTime());
    this.meetingModelInputState.postComplete();
    this.courseHistoryRepository.createMeeting(meetingsModel);
  }

  private long parseEventTime(MeetingsModel meetingsModel) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, meetingsModel.getYearInput());
    calendar.set(Calendar.MONTH, meetingsModel.getMonthInput());
    calendar.set(Calendar.DAY_OF_MONTH, meetingsModel.getDayOfMonthInput());
    calendar.set(Calendar.HOUR_OF_DAY, meetingsModel.getHourInput());
    calendar.set(Calendar.MINUTE, meetingsModel.getMinuteInput());

    return calendar.getTimeInMillis();
  }

}
