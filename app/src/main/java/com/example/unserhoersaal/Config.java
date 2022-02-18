package com.example.unserhoersaal;

import java.text.SimpleDateFormat;

/** Config Class. */
public class Config {

  /** ChatAdapter. */
  public static final SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("HH:mm");
  public static final SimpleDateFormat OLD_FORMAT = new SimpleDateFormat("dd. MMMM");

  /** Database Children. */
  public static final String CHILD_USER = "users";

  public static final String CHILD_COURSES = "courses";
  public static final String CHILD_COURSES_NAME = "title";

  public static final String CHILD_MESSAGES = "messages";

  /** UI. */
  public static final String COURSES_EMPTY = "Du bist noch keinen Kursen beigetreten";

}
