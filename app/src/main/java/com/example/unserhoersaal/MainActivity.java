package com.example.unserhoersaal;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Initiates the main activity.
 */
public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStatusBarGradiant();
    setContentView(R.layout.activity_main);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

  }

  /** JavaDoc for this method TODO. */
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public void setStatusBarGradiant() {
    Window window = this.getWindow();
    Drawable background = ResourcesCompat.getDrawable(getResources(),
            R.drawable.app_background, null);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    window.setNavigationBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    window.setBackgroundDrawable(background);
    saveText(getApplicationContext());
  }

  private static void saveText(Context context) {
    String text = "Beispiel Text";
    FileOutputStream fileOutputStream = null;

    try {
      fileOutputStream = context.openFileOutput("dateiName2", Context.MODE_PRIVATE);
      fileOutputStream.write(text.getBytes());
      Log.d(Config.QR_CODE, "speicher Text 2");
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
