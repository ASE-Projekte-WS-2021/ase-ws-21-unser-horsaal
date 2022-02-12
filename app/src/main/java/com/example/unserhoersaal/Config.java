package com.example.unserhoersaal;

import java.text.SimpleDateFormat;

/** Config Class. */
public class Config {

  /** ChatAdapter. */
  public static final SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("HH:mm");
  public static final SimpleDateFormat OLD_FORMAT = new SimpleDateFormat("dd. MMMM");

  /** Database Children. */
  public static final String CHILD_USER = "User";

  public static final String CHILD_COURSES = "Courses";
  public static final String CHILD_COURSES_NAME = "courseName";

  public static final String CHILD_MESSAGES = "Messages";

  /** UI. */
  public static final String COURSES_TITLE = "Meine Kurse";
}
