package com.example.unserhoersaal;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
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
    setContentView(R.layout.activity_main);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }
    setupBottomNavigationMenu();
  }

  private void setupBottomNavigationMenu() {
    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
    NavController navController = navHostFragment.getNavController();
    BottomNavigationView bottomNavigationView =
            findViewById(R.id.activity_main_bottom_navigation_view);
    NavigationUI.setupWithNavController(bottomNavigationView, navController);
    navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
      if (destination.getId() == R.id.loginFragment
              || destination.getId() == R.id.registrationFragment) {
        bottomNavigationView.setVisibility(View.GONE);
      } else {
        bottomNavigationView.setVisibility(View.VISIBLE);
      }
    });
  }

}