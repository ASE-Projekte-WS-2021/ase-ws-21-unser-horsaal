package com.example.unser_hoersaal.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unser_hoersaal.model.DBCourseCreation;


public class CreateCourseViewModel extends ViewModel {
    private DBCourseCreation databaseCourseCreation;
    private final MutableLiveData<String> courseId = new MutableLiveData<String>();
    public String currentCourseID;

    public CreateCourseViewModel() {
        databaseCourseCreation = new DBCourseCreation();
    }

    public void createCourse(String courseName, String courseDescription, String courseCreatedById, String courseCreatedBy, String courseCreatedAt) {
        databaseCourseCreation.createNewCourse(courseName, courseDescription, courseCreatedById, courseCreatedBy, courseCreatedAt);
    }

    public String getCourseId(){
        return databaseCourseCreation.getCourseId();
    }
}
