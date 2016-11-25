/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.android;

import android.content.Context;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;

public class FileSystemTest {

  private FileSystem fileSystem;

  @Before public void setUp() {
    Context context = getInstrumentation().getContext();
    fileSystem = new FileSystem(context);
  }

  @Test public void theInternalStoragePathShouldBeCorrect() {
    String internalStoragePath = fileSystem.getInternalStoragePath();

    assertEquals(internalStoragePath, "/data/data/io.flowup.test/");
  }

  @Test public void theSharedPreferencesPathShouldBeCorrect() {
    String sharedPreferencesPath = fileSystem.getSharedPreferencesPath();

    assertEquals(sharedPreferencesPath, "/data/data/io.flowup.test/shared_prefs");
  }
}