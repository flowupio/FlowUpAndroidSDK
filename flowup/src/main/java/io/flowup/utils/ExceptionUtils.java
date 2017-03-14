/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

  public static String getStackTrace(final Throwable throwable) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw, true);
    throwable.printStackTrace(pw);
    return sw.getBuffer().toString();
  }

  public static String getMessage(final Throwable t) {
    if (t == null) {
      return "";
    }
    final String clazzName = t.getClass().getCanonicalName();
    final String message = t.getMessage();
    return clazzName + ": " + (message == null ? "" : message);
  }
}

