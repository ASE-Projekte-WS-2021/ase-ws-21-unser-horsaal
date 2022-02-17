package com.example.unserhoersaal.utils;

import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import com.example.unserhoersaal.Config;

public class ImageAdapter {

  @BindingAdapter("setImage")
  public static void setImage(ImageView view, String url) {
    Glide.with(view).load(url).placeholder(Config.PLACEHOLDER_AVATAR).error(Config.ERROR_PROFILE_AVATAR).into(view);
  }

}
