package com.example.unser_hoersaal.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.unser_hoersaal.model.DBCourseCreation;

import java.util.List;
import java.util.UUID;


public class CreateCourseViewModel extends AndroidViewModel {
    private DBCourseCreation databaseCourseCreation;
    public String currentCourseID;

    public CreateCourseViewModel(@NonNull Application application) {
        super(application);

        databaseCourseCreation = new DBCourseCreation(application);
    }

    public String createCourse(String courseName, String courseDescription, String courseCreatedById, String courseCreatedBy, String courseCreatedAt) {
        currentCourseID = databaseCourseCreation.createNewCourse(courseName, courseDescription, courseCreatedById, courseCreatedBy, courseCreatedAt);
        return currentCourseID;
    }
}
