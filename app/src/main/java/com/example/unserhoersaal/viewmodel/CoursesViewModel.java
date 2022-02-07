package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.repository.CoursesRepository;
import com.example.unserhoersaal.model.UserCourse;

import java.util.ArrayList;


public class CoursesViewModel extends ViewModel {
    private CoursesRepository coursesRepository;
    private ArrayList emptyCourses = new ArrayList();

    public CoursesViewModel() {
        coursesRepository = new CoursesRepository();
    }

    public MutableLiveData<ArrayList<UserCourse>> getUserCourses(){
        return coursesRepository.getUserCourses();
    }

    public ArrayList getEmptyCourses() {
        return emptyCourses;
    }

}