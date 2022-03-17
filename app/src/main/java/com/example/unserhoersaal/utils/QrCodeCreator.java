package com.example.unserhoersaal.utils;

import android.graphics.Bitmap;

import androidmads.library.qrgenearator.QRGEncoder;

public class QrCodeCreator {
  QRGEncoder qrgEncoder;
  Bitmap bitmap;

  int dimen = width < height ? width : height;
  dimen = dimen * 3 / 4;

  // setting this dimensions inside our qr code
  // encoder to generate our qr code.
  qrgEncoder = new QRGEncoder(dataEdt.getText().toString(), null, QRGContents.Type.TEXT, dimen);
                    try {
    // getting our qrcode in the form of bitmap.
    bitmap = qrgEncoder.encodeAsBitmap();
    // the bitmap is set inside our image
    // view using .setimagebitmap method.
    qrCodeIV.setImageBitmap(bitmap);
  } catch (WriterException e) {
    // this method is called for
    // exception handling.
    Log.e("Tag", e.toString());
  }
}
