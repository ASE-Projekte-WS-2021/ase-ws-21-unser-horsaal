package com.example.unserhoersaal.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.example.unserhoersaal.enums.ErrorTag;

/** Extension to MutableLiveData. Offers public methods to access DataStatus, Error messages
 * and Error Tags in StateData helping with handling invalid inputs and various results
 * from Firebase. DataStatus is important for the Fragments when observing LiveData: while Status
 * loading a progressbar may be displayed, while Status Error various error messages are rendered
 * into special TextViews and Status success overrides existing data resulting in rerendering e.g.
 * the items in a RecyclerView or logging in the user. Error Tags are used to further differentiate
 * Views. postCreate is used to initiate and reset the StatusLiveData.
 * code reference: https://stackoverflow.com/a/53420462/13620136
 **/
public class StateLiveData<T> extends MutableLiveData<StateData<T>> {

  private static final String TAG = "StateLiveData";

  /**
   * Initiate or reset the data instance of a model class in a StateLiveData instance. The model
   * class is wrapped with StateDate which holds the actual data along with error messages and data
   * status.
   *
   * @param modelInstance new instance of a model class / list of model classes
   */
  public void postCreate(@Nullable T modelInstance) {
    setValue(new StateData<T>().create(modelInstance));
  }

  /**
   * Use this method to set the DataStatus to loading. Loading is used to set the visibility of a
   * progressspinner.
   */
  public void postLoading() {
    if (this.getValue() != null) {
      setValue(this.getValue().loading());
    } else {
      setValue(new StateData<T>().loading());
    }
  }

  /**
   * Use this method to set the DataStatus to error. While Error the Fragment can display error
   * messages to the user.
   *
   * @param throwable the error to be handled
   * @param errorTag to help differentiate ErrorTextViews
   */
  public void postError(@NonNull Throwable throwable, @NonNull ErrorTag errorTag) {
    if (this.getValue() != null) {
      setValue(this.getValue().error(throwable, errorTag));
    } else {
      setValue(new StateData<T>().error(throwable, errorTag));
    }
  }

  /**
   * Use this method to set the DataStatus to success. Success is used in Repositories to update new
   * data and indicate a rerendering of data.
   *
   * @param updatedData model class / list of model classes
   */
  public void postUpdate(@Nullable T updatedData) {
    if (this.getValue() != null) {
      setValue(this.getValue().update(updatedData));
    } else {
      setValue(new StateData<T>().update(updatedData));
    }
  }

  @Deprecated
  public void postComplete() {
    setValue(new StateData<T>().complete());
  }

  @Deprecated
  public void postComplete(T data) {
    setValue(new StateData<T>(data).complete());
  }

}