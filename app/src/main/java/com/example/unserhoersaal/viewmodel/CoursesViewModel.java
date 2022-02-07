package com.example.unserhoersaal.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.model.CourseModel;
import com.example.unserhoersaal.model.UserCourse;
import com.example.unserhoersaal.repository.CoursesRepository;

import java.util.List;


public class CoursesViewModel extends ViewModel {
    private CoursesRepository coursesRepository;
    private MutableLiveData<List<UserCourse>> mUserCourses;

    public void init(){
        if(mUserCourses != null){
            return;
        }
        coursesRepository = CoursesRepository.getInstance();
        mUserCourses = coursesRepository.getUserCourses();
    }

    public LiveData<List<UserCourse>> getUserCourses(){
        return mUserCourses;
    }

}