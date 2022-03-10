package com.example.unserhoersaal.utils;

import android.widget.ImageView;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.unserhoersaal.Config;

/** Adapter for the profile pictures. */
public class ImageAdapter {

  /** Set the profile image. */
  @BindingAdapter("setImage")
  public static void setImage(ImageView view, String url) {
    Glide.with(view)
            .load(url)
            .placeholder(Config.PLACEHOLDER_AVATAR)
            .error(Config.ERROR_PROFILE_AVATAR)
            .into(view);
  }

}
