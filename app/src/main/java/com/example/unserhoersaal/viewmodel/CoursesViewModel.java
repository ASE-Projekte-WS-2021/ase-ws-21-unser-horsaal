package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.DatabaseCourseCreation;
import com.example.unserhoersaal.model.DatabaseCourses;
import com.example.unserhoersaal.model.DatabaseEnterCourse;
import com.example.unserhoersaal.model.UserCourse;

import java.util.ArrayList;
import java.util.HashMap;


public class CoursesViewModel extends ViewModel {
    private DatabaseCourses databaseCourses;
    private ArrayList emptyCourses = new ArrayList();

    public CoursesViewModel() {
        databaseCourses = new DatabaseCourses();
    }

    public MutableLiveData<ArrayList<UserCourse>> getUserCourses(){
        return databaseCourses.getUserCourses();
    }

    public ArrayList getEmptyCourses() {
        return emptyCourses;
    }

}