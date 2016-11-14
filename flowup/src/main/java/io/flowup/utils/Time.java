/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.utils;

import java.util.Calendar;

public class Time {

  public long now() {
    return System.currentTimeMillis();
  }

  public long nowInNanos() {
    return System.nanoTime();
  }

  public long twoDaysAgo() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -2);
    return calendar.getTimeInMillis();
  }

}
