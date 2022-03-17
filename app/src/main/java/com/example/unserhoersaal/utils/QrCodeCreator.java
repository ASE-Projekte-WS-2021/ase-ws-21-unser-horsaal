package com.example.unserhoersaal.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;

import com.example.unserhoersaal.Config;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrCodeCreator {
  QRGEncoder qrgEncoder;


  /** JavaDoc for this method. */
  @BindingAdapter("generateQrCode")
  public static void generateQrCode(View view, String text) {
    Bitmap bitmap;
    String deepLink = Config.DEEP_LINK_URL + text;
    QRGEncoder qrgEncoder = new QRGEncoder(deepLink, null, QRGContents.Type.TEXT, Config.DIMEN);
    try {
      bitmap = qrgEncoder.encodeAsBitmap();
    } catch (WriterException e) {
      Log.e("Tag", e.toString());
    }
    Toast.makeText(view.getContext(),
            Config.COURSE_CODE_MAPPING_CLIPBOARD_TOAST_TEXT, Toast.LENGTH_SHORT).show();
  }


}
