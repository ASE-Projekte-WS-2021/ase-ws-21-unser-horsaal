package com.example.unser_hoersaal.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.unser_hoersaal.model.DBCurrentCourse;

import java.util.ArrayList;
import java.util.List;


public class CurrentCourseViewModel extends ViewModel {
    private DBCurrentCourse databaseCurrentCourse;
    String courseId;

    public CurrentCourseViewModel(String courseId) {
        this.courseId = courseId;
        databaseCurrentCourse = new DBCurrentCourse(courseId);

    }

    public void sendMessage(String messageText){
        databaseCurrentCourse.sendMessage(messageText);
    }

    public MutableLiveData<ArrayList> getMessages(){
        return databaseCurrentCourse.getMessages();
    }

}