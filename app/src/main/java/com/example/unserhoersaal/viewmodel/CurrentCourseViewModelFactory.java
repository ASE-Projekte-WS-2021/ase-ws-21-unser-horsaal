package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**Class CurrentCourseVMFactory.**/

public class CurrentCourseViewModelFactory implements ViewModelProvider.Factory {
  private String courseId;

  /**Class constructor.**/

  public CurrentCourseViewModelFactory(String courseId) {
    this.courseId = courseId;
  }


  @Override
  public <T extends ViewModel> T create(Class<T> modelClass) {
    return (T) new CurrentCourseViewModel(courseId);
  }
}
