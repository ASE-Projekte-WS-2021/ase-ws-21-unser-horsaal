package com.example.unserhoersaal.utils;

import android.view.View;
import androidx.databinding.BindingAdapter;
import com.example.unserhoersaal.enums.FilterEnum;
import com.example.unserhoersaal.enums.SortEnum;
import com.example.unserhoersaal.viewmodel.QuestionsViewModel;
import com.google.android.material.chip.Chip;

/** UtilClass for sorting and filtering. */
public class FilterSortUtil {

  /** Activate a sort.
   *
   * @param view Button that got clicked.
   * @param chipActivated The Chip that got activated.
   * @param vm ViewModel where the Enum gets set.
   * @param sortEnum The Enum that get setted in the ViewModel
   */
  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "sortEnum"})
  public static void sort(Chip view, Chip chipActivated, QuestionsViewModel vm,
                          SortEnum sortEnum) {
    if (view.isChecked()) {
      chipActivated.setVisibility(View.VISIBLE);
      vm.setSortEnum(sortEnum);
    } else {
      removeSort(view, chipActivated, vm, sortEnum);
    }
  }

  /** Remove a sort.
   *
   * @param view View to that represents Chip that got clicked.
   * @param vm QuestionViewModel where the activated chips are removed.
   */
  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "sortEnum"})
  public static void removeSort(Chip view, Chip chipActivated, QuestionsViewModel vm,
                                SortEnum sortEnum) {
    view.setChecked(Boolean.FALSE);
    chipActivated.setVisibility(View.GONE);
    //default is sort by newest
    vm.setSortEnum(SortEnum.NEWEST);
  }

  /** Add a filter.
   *
   * @param view Chip that got clicked.
   * @param chipActivated Chip that got activated.
   * @param vm The ViewModel where the Chip related data is set.
   * @param filterEnum Represents the FilterType that got set
   */
  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "filterEnum"})
  public static void filter(Chip view, Chip chipActivated, QuestionsViewModel vm,
                            FilterEnum filterEnum) {
    if (view.isChecked()) {
      chipActivated.setVisibility(View.VISIBLE);
      vm.setFilterEnum(filterEnum);
    } else {
      removeFilter(view, chipActivated, vm, filterEnum);
    }
  }

  /** Remove a filter.
   *
   * @param view Chip that got clicked.
   * @param chipActivated Chip that gets removed.
   * @param vm The ViewModel where the Chip related data is set.
   * @param filterEnum Represents the FilterType that got removed
   */
  @BindingAdapter({"chipActivated", "courseMeetingViewModel", "filterEnum"})
  public static void removeFilter(Chip view, Chip chipActivated, QuestionsViewModel vm,
                                  FilterEnum filterEnum) {
    view.setChecked(Boolean.FALSE);
    chipActivated.setVisibility(View.GONE);
    vm.setFilterEnum(filterEnum);
  }

}
