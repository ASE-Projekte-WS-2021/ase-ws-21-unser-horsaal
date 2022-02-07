package com.example.unserhoersaal.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unserhoersaal.repository.EnterCourseRepository;

/**Class EnterCourseViewModel.**/

public class EnterCourseViewModel extends ViewModel {

    private EnterCourseRepository enterCourseRepository;

    public EnterCourseViewModel() {
        enterCourseRepository = new EnterCourseRepository();
    }

    public MutableLiveData<EnterCourseRepository.ThreeState> saveUserCourses(String courseId){
        return enterCourseRepository.saveUserCourses(courseId);
    }

}