package com.example.unserhoersaal.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.unserhoersaal.enums.ErrorTag;

/** Wrapper Class for Data in StateLiveData. Extends Data with Error and Status so that
 *  we can observe this Data in any desired Fragment for different cases and display Error or
 *  Warning Messages accordingly to the User. */
//code reference: https://stackoverflow.com/a/53420462/13620136
public class StateData<T> {

  @NonNull
  private DataStatus status;

  @Nullable
  private T data;

  @Nullable
  private Throwable error;

  @NonNull
  private ErrorTag errorTag;

  @Deprecated
  public StateData(@Nullable T data) {
    this.status = DataStatus.CREATED;
    this.data = data;
    this.error = null;
    this.errorTag = ErrorTag.NULL;
  }

  public StateData() {
    this.status = DataStatus.CREATED;
    this.error = null;
    this.errorTag = ErrorTag.NULL;
  }

  /** Instantiate or reset data using the constructor. Status, Error and ErrorTag is created here. */
  public StateData<T> create(T modelInstance) {
    this.status = DataStatus.CREATED;
    this.data = modelInstance;
    this.error = null;
    this.errorTag = ErrorTag.NULL;
    return this;
  }

  /** Set DataStatus to loading. */
  public StateData<T> loading() {
    this.status = DataStatus.LOADING;
    this.error = null;
    this.errorTag = ErrorTag.NULL;
    return this;
  }

  /** Updates the current data in this class.
   * @param data update data */
  public StateData<T> update(@Nullable T data) {
    this.status = DataStatus.UPDATE;
    this.data = data;
    this.error = null;
    this.errorTag = ErrorTag.NULL;
    return this;
  }

  @Deprecated
  public StateData<T> success() {
    this.status = DataStatus.CREATED;
    this.error = null;
    this.errorTag = ErrorTag.NULL;
    return this;
  }

  /** Method to set an error message.
   * Removed this.data = null because it would lead to many null pointer exceptions.
   * Better call postCreate to wipe data.
   * @param error error to be handled
   * @param errorTag to help differentiate ErrorTextViews
   * */
  public StateData<T> error(@NonNull Throwable error, ErrorTag errorTag) {
    this.status = DataStatus.ERROR;
    this.error = error;
    this.errorTag = errorTag;
    return this;
  }

  @Deprecated
  public StateData<T> complete() {
    return this;
  }

  @NonNull
  public DataStatus getStatus() {
    return status;
  }

  @Nullable
  public T getData() {
    return data;
  }

  @Nullable
  public Throwable getError() {
    return error;
  }

  @NonNull
  public ErrorTag getErrorTag() {
    return errorTag;
  }

  public enum DataStatus {
    CREATED,
    LOADING,
    ERROR,
    UPDATE
  }

}