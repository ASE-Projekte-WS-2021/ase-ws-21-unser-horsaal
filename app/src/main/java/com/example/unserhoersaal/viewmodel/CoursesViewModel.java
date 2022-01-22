package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.DatabaseCourseCreation;
import com.example.unserhoersaal.model.DatabaseCourses;
import com.example.unserhoersaal.model.DatabaseEnterCourse;

import java.util.ArrayList;
import java.util.HashMap;


public class CoursesViewModel extends ViewModel {
    private DatabaseCourses databaseCourses;
    private HashMap<String, String> emptyCourses = new HashMap<String, String>();

    public CoursesViewModel() {
        databaseCourses = new DatabaseCourses();
    }

    public MutableLiveData<HashMap> getUserCourses(){
        return databaseCourses.getUserCourses();
    }

    public HashMap<String, String> getEmptyCourses() {
        return emptyCourses;
    }

}