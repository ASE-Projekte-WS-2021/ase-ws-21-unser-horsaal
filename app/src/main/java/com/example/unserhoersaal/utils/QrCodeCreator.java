package com.example.unserhoersaal.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.google.zxing.WriterException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrCodeCreator {
  private static int qrCodeFilesCount = 0;

  /** JavaDoc for this method. */
  @BindingAdapter({"codeMapping", "viewmodel", "context"})
  public static void generateQrCode(View view, String text,
                                    CourseDescriptionViewModel courseDescriptionViewModel,
                                    Context context) {
    saveText(context);
    Bitmap bitmap;
    String deepLink = Config.DEEP_LINK_URL + text;
    QRGEncoder qrgEncoder = new QRGEncoder(deepLink, null, QRGContents.Type.TEXT, Config.DIMEN);
    try {
      bitmap = qrgEncoder.encodeAsBitmap();
      //Log.d(Config.QR_CODE, "created bitmap with qr code");
      //Log.d(Config.QR_CODE, bitmap.toString());
      saveBitmap(bitmap);
      //Log.d(Config.QR_CODE, "saved bitmap");
      courseDescriptionViewModel.setQrCodeBitmap(bitmap);
    } catch (WriterException e) {
      Log.e("Tag", e.toString());
    }
    Toast.makeText(view.getContext(),
            Config.COURSE_CODE_MAPPING_CLIPBOARD_TOAST_TEXT, Toast.LENGTH_SHORT).show();
  }

  @BindingAdapter("setImageView")
  public static void setImageView(ImageView iv, Bitmap bitmap) {
    iv.setImageBitmap(bitmap);
  }

  private static void saveBitmap(Bitmap bitmap) {
    qrCodeFilesCount++;
    String filename = "qrCode" + qrCodeFilesCount;
    try (FileOutputStream out = new FileOutputStream(filename)) {
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
      //Log.d(Config.QR_CODE, "saving bitmap");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void saveText(Context context) {
    String text = "Beispiel Text";
    FileOutputStream fileOutputStream = null;

    try {
      fileOutputStream = context.openFileOutput("dateiName1", Context.MODE_PRIVATE);
      fileOutputStream.write(text.getBytes());
      Log.d(Config.QR_CODE, "speicher Text");
      Log.d(Config.QR_CODE, context.getFilesDir().toString());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
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
