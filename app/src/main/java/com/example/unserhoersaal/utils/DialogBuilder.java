package com.example.unserhoersaal.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import androidx.databinding.BindingAdapter;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.enums.ResetPasswordEnum;
import com.example.unserhoersaal.viewmodel.LoginViewModel;
import com.example.unserhoersaal.viewmodel.RegistrationViewModel;

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

  /** Dialog for the reset password process in the login screen (via email).*/
  @BindingAdapter("viewModel")
  public void passwordResetDialog(View view, LoginViewModel viewModel) {

    /** Settings */
    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext(),
            AlertDialog.THEME_HOLO_DARK);
    dialog.setTitle(Config.DIALOG_RESET_PASSWORD_TITLE);
    dialog.setMessage(Config.DIALOG_RESET_PASSWORD_MESSAGE);
    dialog.setCancelable(true);
    EditText emailInput = new EditText(view.getContext());
    emailInput.setInputType(InputType.TYPE_CLASS_TEXT);
    emailInput.setGravity(Gravity.CENTER_HORIZONTAL);
    emailInput.setHint(Config.DIALOG_PASSWORD_RESET_HINT);
    emailInput.setTextColor(Color.WHITE);
    emailInput.setHintTextColor(Color.WHITE);
    dialog.setView(emailInput);

    /** Reset password via Email button.*/
    dialog.setPositiveButton(Config.DIALOG_PASSWORD_RESET_BUTTON,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int arg1) {
                String email = emailInput.getText().toString();
                /** Error-Case 1: email empty */
                if (Validation.emptyEmail(email)) {
                  Toast.makeText(view.getContext(), Config.EMAIL_EMPTY,
                          Toast.LENGTH_LONG).show();
                }
                /** Error-Case 2: email not empty but pattern is wrong */
                else if (!Validation.emptyEmail(email) && !Validation.emailHasPattern(email)) {
                  Toast.makeText(view.getContext(), Config.DIALOG_EMAIL_PATTERN_WRONG,
                          Toast.LENGTH_LONG).show();
                /** Success: send password reset mail */
                }
                else if (!Validation.emptyEmail(email) && Validation.emailHasPattern(email)) {
                  viewModel.sendPasswordResetMail(emailInput.getText().toString());
                  Toast.makeText(view.getContext(), Config.LOG_PASSWORD_RESET_EMAIL,
                          Toast.LENGTH_LONG).show();
                  viewModel.resetPasswordStatus.setValue(ResetPasswordEnum.RESET_PASSWORD_NO);
                } else {
                /** Error-Case 3: unknown problem */
                  Toast.makeText(view.getContext(), Config.DIALOG_PASSWORD_RESET_ERROR,
                          Toast.LENGTH_LONG).show();
                  viewModel.resetPasswordStatus.setValue(ResetPasswordEnum.RESET_PASSWORD_NO);
                }
              }
            });
    /** Cancel button */
    dialog.setNegativeButton(Config.DIALOG_PASSWORD_RESET_CANCEL_BUTTON,
            new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        viewModel.resetPasswordStatus.setValue(ResetPasswordEnum.RESET_PASSWORD_NO);
      }
    });
    dialog.show();
  }

}
