package com.example.unserhoersaal.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ResetPasswordEnum;
import com.example.unserhoersaal.viewmodel.LoginViewModel;
import com.example.unserhoersaal.viewmodel.RegistrationViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.SignInMethodQueryResult;

/**
 * Utility class for alert dialogs
 * used in login and registraton
 * for: email verification and password reset
 */

public class DialogBuilder {

  /** Dialog for the email verification process in the registration screen.*/
  @BindingAdapter("viewModel")
  public AlertDialog.Builder verifyEmailDialogRegistration
          (View view, RegistrationViewModel viewModel) {

    /** Settings */
    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext(),
            AlertDialog.THEME_HOLO_DARK);
    dialog.setTitle(Config.DIALOG_VERIFICATION_TITLE);
    dialog.setMessage(Config.DIALOG_VERIFICATION_MESSAGE);
    dialog.setCancelable(false);

    /** Resend email verification button*/
    dialog.setPositiveButton(Config.DIALOG_SEND_BUTTON,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int arg1) {
                viewModel.resendEmailVerification();
                Toast.makeText(view.getContext(), Config.REG_VERIFY_EMAIL, Toast.LENGTH_LONG).show();
                dialog.show();
              }
            });

    return dialog;
  }

  /**
   * Dialog for the email verification process in the login screen.
   * used if user tries to log in without verifying its email
   */
  @BindingAdapter("viewModel")
  public void verifyEmailDialogLogin(View view, LoginViewModel viewModel) {

    /** Settings */
    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext(),
            AlertDialog.THEME_HOLO_DARK);
    dialog.setTitle(Config.DIALOG_VERIFICATION_TITLE);
    dialog.setMessage(Config.DIALOG_VERIFICATION_MESSAGE_LOGIIN);
    dialog.setCancelable(true);

    /** Resend email verification button*/
    dialog.setPositiveButton(Config.DIALOG_SEND_BUTTON,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int arg1) {
                viewModel.resendEmailVerification();
                Toast.makeText(view.getContext(), Config.REG_VERIFY_EMAIL,
                        Toast.LENGTH_LONG).show();
                dialog.show();
              }
            });

    /** Cancel button (Alternative: click outside the dialog box) */
    dialog.setNegativeButton(Config.DIALOG_CANCEL_BUTTON, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        viewModel.setVerificationStatusOnNull();
      }
    });

    dialog.show();
  }

}
