package com.example.unserhoersaal.utils;

import android.view.View;

import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.enums.FilterEnum;
import com.example.unserhoersaal.enums.SortEnum;
import com.example.unserhoersaal.viewmodel.CourseMeetingViewModel;
import com.google.android.material.chip.Chip;

public class FilterSortUtil {

  private static final String TAG = "FilterSortUtil";

  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "sortEnum"})
  public static void sort(Chip view, Chip chipActivated, CourseMeetingViewModel vm,
                          SortEnum sortEnum) {
    if (view.isChecked()) {
      chipActivated.setVisibility(View.VISIBLE);
      vm.setSortEnum(sortEnum);
    } else {
      removeSort(view, chipActivated, vm, sortEnum);
    }
  }

  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "sortEnum"})
  public static void removeSort(Chip view, Chip chipActivated, CourseMeetingViewModel vm,
                                SortEnum sortEnum) {
    view.setChecked(Boolean.FALSE);
    chipActivated.setVisibility(View.GONE);
    //default is sort by newest
    vm.setSortEnum(SortEnum.NEWEST);
  }

  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "filterEnum"})
  public static void filter(Chip view, Chip chipActivated, CourseMeetingViewModel vm,
                            FilterEnum filterEnum) {
    if (view.isChecked()) {
      chipActivated.setVisibility(View.VISIBLE);
      vm.setFilterEnum(filterEnum);
    } else {
      removeFilter(view, chipActivated, vm, filterEnum);
    }
  }

  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "filterEnum"})
  public static void removeFilter(Chip view, Chip chipActivated, CourseMeetingViewModel vm,
                                  FilterEnum filterEnum) {
    view.setChecked(Boolean.FALSE);
    chipActivated.setVisibility(View.GONE);
    vm.setFilterEnum(FilterEnum.NONE);
  }

}
