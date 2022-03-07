package com.example.unserhoersaal;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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
    window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
    window.setNavigationBarColor(this.getResources().getColor(android.R.color.transparent));
    window.setBackgroundDrawable(background);
  }

}
