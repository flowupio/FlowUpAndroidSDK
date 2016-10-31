/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.utils;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class AppTrafficStatsUtilsTest {

  @Test public void returnsTrueIfTheValueIsNotMinusOne() {
    assertTrue(TrafficStatsUtils.isAPISupported(1024));
  }

  @Test public void returnsFalseIfTheValueIsMinusOne() {
    assertFalse(TrafficStatsUtils.isAPISupported(-1));
  }
}