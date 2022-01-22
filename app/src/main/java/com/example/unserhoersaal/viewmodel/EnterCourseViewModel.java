package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.DatabaseCourseCreation;
import com.example.unserhoersaal.model.DatabaseEnterCourse;


public class EnterCourseViewModel extends ViewModel {
    private DatabaseEnterCourse databaseEnterCourse;

    public EnterCourseViewModel() {
        databaseEnterCourse = new DatabaseEnterCourse();
    }

    public MutableLiveData<DatabaseEnterCourse.ThreeState> saveUserCourses(String courseId){
        return databaseEnterCourse.saveUserCourses(courseId);
    }

}