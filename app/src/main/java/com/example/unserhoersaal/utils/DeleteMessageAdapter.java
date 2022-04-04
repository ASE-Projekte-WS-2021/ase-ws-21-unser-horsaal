package com.example.unserhoersaal.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import androidx.databinding.BindingAdapter;

import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.google.zxing.WriterException;

import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class DeleteMessageAdapter {

  private static final String TAG = "QrCodeCreator";

  /** JavaDoc for this method. */
  @BindingAdapter({"codeMapping", "viewmodel"})
  public static void generateQrCode(View view, String text,
                                    CourseDescriptionViewModel courseDescriptionViewModel) {
  }
}
