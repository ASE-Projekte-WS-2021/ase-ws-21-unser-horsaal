package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CreateCourseRepository;
import com.example.unserhoersaal.utils.PreventDoubleClick;
import com.example.unserhoersaal.utils.StateLiveData;
import com.example.unserhoersaal.utils.Validation;
import java.util.Random;

/**Class transfers data from CreateCourseRepository to CreateCourseFragment and viceversa.**/
public class CreateCourseViewModel extends ViewModel {

  private static final String TAG = "CreateCourseViewModel";

  private CreateCourseRepository createCourseRepository;
  private StateLiveData<CourseModel> courseModel;
  public StateLiveData<CourseModel> courseModelInputState = new StateLiveData<>();
  private boolean isEditing = false;

  /** Initialization of the CreateCourseViewModel. */
  public void init() {
    if (this.courseModel != null) {
      return;
    }
    this.createCourseRepository = CreateCourseRepository.getInstance();
    this.courseModel = this.createCourseRepository.getUserCourse();
    this.courseModelInputState.postCreate(new CourseModel());
  }

  /** create a copy from coursemodel to cut off references to live data.
   *
   * @param courseModel makes a copy of the coursemodel
   *                    to make it editable and not affect livedata. */
  public void makeEditable(CourseModel courseModel) {
    this.isEditing = true;

    CourseModel c = new CourseModel();
    c.setTitle(courseModel.getTitle());
    c.setDescription(courseModel.getDescription());
    c.setInstitution(courseModel.getInstitution());
    c.setKey(courseModel.getKey());
    c.setCreatorId(courseModel.getCreatorId());
    c.setCodeMapping(courseModel.getCodeMapping());
    c.setMemberCount(courseModel.getMemberCount());
    c.setCreationTime(courseModel.getCreationTime());

    this.courseModelInputState.postCreate(c);
  }

  public StateLiveData<CourseModel> getCourseModelInputState() {
    return courseModelInputState;
  }

  public StateLiveData<CourseModel> getCourseModel() {
    return this.courseModel;
  }

  public void resetCourseModelInput() {
    this.courseModelInputState.postCreate(new CourseModel());
    this.courseModel.postCreate(null);
  }

  public void setLiveDataComplete() {
    this.courseModelInputState.postComplete();
    this.courseModel.postComplete();
  }

  /** Create a new course. */
  public void createCourse() {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    this.courseModelInputState.postLoading();

    CourseModel courseModel = Validation.checkStateLiveData(this.courseModelInputState, TAG);

    if (courseModel == null) {
      this.courseModel.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    this.checkInput(courseModel);
  }

  private void checkInput(CourseModel courseModel) {
    if (Validation.emptyString(courseModel.getTitle())) {
      this.courseModelInputState.postError(
              new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.TITLE);
    } else if (!Validation.stringHasPattern(courseModel.getTitle(), Config.REGEX_PATTERN_TITLE)) {
      this.courseModelInputState.postError(
              new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.TITLE);
    } else if (Validation.emptyString(courseModel.getDescription())) {
      this.courseModelInputState.postError(
              new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.DESCRIPTION);
    } else if (!Validation.stringHasPattern(
            courseModel.getDescription(), Config.REGEX_PATTERN_DESCRIPTION)) {
      this.courseModelInputState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.DESCRIPTION);
    } else if (Validation.emptyString(courseModel.getInstitution())) {
      this.courseModelInputState.postError(
              new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.INSTITUTION);
    } else if (!Validation.stringHasPattern(
            courseModel.getInstitution(), Config.REGEX_PATTERN_TEXT)) {
      this.courseModelInputState.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.INSTITUTION);
    } else {
      this.passCourseModelToRepo(courseModel);
    }
  }

  private void passCourseModelToRepo(CourseModel courseModel) {
    this.courseModelInputState.postComplete();
    if (isEditing) {
      this.createCourseRepository.editCourse(courseModel);
    } else {
      courseModel.setCodeMapping(this.getCodeMapping());
      this.createCourseRepository.createNewCourse(courseModel);
    }
  }

  //https://www.codegrepper.com/code-examples/java/how+to+generate+random+letters+in+java
  private String getCodeMapping() {
    String chars = Config.CODE_MAPPING_ALLOWED_CHARACTERS;
    Random random = new Random();
    StringBuilder sb = new StringBuilder(Config.CODE_MAPPING_LENGTH);
    for (int i = 0; i < Config.CODE_MAPPING_LENGTH; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }

  public void setIsEditing(Boolean isEditing) {
    this.isEditing = isEditing;
  }

  public Boolean getIsEditing() {
    return isEditing;
  }

}
