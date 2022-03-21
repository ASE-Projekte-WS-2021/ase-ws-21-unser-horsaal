package com.example.unserhoersaal.utils;

import android.util.Log;
import android.view.View;

import androidx.databinding.BindingAdapter;

import com.example.unserhoersaal.R;
import com.example.unserhoersaal.enums.FilterEnum;
import com.example.unserhoersaal.enums.SortEnum;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;
import com.google.android.material.chip.Chip;

public class FilterSortUtil {

  private static final String TAG = "FilterSortUtil";

  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "sortEnum"})
  public static void sort(Chip view, Chip chipActivated, CourseMeetingViewModel vm, SortEnum sortEnum) {
    if (view.isChecked()) {
      chipActivated.setVisibility(View.VISIBLE);
      //TODO better way than observing
      vm.setSortEnum(sortEnum);
      //TODO set sort with vm and sortEnum
    } else {
      removeSort(view, chipActivated, vm, sortEnum);
    }
  }

  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "sortEnum"})
  public static void removeSort(Chip view, Chip chipActivated, CourseMeetingViewModel vm, SortEnum sortEnum) {
    view.setChecked(Boolean.FALSE);
    chipActivated.setVisibility(View.GONE);
    //TODO remove sort with vm and sortEnum
  }

  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "filterEnum"})
  public static void filter(Chip view, Chip chipActivated, CourseMeetingViewModel vm, FilterEnum filterEnum) {
    if (view.isChecked()) {
      chipActivated.setVisibility(View.VISIBLE);
      //TODO set filter with vm and filterEnum
    } else {
      removeFilter(view, chipActivated, vm, filterEnum);
    }
  }

  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "filterEnum"})
  public static void removeFilter(Chip view, Chip chipActivated, CourseMeetingViewModel vm, FilterEnum filterEnum) {
    view.setChecked(Boolean.FALSE);
    chipActivated.setVisibility(View.GONE);
    //TODO remove filter with vm and filterEnum
  }

}
