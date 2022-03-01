package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.MeetingsModel;
import com.example.unserhoersaal.repository.CourseHistoryRepository;
import java.util.List;

/** ViewModel for the CourseHistoryFragment. */
public class CourseHistoryViewModel extends ViewModel {

  private static final String TAG = "CourseHistoryViewModel";

  private CourseHistoryRepository courseHistoryRepository;

  private MutableLiveData<String> courseId = new MutableLiveData<>();
  private MutableLiveData<List<MeetingsModel>> meetings;
  private MutableLiveData<MeetingsModel> meetingsModelMutableLiveData;
  //databinding
  public MutableLiveData<MeetingsModel> newMeeting;

  /** Initialise the ViewModel. */
  public void init() {
    if (this.meetings != null) {
      return;
    }

    this.courseHistoryRepository = CourseHistoryRepository.getInstance();
    this.courseId = this.courseHistoryRepository.getCourseId();
    this.meetingsModelMutableLiveData
            = this.courseHistoryRepository.getMeetingsModelMutableLiveData();

    if (this.courseId.getValue() != null) {
      this.meetings = this.courseHistoryRepository.getMeetings();
    }

    this.newMeeting = new MutableLiveData<>(new MeetingsModel());
  }

  public void resetMeetingData() {
    this.newMeeting.setValue(new MeetingsModel());
  }

  public LiveData<List<MeetingsModel>> getMeetings() {
    return this.meetings;
  }

  public LiveData<String> getCourseId() {
    return this.courseId;
  }

  public LiveData<MeetingsModel> getMeetingsModel() {
    return this.meetingsModelMutableLiveData;
  }

  public void setCourseId(String courseId) {
    this.courseHistoryRepository.setCourseId(courseId);
  }

  /** Create a new Meeting. */
  public void createMeeting() {
    //TODO: send error back to view
    if (this.newMeeting.getValue() == null) return;

    MeetingsModel meetingsModel = this.newMeeting.getValue();
    //TODO: send error back to view
    if (meetingsModel.getTitle() == null) return;
    if (meetingsModel.getCreationTimeInput() == null) return;
    if (meetingsModel.getEventTimeInput() == null) return;

    this.courseHistoryRepository.createMeeting(meetingsModel);
  }

}
