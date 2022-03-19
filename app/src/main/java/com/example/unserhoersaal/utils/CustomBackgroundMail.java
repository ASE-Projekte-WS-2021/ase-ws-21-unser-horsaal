package com.example.unserhoersaal.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.example.unserhoersaal.Config;
import com.example.unserhoersaal.repository.AuthAppRepository;
import com.google.firebase.auth.FirebaseAuth;

public class CustomBackgroundMail {
  public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

  public static void sendBackgroundMail(Context context, Bitmap bitmap) {
    String userEmail = firebaseAuth.getCurrentUser().getEmail();
    BackgroundMail.newBuilder(context)
            .withUsername(Config.NO_REPLY_EMAIL)
            .withPassword(Config.NO_REPLY_PASSWORD)
            .withMailto(userEmail)
            .withType(BackgroundMail.TYPE_PLAIN)
            .withSubject("Qr Code")
            .withBody("this is the body")
            .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
              @Override
              public void onSuccess() {
                //do some magic
              }
            })
            .withOnFailCallback(new BackgroundMail.OnFailCallback() {
              @Override
              public void onFail() {
                //do some magic
              }
            })
            .send();
  }

}
