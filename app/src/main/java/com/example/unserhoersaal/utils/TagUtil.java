package com.example.unserhoersaal.utils;

import android.util.Log;
import android.view.View;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.enums.TagEnum;
import com.example.unserhoersaal.model.ThreadModel;
import java.util.List;

/** JavaDoc. */
public class TagUtil {

  private static final String TAG = "TagUtil";

  /** JavaDoc. */
  @BindingAdapter({"threadModel", "tagEnum"})
  public static void setTag(View view, ThreadModel threadModel, TagEnum tagEnum) {
    List<TagEnum> tags = threadModel.getTags();

    if (tagEnum != null) {
      if (tags.contains(tagEnum)) {
        tags.remove(tagEnum);
      } else {
        tags.add(tagEnum);
      }
    }
  }

}
