package com.example.unserhoersaal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.unserhoersaal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public void setStatusBarGradiant() {
    Window window = this.getWindow();
    Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.app_background, null);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
    window.setNavigationBarColor(this.getResources().getColor(android.R.color.transparent));
    window.setBackgroundDrawable(background);
    }
  }
