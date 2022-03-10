package com.example.unserhoersaal.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.unserhoersaal.enums.EmailVerificationEnum;
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

  public StateData(@Nullable T data) {
    this.status = DataStatus.CREATED;
    this.data = data;
    this.error = null;
    this.errorTag = ErrorTag.DEFAULT;
  }

  public StateData() {
    this.status = DataStatus.CREATED;
    this.data = null;
    this.error = null;
    this.errorTag = ErrorTag.DEFAULT;
  }

  public StateData<T> setData(T data) {
    this.data = data;
    return this;
  }

  public StateData<T> loading() {
    this.status = DataStatus.LOADING;
    this.data = null;
    this.error = null;
    return this;
  }

  public StateData<T> success(@NonNull T data) {
    this.status = DataStatus.SUCCESS;
    this.data = data;
    this.error = null;
    this.errorTag = ErrorTag.DEFAULT;
    return this;
  }

  public StateData<T> error(@NonNull Throwable error, ErrorTag errorTag) {
    this.status = DataStatus.ERROR;
    this.data = null;
    this.error = error;
    this.errorTag = errorTag;
    return this;
  }

  public StateData<T> complete() {
    this.status = DataStatus.COMPLETE;
    this.errorTag = ErrorTag.DEFAULT;
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
    SUCCESS,
    ERROR,
    LOADING,
    COMPLETE
  }
}