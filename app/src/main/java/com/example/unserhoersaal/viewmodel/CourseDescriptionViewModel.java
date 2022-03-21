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
  private StateLiveData<String> currentCourseIdRepoState;
  public StateLiveData<CourseModel> courseRepoState;
  //private Bitmap qrCodeBitmap;

  /** Initialize the ViewModel. */
  public void init() {
    if (this.courseRepoState != null) {
      return;
    }

    this.courseDescriptionRepository = CourseDescriptionRepository.getInstance();
    this.currentCourseIdRepoState = this.courseDescriptionRepository.getCurrentCourseIdRepoState();
    this.courseRepoState = this.courseDescriptionRepository.getCourseRepoState();
  }

  public StateLiveData<String> getCurrentCourseIdRepoState() {
    return this.currentCourseIdRepoState;
  }

  public StateLiveData<CourseModel> getCourseRepoState() {
    return this.courseRepoState;
  }

  public void setCurrentCourseIdRepoState(String currentCourseIdRepoState) {
    this.courseDescriptionRepository.setCourseId(currentCourseIdRepoState);
  }

  /** JavaDoc. */
  public void unregisterFromCourse() {
    String courseKey = Validation.checkStateLiveData(this.currentCourseIdRepoState, TAG);

    if (courseKey == null) {
      Log.d(TAG, Config.DESCRIPTION_NO_TITLE);
      this.currentCourseIdRepoState.postError(new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.VM);
      return;
    } else if (!Validation.stringHasPattern(courseKey, Config.REGEX_PATTERN_CODE_MAPPING)) {
      Log.d(TAG, Config.DESCRIPTION_WRONG_TITLE_PATTERN);
      this.currentCourseIdRepoState.postError(new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.VM);
      return;
    }

    this.currentCourseIdRepoState.postUpdate(null);
    this.courseDescriptionRepository.unregisterFromCourse(courseKey);
  }
/*
  public void setQrCodeBitmap(Bitmap qrCodeBitmap) {
    this.qrCodeBitmap = qrCodeBitmap;
  }

  public Bitmap getQrCodeBitmap() {
    return this.qrCodeBitmap;
  }
 */
}
