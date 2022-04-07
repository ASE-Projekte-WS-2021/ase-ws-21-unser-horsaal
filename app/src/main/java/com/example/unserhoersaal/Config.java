package com.example.unserhoersaal;

import androidx.databinding.Bindable;

import java.text.SimpleDateFormat;

/** Config Class. */
public class Config {
  /**=======================.
   * User Input Lengths
   * ======================= */
  public static final int PASSWORD_LENGTH_MIN = 6;
  public static final int PASSWORD_LENGTH_MAX = 64;
  public static final int USERNAME_LENGTH_MIN = 3;
  public static final int USERNAME_LENGTH_MAX = 15;
  public static final int EMAIL_LENGTH_MAX = 64;
  public static final int INSTITUTION_LENGTH_MIN = 0;
  public static final int INSTITUTION_LENGTH_MAX = 75;

  public static final int COURSE_TITLE_LENGTH_MIN = 3;
  public static final int COURSE_TITLE_LENGTH_MAX = 100;
  public static final int MEETING_TITLE_LENGTH_MIN = 3;
  public static final int MEETING_TITLE_LENGTH_MAX = 100;
  public static final int THREAD_TITLE_LENGTH_MIN = 3;
  public static final int THREAD_TITLE_LENGTH_MAX = 100;

  public static final int COURSE_TEXT_LENGTH_MIN = 1;
  public static final int COURSE_TEXT_LENGTH_MAX = 500;
  public static final int MEETING_TEXT_LENGTH_MIN = 1;
  public static final int MEETING_TEXT_LENGTH_MAX = 500;
  public static final int THREAD_TEXT_LENGTH_MIN = 1;
  public static final int THREAD_TEXT_LENGTH_MAX = 500;
  public static final int MESSAGE_TEXT_LENGTH_MIN = 1;
  public static final int MESSAGE_TEXT_LENGTH_MAX = 500;

  public static final int COURSE_DESCRIPTION_LENGTH_MIN = 0; //optional
  public static final int COURSE_DESCRIPTION_LENGTH_MAX = 500;

  public static final int THREAD_OPTIONS_LENGTH_MIN = 1;
  public static final int THREAD_OPTIONS_LENGTH_MAX = 100;

  public static final int MEETING_HOUR_DURATION_MAX = 3;
  public static final int MEETING_MINUTE_DURATION_MAX = 2;

  public static final int TIME_HOUR_TO_MILLI = 3600000;
  public static final int TIME_MINUTE_TO_MILLI = 60000;
  public static final int TIME_HOUR_PER_DAY = 24;
  public static final int TIME_MINUTE_PER_HOUR = 60;
  public static final int TIME_MAX_MINUTES_PER_HOUR = 59;

  public static final int CODE_MAPPING_LENGTH = 9;

  /**=======================.
   * User Allowed Input Characters
   * ======================= */
  public static final String CODE_MAPPING_ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  /**=======================.
   *  Regex Patterns
   * ======================= */
  public static final String REGEX_PATTERN_USERNAME =
          String.format("^[a-zA-Z0-9_-]{%s,%s}$", USERNAME_LENGTH_MIN, USERNAME_LENGTH_MAX);
  public static final String REGEX_PATTERN_PASSWORD =
          String.format("^(?=.*[0-9a-zA-Z]).{%s,%s}$",
                  PASSWORD_LENGTH_MIN, PASSWORD_LENGTH_MAX);

  public static final String REGEX_PATTERN_TITLE = String.format(".{%s,%s}",
          COURSE_TITLE_LENGTH_MIN, COURSE_TITLE_LENGTH_MAX);
  public static final String REGEX_PATTERN_INSTITUTION = String.format(".{%s,%s}",
          INSTITUTION_LENGTH_MIN, INSTITUTION_LENGTH_MAX);
  public static final String REGEX_PATTERN_TEXT = String.format(".{%s,%s}",
          COURSE_TEXT_LENGTH_MIN, COURSE_TEXT_LENGTH_MAX);
  public static final String REGEX_PATTERN_DESCRIPTION = String.format(".{%s,%s}",
          COURSE_DESCRIPTION_LENGTH_MIN, COURSE_DESCRIPTION_LENGTH_MAX);
  public static final String REGEX_PATTERN_OPTIONS = String.format(".{%s,%s}",
          THREAD_OPTIONS_LENGTH_MIN, THREAD_OPTIONS_LENGTH_MAX);

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
  public static final String CHILD_PHOTO_URL = "photoUrl";
  public static final String CHILD_MEETINGS_COUNT = "meetingsCount";
  public static final String CHILD_MEMBER_COUNT = "memberCount";
  public static final String CHILD_POLL = "poll";
  public static final String CHILD_USER_POLL = "userPoll";
  public static final String CHILD_POLL_USER = "pollUser";
  public static final String CHILD_VOTES_COUNT = "votesCount";
  public static final String CHILD_OPTION_COUNT_1 = "optionsCount1";
  public static final String CHILD_OPTION_COUNT_2 = "optionsCount2";
  public static final String CHILD_OPTION_COUNT_3 = "optionsCount3";
  public static final String CHILD_OPTION_COUNT_4 = "optionsCount4";
  public static final String CHILD_DESCRIPTION = "description";
  public static final String CHILD_TITLE = "title";
  public static final String CHILD_TEXT = "text";
  public static final String CHILD_TEXT_DELETED = "isTextDeleted";

  /**=======================.
   * Shared Preferences Keys
   * ======================= */
  public static final String SHARED_PREF_KEY = "settings";
  public static final String SHARED_PREF_ONBOARDING_KEY = "onboard_complete";

  /**=======================.
   * Tabviews
   * ======================= */
  //Courses Tabview
  public static final int TAB_TODAY = 0;
  public static final String TAB_TODAY_NAME = "Heute";
  public static final int TAB_ALL = 1;
  public static final String TAB_ALL_NAME = "Alle";
  public static final int TAB_OWNED = 2;
  public static final String TAB_OWNED_NAME = "Erstellt";
  public static final int COURSES_NUMBER_OF_TABS = 3;

  //Meetings Tabview
  public static final int TAB_LIVE_CHAT = 0;
  public static final String TAB_LIVE_CHAT_NAME = "Live-Chat";
  public static final int TAB_POLL = 1;
  public static final String TAB_POLL_NAME = "Voting";
  public static final int TAB_QUESTIONS = 2;
  public static final String TAB_QUESTIONS_NAME = "Fragen";
  public static final int MEETINGS_NUMBER_OF_TABS = 3;

  //Onboarding TabView
  public static final int ONBOARDING_FRAGMENT_TAB_LENGTH = 6;
  public static final int ONBOARDING_USERNAME_FRAGMENT_POSITION = 1;
  public static final int ONBOARDING_ACCOUNT_FRAGMENT_POSITION = 3;

  /**=======================.
   * Poll
   * ======================= */
  //PollUtil
  public static final String PERCENTAGE_SING = "%";
  public static final int POLL_BAR_MIN_LENGTH = 1;
  public static final int POLL_BAR_LENGTH_FACTOR = 5;
  public static final int FACTOR_PROPORTION_TO_PERCENTAGE = 100;

  //PollViewModel
  public static final String OPTION_YES = "Ja";
  public static final String OPTION_NO = "Nein";
  public static final String OPTION_EMPTY = "";

  /**=======================.
   * Profile
   * ======================= */

  public static final String STORAGE_USER = "users/";
  public static final String STORAGE_FILENAME = "/profile.jpg";

  /**=======================.
   * Search Hint
   * ======================= */
  public static final String SEARCH_VIEW_HINT = "Deine Suche ...";
  public static final String TAG_SUBJECT_MATTER = "lehrstoff";
  public static final String TAG_EXAMINATION = "prüfung";
  public static final String TAG_MISTAKE = "fehler";
  public static final String TAG_ORGANISATION = "organisatorisch";
  public static final String TAG_OTHER = "sonstiges";

  /**=======================.
   * Internal Error Messages
   * ======================= */
  public static final String INTERNAL_AUTH_TOO_MANY_REQUESTS = "To many requests";
  public static final String FIREBASE_USER_NULL = "Firebase User is null";
  public static final String STATE_LIVE_DATA_NULL = "Databinding Error";
  public static final String AUTH_LOGOUT_SUCCESS = "";
  public static final String AUTH_LOGOUT_FAIL = "";
  public static final String LISTENER_FAILED_TO_RESOLVE = "Listener failed to reslove";
  public static final String UNKNOWN_USER = "<gelöschter Nutzer>";

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
          "Der Beitrittscode muss aus 9 Buchstaben bestehen.";
  public static final String DATABINDING_OPTION_NULL = "Option 1 und 2 dürfen nicht leer sein";
  public static final String DATABINDING_OPTION_WRONG_PATTERN =
          "Option 1 und/oder 2 enthalten ungültige Charaktere.";

  public static final String AUTH_EMAIL_EMPTY = "Bitte gib eine Email-Adresse ein!";
  public static final String AUTH_EMAIL_WRONG_PATTERN_REGISTRATION =
          "Email ist ungültig oder bereits vergeben!";
  public static final String AUTH_EMAIL_EXISTS =
          "Ein Account zu dieser Email-Adresse existiert bereits!";
  public static final String AUTH_EMAIL_WRONG_PATTERN_LOGIN = "Ungültige Email-Adresse!";
  public static final String AUTH_PASSWORD_EMPTY = "Bitte gib ein Passwort ein!";
  public static final String AUTH_PASSWORD_WRONG_PATTERN =
          "Das Passwort muss aus mindestens 6 Zeichen bestehen!";
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
  public static final String AUTH_EDIT_PROFILE_PICTURE_CHANGE_FAILED =
          "Das Profilbild konnte nicht geändert werden!";
  public static final String AUTH_VERIFICATION_EMAIL_SENT =
          "Verifizierungsemail versandt";
  public static final String AUTH_VERIFICATION_EMAIL_NOT_SENT =
          "Die Verfizierungsemail konnte nicht versandt werden!";
  public static final String AUTH_VERIFICATION_TOO_MANY_REQUESTS =
          "Von deinem Gerät wurden zu viele Anfragen registriert. Versuch es später erneut!";
  public static final String AUTH_PASSWORD_RESET_MAIL_SENT =
          "Email zur Passwortzurücksetzung versandt.";
  public static final String AUTH_PASSWORD_RESET_MAIL_NOT_SENT =
          "Die Email zur Passwortzurücksetzung konnte nicht versandt werden!";
  public static final String AUTH_ACCOUNT_DELETE_FAIL =
          "Der Account konnte nicht gelöscht werden!";

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
  public static final String NO_PICUTRE_SELECTED = "Es wurde kein Bild selektiert!";

  public static final String POLL_CREATION_FAILURE = "Die Umfrage konnte nicht erstellt werden!";
  public static final String POLLS_FAILED_TO_LOAD = "Die Umfragen konnten nicht geladen werden!";
  public static final String POLL_ID_NULL = "PollId is null.";
  public static final String POLL_MODEL_NULL = "pollModel is null.";

  public static final String LIVE_CHAT_FAILED_TO_LOAD = "Der Chat konnte nicht geladen werden!";

  public static final String MEETING_OBJECT_NULL = "meetingObj is null.";

  public static final String CREATE_MEETING_DATE_WRONG = "Es wurde kein Datum gewählt!";
  public static final String CREATE_MEETING_TIME_WRONG = "Es wurde kein Startzeitpunkt gewählt!";
  public static final String CREATE_MEETING_HOUR_DURATION_WRONG = "Bitte Dauer in Stunden wählen!";
  public static final String CREATE_MEETING_MINUTE_DURATION_WRONG = "Bitte Dauer in Minuten wählen!";
  public static final String CREATE_MEETING_MINUTE_DURATION_TOO_LONG
          = "Werte über 59 Minuten sind nicht möglich";

  /**=======================.
   *  Avatar Placeholder Ids
   * ======================= */
  public static final int PLACEHOLDER_AVATAR = R.drawable.ic_baseline_account_circle_24_black;
  public static final int ERROR_PROFILE_AVATAR = R.drawable.ic_baseline_account_circle_24_black;

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

  /**=======================.
   *  Thread-Card
   * ======================= */
  public static final int CARD_STROKE_WIDTH = 2;
  public static final int NO_STROKE = 0;

  /**=======================.
   *  Thread-Sort-Algorithm
   * ======================= */
  public static final String ASCENDING = "ascending";
  public static final String DESCENDING = "descending";

  /**=======================.
   *  Live-Chat
   * ======================= */
  public static final String LIVE_CHAT_MESSAGES_CHILD = "LiveChatMessages";

  /**=======================.
   *  Impressum
   * ======================= */
  public static final String GITHUB_LINK_LEGAL = "https://github.com/ASE-Projekte-WS-2021/ase-ws-21-unser-horsaal/tree/master/legal/";

  /**=======================.
   *  NavUtil
   * ======================= */

  public static final String TEXT_PLAIN = "text/plain";

   /**=======================.
   *  Camera-Intent
   * ======================= */
  public static final String CAMERA_INTENT_ERROR_TOAST = "Es konnte keine Kamera App auf deinem Smartphone gefunden werden";


}
