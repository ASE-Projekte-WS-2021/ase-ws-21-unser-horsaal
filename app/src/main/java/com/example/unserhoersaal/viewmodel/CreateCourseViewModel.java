package com.example.unserhoersaal.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ErrorTag;
import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.repository.CreateCourseRepository;
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

  /** Create a new course. */
  public void createCourse() {
    this.courseModel.postLoading();

    CourseModel courseModel = Validation.checkStateLiveData(this.courseModelInputState, TAG);

    if (courseModel == null) {
      Log.e(TAG, "courseModel is null.");
      this.courseModel.postError(new Error(Config.UNSPECIFIC_ERROR), ErrorTag.VM);
      return;
    }

    if (Validation.emptyString(courseModel.getTitle())) {
      Log.d(TAG, "title is null.");
      this.courseModel.postError(new Error(Config.DATABINDING_TITLE_NULL), ErrorTag.TITLE);
      return;
    } else if (!Validation.stringHasPattern(courseModel.getTitle(), Config.REGEX_PATTERN_TITLE)) {
      Log.d(TAG, "title has wrong pattern.");
      this.courseModel.postError(new Error(Config.DATABINDING_TITLE_WRONG_PATTERN), ErrorTag.TITLE);
      return;
    }

    if (Validation.emptyString(courseModel.getDescription())) {
      Log.d(TAG, "description is null.");
      this.courseModel.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.DESCRIPTION);
      return;
    } else if (!Validation.stringHasPattern(
            courseModel.getDescription(), Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, "description has wrong pattern.");
      this.courseModel.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.DESCRIPTION);
      return;
    }

    if (Validation.emptyString(courseModel.getInstitution())) {
      Log.d(TAG, "institution is null.");
      this.courseModel.postError(new Error(Config.DATABINDING_TEXT_NULL), ErrorTag.INSTITUTION);
      return;
    } else if (!Validation.stringHasPattern(
            courseModel.getInstitution(), Config.REGEX_PATTERN_TEXT)) {
      Log.d(TAG, "institution has wrong pattern.");
      this.courseModel.postError(
              new Error(Config.DATABINDING_TEXT_WRONG_PATTERN), ErrorTag.INSTITUTION);
      return;
    }

    if (isEditing) {
      this.createCourseRepository.editCourse(courseModel);
    } else {
      courseModel.setCodeMapping(this.getCodeMapping());

      this.courseModelInputState.postCreate(new CourseModel());
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

  public Boolean getIsEditing() { return isEditing; }


}
