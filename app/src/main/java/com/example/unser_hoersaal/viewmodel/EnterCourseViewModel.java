package com.example.unser_hoersaal.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unser_hoersaal.model.DBCourseCreation;
import com.example.unser_hoersaal.model.DBCurrentCourse;

import java.util.ArrayList;



public class EnterCourseViewModel extends ViewModel {
    private DBCourseCreation databaseCourseCreation;
    String courseId;

    public EnterCourseViewModel() {
    }


    public void sendCourseId(String courseId){

    }

}