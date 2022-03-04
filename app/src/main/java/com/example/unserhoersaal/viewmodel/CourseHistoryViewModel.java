package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.repository.CourseHistoryRepository;
import java.util.List;

/** ViewModel for the CourseHistoryFragment. */
public class CourseHistoryViewModel extends ViewModel {

  private static final String TAG = "CourseHistoryViewModel";

  private CourseHistoryRepository courseHistoryRepository;

  private MutableLiveData<CourseModel> course = new MutableLiveData<>();
  private MutableLiveData<List<MeetingsModel>> meetings;
  private MutableLiveData<MeetingsModel> meetingsModelMutableLiveData;

  public MutableLiveData<MeetingsModel> dataBindingMeetingInput;
  public MutableLiveData<String> userId;


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

    this.dataBindingMeetingInput = new MutableLiveData<>(new MeetingsModel());
  }

  public void resetMeetingData() {
    this.dataBindingMeetingInput.setValue(new MeetingsModel());
    this.meetingsModelMutableLiveData.setValue(null);
  }

  public LiveData<List<MeetingsModel>> getMeetings() {
    return this.meetings;
  }

  public LiveData<CourseModel> getCourse() {
    return this.course;
  }

  public LiveData<MeetingsModel> getMeetingsModel() {
    return this.meetingsModelMutableLiveData;
  }

  public void setCourse(CourseModel course) {
    this.courseHistoryRepository.setCourse(course);
  }

  /** Create a new Meeting. */
  public void createMeeting() {
    //TODO: send error back to view
    if (this.dataBindingMeetingInput.getValue() == null) return;

    MeetingsModel meetingsModel = this.dataBindingMeetingInput.getValue();
    //TODO: send error back to view
    if (meetingsModel.getTitle() == null) return;
    if (meetingsModel.getCreationTimeInput() == null) return;
    if (meetingsModel.getEventTimeInput() == null) return;

    //TODO set to real eventtime
    meetingsModel.setEventTime(Long.parseLong(meetingsModel.getEventTimeInput()));
    this.courseHistoryRepository.createMeeting(meetingsModel);
  }

}
