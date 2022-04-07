package com.example.unserhoersaal.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.viewmodel.CourseDescriptionViewModel;
import com.google.zxing.WriterException;
import java.io.IOException;
import java.io.OutputStream;

/** Class to generate a QrCode from a DeepLink. */
public class QrCodeCreator {

  private static final String TAG = "QrCodeCreator";

  /** Methode to create QrCode.
   *
   * @param  view  The Button view that was clicked.
   * @param  courseDescriptionViewModel is the ViewModel of the CourseDescriptionFragment.
   */
  @BindingAdapter({"codeMapping", "viewmodel"})
  public static void generateQrCode(View view, String text,
                                    CourseDescriptionViewModel courseDescriptionViewModel) {
    Bitmap bitmap;
    String deepLink = Config.DEEP_LINK_URL + text;
    QRGEncoder qrgEncoder = new QRGEncoder(deepLink, null, QRGContents.Type.TEXT, Config.DIMEN);

    try {
      bitmap = qrgEncoder.encodeAsBitmap();
      saveImage(bitmap, "name", view.getContext());
      galleryIntent(view.getContext());
      //courseDescriptionViewModel.setQrCodeBitmap(bitmap);
    } catch (WriterException | IOException e) {
      Log.e(TAG, e.getMessage());
    }
  }

  /** Constructor of ThreadAdapter.
   *
   * @param  bitmap  The Bitmap of the QrCode.
   * @param  name The Filename.
   * @param  context The Context of the Fragment that called the generateQrCode.
   */
  public static void saveImage(Bitmap bitmap, @NonNull String name, Context context)
          throws IOException {
    ContentResolver resolver = context.getContentResolver();
    ContentValues contentValues = new ContentValues();
    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, Config.TYPE_PNG);
    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Config.PATH_FOR_QR_CODE
            + Config.QR_CODE_FILE_NAME);
    Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    OutputStream fos = resolver.openOutputStream(imageUri);

    bitmap.compress(Bitmap.CompressFormat.PNG, Config.QR_CODE_COMPRESSION, fos);
    fos.flush();
    fos.close();
  }

  /** Constructor of ThreadAdapter.
   *
   * @param  context Context of the fragment that called the Methode.
   */
  public static void galleryIntent(Context context) {
    Intent intent = new Intent();
    intent.setType(Config.TYPE_IMAGE);
    intent.setAction(Intent.ACTION_VIEW);
    Activity activity = (Activity) context;
    activity.startActivity(Intent.createChooser(intent, Config.GALLERY_INTENT_TITLE), null);
  }

}