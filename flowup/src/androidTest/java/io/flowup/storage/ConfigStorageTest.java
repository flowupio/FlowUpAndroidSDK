/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.content.Context;
import io.flowup.config.Config;
import io.flowup.config.storage.ConfigStorage;
import io.realm.Realm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

public class ConfigStorageTest {

  private ConfigStorage storage;

  @Before public void setUp() {
    Context context = getInstrumentation().getContext();
    storage = new ConfigStorage(context);
    clearRealmDB();
  }

  @After public void tearDown() {
    clearRealmDB();
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

  @Test public void thereIsJustOneConfigConfigPersisted() {
    Config newConfig = new Config(false);
    storage.updateConfig(newConfig);
    storage.updateConfig(newConfig);

    long numberOfConfigs = getRealm().where(io.flowup.config.storage.RealmConfig.class).count();
    assertEquals(1, numberOfConfigs);
  }

  private void clearRealmDB() {
    Realm realm = getRealm();
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.deleteAll();
      }
    });
    realm.close();
  }

  private Realm getRealm() {
    Context context = getInstrumentation().getContext();
    Realm.init(context);
    return Realm.getInstance(RealmConfig.getRealmConfig(context));
  }
}
