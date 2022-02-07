package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.Message;
import com.example.unserhoersaal.repository.CurrentCourseRepository;

import java.util.ArrayList;
import java.util.List;


public class CurrentCourseViewModel extends ViewModel {
  private CurrentCourseRepository currentCourseRepository;
  private MutableLiveData<List<Message>> mMessages;
  private String courseID;

  public void init(){
    if (mMessages != null){
      return;
    }
    currentCourseRepository = CurrentCourseRepository.getInstance();
    currentCourseRepository.setCourseId(this.courseID);
    mMessages = currentCourseRepository.getMessages();
  }

  public void sendMessage(String messageText){
    currentCourseRepository.sendMessage(messageText);
  }

  public LiveData<List<Message>> getMessages(){
    return mMessages;
  }

  public void setupCurrentCourseViewModel(String mCourseId)
  {
    this.courseID = mCourseId;
  }

}