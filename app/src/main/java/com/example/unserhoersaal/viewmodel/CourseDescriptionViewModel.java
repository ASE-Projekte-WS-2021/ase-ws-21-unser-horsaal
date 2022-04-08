package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CourseDescriptionRepository;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;

/** ViewModel for CoursesDescriptionDetailFragment and CoursesDescriptionFragment.
 * Provides statuslivedata courses for tab "Alle".*/
public class CourseDescriptionViewModel extends ViewModel {

  private static final String TAG = "courseDescriptionViewModel";

  private CourseDescriptionRepository courseDescriptionRepository;
  private StateLiveData<String> courseId = new StateLiveData<>();
  public StateLiveData<CourseModel> courseModelInputState;

  /** Initialize the ViewModel. Checks if statuslivedata is already connected with this VM.*/
  public void init() {
    if (this.courseModelInputState != null) {
      return;
    }

    this.courseDescriptionRepository = CourseDescriptionRepository.getInstance();
    this.courseId = this.courseDescriptionRepository.getCourseId();
    this.courseModelInputState = this.courseDescriptionRepository.getCourse();
  }

  public StateLiveData<String> getCourseId() {
    return this.courseId;
  }

  public StateLiveData<CourseModel> getCourseModel() {
    return this.courseModelInputState;
  }

  /** Set the courseId for the selected course. Used to load a selected course.
   *
   * @param courseId String
   * */
  public void setCourseId(String courseId) {
    this.courseDescriptionRepository.setCourseId(courseId);
  }

  /** Set the creatorId for the selected course. Used to offer different functionality for
   * the course creator than normal users.
   *
   * @param creatorId String
   * */
  public void setCreatorId(String creatorId) {
    this.courseDescriptionRepository.setCreatorId(creatorId);
  }

  /** Passes courseId to Repository to unregister user from course. */
  public void unregisterFromCourse() {
    String courseKey = Validation.checkStateLiveData(this.courseId, TAG);
    if (courseKey == null) {
      return;
    }

    this.courseId.postUpdate(null);
    this.courseDescriptionRepository.unregisterFromCourse(courseKey);
  }

  public Boolean isCreator() {
    return courseDescriptionRepository.getUid().equals(courseDescriptionRepository.getCreatorId());
  }

}
