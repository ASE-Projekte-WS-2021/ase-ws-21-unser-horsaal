package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.DatabaseCurrentCourse;
import java.util.ArrayList;

/**Class CurrentCourseViewModel.**/

public class CurrentCourseViewModel extends ViewModel {
  private DatabaseCurrentCourse databaseCurrentCourse;
  String courseId;

  /**Method CurrentCourseView.**/

  public CurrentCourseViewModel(String courseId) {
    this.courseId = courseId;
    databaseCurrentCourse = new DatabaseCurrentCourse(courseId);

  }

  public void sendMessage(String messageText) {
    databaseCurrentCourse.sendMessage(messageText);
  }

  public MutableLiveData<ArrayList> getMessages() {
    return databaseCurrentCourse.getMessages();
  }

}