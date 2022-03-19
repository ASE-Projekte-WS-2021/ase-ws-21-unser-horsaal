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
import java.io.FileNotFoundException;
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
      //saveBitmap(bitmap, context);
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

  public static void saveImage(Bitmap bitmap, @NonNull String name, Context context) throws IOException {
    boolean saved;
    OutputStream fos;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ContentResolver resolver = context.getContentResolver();
      ContentValues contentValues = new ContentValues();
      contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
      contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
      contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "qrCode");
      Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
      fos = resolver.openOutputStream(imageUri);
    } else {
      String imagesDir = Environment.getExternalStoragePublicDirectory(
              Environment.DIRECTORY_DCIM).toString() + File.separator + "qrCode";

      File file = new File(imagesDir);

      if (!file.exists()) {
        file.mkdir();
      }

      File image = new File(imagesDir, name + ".png");
      fos = new FileOutputStream(image);

    }

    saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
    fos.flush();
    fos.close();
  }

  public static void galleryIntent(Context context) {
    // Select Image From Gallery
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_VIEW);
    Activity activity = (Activity) context;
    activity.startActivityForResult(Intent.createChooser(intent, "Betrachte generierten Qr-Code"),1);
  /*
// Override this method too
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      activity.getParent().onActivityResult(requestCode, resultCode, data);
      if (requestCode == 1) {
        if (resultCode == Activity.RESULT_OK) {
          if (data != null) {
            try {
              Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        } else if (resultCode == Activity.RESULT_CANCELED)  {
          Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
        }
      }
    }
    */

  }

}
