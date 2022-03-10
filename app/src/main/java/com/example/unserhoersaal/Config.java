package com.example.unserhoersaal;

import java.text.SimpleDateFormat;

/** Config Class. */
public class Config {

  /** ChatAdapter. */
  public static final SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("HH:mm");
  public static final SimpleDateFormat OLD_FORMAT = new SimpleDateFormat("dd. MMMM");
  public static final SimpleDateFormat DATE_TIME_FORMAT
          = new SimpleDateFormat("dd. MMMM, HH:mm");
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd. MM. yyyy");

  /** Database Children. */
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

  /** Repository Errors */
  public static final String COURSE_DESCRIPTION_UNREGISTER_COURSE_FAILED = "Das Abmelden vom Kurs war nicht erfolgreich!";
  public static final String COURSE_DESCRIPTION_SETCOURSEID_FAILED = "Kursid konnte nicht gesetzt werden.";
  public static final String COURSE_DESCRIPTION_COULD_NOT_LOAD_USER = "Nutzer konnte nicht geladen werden.";
  public static final String COURSE_HISTORY_MEETING_NOT_CREATED = "Das Meeting konnte nicht erstellt werden.";
  public static final String COURSE_HISTORY_LISTENER_FAILURE = "Course History Listener Failure";
  public static final String COURSE_MEETING_THREAD_CREATION_FAILURE = "Der Thread konnte nicht erstellt werden";
  public static final String COURSE_MEETING_LISTENER_FAILURE = "Course Meeting Listener Failure";
  public static final String COURSE_PARTICIPANTS_LISTENER_FAILURE = "Course Participants Listener Failure";

  /** Profile Error */
  public static final String PROFILE_FAILED_TO_LOAD_USER = "Der Nutzer konnte nicht geladen werden.";
  public static final String PROFILE_FAILED_TO_CHANGE_PASSWORD = "Das Password konnte nicht geändert werden.";
  public static final String PROFILE_FAILED_TO_CHANGE_INSTITUTION = "Die Institution konnte nicht geändert werden.";
  public static final String PROFILE_FAILED_TO_CHANGE_DISPLAYNAME = "Der Nutzername konnte nicht geändert werden.";

  /** ViewModel */
  public static final String VM_TITLE_NULL = "Der Titel darf nicht null sein.";
  public static final String VM_TITLE_WRONG_PATTERN = "Der Titel entspricht nicht dem vorgegebenen Pattern.";
  public static final String VM_TEXT_NULL = "Der Titel darf nicht null sein.";
  public static final String VM_TEXT_WRONG_PATTERN = "Der Titel entspricht nicht dem vorgegebenen Pattern.";
  public static final String VM_INSTITUTION_NULL = "Der Text für Institution darf nicht null sein.";
  public static final String VM_INSTITUTION_WRONG_PATTERN = "Der Text für Institution entspricht nicht dem vorgegebenen Pattern.";
  public static final String VM_CODEMAPPING_NULL = "Der Beitrittscode darf nicht leer sein.";
  public static final String VM_CODEMAPPING_WRONG_PATTERN = "Der Beitrittscode hat ein ungültiges Muster.";

  /** UI. */
  public static final int PLACEHOLDER_AVATAR = R.drawable.ic_baseline_account_circle_24;
  public static final int ERROR_PROFILE_AVATAR = R.drawable.ic_baseline_account_circle_24;

  /** Login Failed */
  public static final String LOGIN_FAILED = "Der Loginversuch war nicht erfolgreich.";

  /** Password Reset Failed */
  public static final String PASSWORD_RESET_FAILED = "Password reset failed.";

  /** Verification */
  public static final String VERIFICATION_FIREBASE_USER_NULL = "Firebase User is null";

  /** Login Error Message. */
  public static final String EMAIL_EMPTY = "Bitte geben Sie eine Emailadresse ein!";
  public static final String EMAIL_PATTERN_WRONG = "Die eingegebene Email ist falsch!";
  public static final String PASSWORD_EMPTY = "Bitte geben Sie ein Passwort ein!";
  public static final String PASSWORD_PATTERN_WRONG = "Das eingegebene Passwort ist falsch!"
          + "Passwort vergessen?";

  /** Registration Error Message. */
  public static final String REG_FAILED = "Die Registrierung konnte nicht abgeschlossen werden.";
  public static final String REG_EMAIL_PATTERN_WRONG = "Die Email wurde falsch eingegeben.";
  public static final String REG_EMAIL_EMPTY = "Bitte geben Sie eine Emailaddresse ein.";
  public static final String REG_PASSWORD_PATTERN_WRONG = "Das Passwort muss mind. 8 Zeichen "
          + "lang sein und mind. 1 Groß- und Kleinbuchstaben, sowie mind. 1 Zahl enthalten.";
  public static final String REG_PASSWORD_EMPTY = "Bitte geben Sie ein Password ein.";
  public static final String REG_USERNAME_EMPTY = "Bitte geben Sie einen Nutzernamen ein.";
  public static final String REG_USERNAME_WRONG_PATTERN = "Der eingegebene Nutzername muss "
          + "mind. 3 und max. 15 Zeichen lang sein.";
  public static final String VERIFICATION_EMAIL_NOT_SENT = "Die Verfizierungsemail konnte"
          + " nicht versandt werden";

  public static final String STATE_LIVE_DATA_NULL = "Databinding Error";

  /** Toast Messages. */
  public static final String REG_VERIFY_EMAIL = "Verifizierungsemail versandt";

  /** Alert Dialog Registration. */
  public static final String DIALOG_VERIFICATION_TITLE = "Emailverifizierung";
  public static final String DIALOG_VERIFICATION_MESSAGE = "Eine Email mit dem Verifizierungscode"
          + " wurde an die von Ihnen angegebene Email versandt.";
  public static final String DIALOG_SEND_BUTTON = "Erneut senden";
  public static final String DIALOG_CANCEL_BUTTON = "Schließen";

  /** Alert Dialog Login. */
  public static final String DIALOG_VERIFICATION_MESSAGE_LOGIIN = "Sie haben Ihre Email noch "
          + "nicht verifiziert. Verifizierungsmail erneut senden?";

  /** Dialog Password Reset. */
  public static final String DIALOG_EMAIL_PATTERN_WRONG = "Die eingegebene Email wurde falsch "
          + "eingegeben oder es existiert kein Account zu dieser Email Adresse";
  public static final String DIALOG_PASSWORD_RESET_SUCCESS = "Email mit einem Link zur "
          + "Passwortzurücksetzung wurde versandt";

  /** Regex. */
  /* reference: https://ihateregex.io/expr/username/ */
  public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
  /* reference: https://ihateregex.io/expr/password/
     In this case a password may contain:
    (?=.*?[A-Z]) : At least one upper case English letter
    (?=.*?[a-z]) : At least one lower case English letter
    (?=.*?[0-9]) : At least one digit
    .{8,} : Minimum eight in length
    The lookahead ( (?=.*?[A-Z]) ) is used to check if after some characters if there is an
    occurance of an upper case letter. Similarly all the other lookaheads(lower, numbers, etc)
    are checked to complete the whole regex.
  */
  public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$";
  //TODO: check if this matches our policy
  public static final String TITLE_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,64}$";
  public static final String INSTITUTION_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,30}$";
  public static final String TEXT_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{1,200}$";

  /** CodeMapping. */
  public static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final int CODE_LENGTH = 9;
  public static final String CODE_MAPPING_DEEP_LINK_KEY = "codeMapping";
  public static final String COURSE_CODE_MAPPING_CLIPBOARD = "Code_Mapping_Clip_Data";
  public static final String COURSE_CODE_MAPPING_CLIPBOARD_TOAST_TEXT = "Copied!";
  public static final String DEEP_LINK_URL = "https://app.vairasza.dev/unserhoersaal/";

}
