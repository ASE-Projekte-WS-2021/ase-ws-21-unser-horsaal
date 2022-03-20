package com.example.unserhoersaal.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


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
      try {
        saveImage(bitmap, "name", context);
      } catch (IOException e) {
        e.printStackTrace();
      }
      galleryIntent(context);
      courseDescriptionViewModel.setQrCodeBitmap(bitmap);
    } catch (WriterException e) {
      Log.e(Config.UNSPECIFIC_ERROR, e.toString());
    }
    Toast.makeText(view.getContext(),
            Config.QR_CODE_TOAST, Toast.LENGTH_SHORT).show();
  }

  public static void saveImage(Bitmap bitmap, @NonNull String name, Context context) throws IOException {
    OutputStream fos;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ContentResolver resolver = context.getContentResolver();
      ContentValues contentValues = new ContentValues();
      contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
      contentValues.put(MediaStore.MediaColumns.MIME_TYPE, Config.TYPE_PNG);
      contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Config.PATH_FOR_QR_CODE + Config.QR_CODE_FILE_NAME);
      Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
      fos = resolver.openOutputStream(imageUri);
    } else {
      String imagesDir = Environment.getExternalStoragePublicDirectory(
              Environment.DIRECTORY_DCIM).toString() + File.separator + "qrCode";
      File file = new File(imagesDir);
      if (!file.exists()) {
        file.mkdir();
      }
      File image = new File(imagesDir, name + Config.PNG_FILE_ENDING);
      fos = new FileOutputStream(image);
    }

    bitmap.compress(Bitmap.CompressFormat.PNG, Config.QR_CODE_COMPRESSION, fos);
    fos.flush();
    fos.close();
  }

  public static void galleryIntent(Context context) {
    Intent intent = new Intent();
    intent.setType(Config.TYPE_IMAGE);
    intent.setAction(Intent.ACTION_VIEW);
    Activity activity = (Activity) context;
    activity.startActivity(Intent.createChooser(intent, Config.GALLERY_INTENT_TITLE),null);
  }
}
