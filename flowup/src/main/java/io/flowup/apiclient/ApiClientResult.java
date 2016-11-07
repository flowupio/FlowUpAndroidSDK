/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.apiclient;

public class ApiClientResult<T> {

  private T value;
  private Error error;

  public ApiClientResult(T value) {
    this.value = value;
  }

  public ApiClientResult(Error error) {
    this.error = error;
  }

  public boolean isSuccess() {
    return value != null && error == null;
  }

  public boolean hasDataPendingToSync() {
    return false;
  }

  public T getValue() {
    return value;
  }

  public Error getError() {
    return error;
  }

  public enum Error {
    NETWORK_ERROR,
    UNAUTHORIZED,
    SERVER_ERROR,
    UNKNOWN
  }
}
