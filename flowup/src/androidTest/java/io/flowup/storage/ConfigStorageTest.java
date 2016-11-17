/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import io.flowup.config.Config;
import io.flowup.config.storage.ConfigStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

public class ConfigStorageTest {

  private ConfigStorage storage;
  private SQLDelightfulOpenHelper openHelper;

  @Before public void setUp() {
    Context context = getInstrumentation().getContext();
    openHelper = new SQLDelightfulOpenHelper(context);
    storage = new ConfigStorage(openHelper);
    clearDatabase();
  }

  @After public void tearDown() {
    clearDatabase();
  }

  @Test public void returnsEnabledIfThereIsNoConfigPersisted() {
    Config config = storage.getConfig();

    assertEquals(new Config(), config);
  }

  @Test public void updatesThePersistedConfig() {
    Config newConfig = new Config(false);

    storage.updateConfig(newConfig);
    Config persistedConfig = storage.getConfig();

    assertEquals(newConfig, persistedConfig);
  }

  @Test public void thereIsJustOneConfigObjectPersisted() {
    Config newConfig = new Config(false);
    storage.updateConfig(newConfig);
    storage.updateConfig(newConfig);

    long numberOfConfigs = getNumberOfConfigs();

    assertEquals(1, numberOfConfigs);
  }

  private void clearDatabase() {
    storage.clearConfig();
  }

  private int getNumberOfConfigs() {
    SQLiteDatabase readableDatabase = openHelper.getReadableDatabase();
    int count = readableDatabase.rawQuery("SELECT * FROM config", new String[0]).getCount();
    readableDatabase.close();
    return count;
  }
}
