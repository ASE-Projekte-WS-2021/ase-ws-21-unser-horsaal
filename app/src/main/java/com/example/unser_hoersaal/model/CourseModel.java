package com.example.unser_hoersaal.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;

@IgnoreExtraProperties
public class CourseModel {

    public String courseName;
    public String courseId;
    public String courseDescription;
    public String courseCreatedById;
    public String courseCreatedBy;
    public String courseCreatedAt;
    public Array courseThreads;

    public CourseModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CourseModel(String courseName, String courseId, String courseDescription, String courseCreatedById, String courseCreatedBy, String courseCreatedAt) {
        this.courseName = courseName;
        this.courseId = courseId;
        this.courseDescription = courseDescription;
        this.courseCreatedById = courseCreatedById;
        this.courseCreatedBy = courseCreatedBy;
        this.courseCreatedAt = courseCreatedAt;
    }

}