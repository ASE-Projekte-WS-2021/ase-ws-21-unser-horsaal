package com.example.unserhoersaal.utils;

import androidx.lifecycle.MutableLiveData;

import com.example.unserhoersaal.enums.ErrorTag;

/** Extension to MutableLiveData. Offers public methods to access DataStatus and Error messages
 * in StateData helping with handling invalid inputs and various results from Firebase.
 * code reference: https://stackoverflow.com/a/53420462/13620136
 **/
public class StateLiveData<T> extends MutableLiveData<StateData<T>> {

  /**
   * Use this to put the Data on a LOADING Status
   */
  public void postLoading() {
    postValue(new StateData<T>().loading());
  }

  /**
   * Use this to put the Data on a ERROR DataStatus
   * @param throwable the error to be handled
   */
  public void postError(Throwable throwable, ErrorTag errorTag) {
    postValue(new StateData<T>().error(throwable, errorTag));
  }

  /**
   * Use this to put the Data on a SUCCESS DataStatus
   * @param data
   */
  public void postSuccess(T data) {
    postValue(new StateData<T>().success(data));
  }

  /**
   * Use this to put the Data on a COMPLETE DataStatus
   */
  public void postComplete() {
    postValue(new StateData<T>().complete());
  }

}