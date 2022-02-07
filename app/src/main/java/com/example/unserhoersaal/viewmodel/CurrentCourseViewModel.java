package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.repository.CurrentCourseRepository;

import java.util.ArrayList;


public class CurrentCourseViewModel extends ViewModel {
  private CurrentCourseRepository currentCourseRepository;


  public CurrentCourseViewModel() {
    currentCourseRepository = new CurrentCourseRepository();

  }

  public void sendMessage(String messageText){
    currentCourseRepository.sendMessage(messageText);
  }

  public MutableLiveData<ArrayList> getMessages(){
    return currentCourseRepository.getMessages();
  }

  public void setupCurrentCourseViewModel(String courseId)
  {
    currentCourseRepository.setCourseId(courseId);
    currentCourseRepository.setupListeners();
  }

}