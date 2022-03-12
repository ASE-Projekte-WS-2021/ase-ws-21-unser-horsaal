package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CourseDescriptionRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;

/** ViewModel for the CourseDescriptionFragment. */
public class CourseDescriptionViewModel extends ViewModel {

  private static final String TAG = "courseDescriptionViewModel";
  private CourseDescriptionRepository courseDescriptionRepository;
  private StateLiveData<String> courseId = new StateLiveData<>();
  public StateLiveData<CourseModel> courseModelInputState;

  /** Initialize the ViewModel. */
  public void init() {
    if (this.courseModelInputState != null) {
      return;
    }

    this.courseDescriptionRepository = CourseDescriptionRepository.getInstance();
    this.courseId = this.courseDescriptionRepository.getCourseId();
    this.courseModelInputState = this.courseDescriptionRepository.getCourseModel();
  }

  public StateLiveData<String> getCourseId() {
    return this.courseId;
  }

  public StateLiveData<CourseModel> getCourseModel() {
    return this.courseModelInputState;
  }

  public void setCourseId(String courseId) {
    this.courseDescriptionRepository.setCourseId(courseId);
  }

  public void unregisterFromCourse() {
    String courseKey = Validation.checkStateLiveData(this.courseId, TAG);

    if (courseKey == null) {
      Log.d(TAG, "title is null.");
      this.courseId.postError(new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.stringHasPattern(courseKey, Config.REGEX_PATTERN_CODE_MAPPING)) {
      Log.d(TAG, "title has wrong pattern.");
      this.courseId.postError(new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.VM);
      return;
    }

    this.courseId.postComplete();
    this.courseDescriptionRepository.unregisterFromCourse(courseKey);
  }

}
