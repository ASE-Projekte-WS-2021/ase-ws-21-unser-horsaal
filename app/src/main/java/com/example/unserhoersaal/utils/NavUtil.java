package com.example.unserhoersaal.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import androidx.databinding.BindingAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.unserhoersaal.R;
import com.example.unserhoersaal.viewmodel.ProfileViewModel;

public class NavUtil {

  @BindingAdapter("navigate")
  public static void navigate(View view, int navAction) {
    NavController navController = Navigation.findNavController(view);
    navController.navigate(navAction);
  }

  //reference: https://developer.android.com/guide/topics/ui/dialogs
  @BindingAdapter("viewModel")
  public static void deleteAccountDialog(View view, ProfileViewModel viewModel) {
    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
    builder.setMessage(R.string.dialog_delete_account_message)
            .setTitle(R.string.dialog_delete_account_title)
            .setPositiveButton(R.string.dialog_delete_account_true, (DialogInterface.OnClickListener) (dialog, which) -> {
              viewModel.deleteAccount();
              dialog.dismiss();
            })
            .setNegativeButton(R.string.dialog_delete_account_false, (DialogInterface.OnClickListener) (dialog, which) -> {
              dialog.dismiss();
            });

    AlertDialog dialog = builder.create();
    dialog.show();
  }
}
