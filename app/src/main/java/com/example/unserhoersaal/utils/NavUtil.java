package com.example.unserhoersaal.utils;

import android.view.View;
import androidx.databinding.BindingAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class NavUtil {

  @BindingAdapter("navigate")
  public static void navigate(View view, int navAction) {
    NavController navController = Navigation.findNavController(view);
    navController.navigate(navAction);
  }
}
