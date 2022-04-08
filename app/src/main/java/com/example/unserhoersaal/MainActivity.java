package com.example.unserhoersaal;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Initiates the main activity.
 */
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }
  }
}