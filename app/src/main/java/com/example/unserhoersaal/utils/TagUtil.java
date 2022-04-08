package com.example.unserhoersaal.utils;

import android.view.View;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.enums.TagEnum;
import com.example.unserhoersaal.model.ThreadModel;
import java.util.List;

/** Class for setting and removing tags. */
public class TagUtil {

  /** Sets and removes tags. */
  @BindingAdapter({"threadModel", "tagEnum"})
  public static void setTag(View view, ThreadModel threadModel, TagEnum tagEnum) {
    if (PreventDoubleClick.checkIfDoubleClick()) {
      return;
    }
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
