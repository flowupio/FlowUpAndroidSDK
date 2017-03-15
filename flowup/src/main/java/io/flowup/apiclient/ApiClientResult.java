/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.apiclient;

public class ApiClientResult<T> {

  private final T value;
  private final Error error;

  public ApiClientResult(T value) {
    this.value = value;
    this.error = null;
  }

  public ApiClientResult(Error error) {
    this.error = error;
    this.value = null;
  }

  public boolean isSuccess() {
    return value != null && error == null;
  }

  public T getValue() {
    return value;
  }

  public Error getError() {
    return error;
  }

  public enum Error {
    NETWORK_ERROR, UNAUTHORIZED, SERVER_ERROR, CLIENT_DISABLED, UNKNOWN
  }
}
