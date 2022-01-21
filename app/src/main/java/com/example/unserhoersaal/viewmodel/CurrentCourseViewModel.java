package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unserhoersaal.model.DatabaseCurrentCourse;

import java.util.ArrayList;


public class CurrentCourseViewModel extends ViewModel {
    private DatabaseCurrentCourse databaseCurrentCourse;


    public CurrentCourseViewModel() {
        databaseCurrentCourse = new DatabaseCurrentCourse();

    }

    public void sendMessage(String messageText){
        databaseCurrentCourse.sendMessage(messageText);
    }

    public MutableLiveData<ArrayList> getMessages(){
        return databaseCurrentCourse.getMessages();
    }

    public void setupCurrentCourseViewModel(String courseId)
    {
        databaseCurrentCourse.setCourseId(courseId);
        databaseCurrentCourse.setupListeners();
    }

}