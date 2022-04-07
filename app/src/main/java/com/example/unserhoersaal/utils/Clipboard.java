package com.example.unserhoersaal.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;

/** JavaDoc for this class. */
public class Clipboard {

  /** JavaDoc for this method. */
  @BindingAdapter("copyToClipboard")
  public static void copyToClipboard(View view, String text) {
    ClipboardManager clipboardManager = (ClipboardManager) view.getContext()
            .getSystemService(Context.CLIPBOARD_SERVICE);
    String deepLink = Config.DEEP_LINK_URL + text;
    ClipData clipData = ClipData.newPlainText(Config.COURSE_CODE_MAPPING_CLIPBOARD, deepLink);
    clipboardManager.setPrimaryClip(clipData);

    Toast.makeText(view.getContext(),
            Config.COURSE_CODE_MAPPING_CLIPBOARD_TOAST_TEXT, Toast.LENGTH_SHORT).show();
  }

  /** Copy enter code for course. */
  @BindingAdapter("copyCodeToClipboard")
  public static void copyCodeToClipboard(View view, String text) {
    if(PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
    ClipboardManager clipboardManager = (ClipboardManager) view.getContext()
            .getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clipData = ClipData.newPlainText(Config.COPY_KEY_CLIPBOARD, text);
    clipboardManager.setPrimaryClip(clipData);

    Toast.makeText(view.getContext(),
            Config.COURSE_CODE_MAPPING_CLIPBOARD_TOAST_TEXT, Toast.LENGTH_SHORT).show();
  }

}