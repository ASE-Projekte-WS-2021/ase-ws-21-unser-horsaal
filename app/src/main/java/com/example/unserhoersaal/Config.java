package com.example.unserhoersaal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;

import com.example.unserhoersaal.viewmodel.LoginViewModel;

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
  public static final int PLACEHOLDER_AVATAR = R.drawable.ic_baseline_account_circle_24;
  public static final int ERROR_PROFILE_AVATAR = R.drawable.ic_baseline_account_circle_24;
  public static final String COURSES_EMPTY = "Du bist noch keinen Kursen beigetreten";

  /** Login Error Message. */
  public static final String EMAIL_EMPTY = "Bitte geben Sie eine Email ein";
  public static final String EMAIL_PATTERN_WRONG = "Die eingegebene Email ist falsch";
  public static final String PASSWORD_EMPTY = "Bitte geben Sie ein Passwort ein";
  public static final String PASSWORD_PATTERN_WRONG = "Das eingegebene Passwort ist falsch. " +
          "Passwort vergessen?";

  /** Registration Error Message. */
  public static final String REG_EMAIL_PATTERN_WRONG = "Die Email wurde falsch eingegeben.";
  public static final String REG_PASSWORD_PATTERN_WRONG = "Das Passwort muss mind. 8 Zeichen " +
          "lang sein und mind. 1 Groß- und Kleinbuchstaben, sowie mind. 1 Zahl enthalten.";
  public static final String REG_USERNAME_EMPTY = "Bitte geben Sie einen Nutzernamen ein.";
  public static final String REG_USERNAME_WRONG_PATTERN = "Der eingegebene Nutzername muss " +
          "mind. 3 und max. 15 Zeichen lang sein.";

  /** Toast Messages. */
  public static final String REG_VERIFY_EMAIL = "Verifizierungsemail versandt";
  public static final String LOG_PASSWORD_RESET_EMAIL = "Email zur Passwordzurücksetzung wurde" +
          " versandt";

  /** Alert Dialog Registration. */
  public static final String DIALOG_VERIFICATION_TITLE = "Emailverifizierung";
  public static final String DIALOG_VERIFICATION_MESSAGE = "Eine Email mit dem Verifizierungscode" +
          " wurde an die von Ihnen angegebene Email versandt.";
  public static final String DIALOG_SEND_BUTTON = "Erneut senden";
  public static final String DIALOG_CANCEL_BUTTON = "Schließen";

  /** Alert Dialog Login. */
  public static final String DIALOG_VERIFICATION_MESSAGE_LOGIIN = "Sie haben Ihre Email noch " +
          "nicht verifiziert. Verifizierungsmail erneut senden?";

  /** Alert Dialog Password Reset. */
  public static final String DIALOG_RESET_PASSWORD_TITLE = "Passwort zurücksetzen";
  public static final String DIALOG_RESET_PASSWORD_MESSAGE = "Sie erhalten eine Mail zum " +
          "Zurücksetzen des Passwords. Bitte geben Sie die zum Account zugehörige Email an: ";
  public static final String DIALOG_PASSWORD_RESET_BUTTON = "Passwort zurücksetzen";
  public static final String DIALOG_PASSWORD_RESET_CANCEL_BUTTON = "Abbrechen";
  public static final String DIALOG_PASSWORD_RESET_HINT = "Hier eingeben";
  public static final String DIALOG_EMAIL_PATTERN_WRONG = "Die eingegebene Email wurde falsch " +
          "eingegeben oder es existiert kein Account zu dieser Email Adresse";
  public static final String DIALOG_PASSWORD_RESET_ERROR = "Passwortrücksetzung gescheitert";

  /** Regex */
  /* reference: https://ihateregex.io/expr/username/ */
  public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
  /* reference: https://ihateregex.io/expr/password/
     In this case a password may contain:
    (?=.*?[A-Z]) : At least one upper case English letter
    (?=.*?[a-z]) : At least one lower case English letter
    (?=.*?[0-9]) : At least one digit
    .{8,} : Minimum eight in length
    The lookahead ( (?=.*?[A-Z]) ) is used to check if after some characters if there is an occurance of an upper case letter. Similarly all the other lookaheads(lower, numbers, etc) are checked to complete the whole regex.
  */
  public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$";

}
