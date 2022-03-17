package com.example.unserhoersaal.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.enums.TagEnum;
import com.example.unserhoersaal.model.ThreadModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class TagUtil {

  private static final String TAG = "TagUtil";

  @BindingAdapter("setTag")
  public static void setTag(Chip chip, ThreadModel threadModel) {
    List<TagEnum> tags = threadModel.getTags();
    TagEnum tagEnum = null;
    switch (chip.getId()) {
      case R.id.chipTagSubjectMatter:
        tagEnum = TagEnum.SUBJECT_MATTER;
        break;
      case R.id.chipTagMistake:
        tagEnum = TagEnum.MISTAKE;
        break;
      case R.id.chipTagExamination:
        tagEnum = TagEnum.EXAMINATION;
        break;
      case R.id.chipTagOrganisation:
        tagEnum = TagEnum.ORGANISATION;
        break;
      case R.id.chipTagOther:
        tagEnum = TagEnum.OTHER;
        break;
      default:
        break;
    }
    if (tagEnum != null) {
      if (tags.contains(tagEnum)) {
        tags.remove(tagEnum);
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.LTGRAY));
      } else {
        tags.add(tagEnum);
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.GRAY));
      }
      Log.d(TAG, "setTag: " + threadModel.getTags().size());
    }
  }

  @BindingAdapter("addTags")
  public static void addTags(LinearLayout linearLayout, List<TagEnum> tags) {

  }
}
