package com.example.unserhoersaal.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.google.zxing.WriterException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/** JavaDoc for this class. */
public class QrCodeCreator {

  /** JavaDoc for this method. */
  @BindingAdapter({"codeMapping", "viewmodel", "context"})
  public static void generateQrCode(View view, String text,
                                    CourseDescriptionViewModel courseDescriptionViewModel,
                                    Context context) {
    Bitmap bitmap;
    String deepLink = Config.DEEP_LINK_URL + text;
    QRGEncoder qrgEncoder = new QRGEncoder(deepLink, null, QRGContents.Type.TEXT, Config.DIMEN);
    try {
      bitmap = qrgEncoder.encodeAsBitmap();
      saveBitmap(bitmap, context);
      courseDescriptionViewModel.setQrCodeBitmap(bitmap);
    } catch (WriterException e) {
      Log.e(Config.UNSPECIFIC_ERROR, e.toString());
    }
    Toast.makeText(view.getContext(),
            Config.QR_CODE_TOAST, Toast.LENGTH_SHORT).show();
  }

  private static void saveBitmap(Bitmap bitmap, Context context) {
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = context.openFileOutput(Config.QR_CODE_FILE_NAME, Context.MODE_PRIVATE);
      bitmap.compress(Bitmap.CompressFormat.PNG, Config.QR_CODE_COMPRESSION, fileOutputStream);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (fileOutputStream != null) {
        try {
          fileOutputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
