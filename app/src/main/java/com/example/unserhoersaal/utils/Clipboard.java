package com.example.unserhoersaal.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;

public class Clipboard {

  @BindingAdapter("copyToClipboard")
  public static void copyToClipboard(View view, String text) {
    ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
    String deepLink = Config.DEEP_LINK_URL + Config.COURSE_CODE_MAPPING_CLIPBOARD;
    ClipData clipData = ClipData.newPlainText(deepLink, text);
    clipboardManager.setPrimaryClip(clipData);

    Toast.makeText(view.getContext(), Config.COURSE_CODE_MAPPING_CLIPBOARD_TOAST_TEXT, Toast.LENGTH_SHORT).show();
  }
}