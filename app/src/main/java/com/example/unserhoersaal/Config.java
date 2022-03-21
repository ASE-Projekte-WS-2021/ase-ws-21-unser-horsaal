package com.example.unserhoersaal;

import java.text.SimpleDateFormat;

/** Config Class. */
public class Config {
  /**=======================.
   * User Input Lengths
   * ======================= */
  public static final int PASSWORD_LENGTH_MIN = 8;
  public static final int PASSWORD_LENGTH_MAX = 64;
  public static final int USERNAME_LENGTH_MIN = 3;
  public static final int USERNAME_LENGTH_MAX = 15;
  public static final int INSTITUTION_LENGTH_MIN = 0; //optional
  public static final int INSTITUTION_LENGTH_MAX = 75;

  public static final int TITLE_LENGTH_MIN = 3;
  public static final int TITLE_LENGTH_MAX = 100;
  public static final int TEXT_LENGTH_MIN = 1;
  public static final int TEXT_LENGTH_MAX = 500;
  public static final int DESCRIPTION_LENGTH_MIN = 0; //optional
  public static final int DESCRIPTION_LENGTH_MAX = 500;

  public static final int CODE_MAPPING_LENGTH = 9;

  /**=======================.
   * User Allowed Input Characters
   * ======================= */
  public static final String PASSWORD_ALLOWED_CHARACTERS = "";
  public static final String USERNAME_ALLOWED_CHARACTERS = "";
  public static final String TEXT_ALLOWED_CHARACTERS = "";
  public static final String CODE_MAPPING_ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  /**=======================.
   *  Regex Patterns
   * ======================= */
  // reference: https://ihateregex.io/expr/username/
  public static final String REGEX_PATTERN_USERNAME =
          String.format("^[a-zA-Z0-9_-]{%s,%s}$", USERNAME_LENGTH_MIN, USERNAME_LENGTH_MAX);
  // reference: https://ihateregex.io/expr/password/
  public static final String REGEX_PATTERN_PASSWORD =
          String.format("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{%s,%s}$",
                  PASSWORD_LENGTH_MIN, PASSWORD_LENGTH_MAX);
  public static final String REGEX_PATTERN_TITLE =
          String.format("^.{%s,%s}$", TITLE_LENGTH_MIN, TITLE_LENGTH_MAX);
  public static final String REGEX_PATTERN_INSTITUTION =
          String.format("^.{%s,%s}$", INSTITUTION_LENGTH_MIN, INSTITUTION_LENGTH_MAX);
  public static final String REGEX_PATTERN_TEXT =
          String.format("^.{%s,%s}$", TEXT_LENGTH_MIN, TEXT_LENGTH_MAX);
  public static final String REGEX_PATTERN_CODE_MAPPING =
          "^([A-Z]{3}|[a-z]{3})[\\s-]?([A-Z]{3}|[a-z]{3})[\\s-]?([A-Z]{3}|[a-z]{3})$";

  /**=======================.
   * Date / Time Formats
   * ======================= */
  public static final SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("HH:mm");
  public static final SimpleDateFormat OLD_FORMAT = new SimpleDateFormat("dd. MMMM");
  public static final SimpleDateFormat DATE_TIME_FORMAT
          = new SimpleDateFormat("dd. MMMM, HH:mm");
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd. MM. yyyy");

  /**=======================.
   * Database Children
   * ======================= */
  public static final String CHILD_USER = "users";
  public static final String CHILD_COURSES = "courses";
  public static final String CHILD_MESSAGES = "messages";
  public static final String CHILD_CODE_MAPPING = "codeMapping";
  public static final String CHILD_MEETINGS = "meetings";
  public static final String CHILD_THREADS = "threads";
  public static final String CHILD_USER_COURSES = "usersCourses";
  public static final String CHILD_COURSES_USER = "coursesUsers";
  public static final String CHILD_USER_NAME = "displayName";
  public static final String CHILD_LIKE = "likes";
  public static final String CHILD_TOP_ANSWER = "topAnswer";
  public static final String CHILD_ANSWER_COUNT = "answersCount";
  public static final String CHILD_ANSWERED = "answered";
  public static final String CHILD_USER_LIKE = "userLike";
  public static final String CHILD_LIKE_USER = "likeUser";
  public static final String CHILD_DISPLAY_NAME = "displayName";
  public static final String CHILD_INSTITUTION = "institution";
  public static final String CHILD_MEETINGS_COUNT = "meetingsCount";
  public static final String CHILD_MEMBER_COUNT = "memberCount";

  /** Tabview. */
  public static final int TAB_TODAY = 0;
  public static final String TAB_TODAY_NAME = "Heute";
  public static final int TAB_ALL = 1;
  public static final String TAB_ALL_NAME = "Alle";
  public static final int TAB_OWNED = 2;
  public static final String TAB_OWNED_NAME = "Erstellt";
  public static final int NUMBER_OF_TABS = 3;

  /**=======================.
   * Internal Error Messages
   * ======================= */

  public static final String FIREBASE_USER_NULL = "Firebase User is null";
  public static final String STATE_LIVE_DATA_NULL = "Databinding Error";
  public static final String AUTH_LOGOUT_SUCCESS = "";
  public static final String AUTH_LOGOUT_FAIL = "";
  public static final String LISTENER_FAILED_TO_RESOLVE = "Listener failed to reslove";
  public static final String UNKNOWN_USER = "<gelöschter Nutzer";

  /**=======================.
   * Error Messages For User
   * ======================= */
  public static final String UNSPECIFIC_ERROR =
          "Ein Fehler ist aufgetreten!"; //hide too specific error from user

  public static final String DATABINDING_TITLE_NULL = "Der Titel darf nicht leer sein!";
  public static final String DATABINDING_TITLE_WRONG_PATTERN =
          "Der Titel enthält ungültige Charaktere!"; //TODO: add characters
  public static final String DATABINDING_TEXT_NULL = "Dieses Textfeld darf nicht leer sein!";
  public static final String DATABINDING_TEXT_WRONG_PATTERN =
          "Der Titel enthält ungültige Charaktere!";
  public static final String DATABINDING_CODEMAPPING_NULL =
          "Der Beitrittscode darf nicht leer sein!";
  public static final String DATABINDING_CODEMAPPING_WRONG_PATTERN =
          "Der Beitrittscode enthält ungültige Charaktere.";

  public static final String AUTH_EMAIL_EMPTY = "Bitte gib eine Email-Adresse ein!";
  public static final String AUTH_EMAIL_WRONG_PATTERN_REGISTRATION =
          "Email ist ungültig oder bereits vergeben!";
  public static final String AUTH_EMAIL_WRONG_PATTERN_LOGIN = "Ungültige Email-Adresse!";
  public static final String AUTH_PASSWORD_EMPTY = "Bitte gib ein Password ein!";
  public static final String AUTH_PASSWORD_WRONG_PATTERN =
          "Das Passwort muss aus Groß- und Kleinbuchstaben, sowie mindestens einer Zahl bestehen! "
                  + "Zudem muss es mindestens 8 Zeichen lang sein!";
  public static final String AUTH_USERNAME_EMPTY = "Bitte gib einen Nutzernamen ein!";
  public static final String AUTH_USERNAME_WRONG_PATTERN =
          "Nutzername ist ungültig oder bereits vergeben!";
  public static final String AUTH_INSTITUTION_EMPTY = "Bitte gib eine Institution ein!";
  public static final String AUTH_INSTITUTION_WRONG_PATTERN = "Institution ist ungültig!";
  public static final String AUTH_REGISTRATION_FAILED =
          "Die Registrierung konnte nicht abgeschlossen werden!";
  public static final String AUTH_LOGIN_FAILED = "Falsche Email-Adresse oder falsches Passwort!";
  public static final String AUTH_EDIT_PASSWORD_CHANGE_FAILED =
          "Das Passwort konnte nicht geändert werden!";
  public static final String AUTH_EDIT_PASSWORD_CHANGE_SUCCESS =
          "Das Passwort wurde erfolgreich geändert!";
  public static final String AUTH_EDIT_USERNAME_CHANGE_FAILED =
          "Der Nutzername konnte nicht geändert werden!";
  public static final String AUTH_EDIT_USERNAME_CHANGE_SUCCESS =
          "Der Nutzername wurde erfolgreich geändert!";
  public static final String AUTH_EDIT_INSTITUTION_CHANGE_FAILED =
          "Die Institution konnte nicht geändert werden!";
  public static final String AUTH_EDIT_INSTITUTION_CHANGE_SUCCESS =
          "Die Institution wurde erfolgreich geändert!";
  public static final String AUTH_VERIFICATION_EMAIL_SENT =
          "Verifizierungsemail versandt";
  public static final String AUTH_VERIFICATION_EMAIL_NOT_SENT =
          "Die Verfizierungsemail konnte nicht versandt werden!";
  public static final String AUTH_PASSWORD_RESET_MAIL_SENT =
          "Email zur Passwortzurücksetzung versandt.";
  public static final String AUTH_PASSWORD_RESET_MAIL_NOT_SENT =
          "Die Email zur Passwortzurücksetzung konnte nicht versandt werden!";

  public static final String COURSES_COURSE_CREATION_FAILURE =
          "Der Kurs konnte nicht erstellt werden!";
  public static final String COURSE_HISTORY_MEETING_CREATION_FAILURE =
          "Das Meeting konnte nicht erstellt werden!";
  public static final String COURSE_MEETING_THREAD_CREATION_FAILURE =
          "Der Thread konnte nicht erstellt werden!";
  public static final String CURRENT_COURSE_CREATION_MESSAGE =
          "Die Chatnachricht konnte nicht versandt werden!";

  public static final String COURSE_DESCRIPTION_UNREGISTER_COURSE_FAILED =
          "Das Abmelden vom Kurs war nicht erfolgreich!";
  public static final String COURSE_DESCRIPTION_UNREGISTER_COURSE_SUCCESS =
          "Erfolgreich vom Kurs abgemeldet!";
  public static final String COURSE_DESCRIPTION_SETCOURSEID_FAILED =
          "Der ausgewählte Kurs konnte nicht gefunden werden!";

  public static final String COURSE_DESCRIPTION_COULD_NOT_LOAD_USER =
          "Nutzer konnte nicht geladen werden!";
  public static final String PROFILE_FAILED_TO_LOAD_USER =
          "Das Nutzerprofil konnte nicht geladen werden!";
  public static final String COURSES_FAILED_TO_LOAD = "Kurse konnten nicht geladen werden!";
  public static final String THREADS_FAILED_TO_LOAD = "Threads konnten nicht geladen werden!";
  public static final String MEETINGS_FAILED_TO_LOAD = "Meeting konnten nicht geladen werden!";
  public static final String MESSAGES_FAILED_TO_LOAD = "Nachrichten konnten nicht geladen werden!";

  /**=======================.
   * Internal Error Messages
   * ======================= */
  public static final String AUTH_LOGIN_FIREBASE_USER_NULL = "Firebase did not authenticate a user";
  public static final String AUTH_LOGIN_SUCCESSFUL = "Login with Firebase Auth was successful";
  public static final String AUTH_REGISTRATION_SUCCESSFUL = "Successfully registered with Firebase Auth.";
  public static final String AUTH_REGISTRATION_USER_CREATED = "A new user was created in the database.";
  public static final String AUTH_REGISTRATION_NO_USER_CREATED = "Could not create a new user!";
  public static final String AUTH_LISTENER_STATE_CHANGE = "Firebase Auth State changed!";
  public static final String AUTH_USER_VERIFIED = "User was successfully verified.";
  public static final String AUTH_USER_NOT_VERIFIED = "User could not but verified.";
  public static final String AUTH_CURRENT_USER_RELOADED = "Reloaded current user.";
  public static final String DESCRIPTION_NO_COURSE_MODEL = "Model is null.";
  public static final String DESCRIPTION_SET_COURSE_ID_FAILED = "Setcourseid task failed.";
  public static final String DESCRIPTION_SIGN_OUT_FAILED = "Could not unregister User from course.";
  public static final String HISTORY_NO_COURSE_MODEL = "courseObj is null.";
  public static final String HISTORY_NO_USER_MODEL = "userModel is null.";
  public static final String HISTORY_NO_MEETING_ID = "meeting id is null.";
  public static final String HISTORY_LISTENER_FAILURE = "Course History Listener Failure";
  public static final String MEETING_NO_MEETING_MODEL = "meetingObj is null.";
  public static final String CREATE_COURSE_NO_COURSE_ID = "courseid is null.";
  public static final String CREATE_COURSE_CREATION_FAILED = "Kurs konnte nicht erstellt werden: ";
  public static final String CREAT_COURSE_ADDING_USER_FAILED = "User konnte dem Kurs nicht hinzugefügt werden: ";
  public static final String CREAT_COURSE_MAPPING_FAILED = "Das Mapping konnte nicht erstellt werden: ";
  public static final String CURRENT_COURSE_NO_THREAD_ID =  "threadKey is null.";
  public static final String CURRENT_COURSE_NO_MESSAGE_ID = "messageid is null";
  public static final String CURRENT_COURSE_SEND_MESSAGE_FAILED = "Nachricht konnte nicht versendet werden: ";
  public static final String CURRENT_COURSE_NO_MEETING_MODEL = "meetingObj is null.";
  public static final String CURRENT_COURSE_NO_USER_ID = "userKey is null.";
  public static final String CURRENT_COURSE_NO_MODEL = "model is null.";
  public static final String CURRENT_COURSE_NO_THREAD_MODEL = "threadModel is null.";
  public static final String CURRENT_COURSE_NO_THREAD_OBJECT = "threadObj is null.";
  public static final String CURRENT_COURSE_MESSAGE_MARKED = "a message is already marked.";
  public static final String ENTER_COURSE_LOADING_FAILURE = "Loading Course failed: ";
  public static final String PROFILE_NO_USER_MODEL = "user model null.";
  public static final String PROFILE_PASSWORD_RESET_FAILED = "reset failed.";
  public static final String PROFILE_OLD_PASSWORD_FALSE = "old password wrong.";

  /** ViewModels */
  public static final String LOGIN_VW_MODEL_NULL = "One of the preceding models is null.";
  public static final String LOGIN_VW_EMAIL_NULL = "Email is null.";
  public static final String LOGIN_VW_EMAIL_WRONG_PATTERN = "Email has wrong pattern.";
  public static final String LOGIN_VW_PASSWORD_NULL = "Password is null.";
  public static final String LOGIN_VW_PASSWORD_WRONG_PATTERN = "Password has wrong pattern.";
  public static final String DESCRIPTION_NO_TITLE = "title is null.";
  public static final String DESCRIPTION_WRONG_TITLE_PATTERN = "title has wrong pattern.";
  public static final String HISTORY_NO_MEETING_MODEL = "meetingsModel is null.";
  public static final String HISTORY_NO_TITLE = "title is null.";
  public static final String HISTORY_WRONG_TITLE_PATTERN = "title wrong pattern.";
  public static final String MEETING_NO_THREAD_MODEL = "threadModel is null.";
  public static final String MEETING_NO_TEXT = "text is null.";
  public static final String MEETING_WRONG_TEXT_PATTERN = "text wrong pattern.";
  public static final String CREATE_COURSE_NO_COURES_MODEL = "courseModel is null.";
  public static final String CREATE_COURSE_NO_TITLE = "title is null.";
  public static final String CREATE_COURSE_WRONG_TITLE_PATTERN = "title has wrong pattern.";
  public static final String CREATE_COURSE_NO_DESCRIPTION = "description is null.";
  public static final String CREATE_COURSE_WRONG_DESCRIPTION_PATTERN = "description has wrong pattern.";
  public static final String CREATE_COURSE_NO_INSTITUTION = "institution is null.";
  public static final String CREATE_COURSE_WRONG_INSTITUTION_PATTERN = "institution has wrong pattern.";
  public static final String CURRENT_COURSE_NO_MESSAGE_MODEL = "messageModel is null.";
  public static final String CURRENT_COURSE_NO_TITLE = "title is null.";
  public static final String CURRENT_COURSE_WORNG_TITLE_PATTERN = "title has wrong pattern.";
  public static final String ENTER_COURSE_NO_COURSE_MODEL = "courseModel is null.";
  public static final String ENTER_COURSE_NO_CODE_MAPPING = "codeMapping is null.";
  public static final String ENTER_COURSE_WRONG_CODE_MAPPING_PATTERN = "codeMapping has wrong pattern.";
  public static final String PROFILE_NO_NAME = "displayName is null.";
  public static final String PROFILE_WRONG_NAME_PATTERN = "displayName has wrong pattern.";
  public static final String PROFILE_NO_INSTITUTION = "institution is null.";
  public static final String PROFILE_WRONG_INSTITUTION_PATTERN = "institution has wrong pattern.";
  public static final String PROFILE_PASSWORD_MODEL_NULL = "passwordModel is null.";
  public static final String PROFILE_NO_OLD_PASSWORD = "oldPassword is null.";
  public static final String PROFILE_WRONG_OLD_PASSWORD_PATTERN = "oldPassword has wrong pattern.";
  public static final String PROFILE_NO_NEW_PASSWORD = "newPassword is null.";
  public static final String PROFILE_WRONG_NEW_PASSWORD_PATTERN = "newPassword has wrong pattern.";
  public static final String REGISTRATION_USER_MODEL_NULL = "userModel or passwordModel is null.";
  public static final String REGISTRATION_USERNAME_NULL = "";
  public static final String REGISTRATION_USERNAME_WRONG_PATTERN = "";
  public static final String REGISTRATION_EMAIL_NULL = "";
  public static final String REGISTRATION_EMAIL_WRONG_PATTERN = "";
  public static final String REGISTRATION_PASSWORD_NULL = "";
  public static final String REGISTRATION_PASSWORD_WRONG_PATTERN = "";

  /**=======================.
   *  Avatar Placeholder Ids
   * ======================= */
  public static final int PLACEHOLDER_AVATAR = R.drawable.ic_baseline_account_circle_24;
  public static final int ERROR_PROFILE_AVATAR = R.drawable.ic_baseline_account_circle_24;

  /**=======================.
   *  CodeMapping
   * ======================= */
  public static final String CODE_MAPPING_DEEP_LINK_KEY = "codeMapping";
  public static final String COURSE_CODE_MAPPING_CLIPBOARD = "Code_Mapping_Clip_Data";
  public static final String COURSE_CODE_MAPPING_CLIPBOARD_TOAST_TEXT = "Copied!";

  /**=======================.
   *  Deep Link
   * ======================= */
  public static final String DEEP_LINK_URL = "https://app.vairasza.dev/unserhoersaal/";
  public static final String COPY_KEY_CLIPBOARD = "Copy_Key_Clipboard";
  public static final String CODE_MAPPING_READABILITY_ITEM = "-";
  public static final int READABILITY_ITEM_POSITION_1 = 3;
  public static final int READABILITY_ITEM_POSITION_2 = 6;

  /**=======================.
   *  Verification Check
   * ======================= */
  public static final int VERIFICATION_EMAIL_VERIFIED_CHECK_INTERVAL = 2000;

  /** QrCode. */
  public static final int DIMEN = 400;
  public static final String QR_CODE = "Qr_Code";
  public static final String QR_CODE_TOAST = "Created QR code";
  public static final String QR_CODE_FILE_NAME = "qrCodeImage";
  public static final int QR_CODE_COMPRESSION = 100;
  public static final String TYPE_PNG = "image/png";
  public static final String PATH_FOR_QR_CODE = "DCIM/";
  public static final String NO_REPLY_PASSWORD = "ASE-2022";
  public static final String NO_REPLY_EMAIL = "noreply.unserhoersaal@gmail.com";
  public static final String TYPE_IMAGE = "image/*";
  public static final String GALLERY_INTENT_TITLE = "Betrachte generierten Qr-Code";
  public static final String PNG_FILE_ENDING = ".png";
}
